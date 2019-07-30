package com.croodstech.grocery.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.adapter.ViewCartListAdapter
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CartListModel
import com.croodstech.grocery.model.CartVo
import com.croodstech.grocery.model.ProductVarientsVo
import com.kaopiz.kprogresshud.KProgressHUD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button as Button1

class ViewCartListActivity : AppCompatActivity() {
    lateinit var tool_cart: Toolbar

    lateinit var txt_tool_cart: TextView
    lateinit var txt_cart_total: TextView
    lateinit var img_cart_empty: ImageView

    lateinit var lst_cart: RecyclerView
    lateinit var layout_checkout: RelativeLayout
    lateinit var layout_empty: LinearLayout

    lateinit var btn_checkout: Button1

    var apiINterface: ApiInterface? = null
    var ctx: Context = this
    var cartList: ArrayList<CartVo> = ArrayList()

    var storage: DataStorage? = null


    var token: String = ""
    var tokenType: String = ""
    var progressBar: KProgressHUD? = null

    var bundle: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_cart_list)
        allocateMemory()

        setSupportActionBar(tool_cart)

        getViewCartList()

        btn_checkout.setOnClickListener {
            val intent = Intent(ctx, ViewAddressActivity::class.java)
            startActivity(intent)
        }

    }


    fun getViewCartList() {
        progressBar?.show()
        apiINterface?.viewCartList(Common.companyId, "$tokenType $token")?.enqueue(object :
            Callback<CartListModel> {
            override fun onFailure(call: Call<CartListModel>, t: Throwable) {
                progressBar?.dismiss()
                Toast.makeText(ctx, "Something Went wrong Please try again latter", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<CartListModel>, response: Response<CartListModel>) {
                progressBar?.dismiss()
                if (response.body() != null) {
                    val cartListResponse = response.body()!!
                    cartList = cartListResponse.response!!

                    if (cartList.size != 0) {
                        layout_empty.visibility = View.GONE
                        layout_checkout.visibility = View.VISIBLE
                        lst_cart.adapter = ViewCartListAdapter(cartList, ctx)

                        var total = 0.0
                        for (model in cartList) {
                            val modelVarient: ProductVarientsVo = model.productVarientsVo

                            total += modelVarient.price * model.qty
                        }
                        txt_cart_total.text = "RS. " + total

                    } else {
                        layout_empty.visibility = View.VISIBLE
                        layout_checkout.visibility = View.GONE
                    }


                }
            }
        })
    }

    private fun allocateMemory() {
        tool_cart = findViewById(R.id.tool_cart)

        txt_tool_cart = findViewById(R.id.txt_tool_cart)
        txt_cart_total = findViewById(R.id.txt_cart_total)
        layout_checkout = findViewById(R.id.layout_checkout)

        layout_empty = findViewById(R.id.layout_empty)

        lst_cart = findViewById(R.id.lst_cart)
        btn_checkout = findViewById(R.id.btn_checkout)
        img_cart_empty = findViewById(R.id.img_cart_empty)

        txt_tool_cart.text = "Review Cart"

        apiINterface = UtilApi.apiService
        progressBar = ctx?.let { Common.progressBar(it) }
        storage = ctx?.let { DataStorage("loginPref", it) }

        bundle = intent.extras;
        token = storage?.read("token", DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType", DataStorage.STRING).toString()

        lst_cart.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

    }
}
