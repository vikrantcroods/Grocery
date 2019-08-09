package com.croodstech.grocery.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.croodstech.grocery.R
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CheckMobileNoResponse
import com.croodstech.grocery.model.CommonResponse
import com.croodstech.grocery.model.ProductVarientsVo
import com.croodstech.grocery.model.ProductVo
import kotlinx.android.synthetic.main.product_list_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProductListAdapter(val productList: ArrayList<ProductVo>, val ctx: Context) :
    RecyclerView.Adapter<ViiewHolder>() {

    var arraylist = ArrayList(productList)
    var total : Double = 0.0

   // lateinit var mOnDataChangeListener : OnCartChangeListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViiewHolder {
        return ViiewHolder(LayoutInflater.from(ctx).inflate(R.layout.product_list_row, parent, false))
    }

    override
    fun getItemCount(): Int {
        return productList.size
    }


    override
    fun onBindViewHolder(holder: ViiewHolder, position: Int) {

        val model: ProductVo = productList[position]
        val modelVarientList: List<ProductVarientsVo> = productList[position].productVarientsVos
        val modelVarient: ProductVarientsVo = modelVarientList[0]

        val imageUrl = model.imageSrc
        val description = model.description
        val haveVariation = model.haveVariation

        var productVarientPosition = 0

        var productqty = 0

        val progressDialog = Common.progressBar(ctx)
        val storage = DataStorage("loginPref", ctx)
        val token = storage.read("token", DataStorage.STRING)
        val tokenType = storage.read("tokenType", DataStorage.STRING)
        val apiInterface = UtilApi.apiService


        if (imageUrl != "") {
            Glide.with(ctx).load(Uri.parse(imageUrl)).into(holder.img_product)
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
        holder.txt_product_name.text = model.name
        if (description != "")
            holder.txt_product_desc.text = model.description
        else
            holder.txt_product_desc.visibility = View.GONE
        if (haveVariation == 0)
        {
            holder.spn_novarient_product.visibility = View.VISIBLE
            holder.spn_product_varient.visibility = View.GONE
            holder.spn_novarient_product.text = ""+ modelVarientList[0].price+" RS"
        }
        else
        {
            holder.spn_novarient_product.visibility = View.GONE
            holder.spn_product_varient.visibility = View.VISIBLE
            holder.spn_product_varient.text = ""+ modelVarientList[0].varientName+" - "+ modelVarientList[0].price+" RS"
        }

        holder.btn_add_cart.setOnClickListener {
            holder.btn_add_cart.visibility = View.GONE
            holder.btn_add_cart_qty.visibility = View.VISIBLE
            addToCart(modelVarientList[productVarientPosition].productVarientId.toString(),"1")
            //holder.btn_add_cart_qty.visibility = View.VISIBLE
            //holder.btn_add_cart.visibility = View.GONE
        }

        holder.spn_product_varient.setOnClickListener {
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_product_varient)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)


            val lst_product_varient = dialog.findViewById(R.id.lst_product_varient) as ListView
            val productname = dialog.findViewById(R.id.productname) as TextView

            productname.text = productList[position].name

            val spinnerAdapter = ProductVarientDropDownAdapter(ctx, modelVarientList)
            lst_product_varient.adapter = spinnerAdapter

            lst_product_varient.setOnItemClickListener { _, _, position, _ ->
                productVarientPosition = position
                holder.spn_product_varient.text= ""+ modelVarientList[position].varientName+" - "+ modelVarientList[position].price+" RS"
                //holder.txt_product_price.text= ""+ modelVarientList[position].price
                modelVarientList[productVarientPosition].productVarientId = modelVarientList[position].productVarientId

                dialog .dismiss()
            }
            dialog .show()
        }

        holder.img_qty_add.setOnClickListener {

            holder.btn_add_cart.visibility = View.GONE
            holder.btn_add_cart_qty.visibility = View.VISIBLE

            val s = holder.txt_qty.text.toString().toDouble().toInt()
            productqty = s
            total = 0.0
            progressDialog.show()
            apiInterface.addToCart(
                Common.companyId,
                modelVarientList[productVarientPosition].productVarientId.toString(),
                "Plus",
                "$tokenType $token"
            ).enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {

                    progressDialog.dismiss()
                    if (response.body() != null) {
                        if (response.body()!!.status) {
                            productqty += 1
                            holder.txt_qty.text = "" + productqty
                            productList[position].qty = productqty.toFloat()

                            /*for (model in productList[position].productVarientsVos) {

                                total += model.price * model.qty
                            }*/

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
                    modelVarientList[productVarientPosition].productVarientId.toString(),
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
                                    productList[position].qty = productqty.toFloat()

                                    /*for (model in cartList) {
                                        val modelVarient: ProductVarientsVo = model.productVarientsVo

                                        total += modelVarient.price * model.qty
                                    }*/

                                }

                                if (holder.txt_qty.text.toString() == "0")
                                {
                                    total = 0.0
                                    holder.btn_add_cart.visibility = View.VISIBLE
                                    holder.btn_add_cart_qty.visibility = View.GONE
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

    }

    fun addToCart(productVarientId : String , qty : String)
    {
        val progressDialog = Common.progressBar(ctx)
        val storage = DataStorage("loginPref",ctx)
        val token = storage.read("token",DataStorage.STRING)
        val tokenType = storage.read("tokenType",DataStorage.STRING)
        val apiInterface = UtilApi.apiService

        progressDialog.show()
        apiInterface.addToCart(Common.companyId,productVarientId,"Plus","$tokenType $token").enqueue(object : Callback<CommonResponse>
        {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {

                progressDialog.dismiss()
                if (response.body() != null)
                {
                    if (response.body()!!.status)
                    {
                        //mOnDataChangeListener.onCartChanged(0)

                        Common.showToast(ctx,"Product Added to cart")
                    }
                    else
                    {
                       // mOnDataChangeListener.onCartChanged(1)
                        Common.showToast(ctx,"Please try again latter")
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                progressDialog.dismiss()
                Common.showToast(ctx,"Please try again latter")
            }
        })

    }
    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        productList.clear()
        if (charText.isEmpty()) {
            productList.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    productList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

   /* fun setOnDataChangeListener(OnCartChangeListener : OnCartChangeListener)
    {
        mOnDataChangeListener = OnCartChangeListener
    }*/


}

class ViiewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var img_product = view.img_product
    val txt_product_name = view.txt_product_name
    val txt_product_desc = view.txt_product_desc
    val spn_product_varient = view.spn_product_varient
    val txt_product_price = view.txt_product_price
    val btn_add_cart = view.btn_add_cart
    val progress_bar_img = view.progress_bar_img
    val spn_novarient_product = view.spn_novarient_product

    val btn_add_cart_qty = view.btn_add_cart_qty

    val img_qty_add = view.img_qty_add
    val txt_qty = view.txt_qty
    val img_qty_less = view.img_qty_less
}

interface OnCartChangeListener {
    fun onCartChanged(size: Int)

}


