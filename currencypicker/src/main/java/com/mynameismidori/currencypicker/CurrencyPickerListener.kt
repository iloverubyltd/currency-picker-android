package com.mynameismidori.currencypicker

/**
 * Created by midorikocak on 30/09/2017.
 */
interface CurrencyPickerListener {
    fun onSelectCurrency(
        name: String?,
        code: String?,
        symbol: String?,
        flagDrawableResID: Int
    )
}