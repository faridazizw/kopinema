package com.example.faridaziz.kopinema.view.fragments.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.faridaziz.kopinema.App
import com.example.faridaziz.kopinema.R
import com.example.faridaziz.kopinema.SharePreference
import com.example.faridaziz.kopinema.models.Board
import com.example.faridaziz.kopinema.utils.showMessage
import com.example.faridaziz.kopinema.view.activities.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SetUserOrDIdFragment : Fragment() {
    private val TAG = this.javaClass.simpleName

    companion object {
        const val ARG_ = "SET_APA_" }

    private var _set_: String? = null

    private val sharedPreferences by lazy {
        SharePreference(context!!) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        _set_ = arguments?.getString(ARG_) ?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(
                    if (_set_ == "USERNAME") R.layout.fragment_set_username
                    else R.layout.fragment_set_device_id
            , container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding View
        val btn: Button = view.findViewById(R.id.btn_set)
        val edt: EditText = view.findViewById(R.id.edt_set)

        // Set OnClick Listener
        btn.setOnClickListener {
            val data = edt.text.toString().trim()
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference(App.DB)

            if (data.isEmpty()) {
                showMessage(context!!, R.string.warningInputEmpty)
                return@setOnClickListener
            }

            if (_set_ == "USERNAME") {
                if (sharedPreferences.idBoard != "NONE") {
                    /**
                     * Mengambil data users dari referensi /database/users
                     * Membungkus data idboard dan username ke HashMap
                     * dan melakukan push ke database supaya mendapatkan key unique.
                     */
                    val params = mapOf<String, String>(
                            "id_board" to sharedPreferences.idBoard,
                            "username" to data )

                    reference.child(App.USER)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}

                                override fun onDataChange(p0: DataSnapshot) {
                                    val dataString = p0.value.toString()
                                    val username = sharedPreferences.user

                                    if(! dataString.contains(edt.text.toString())) {
                                        reference.child(App.USER).push().setValue(params)
                                        sharedPreferences.user = data
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(context, "Username Sudah terdaftar",
                                                Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                } else showMessage(context!!, R.string.warningIdNotSet)
            } else {
                /**
                 * Mengambil data board dengan referensi /database/board
                 * Mengecek apakah id-board ada atau tidak. Jika :
                 * @ada maka akan menyimpan nilai ke data presistence.
                 * @tidakAda maka akan menampilkan pesan dengan Toast.
                 */
                reference.child(App.BOARD).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        for (p in p0.children) {
                            val board = p.getValue(Board::class.java) as Board

                            if (board.id == data) {
                                sharedPreferences.idBoard = data
                                break
                            } else {
                                showMessage(context!!, R.string.warningIdNotFound) }
                        }
                    }
                })

                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
        }
    }
}