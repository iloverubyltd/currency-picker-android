package uk.co.iloveruby.currencypickerexample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mynameismidori.currencypicker.ExtendedCurrency

class CurrencyViewModel() : ViewModel() {

    private val _selectedCurrency: LiveData<ExtendedCurrency?> = MutableLiveData()
    val selectedCurrency: LiveData<ExtendedCurrency?>
        get() = _selectedCurrency

    val displaySelectedCurrency: LiveData<Boolean>
        get() = Transformations.map(selectedCurrency) { currency ->
            currency?.code?.isNotEmpty() ?: false
        }

    fun updateCurrency(code: String) {
        ExtendedCurrency.getCurrencyByISO(code)?.let {
            _selectedCurrency.apply { value = it }
            // TODO: enable with binding
            // selectedCurrencyFlagImageView.setImageResource(it.flag)
        }
    }

}
