package com.croodstech.grocery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.model.OrderVo
import kotlinx.android.synthetic.main.order_list_row.view.*

class OrderListAdapter(val orderList: List<OrderVo>, val ctx: Context) :
    RecyclerView.Adapter<ViewOrderHolder>(

    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewOrderHolder {
        return ViewOrderHolder(LayoutInflater.from(ctx).inflate(R.layout.order_list_row, parent, false))
    }

    override fun getItemCount(): Int {

        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewOrderHolder, position: Int) {

        holder.txt_order_date.text = orderList[position].salesDate
        holder.txt_order_name.text = orderList[position].salesNo
        holder.txt_order_total.text = "RS. " +orderList[position].total
        holder.txt_order_total_item.text = ""+orderList[position].totalQty+" Item "
        holder.txt_order_status.text = orderList[position].status
    }


}

class ViewOrderHolder(view: View) : RecyclerView.ViewHolder(view) {

    var txt_order_date = view.txt_order_date
    val txt_order_name = view.txt_order_name
    val txt_order_total = view.txt_order_total
    val txt_order_total_item = view.txt_order_total_item
    val txt_order_status = view.txt_order_status
}
