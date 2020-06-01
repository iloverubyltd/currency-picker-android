package com.mynameismidori.currencypicker

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
        arguments?.let { args ->
            dialog?.let {
                it.setTitle(args.getString("dialogTitle"))
                it.window!!.setLayout(
                    resources.getDimensionPixelSize(R.dimen.cp_dialog_width),
                    resources.getDimensionPixelSize(R.dimen.cp_dialog_height)
                )
            }
        }

        val view = inflater.inflate(R.layout.currency_picker, null).also {
            searchEditText = it.findViewById<EditText>(R.id.currency_code_picker_search)
            currencyListView = it.findViewById<ListView>(R.id.currency_code_picker_listview)
        }
        searchEditText.doAfterTextChanged { text -> search(text.toString()) }

        selectedCurrenciesList = arrayListOf<ExtendedCurrency>().apply { addAll(currenciesList) }

        adapter = CurrencyListAdapter(requireActivity(), selectedCurrenciesList)
        currencyListView.adapter = adapter
        currencyListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            listener?.let {
                val currency = selectedCurrenciesList[position]
                it.onSelectCurrency(
                    currency.name, currency.code, currency.symbol,
                    currency.flag
                )
            }
        }

        return view
    }

    override fun dismiss() {
        if (dialog != null) {
            super.dismiss()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    fun setListener(listener: CurrencyPickerListener?) {
        this.listener = listener
    }

    private fun search(text: String) {
        selectedCurrenciesList.clear()
        for (currency in currenciesList) {
            if (currency.name.toLowerCase(Locale.ENGLISH)
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
        fun newInstance(dialogTitle: String?): CurrencyPicker = CurrencyPicker().apply {
            arguments = bundleOf("dialogTitle" to dialogTitle)
        }
    }

}
