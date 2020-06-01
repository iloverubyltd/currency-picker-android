package com.mynameismidori.currencypicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.preference.PreferenceDialogFragmentCompat
import uk.co.iloveruby.currencypicker.CurrencyPickerPreferenceDialog
import uk.co.iloveruby.currencypicker.DialogPreferenceCompat

@SuppressLint("RestrictedApi")
class CurrencyPickerPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreferenceCompat(context, attrs, defStyleAttr, defStyleRes) {

    private var _entries: Array<CharSequence>? = null
    private var _entryValues: Array<CharSequence>? = null

    val entries: Array<CharSequence>?
        get() = _entries

    val entryValues: Array<CharSequence>?
        get() = _entryValues

    private var _summary: String? = null
    private var valueSet = false

    var value: String = ""
        set(value) {
            val changed = !TextUtils.equals(field, value)
            if (changed || !valueSet) {
                field = value
                valueSet = true
                persistString(value)
                notifyChanged()
            }
        }

    init {

        dialogLayoutResource = R.layout.currency_picker
        // _entries =
        // _entryValues =

        context.withStyledAttributes(
            attrs,
            R.styleable.CurrencyPickerPreference
        ) {
            if (getBoolean(R.styleable.CurrencyPickerPreference_useSimpleSummaryProvider, false)) {
                summaryProvider = SimpleSummaryProvider.instance
            }
        }

        // Retrieve the Preference summary attribute since it's private in the Preference class.
        context.withStyledAttributes(
            attrs,
            androidx.preference.R.styleable.Preference, defStyleAttr, defStyleRes
        ) {
            _summary = getString(androidx.preference.R.styleable.Preference_summary)
                ?: getString(androidx.preference.R.styleable.Preference_android_summary)
        }
    }

    override fun createDialog(): PreferenceDialogFragmentCompat =
        CurrencyPickerPreferenceDialog()

    override fun setSummary(summary: CharSequence?) {
        super.setSummary(summary)
        if (summary == null && _summary != null) {
            _summary = null
        } else if (summary != null && summary != _summary) {
            _summary = summary.toString()
        }
    }

    override fun getSummary(): CharSequence? {
        if (summaryProvider != null) {
            return summaryProvider!!.provideSummary(this)
        }

        return super.getSummary()
    }

    @Suppress("unused")
    val entry: CharSequence?
        get() = if (valueIndex >= 0) _entries?.get(valueIndex) else null

    /**
     * Returns the index of the given value (in the entry values array).
     *
     * @param value The value whose index should be returned
     * @return The index of the value, or -1 if not found
     */
    fun findIndexOfValue(value: String?): Int {
        return value?.let {
            _entryValues?.indexOfLast { it == value }
        } ?: -1
    }

    /**
     * Sets the value to the given index from the entry values.
     *
     * @param index The index of the value to set
     */
    @Suppress("unused")
    var valueIndex: Int
        get() = findIndexOfValue(value)
        set(index) {
            _entryValues?.run { value = get(index).toString() }
        }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? = a.getString(index)

    override fun onSetInitialValue(defaultValue: Any?) {
        value = getPersistedString(defaultValue as String?)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (isPersistent) return superState

        return SavedState(superState).also { it.value = value }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state.javaClass != SavedState::class.java) {
            super.onRestoreInstanceState(state)
            return
        }

        val savedState: SavedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        value = savedState.value.toString() // FIXME: nullable?
    }

    private class SavedState : BaseSavedState {
        var value: String? = null

        internal constructor(source: Parcel) : super(source) {
            value = source.readString()
        }

        internal constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(value)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState? = SavedState(parcel)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    class SimpleSummaryProvider private constructor() : SummaryProvider<CurrencyPickerPreference> {
        override fun provideSummary(preference: CurrencyPickerPreference): CharSequence {
            return if (TextUtils.isEmpty(preference.value)) {
                preference.context.getString(R.string.not_set)
            } else {
                preference.value
            }
        }

        companion object {
            private var simpleSummaryProvider: SimpleSummaryProvider? = null

            /**
             * Retrieve a singleton instance of this simple
             * [androidx.preference.Preference.SummaryProvider] implementation.
             *
             * @return a singleton instance of this simple
             * [androidx.preference.Preference.SummaryProvider] implementation
             */
            val instance: SimpleSummaryProvider?
                get() {
                    if (simpleSummaryProvider == null) {
                        simpleSummaryProvider = SimpleSummaryProvider()
                    }
                    return simpleSummaryProvider
                }
        }
    }

    companion object {
        const val TAG = "CurrencyPickerPreference"
    }
}
