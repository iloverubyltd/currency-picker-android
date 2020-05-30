package com.mynameismidori.currencypicker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.allCurrencies
import com.mynameismidori.currencypicker.ExtendedCurrency.Companion.getCurrencyByISO
import java.util.*

class CurrencyPicker : DialogFragment() {
    private lateinit var searchEditText: EditText
    private lateinit var currencyListView: ListView
    private lateinit var adapter: CurrencyListAdapter
    private val currenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    private var selectedCurrenciesList: MutableList<ExtendedCurrency> = arrayListOf()
    private var listener: CurrencyPickerListener? = null

    init {
        setCurrenciesList(allCurrencies)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.currency_picker, null)
        val args = arguments
        if (args != null && dialog != null) {
            val dialogTitle = args.getString("dialogTitle")
            dialog!!.setTitle(dialogTitle)
            val width = resources.getDimensionPixelSize(R.dimen.cp_dialog_width)
            val height = resources.getDimensionPixelSize(R.dimen.cp_dialog_height)
            dialog!!.window!!.setLayout(width, height)
        }
        searchEditText =
            view.findViewById<View>(R.id.currency_code_picker_search) as EditText
        currencyListView =
            view.findViewById<View>(R.id.currency_code_picker_listview) as ListView
        selectedCurrenciesList = ArrayList(currenciesList.size)
        selectedCurrenciesList.addAll(currenciesList)
        adapter = CurrencyListAdapter(requireActivity(), selectedCurrenciesList)
        currencyListView.adapter = adapter
        currencyListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (listener != null) {
                val currency = selectedCurrenciesList[position]
                listener!!.onSelectCurrency(
                    currency.name, currency.code, currency.symbol,
                    currency.flag
                )
            }
        }
        searchEditText.doAfterTextChanged { text -> search(text.toString()) }

        return view
    }

    override fun dismiss() {
        if (dialog != null) {
            super.dismiss()
        } else {
            requireFragmentManager().popBackStack()
        }
    }

    fun setListener(listener: CurrencyPickerListener?) {
        this.listener = listener
    }

    @SuppressLint("DefaultLocale")
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
        for (code in savedCurrencies) {
            currenciesList.add(getCurrencyByISO(code)!!)
        }
    }

    companion object {
        /**
         * To support show as dialog
         */
        @JvmStatic
        fun newInstance(dialogTitle: String?): CurrencyPicker {
            return CurrencyPicker().apply {
                arguments = bundleOf("dialogTitle" to dialogTitle)
            }
        }
    }

}