package com.mynameismidori.currencypicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceManager
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.allCurrencies
import java.util.*

open class MultiCurrencyPreference(
    context: Context?,
    attrs: AttributeSet?
) : MultiSelectListPreference(context, attrs) {
    private var searchEditText: EditText? = null
    private var currencyListView: ListView? = null
    private var currencyName: Array<CharSequence>? = null
    private var currencyCode: Array<CharSequence>? = null
    private var adapter: MultiCurrencyListAdapter? = null
    private val currentIndex = 0
    private val currenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    private var selectedCurrenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        setCurrenciesList(allCurrencies)
    }

    protected fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        Log.v("VALUES", values.toString())
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.currency_picker, null)
        searchEditText =
            view.findViewById<View>(R.id.currency_code_picker_search) as EditText
        currencyListView =
            view.findViewById<View>(R.id.currency_code_picker_listview) as ListView
        searchEditText!!.doAfterTextChanged { text -> search(text.toString()) }

        val currencyListView =
            view.findViewById<View>(R.id.currency_code_picker_listview) as ListView
        selectedCurrenciesList = ArrayList(currenciesList.size)
        selectedCurrenciesList.addAll(currenciesList)
        adapter = MultiCurrencyListAdapter(context, selectedCurrenciesList, values)
        currencyListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        currencyListView.adapter = adapter
        builder.setView(view)
        builder.setNegativeButton(null, null)
        builder.setPositiveButton("Ok", null)
        currencyCode = entries
        currencyName = entryValues
        check(!(currencyName == null || currencyCode == null || currencyCode!!.size != currencyName!!.size)) { "Preference requires an entries array and an entryValues array which are both the same length" }
    }

    protected fun onDialogClosed(positiveResult: Boolean) {
        preferences.edit(commit = true) {
            putStringSet(key, values)
        }
        // super.onDialogClosed(positiveResult);
    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {
        selectedCurrenciesList.clear()
        for (currency in currenciesList) {
            if (currency.name!!.toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                selectedCurrenciesList.add(currency)
            }
        }
        adapter!!.notifyDataSetChanged()
    }

    fun setCurrenciesList(newCurrencies: List<ExtendedCurrency>) {
        currenciesList.clear()
        currenciesList.addAll(newCurrencies)
    }
}