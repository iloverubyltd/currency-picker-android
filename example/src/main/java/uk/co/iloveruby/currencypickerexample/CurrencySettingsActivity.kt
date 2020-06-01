package uk.co.iloveruby.currencypickerexample

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mynameismidori.currencypicker.*
import com.mynameismidori.currencypicker.CurrencyPicker.Companion.newInstance
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.allCurrencies
import uk.co.iloveruby.currencypickerexample.databinding.ActivityMainBinding
import java.util.*

class CurrencySettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.fragment_container, CurrencyPreferenceFragment.newInstance())
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    class CurrencyPreferenceFragment : PreferenceFragmentCompat(),
        CurrencyPickerListener, OnSharedPreferenceChangeListener {

        private lateinit var preferences: SharedPreferences
        private lateinit var currencyPicker: CurrencyPicker
        private lateinit var currencyPreference: CurrencyPreference
        private lateinit var multiSelectListPreference: MultiCurrencyPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            currencyPreference = findPreference("selectedCurrency")!!
            currencyPreference.setCurrenciesList(
                preferences.getStringSet(
                    "selectedCurrencies",
                    HashSet()
                )!!
            )
            currencyPicker = newInstance("Select Currency")
            multiSelectListPreference = findPreference("selectedCurrencies")!!

            val availableCurrencyNames: MutableList<CharSequence?> = arrayListOf()
            val availableCurrencyCodes: MutableList<CharSequence?> = arrayListOf()

            // You can limit the displayed countries
            val nc = arrayListOf<ExtendedCurrency>()
            for (c in allCurrencies) {
                //if (c.getSymbol().endsWith("0")) {
                nc.add(c)
                availableCurrencyCodes.add(c.code)
                availableCurrencyNames.add(c.name)
                //}
            }

            // and decide, in which order they will be displayed
            //Collections.reverse(nc);
            multiSelectListPreference.entries = availableCurrencyNames.toTypedArray()
            multiSelectListPreference.entryValues = availableCurrencyCodes.toTypedArray()

            currencyPicker.setCurrenciesList(nc)
            currencyPicker.setListener(this)
        }

        override fun onResume() {
            preferences.registerOnSharedPreferenceChangeListener(this)
            super.onResume()
        }

        override fun onPause() {
            preferences.registerOnSharedPreferenceChangeListener(this)
            super.onPause()
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences,
            key: String
        ) {
            if (key == "selectedCurrencies") {
                currencyPreference.setCurrenciesList(
                    preferences.getStringSet(
                        "selectedCurrencies",
                        HashSet()
                    )!!
                )
            }
        }

        override fun onSelectCurrency(
            name: String?,
            code: String?,
            symbol: String?,
            flagDrawableResID: Int
        ) {
            currencyPreference.value = code
        }

        companion object {
            private const val TAG = "CurrencyPreference"

            @JvmStatic @JvmOverloads
            fun newInstance(args: Bundle? = null) = CurrencyPreferenceFragment()
                .apply { arguments = args }
        }
    }
}
