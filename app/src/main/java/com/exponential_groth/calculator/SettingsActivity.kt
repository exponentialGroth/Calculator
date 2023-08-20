package com.exponential_groth.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.title = getString(R.string.settings_title)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
        }

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    companion object {
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            preference.summary = value.toString()
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference, list: List<String> = listOf()) {

            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            val value = when (PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, "")) {
                "deg" -> list[0]
                "rad" -> list[1]

                "decimal" -> list[0]
                "fraction" -> list[1]
                "scientific" -> list[2]
                "engineering" -> list[3]

                else -> PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, "")
            }

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value)
        }
    }


    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)
            findPreference<ListPreference>(getString(R.string.angle_key))?.let {
                bindPreferenceSummaryToValue(it, listOf(getString(R.string.item_degree), getString(R.string.item_radians)))
            }
            findPreference<ListPreference>(getString(R.string.orientation_background_key))?.let { bindPreferenceSummaryToValue(it) }
            findPreference<ListPreference>(getString(R.string.display_result_key))?.let { bindPreferenceSummaryToValue(it, listOf(getString(R.string.display_result_item_decimal), getString(R.string.display_result_item_fraction), getString(R.string.display_result_item_scientific), getString(R.string.display_result_item_eng))) }
        }
    }

}