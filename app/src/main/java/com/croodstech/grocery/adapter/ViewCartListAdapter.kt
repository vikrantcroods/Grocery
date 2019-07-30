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
import com.bumptech.glide.load.model.FileLoader
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
        holder.txt_cartproduct_qty.text = "Qty : " + model.qty

        holder.img_qty_add.setOnClickListener {

            productqty = model.qty.toInt()
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

                            holder.txt_cartproduct_qty.text = "Qty : " +  model.qty

                            Common.showToast(ctx, "Product Added to cart")
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
            notifyDataSetChanged()
            //getViewCartList()
        }
        holder.img_qty_less.setOnClickListener {

            productqty = model.qty.toInt()
            if (productqty>=1)
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
                                productqty -= 1
                                if (productqty == 0 && productqty <=0)
                                {
                                    notifyItemRemoved(position)
                                    notifyDataSetChanged()
                                }
                                else
                                {
                                    holder.txt_cartproduct_qty.text = "Qty : " + productqty
                                }
                                Common.showToast(ctx, "Product removed to cart")
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
                notifyDataSetChanged()
                //getViewCartList()
            }


        }

        holder.img_cart_delete.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(p0: View?) {
                val builder = AlertDialog.Builder(ctx)

                // Set the alert dialog title
                builder.setTitle("Delete cart item confirmation  ")

                // Display a message on alert dialog
                builder.setMessage("Are you want to delete this item?")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("YES"){dialog, which ->
                    // Do something when user press the positive button

                    apiInterface.deleteCartItem(cartList[position].cartId.toString(),Common.companyId,"$tokenType $token").enqueue( object : Callback<CheckMobileNoResponse>
                    {
                        override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                            Common.showToast(ctx,"Please try again latter")
                        }

                        override fun onResponse(
                            call: Call<CheckMobileNoResponse>,
                            response: Response<CheckMobileNoResponse>
                        ) {
                            if (response.body() != null)
                            {
                                if (response.body()!!.status!!)
                                {
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
        })
    }

    fun getViewCartList() {
        val progressBar = Common.progressBar(ctx)
        val storage = DataStorage("loginPref", ctx)
        val token = storage.read("token", DataStorage.STRING)
        val tokenType = storage.read("tokenType", DataStorage.STRING)
        val apiINterface = UtilApi.apiService
        cartList = ArrayList()

        progressBar.show()
        apiINterface.viewCartList(Common.companyId, "$tokenType $token")?.enqueue(object :
            Callback<CartListModel> {
            override fun onFailure(call: Call<CartListModel>, t: Throwable) {
                progressBar.dismiss()
                Toast.makeText(ctx, "Something Went wrong Please try again latter", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<CartListModel>, response: Response<CartListModel>) {
                progressBar.dismiss()
                if (response.body() != null) {

                    val cartListResponse = response.body()!!
                    cartList = cartListResponse.response!!

                    //ViewCartListAdapter(cartList, ctx)

                }
            }

        })
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

