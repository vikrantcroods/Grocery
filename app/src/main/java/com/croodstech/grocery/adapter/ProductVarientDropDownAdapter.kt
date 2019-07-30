package com.croodstech.grocery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.croodstech.grocery.R
import com.croodstech.grocery.model.ProductVarientsVo

class ProductVarientDropDownAdapter(val context: Context, var listItemsTxt: List<ProductVarientsVo>) : BaseAdapter() {


    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.product_varient_spinner_layout, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        // setting adapter item height programatically.

        val params = view.layoutParams
        params.height = 60
        view.layoutParams = params

        vh.label.text = listItemsTxt[position].varientName
        vh.txt_product_varient_price.text ="-  Rs. "+ listItemsTxt[position].price
        return view
    }

    override fun getItem(position: Int): Any? {

        return null

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView = row?.findViewById(R.id.txt_product_varient_name) as TextView
        val txt_product_varient_price: TextView = row?.findViewById(R.id.txt_product_varient_price) as TextView

    }
}