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

        angleSummaries = resources.getStringArray(R.array.angle_unit_entries).toList()
        outputTypeSummaries = listOf(getString(R.string.display_result_item_decimal), getString(R.string.display_result_item_fraction), getString(R.string.display_result_item_scientific), getString(R.string.display_result_item_eng))

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
        var angleSummaries: List<String>? = null
        var outputTypeSummaries: List<String>? = null
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            preference.summary = when (value) {
                "deg" -> angleSummaries?.get(0)?: "deg"
                "rad" -> angleSummaries?.get(1)?: "rad"
                "gon" -> angleSummaries?.get(2)?: "gon"

                "decimal" -> outputTypeSummaries?.get(0)?: "decimal"
                "fraction" -> outputTypeSummaries?.get(1)?: "fraction"
                "scientific" -> outputTypeSummaries?.get(2)?: "scientific"
                "engineering" -> outputTypeSummaries?.get(3)?: "engineering"

                else -> value.toString()
            }
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, ""))
        }
    }


    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)
            for (pref in listOf(R.string.angle_key, R.string.orientation_background_key, R.string.display_result_key)) {
                findPreference<ListPreference>(getString(pref))?.also {
                    bindPreferenceSummaryToValue(it)
                }
            }
        }
    }

}