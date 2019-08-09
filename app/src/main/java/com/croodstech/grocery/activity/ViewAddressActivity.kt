package com.croodstech.grocery.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.adapter.AddressAdapter
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.*
import com.kaopiz.kprogresshud.KProgressHUD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAddressActivity : AppCompatActivity() {

    lateinit var tool_addr: Toolbar


    /* lateinit var txt_addr_name: TextView
     lateinit var txt_addr1: TextView
     lateinit var txt_addr2: TextView
     lateinit var txt_addr_pincode: TextView
     lateinit var txt_addr_mono: TextView*/
    lateinit var lbl_no_addr: TextView
    lateinit var lbl_add_addr: TextView

    lateinit var dialog: Dialog


    lateinit var lst_address: RecyclerView

    lateinit var btn_confirm_order: Button

    var apiINterface: ApiInterface? = null
    var ctx: Context = this
    var cartList: ArrayList<CartVo> = ArrayList()

    var storage: DataStorage? = null


    var token: String = ""
    var tokenType: String = ""
    var progressBar: KProgressHUD? = null

    var bundle: Bundle? = null


    var maincitiesList: ArrayList<CityVo>? = null
    var citiesList: ArrayList<String>? = null
    var spinnerPosition = 0
    var selectedCityName = ""
    var cityCode = ""
    var addr1 = ""
    var addr2 = ""
    var pincode = ""
    var isView = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_address)

        allocateMemory()

        setSupportActionBar(tool_addr)

        if (bundle != null) {
            isView = bundle!!.getString("isView").toString()
        }

        getAddress()

        if (isView == "true") {
            btn_confirm_order.visibility = View.GONE
        } else {
            btn_confirm_order.visibility = View.VISIBLE
        }

        btn_confirm_order.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                apiINterface?.placeOrder(Common.companyId, "$tokenType $token")
                    ?.enqueue(object : Callback<CheckMobileNoResponse> {
                        override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                            Common.showToast(ctx, "Please try again latter")
                        }

                        override fun onResponse(
                            call: Call<CheckMobileNoResponse>,
                            response: Response<CheckMobileNoResponse>
                        ) {
                            if (response.body() != null) {
                                if (response.body()!!.status!!) {
                                    Common.showToast(ctx, "Order Placed successfully")
                                    val intent = Intent(ctx, DashBoard::class.java)
                                    intent.putExtra("fragmentOrder", 2)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })

            }
        })

        lbl_add_addr.setOnClickListener { showAddAddressDialog() }
    }

    fun getAddress() {
        apiINterface?.getAllAddress(Common.companyId, "$tokenType $token")?.enqueue(object : Callback<AddressModel> {
            override fun onFailure(call: Call<AddressModel>, t: Throwable) {
                // Common.showToast(ctx,"Please try again latter")
                Log.d("trace", "-------------" + t.message)
            }


            override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {

                if (response.body() != null) {
                    if (response.body()!!.status) {
                        val addrmodelVo = response.body()!!.response

                        if (addrmodelVo.isEmpty()) {
                            lbl_no_addr.visibility = View.VISIBLE
                        } else {
                            lbl_no_addr.visibility = View.GONE

                            val adapter = AddressAdapter(addrmodelVo, ctx)
                            lst_address.adapter = adapter
                        }


                        /* txt_addr_name.text = ContactAddressVo.firstName + " " + ContactAddressVo.lastName
                         txt_addr1.text = ContactAddressVo.addressLine1
                         txt_addr2.text = ContactAddressVo.addressLine2
                         txt_addr_pincode.text = ContactAddressVo.cityName + ", " + ContactAddressVo.pinCode
                         txt_addr_mono.text = ContactAddressVo.phoneNo*/
                    }
                }
            }
        })

    }

    fun showAddAddressDialog() {
        dialog = Dialog(this, R.style.AlertDialogCustom)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.address_dialog)

        val txt_add_address1 = dialog.findViewById(R.id.txt_add_address1) as EditText
        val txt_add_address2 = dialog.findViewById(R.id.txt_add_address2) as EditText
        val txt_add_address_pincode = dialog.findViewById(R.id.txt_add_address_pincode) as EditText
        val spn_addr_city = dialog.findViewById(R.id.spn_addr_city) as AppCompatSpinner
        val img_addr_cancel = dialog.findViewById(R.id.img_addr_cancel) as ImageView

        val btn_add_addr = dialog.findViewById(R.id.btn_add_addr) as Button

        maincitiesList = ArrayList()
        citiesList = ArrayList()

        apiINterface?.getAllCity("$tokenType $token")?.enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                if (response.body() != null) {
                    maincitiesList = response.body()!!.response
                    for (city in maincitiesList!!) {
                        (citiesList)?.add(city.cityName)
                    }
                    val adapter = ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, citiesList!!)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_addr_city.adapter = adapter

                    spinnerPosition = adapter.getPosition("Ahmedabad")
                    spn_addr_city.setSelection(spinnerPosition)
                    cityCode = maincitiesList!![spinnerPosition].cityCode

                } else {
                    Common.showToast(ctx, "Please try again latter")
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                Common.showToast(ctx, "Server error,Please try again latter")
            }
        })

        spn_addr_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                selectedCityName = maincitiesList!![i].cityName
                cityCode = maincitiesList!![i].cityCode

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        img_addr_cancel.setOnClickListener { dialog.dismiss() }

        btn_add_addr.setOnClickListener {

            addr1 = txt_add_address1.text.toString()
            addr2 = txt_add_address2.text.toString()
            pincode = txt_add_address_pincode.text.toString()

            if (validateData(addr1, addr2, pincode, cityCode)) {
                addAddress(addr1, addr2, pincode, cityCode)

                dialog.dismiss()
                getAddress()
            }
        }
        dialog.show()
    }

    fun validateData(addr1: String, addr2: String, pincode: String, citycode: String): Boolean {
        if (addr1 != "" || addr2 != "" || pincode != "" || citycode != "") {
            return true
        } else {
            Common.showToast(ctx, "All fields are complusary")
            return false
        }
    }

    fun addAddress(addr1: String, addr2: String, pincode: String, citycode: String) {
        progressBar?.show()
        apiINterface?.addAddress(AddAddressRequest(addr1, addr2, citycode, pincode), "$tokenType $token")
            ?.enqueue(object : Callback<CheckMobileNoResponse> {
                override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                    progressBar?.dismiss()
                }

                override fun onResponse(call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>) {
                    progressBar?.dismiss()
                    if (response.body() != null) {
                        if (response.body()!!.status!!)
                            Common.showToast(ctx, "Address Add successfully")
                    }

                }
            })

    }

    private fun allocateMemory() {
        tool_addr = findViewById(R.id.tool_addr)

        lbl_add_addr = findViewById<TextView>(R.id.lbl_add_addr)
        lbl_no_addr = findViewById<TextView>(R.id.lbl_no_addr)


        /*  txt_addr2 = findViewById<TextView>(R.id.txt_addr2)
         txt_addr_pincode = findViewById<TextView>(R.id.txt_addr_pincode)
         txt_addr_mono = findViewById<TextView>(R.id.txt_addr_mono)*/

        lst_address = findViewById(R.id.lst_address)

        btn_confirm_order = findViewById(R.id.btn_confirm_order)

        apiINterface = UtilApi.apiService
        progressBar = Common.progressBar(ctx)
        storage = DataStorage("loginPref", ctx)

        bundle = intent.extras
        token = storage?.read("token", DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType", DataStorage.STRING).toString()


    }
}
