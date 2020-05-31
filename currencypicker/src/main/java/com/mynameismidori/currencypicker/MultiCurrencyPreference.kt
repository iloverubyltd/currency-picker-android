package com.mynameismidori.currencypicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceManager
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.allCurrencies
import java.util.*

open class MultiCurrencyPreference(
    context: Context?,
    attrs: AttributeSet?
) : MultiSelectListPreference(context, attrs) {

    private lateinit var searchEditText: EditText
    private lateinit var currencyListView: ListView

    private var currencyName: Array<CharSequence>? = null
    private var currencyCode: Array<CharSequence>? = null
    private lateinit var adapter: MultiCurrencyListAdapter
    private val currentIndex = 0
    private val currenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    private var selectedCurrenciesList: MutableList<ExtendedCurrency> = arrayListOf()

    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        setCurrenciesList(allCurrencies)
    }

    protected fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        Log.v("VALUES", values.toString())

        val inflater: LayoutInflater? = context.getSystemService() ?: return
        val view = inflater!!.inflate(R.layout.currency_picker, null).also {
            searchEditText = it.findViewById<EditText>(R.id.currency_code_picker_search)
            currencyListView = it.findViewById<ListView>(R.id.currency_code_picker_listview)
        }
        searchEditText.doAfterTextChanged { text -> search(text.toString()) }

        selectedCurrenciesList = arrayListOf<ExtendedCurrency>().apply { addAll(currenciesList) }

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

    private fun search(text: String) {
        selectedCurrenciesList.clear()
        for (currency in currenciesList) {
            if (currency.name!!.toLowerCase(Locale.ENGLISH)
                    .contains(text.toLowerCase(Locale.ENGLISH))
            ) {
                selectedCurrenciesList.add(currency)
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun setCurrenciesList(newCurrencies: List<ExtendedCurrency>) {
        currenciesList.clear()
        currenciesList.addAll(newCurrencies)
    }
}