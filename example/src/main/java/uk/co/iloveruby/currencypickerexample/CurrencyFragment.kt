package uk.co.iloveruby.currencypickerexample

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.mynameismidori.currencypicker.CurrencyPicker
import com.mynameismidori.currencypicker.CurrencyPickerListener
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.allCurrencies
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.getCurrencyByISO
import uk.co.iloveruby.currencypickerexample.databinding.FragmentCurrencyBinding
import java.util.*

class CurrencyFragment : Fragment(), View.OnClickListener, CurrencyPickerListener,
    OnSharedPreferenceChangeListener {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding: FragmentCurrencyBinding
        get() = _binding!!

    private lateinit var currencyPicker: CurrencyPicker
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        currencyPicker = CurrencyPicker.newInstance("Select Currency")

        binding.currencyPickerButton.setOnClickListener(this)
        binding.openFragment.setOnClickListener(this)
        binding.openPreferences.setOnClickListener(this)

        preferences.registerOnSharedPreferenceChangeListener(this)

        val selectedCurrency =
            preferences.getString("selectedCurrency", getString(R.string.default_currency))
        binding.textView3.text = selectedCurrency
        Toast.makeText(activity, selectedCurrency, Toast.LENGTH_LONG).show()

        // You can limit the displayed countries
        val nc = arrayListOf<ExtendedCurrency>()
        for (c in allCurrencies) {
            //if (c.getSymbol().endsWith("0")) {
            nc.add(c)
            //}
        }
        // and decide, in which order they will be displayed
        //Collections.reverse(nc);
        currencyPicker.setCurrenciesList(nc)
        currencyPicker.setCurrenciesList(
            preferences.getStringSet(
                "selectedCurrencies",
                HashSet()
            )!!
        )
        currencyPicker.setListener(this)
    }


    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (key == "selectedCurrency") {
            binding.textView3.text = sharedPreferences.getString(key, "")
        }
        if (key == "selectedCurrencies") {
            currencyPicker.setCurrenciesList(
                preferences.getStringSet(
                    "selectedCurrencies",
                    HashSet()
                )!!
            )
        }
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(this)
        binding.textView3.text = preferences.getString("selectedCurrency", "CZK")
    }

    override fun onPause() {
        super.onPause()
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSelectCurrency(
        name: String?, code: String?, symbol: String?,
        flagDrawableResID: Int
    ) {
        binding.run {
            selectedCurrencyFlagImageView.setImageResource(flagDrawableResID)
            selectedCurrencySymbolTextView.text = symbol
            selectedCurrencyIsoTextView.text = code
            selectedCurrencyNameTextView.text = name
        }
        currencyPicker.dismiss()
    }

    override fun onClick(v: View) {
        //do what you want to do when button is clicked
        when (v.id) {
            R.id.currency_picker_button -> currencyPicker.show(
                parentFragmentManager,
                "CURRENCY_PICKER"
            )
            R.id.openPreferences -> {
                val intent = Intent(
                    activity,
                    CurrencySettingsActivity::class.java
                )
                startActivity(intent)
            }
            R.id.openFragment -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, currencyPicker, "currencyFragment")
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun getUserCurrencyInfo(code: String) {
        val currency = getCurrencyByISO(code)
        if (currency != null) {
            binding.run {
                selectedCurrencyFlagImageView.setImageResource(currency.flag)
                selectedCurrencySymbolTextView.text = currency.symbol
                selectedCurrencyIsoTextView.text = currency.code
                selectedCurrencyNameTextView.text = currency.name
            }
        }
    }
}
