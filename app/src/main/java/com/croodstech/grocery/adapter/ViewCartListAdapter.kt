package com.croodstech.grocery.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.croodstech.grocery.R
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.*
import kotlinx.android.synthetic.main.product_list_row.view.*
import kotlinx.android.synthetic.main.product_list_row.view.img_qty_add
import kotlinx.android.synthetic.main.product_list_row.view.img_qty_less
import kotlinx.android.synthetic.main.product_list_row.view.txt_qty
import kotlinx.android.synthetic.main.viewcart_list_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewCartListAdapter(var cartList: ArrayList<CartVo>, val ctx: Context) :
    RecyclerView.Adapter<ViewCartHolder>() {

    lateinit var mOnDataChangeListener : OnDataChangeListener
    var total : Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCartHolder {
        return ViewCartHolder(LayoutInflater.from(ctx).inflate(R.layout.viewcart_list_row, parent, false))
    }

    override
    fun getItemCount(): Int {
        return cartList.size
    }


    override
    fun onBindViewHolder(holder: ViewCartHolder, position: Int) {

        val model: CartVo = cartList[position]
        val modelVarient: ProductVarientsVo = model.productVarientsVo

        val imageUrl = modelVarient.img
        var productqty = 0

        val progressDialog = Common.progressBar(ctx)
        val storage = DataStorage("loginPref", ctx)
        val token = storage.read("token", DataStorage.STRING)
        val tokenType = storage.read("tokenType", DataStorage.STRING)
        val apiInterface = UtilApi.apiService

        if (imageUrl != "") {
            Glide.with(ctx).load(Uri.parse(imageUrl)).into(holder.img_cart)
            /* Glide.with(ctx).load(Uri.parse(imageUrl)).listener(object : RequestListener<Drawable> {
                 override fun onLoadFailed(
                     e: GlideException?,
                     model: Any?,
                     target: Target<Drawable>?,
                     isFirstResource: Boolean
                 ): Boolean {
                     holder.progress_bar_img.visibility = View.GONE
                     holder.img_product.setImageDrawable(ctx.resources.getDrawable(R.drawable.noimage))
                    return false
                 }

                 override fun onResourceReady(
                     resource: Drawable?,
                     model: Any?,
                     target: Target<Drawable>?,
                     dataSource: DataSource?,
                     isFirstResource: Boolean
                 ): Boolean {
                     holder.progress_bar_img.visibility = View.GONE
                     return true
                 }

             }).into(holder.img_product)*/

        }
        holder.txt_cartproduct_name.text = modelVarient.varientName
        holder.txt_cartproduct_price.text = "Rs. " + modelVarient.price
        holder.txt_cartproduct_qty.text = "Qty : " + model.qty.toDouble().toInt()
        holder.txt_qty.text =""+model.qty.toDouble().toInt()

        holder.img_qty_add.setOnClickListener {

            val s = holder.txt_qty.text.toString().toDouble().toInt()
            productqty = s
            total = 0.0
            progressDialog.show()
            apiInterface.addToCart(
                Common.companyId,
                modelVarient.productVarientId.toString(),
                "Plus",
                "$tokenType $token"
            ).enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {

                    progressDialog.dismiss()
                    if (response.body() != null) {
                        if (response.body()!!.status) {
                            productqty += 1
                            holder.txt_qty.text = "" + productqty
                            cartList[position].qty = productqty.toFloat()

                            for (model in cartList) {
                                val modelVarient: ProductVarientsVo = model.productVarientsVo

                                total += modelVarient.price * model.qty
                            }
                            mOnDataChangeListener.onDataChanged(total.toInt())

                            //Common.showToast(ctx, "Product Added to cart")
                        } else {
                            Common.showToast(ctx, "Please try again latter")
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    progressDialog.dismiss()
                    Common.showToast(ctx, "Please try again latter")
                }
            })
        }
        holder.img_qty_less.setOnClickListener {
            total = 0.0
            if (holder.txt_qty.text.toString() != "")
            {
                val s = holder.txt_qty.text.toString().toDouble().toInt()
                productqty = s
            }

            if (productqty>0)
            {
                progressDialog.show()
                apiInterface.addToCart(
                    Common.companyId,
                    modelVarient.productVarientId.toString(),
                    "Minus",
                    "$tokenType $token"
                ).enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {

                        progressDialog.dismiss()
                        if (response.body() != null) {
                            if (response.body()!!.status) {

                                if (productqty > 0) {
                                    productqty -= 1
                                    holder.txt_qty.text =""+productqty
                                    cartList[position].qty = productqty.toFloat()

                                    for (model in cartList) {
                                        val modelVarient: ProductVarientsVo = model.productVarientsVo

                                        total += modelVarient.price * model.qty
                                    }
                                    mOnDataChangeListener.onDataChanged(total.toInt())

                                }

                                if (holder.txt_qty.text.toString() == "0")
                                {
                                    total = 0.0
                                    cartList.removeAt(position)
                                    notifyDataSetChanged()

                                    for (model in cartList) {
                                        val modelVarient: ProductVarientsVo = model.productVarientsVo

                                        total += modelVarient.price * model.qty
                                    }
                                    mOnDataChangeListener.onDataChanged(total.toInt())
                                }

                            } else {
                                Common.showToast(ctx, "Please try again latter")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        progressDialog.dismiss()
                        Common.showToast(ctx, "Please try again latter")
                    }
                })
            }


        }

        holder.img_cart_delete.setOnClickListener {
            val builder = AlertDialog.Builder(ctx)

            // Set the alert dialog title
            builder.setTitle("Delete cart item confirmation  ")

            // Display a message on alert dialog
            builder.setMessage("Are you want to delete this item?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button

                apiInterface.deleteCartItem(cartList[position].cartId.toString(),Common.companyId,"$tokenType $token").enqueue( object : Callback<CheckMobileNoResponse> {
                    override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                        Common.showToast(ctx,"Please try again latter")
                    }

                    override fun onResponse(
                        call: Call<CheckMobileNoResponse>,
                        response: Response<CheckMobileNoResponse>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.status!!) {
                                Common.showToast(ctx,"Item deleted Successfully")
                                cartList.remove(cartList[position])
                                notifyDataSetChanged()
                            }
                        }
                    }

                })

                dialog.dismiss()
            }


            // Display a negative button on alert dialog
            builder.setNegativeButton("No"){dialog,which ->
                dialog.dismiss()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            // Display the alert dialog on app interface
            dialog.show()
        }
    }


    fun setOnDataChangeListener(onDataChangeListener : OnDataChangeListener)
    {
        mOnDataChangeListener = onDataChangeListener
    }
}

class ViewCartHolder(view: View) : RecyclerView.ViewHolder(view) {

    var img_cart = view.img_cart

    var img_cart_delete = view.img_cart_delete

    val txt_cartproduct_name = view.txt_cartproduct_name
    val txt_cartproduct_qty = view.txt_cartproduct_qty
    val txt_cartproduct_price = view.txt_cartproduct_price

    val txt_product_price = view.txt_product_price
    val cart_progress_bar_img = view.cart_progress_bar_img

    val img_qty_add = view.img_qty_add
    val txt_qty = view.txt_qty
    val img_qty_less = view.img_qty_less
}

interface OnDataChangeListener {
    fun onDataChanged(size: Int)

}

