package com.croodstech.grocery.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.*
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_sub_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAddressActivity : AppCompatActivity() {

    lateinit var btn_add_addr: Button
    lateinit var txt_add_address_pincode: EditText
    lateinit var txt_add_address2: EditText
    lateinit var txt_add_address1: EditText

    lateinit var spn_addr_city: AppCompatSpinner
    lateinit var tool_addaddr: Toolbar

    var ctx = this

    var addr1 = ""
    var addr2 = ""
    var pincode = ""
    var citycode = ""


    var apiINterface: ApiInterface? = null
    var cityList: List<CityVo> = ArrayList()
    var citiesList: ArrayList<String> = ArrayList()

    lateinit var lst_home_delivery: GridView
    lateinit var lst_home_offer: RecyclerView

    var storage: DataStorage? = null

    var token: String = ""
    var tokenType: String = ""
    var progressBar: KProgressHUD? = null
    internal var spinnerPosition = 0

    internal var selectedCityName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        allocateMemory()

        setSupportActionBar(tool_subcategory)

        getAllCity()
        btn_add_addr.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                addr1 = txt_add_address1.text.toString()
                addr2 = txt_add_address2.text.toString()
                pincode = txt_add_address_pincode.text.toString()

                if (validateData(addr1, addr2, pincode, citycode)) {
                    addAddress(addr1, addr2, pincode, citycode)
                }
            }
        })

        spn_addr_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                selectedCityName = cityList[i].cityName
                citycode = cityList[i].cityCode

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    }

    fun addAddress(addr1: String, addr2: String, pincode: String, citycode: String) {
        progressBar?.show()
        apiINterface?.addAddress(AddAddressRequest(addr1, addr2, pincode, citycode), "$tokenType $token")
            ?.enqueue(object : Callback<CheckMobileNoResponse> {
                override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                    progressBar?.dismiss()
                }

                override fun onResponse(call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>) {

                    if (response.body() !=null)
                    {
                        if (response.body()!!.status!!)
                            Common.showToast(ctx,"Address Add successfully")
                    }

                }
            })

    }

    fun getAllCity() {
        apiINterface?.getAllCity("$tokenType $token")?.enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                if (response.body() != null) {
                    cityList = response.body()!!.response
                    for (city in cityList) {
                        citiesList.add(city.cityName)
                    }
                    val adapter = ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, citiesList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_addr_city.adapter = adapter

                    spinnerPosition = adapter.getPosition("Ahmedabad")
                    spn_addr_city.setSelection(spinnerPosition)
                    citycode = cityList[spinnerPosition].cityCode

                } else {
                    Common.showToast(ctx, "Please try again latter")
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                Common.showToast(ctx, "Server error,Please try again latter")
            }
        })
    }

    fun validateData(addr1: String, addr2: String, pincode: String, citycode: String): Boolean {
        if (addr1 != "" || addr2 != "" || pincode != "" || citycode != "") {
            return true
        } else {
            Common.showToast(ctx, "All fields are complusary")
            return false
        }
    }

    private fun allocateMemory() {
        btn_add_addr = findViewById(R.id.btn_add_addr)

        txt_add_address_pincode = findViewById(R.id.txt_add_address_pincode)
        txt_add_address2 = findViewById(R.id.txt_add_address2)
        txt_add_address1 = findViewById(R.id.txt_add_address1)

        spn_addr_city = findViewById(R.id.spn_addr_city)
        tool_addaddr = findViewById(R.id.tool_addaddr)


        apiINterface = UtilApi.apiService
        storage = DataStorage("loginPref", ctx)

        token = storage?.read("token", DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType", DataStorage.STRING).toString()
    }
}
