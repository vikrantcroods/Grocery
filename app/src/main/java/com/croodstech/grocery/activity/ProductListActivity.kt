package com.croodstech.grocery.activity

import android.app.PendingIntent.getActivity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.adapter.HomeDeliveryAdapter
import com.croodstech.grocery.adapter.ProductListAdapter
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CategoryVo
import com.croodstech.grocery.model.HomeDeliveryResponse
import com.croodstech.grocery.model.ProductListModel
import com.croodstech.grocery.model.ProductVo
import com.kaopiz.kprogresshud.KProgressHUD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.DividerItemDecoration


class ProductListActivity : AppCompatActivity() {

    lateinit var tool_product: Toolbar
    lateinit var txt_tool_product: TextView
    lateinit var lst_product: RecyclerView

    var apiINterface: ApiInterface? = null
    var ctx: Context = this
    var productList: ArrayList<ProductVo> = ArrayList()

    var storage: DataStorage? = null


    var token: String = ""
    var tokenType: String = ""
    var categoryId: Int = 0
    var progressBar: KProgressHUD? = null

    var bundle: Bundle? = null
    var subcatName = ""
    lateinit var searchView :SearchView
    lateinit var queryTextListener: SearchView.OnQueryTextListener
    lateinit var adapter : ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        allocateMemory()
        setSupportActionBar(tool_product)

        if (bundle != null) {
            subcatName = bundle!!.getString("categoryName").toString()
            categoryId = bundle!!.getInt("categoryId")
            txt_tool_product.text = subcatName
        }

        getProductList()

    }

    private fun allocateMemory() {
        tool_product = findViewById<Toolbar>(R.id.tool_product)
        txt_tool_product = findViewById<TextView>(R.id.txt_tool_product)
        lst_product = findViewById<RecyclerView>(R.id.lst_product)

        apiINterface = UtilApi.apiService
        progressBar = ctx?.let { Common.progressBar(it) }
        storage = ctx?.let { DataStorage("loginPref", it) }

        bundle = intent.extras;
        token = storage?.read("token", DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType", DataStorage.STRING).toString()

        lst_product.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

    }

    fun getProductList() {
        progressBar?.show()
        apiINterface?.getProductList(Common.companyId, categoryId, "$tokenType $token")?.enqueue(object :
            Callback<ProductListModel> {
            override fun onFailure(call: Call<ProductListModel>, t: Throwable) {
                progressBar?.dismiss()
                Toast.makeText(ctx, "Something Went wrong Please try again latter", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ProductListModel>, response: Response<ProductListModel>) {
                progressBar?.dismiss()
                if (response.body() != null) {
                    val productResponse: ProductListModel = response.body()!!
                    productList = productResponse.response

                    adapter = ProductListAdapter(productList, ctx)
                    lst_product.adapter = adapter

                }
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)

        val searchItem = menu.findItem(R.id.home_search)

        val searchManager = ctx.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView

            val icon = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
            val iconclose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
            val line = searchView.findViewById(androidx.appcompat.R.id.search_plate) as View


            icon.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.search))
            iconclose.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_clear_black_24dp))
            line.setBackgroundColor(Color.parseColor("#00000000"))


            searchView.setLayoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>Search...</font>"))

        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i("onQueryTextChange", newText)

                    adapter.filter(newText)
                    adapter.notifyDataSetChanged()

                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)
                    adapter.filter(query)
                    adapter.notifyDataSetChanged()

                    return true
                }
            }
            searchView.setOnQueryTextListener(queryTextListener)
        }

        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.home_search -> {
                searchView.setOnQueryTextListener(queryTextListener)

                return true
            }
            R.id.home_cart -> {
                val intent = Intent(ctx, ViewCartListActivity::class.java)
                //  intent.putExtra("categoryName",subCategoryList[position].categoryName)
                //intent.putExtra("categoryId",subCategoryList[position].categoryId)
                startActivity(intent)
                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }

}
