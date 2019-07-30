package com.croodstech.grocery.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.croodstech.grocery.R
import com.croodstech.grocery.model.HomeDeliveryListResponse
import com.croodstech.grocery.model.ProductVo
import kotlinx.android.synthetic.main.home_delivery_list_row.view.*
import java.util.ArrayList

class HomeDeliveryListAdapter(val homeDeliveryList: List<HomeDeliveryListResponse>, val ctx: Context) :
    RecyclerView.Adapter<ViewHolder>(

    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.home_delivery_list_row, parent, false))
    }

    override fun getItemCount(): Int {

        return homeDeliveryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(ctx)
            .load(Uri.parse(homeDeliveryList.get(position).avatar))
            .into(holder.img_category)
        holder.txt_category.setText(homeDeliveryList.get(position).first_name)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var img_category = view.img_category
    val txt_category = view.txt_category
}
