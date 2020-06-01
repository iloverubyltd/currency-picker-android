package uk.co.iloveruby.currencypicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.preference.PreferenceDialogFragmentCompat
import com.mynameismidori.currencypicker.CurrencyListAdapter
import com.mynameismidori.currencypicker.CurrencyPickerPreference
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.mynameismidori.currencypicker.databinding.CurrencyPickerBinding
import java.util.*


class CurrencyPickerPreferenceDialog : PreferenceDialogFragmentCompat() {

    private lateinit var adapter: CurrencyListAdapter
    private lateinit var binding: CurrencyPickerBinding
    private var clickedDialogEntryIndex = 0
    private lateinit var _entries: Array<CharSequence>
    private lateinit var _entryValues: Array<CharSequence>

    private val currenciesList: List<ExtendedCurrency> = ExtendedCurrency.allCurrencies
    private var selectedCurrenciesList: MutableList<ExtendedCurrency> = mutableListOf()

    private val currencyPickerPreference: CurrencyPickerPreference
        get() = preference as CurrencyPickerPreference

    init {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            savedInstanceState.let { state ->
                clickedDialogEntryIndex = state.getInt(SAVE_STATE_INDEX, 0)
                _entries = state.getCharSequenceArray(SAVE_STATE_ENTRIES)!!
                _entryValues = state.getCharSequenceArray(SAVE_STATE_ENTRY_VALUES)!!
            }
        } else {
            currencyPickerPreference.let {
                check(it.entries != null && it.entryValues != null) {
                    "CurrencyPickerPreference requires an entries array and an entryValues array."
                }
                clickedDialogEntryIndex = it.findIndexOfValue(it.value)
                _entries = it.entries!!
                _entryValues = it.entryValues!!
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.let {
            it.putInt(SAVE_STATE_INDEX, clickedDialogEntryIndex)
            it.putCharSequenceArray(SAVE_STATE_ENTRIES, _entries)
            it.putCharSequenceArray(SAVE_STATE_ENTRY_VALUES, _entryValues)
        }
    }

    override fun onCreateDialogView(context: Context?): View {
        binding = CurrencyPickerBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        selectedCurrenciesList.addAll(currenciesList)

        binding.currencyCodePickerSearch.doAfterTextChanged { text -> search(text.toString()) }

        binding.currencyCodePickerListview.adapter = CurrencyListAdapter(requireContext(), selectedCurrenciesList)
        binding.currencyCodePickerListview.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val currency = selectedCurrenciesList[position]
                // FIXME preference.value = currency.code
                preference.summary = currency.code
                preference.sharedPreferences.edit(commit = true) {
                    putString(preference.key, currency.code)
                }
            }
    }

    // override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    //     val pref = currencyPickerPreference
    //     val params =
    //         ColorPickerDialog.Params.Builder(context)
    //             .setSelectedColor(pref.color)
    //             .setColors(pref.colors)
    //             .setColorContentDescriptions(pref.colorDescriptions)
    //             .setSize(pref.size)
    //             .setSortColors(pref.isSortColors)
    //             .setColumns(pref.columns)
    //             .build()
    //     val dialog = ColorPickerDialog(requireActivity(), this, params)
    //     dialog.setTitle(pref.dialogTitle)
    //     return dialog
    // }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        super.onPrepareDialogBuilder(builder)
        builder.setSingleChoiceItems(
            _entries, clickedDialogEntryIndex
        ) { dialog, which ->
            clickedDialogEntryIndex = which

            // Clicking on an item simulates the positive button click, and dismisses
            // the dialog.
            this@CurrencyPickerPreferenceDialog.onClick(
                dialog,
                DialogInterface.BUTTON_POSITIVE
            )
            dialog.dismiss()
        }

        // The typical interaction for list-based dialogs is to have click-on-an-item dismiss the
        // dialog instead of the user having to press 'Ok'.
        builder.setPositiveButton(null, null)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && clickedDialogEntryIndex >= 0) {
            val value = _entryValues[clickedDialogEntryIndex].toString()
            val preference: CurrencyPickerPreference = currencyPickerPreference
            if (preference.callChangeListener(value)) {
                preference.value = value
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {
        selectedCurrenciesList.clear()
        selectedCurrenciesList.addAll(currenciesList.filter { currency ->
            currency.name.toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())
        })
        adapter.notifyDataSetChanged()
    }

    companion object {
        const val TAG = "CurrencyPickerPreferenceDialogFragment"

        private const val SAVE_STATE_INDEX = "CurrencyPickerPreferenceDialogFragment.index"
        private const val SAVE_STATE_ENTRIES = "CurrencyPickerPreferenceDialogFragment.entries"
        private const val SAVE_STATE_ENTRY_VALUES =
            "CurrencyPickerPreferenceDialogFragment.entryValues"

        fun newInstance(key: String): CurrencyPickerPreferenceDialog {
            return CurrencyPickerPreferenceDialog().apply {
                arguments = bundleOf(ARG_KEY to key)
            }
        }
    }
}
