package com.croodstech.grocery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.croodstech.grocery.R
import com.croodstech.grocery.model.CategoryVo
import java.util.*

class HomeDeliveryAdapter(
    var ctx: Context,
    var deloveryList: ArrayList<CategoryVo>) : BaseAdapter() {

    var arraylist = ArrayList(deloveryList)
    override fun getView(position: Int, cview: View?, p2: ViewGroup?): View {

        val food = this.deloveryList[position]


        val view: View

        var img_category: ImageView? = null
        var txt_category: TextView? = null


        val inflator = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        view = inflator.inflate(R.layout.home_delivery_list_row, null)

        // img_category = view.findViewById<ImageView>(R.id.img_category)
        txt_category = view.findViewById<TextView>(R.id.txt_category)


        /* img_category?.let {
             Glide.with(ctx)
                 .load(Uri.parse("http://192.168.2.93:8080/"+ deloveryList[position].imageSrc))
                 .into(it)
         }*/

        txt_category?.setText(food.categoryName)

        return view
    }

    override fun getItem(p0: Int): Any {
        return deloveryList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return deloveryList.size
    }


    // Filter Class
    fun filter(charText: String) {
        Log.d("trace", "------------" + arraylist.size)
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        deloveryList.clear()
        if (charText.isEmpty()) {
            deloveryList.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.categoryName!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    deloveryList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }


}
