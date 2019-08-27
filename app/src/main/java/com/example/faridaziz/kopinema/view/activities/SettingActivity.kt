package com.example.faridaziz.kopinema.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.faridaziz.kopinema.R
import com.example.faridaziz.kopinema.SharedPreferences
import com.example.faridaziz.kopinema.view.fragments.SetPrefFragment
import com.example.faridaziz.kopinema.view.fragments.SetWifiFragment

class SettingActivity
    : AppCompatActivity() {
    companion object {
        const val RECEIVE_DATA = "DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val arg = intent.getStringExtra(RECEIVE_DATA)
        val fragment = supportFragmentManager.beginTransaction()
        val bundle = Bundle()

        when(arg) {
            SharedPreferences.ID_BOARD, SharedPreferences.USERNAME -> {
                val target = SetPrefFragment()

                bundle.putString(SetPrefFragment.ARG_, arg)
                target.arguments = bundle
                fragment.replace(R.id.wrapper, target)
            }

            "WIFI" -> fragment.replace(R.id.wrapper, SetWifiFragment())
        }

        fragment.commit()
    }
}