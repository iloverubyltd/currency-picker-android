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
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
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

    private val viewModel: CurrencyViewModel by viewModels()

    private lateinit var currencyPicker: CurrencyPicker
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        currencyPicker = CurrencyPicker.newInstance("Select Currency")

        binding.buttonCurrencyPicker.setOnClickListener(this)
        binding.buttonOpenFragment.setOnClickListener(this)
        binding.buttonOpenPreferences.setOnClickListener(this)

        preferences.registerOnSharedPreferenceChangeListener(this)

        val selectedCurrency =
            preferences.getString("selectedCurrency", getString(R.string.default_currency))
        binding.selectedCurrencyPreferenceValue.text = selectedCurrency
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
            binding.selectedCurrencyPreferenceValue.text = sharedPreferences.getString(key, "")
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
        binding.selectedCurrencyPreferenceValue.text =
            preferences.getString("selectedCurrency", "CZK")
    }

    override fun onPause() {
        super.onPause()
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSelectCurrency(
        name: String,
        code: String,
        symbol: String,
        flagDrawableResID: Int
    ) {
        getUserCurrencyInfo(code)
        viewModel.
        currencyPicker.dismiss()
    }

    override fun onClick(v: View) {
        //do what you want to do when button is clicked
        when (v.id) {
            R.id.button_currency_picker -> {
                currencyPicker.show(
                    parentFragmentManager,
                    "CURRENCY_PICKER"
                )
            }
            R.id.button_open_preferences -> {
                startActivity(
                    Intent(
                        activity,
                        CurrencySettingsActivity::class.java
                    )
                )
            }
            R.id.button_open_fragment -> {
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, currencyPicker, "currency_picker_fragment")
                    addToBackStack(null)
                }
            }
        }
    }


    companion object {
        @JvmStatic
        @JvmOverloads
        fun newInstance(args: Bundle? = null) = CurrencyFragment().apply { arguments = args }
    }
}
