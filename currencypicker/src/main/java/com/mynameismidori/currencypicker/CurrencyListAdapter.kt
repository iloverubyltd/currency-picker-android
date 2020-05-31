package com.mynameismidori.currencypicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CurrencyListAdapter(
    private val mContext: Context,
    var currencies: MutableList<ExtendedCurrency>
) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getCount(): Int = currencies.size

    override fun getItem(arg0: Int): Any? = null

    override fun getItemId(arg0: Int): Long = 0

    override fun getView(
        position: Int,
        view: View?,
        parent: ViewGroup
    ): View {
        val itemView = view ?: inflater.inflate(R.layout.row, null)
        val currency = currencies[position]
        val cell = Cell.from(itemView)
        cell!!.textView!!.text = currency.name
        currency.loadFlagByCode(mContext)
        if (currency.flag != -1) cell.imageView!!.setImageResource(currency.flag)
        return itemView
    }

    internal class Cell {
        var textView: TextView? = null
        var imageView: ImageView? = null

        companion object {
            fun from(view: View?): Cell? {
                if (view == null) return null
                return if (view.tag == null) {
                    val cell = Cell()
                    cell.textView = view.findViewById<TextView>(R.id.row_title)
                    cell.imageView = view.findViewById<ImageView>(R.id.row_icon)
                    view.tag = cell
                    cell
                } else {
                    view.tag as Cell
                }
            }
        }
    }

}