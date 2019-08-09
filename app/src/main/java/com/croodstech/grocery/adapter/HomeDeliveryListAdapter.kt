package com.croodstech.grocery.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.croodstech.grocery.R
import com.croodstech.grocery.model.CategoryVo
import com.croodstech.grocery.model.HomeDeliveryListResponse
import com.croodstech.grocery.model.ProductVo
import kotlinx.android.synthetic.main.home_delivery_list_row.view.*
import java.util.*

class HomeDeliveryListAdapter(val homeDeliveryList: ArrayList<CategoryVo>, val ctx: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    var arraylist = ArrayList(homeDeliveryList)
    lateinit var listener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.home_delivery_list_row, parent, false))
    }

    override fun getItemCount(): Int {

        return homeDeliveryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /*Glide.with(ctx)
            .load(Uri.parse(homeDeliveryList[position].avatar))
            .into(holder.img_category)*/
        holder.txt_category.text = homeDeliveryList[position].categoryName
    }

    // Filter Class
    fun filter(charText: String) {
        Log.d("trace", "------------" + arraylist.size)
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        homeDeliveryList.clear()
        if (charText.isEmpty()) {
            homeDeliveryList.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.categoryName!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    homeDeliveryList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: CategoryVo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var img_category = view.img_category
    val txt_category = view.txt_category
}
