package com.croodstech.grocery.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.adapter.HomeDeliveryAdapter
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CategoryVo
import com.croodstech.grocery.model.HomeDeliveryResponse
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_sub_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubCategoryActivity : AppCompatActivity() {

    var  apiINterface : ApiInterface? = null
    var ctx : Context = this
    var subCategoryList : ArrayList<CategoryVo> = ArrayList()

    lateinit var lst_sub_category : GridView
    lateinit var txt_tool_category : TextView

    var storage : DataStorage?=null

    var token : String=""
    var tokenType : String=""
    var categoryId : Int=0
    var progressBar : KProgressHUD?=null

    var bundle : Bundle?=null
    var subcatName =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)

        setSupportActionBar(tool_subcategory)
        bundle = intent.extras

        lst_sub_category = findViewById(R.id.lst_sub_category)
        txt_tool_category = findViewById(R.id.txt_tool_category)

        apiINterface = UtilApi.apiService
        progressBar = Common.progressBar(ctx)
        storage = DataStorage("loginPref", ctx)


        token = storage?.read("token",DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType",DataStorage.STRING).toString()


        if (bundle!=null)
        {
            subcatName = bundle!!.getString("categoryName").toString()
            categoryId = bundle!!.getInt("categoryId")
            txt_tool_category.text = subcatName
        }

        getHomeDeliveryList()

        lst_sub_category.setOnItemClickListener { p0, p1, position, p3 ->
            val intent = Intent(ctx, ProductListActivity::class.java)
            intent.putExtra("categoryName",subCategoryList[position].categoryName)
            intent.putExtra("categoryId",subCategoryList[position].categoryId)
            startActivity(intent)
        }
    }

    fun getHomeDeliveryList()
    {
        progressBar?.show()
        apiINterface?.getSubCategoryList(Common.companyId,categoryId ,"$tokenType $token")?.enqueue(object :
            Callback<HomeDeliveryResponse> {
            override fun onFailure(call: Call<HomeDeliveryResponse>, t: Throwable) {
                progressBar?.dismiss()
                Toast.makeText(ctx,"Something Went wrong Please try again latter", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<HomeDeliveryResponse>, response: Response<HomeDeliveryResponse>)
            {
                progressBar?.dismiss()
                if(response.body() != null)
                {
                    if (response.body()!!.status!!)
                    {
                        val homeDeliveryResponse : HomeDeliveryResponse = response.body()!!
                        subCategoryList = homeDeliveryResponse.response!!

                        lst_sub_category.adapter = HomeDeliveryAdapter(ctx,subCategoryList)
                    }
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.home_search -> {
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
