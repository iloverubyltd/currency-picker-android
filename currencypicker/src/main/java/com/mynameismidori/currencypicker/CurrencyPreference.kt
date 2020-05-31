package com.mynameismidori.currencypicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.preference.ListPreference
import androidx.preference.PreferenceManager
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.allCurrencies
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.getCurrencyByISO
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("CommitPrefEdits")
open class CurrencyPreference(
    context: Context,
    attrs: AttributeSet?
) : ListPreference(context, attrs), OnSharedPreferenceChangeListener {

    private lateinit var searchEditText: EditText
    private lateinit var currencyListView: ListView

    private var currencyName: Array<CharSequence>? = null
    private var currencyCode: Array<CharSequence>? = null
    private lateinit var adapter: CurrencyListAdapter
    private val currentIndex = 0
    private val currenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    private var selectedCurrenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    private val resources: Resources? = null
    private val selectedCurrencyCode: String? = null
    private var defaultCurrencyCode: String? = null

    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (key == "selectedCurrency") {
            summary = value
        }
        if (key == "selectedCurrencies") {
            setCurrenciesList(
                preferences.getStringSet(
                    "selectedCurrencies",
                    setOf<String>()
                )!!
            )
        }
    }

    protected fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        val inflater:LayoutInflater? = context.getSystemService() ?: return
        val view = inflater!!.inflate(R.layout.currency_picker, null).also {
            searchEditText = it.findViewById<EditText>(R.id.currency_code_picker_search)
            currencyListView = it.findViewById<ListView>(R.id.currency_code_picker_listview)
        }
        searchEditText.doAfterTextChanged { text -> search(text.toString()) }

        selectedCurrenciesList = arrayListOf<ExtendedCurrency>().apply { addAll(currenciesList) }

        adapter = CurrencyListAdapter(context, selectedCurrenciesList)
        currencyListView.adapter = adapter
        currencyListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val currency = selectedCurrenciesList[position]
            value = currency.code
            summary = currency.code
            preferences.edit(commit = true) {
                putString(key, currency.code)
            }
            // FIXME getDialog().dismiss();
        }
        builder.setView(view)
        builder.setNegativeButton("Cancel", null)
        builder.setPositiveButton(null, null)
        currencyCode = entries
        currencyName = entryValues
        check(!(currencyName == null || currencyCode == null || currencyCode!!.size != currencyName!!.size)) { "Preference requires an entries array and an entryValues array which are both the same length" }
    }

    private fun search(text: String) {
        selectedCurrenciesList.clear()
        for (currency in currenciesList) {
            if (currency.name!!.toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                selectedCurrenciesList.add(currency)
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun setCurrenciesList(newCurrencies: List<ExtendedCurrency>) {
        currenciesList.clear()
        currenciesList.addAll(newCurrencies)
    }

    fun setCurrenciesList(savedCurrencies: Set<String>) {
        currenciesList.clear()
        currenciesList.addAll(
            savedCurrencies.mapNotNull { code -> getCurrencyByISO(code) }
        )
    }

    init {
        //setDialogLayoutResource(R.layout.currency_picker);
        setCurrenciesList(allCurrencies)
        summary = preferences.getString(key, value)
        val a =
            context.theme.obtainStyledAttributes(attrs, R.styleable.attrs_currency, 0, 0)
        defaultCurrencyCode = try {
            a.getString(R.styleable.attrs_currency_currencyCode)
        } finally {
            a.recycle()
        }
    }
}