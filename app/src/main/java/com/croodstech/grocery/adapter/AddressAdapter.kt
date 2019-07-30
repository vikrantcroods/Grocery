package com.croodstech.grocery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CheckMobileNoResponse
import com.croodstech.grocery.model.ContactAddressVo
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.address_row.view.*
import kotlinx.android.synthetic.main.order_list_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressAdapter(val addressList: List<ContactAddressVo>, val ctx: Context) :
    RecyclerView.Adapter<ViewAddressHolder>(

    ) {

    private var checkedPosition = -1
    var storage: DataStorage? = null


    var apiInterface: ApiInterface? = null
    var token: String = ""
    var tokenType: String = ""
    var progressBar: KProgressHUD? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAddressHolder {
        apiInterface = UtilApi.apiService
        progressBar = Common.progressBar(ctx)
        storage = DataStorage("loginPref", ctx)
        token = storage?.read("token", DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType", DataStorage.STRING).toString()

        return ViewAddressHolder(LayoutInflater.from(ctx).inflate(R.layout.address_row, parent, false))
    }

    override fun getItemCount(): Int {

        return addressList.size
    }

    override fun onBindViewHolder(holder: ViewAddressHolder, position: Int) {

        val ContactAddressVo = addressList[0]

        holder.txt_addr_name.text =  ContactAddressVo.firstName + " " + ContactAddressVo.lastName
        holder.txt_addr1.text = ContactAddressVo.addressLine1
        holder.txt_addr2.text = ContactAddressVo.addressLine2
        holder.txt_addr_pincode.text = ContactAddressVo.cityName + ", " + ContactAddressVo.pinCode
        holder.txt_addr_mono.text = ContactAddressVo.phoneNo

        holder.rdo_addr_select.setOnClickListener {
            checkedPosition = position
            notifyDataSetChanged()
        }

        holder.rdo_addr_select.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                addressList[position].isDefault = 1
                progressBar?.show()
                apiInterface?.setDefaultAddress(addressList[position].contactAddressId.toString(),"$tokenType $token")?.enqueue(object : Callback<CheckMobileNoResponse> {
                    override fun onResponse(call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>) {
                        if (response.body()!=null) {
                            if (response.body()!!.status!!) {

                            }
                        }
                    }

                    override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })

                progressBar?.dismiss()
            }
        })

        holder.itemView.setOnClickListener {
            if (checkedPosition != position) {
                notifyItemChanged(position)
                checkedPosition =position
            }
        }
    }

}


class ViewAddressHolder(view: View) : RecyclerView.ViewHolder(view) {

    var rdo_addr_select = view.rdo_addr_select
    val img_addr_edit = view.img_addr_edit
    val img_addr_delete = view.img_addr_delete

    val txt_addr_name = view.txt_addr_name
    val txt_addr1 = view.txt_addr1
    val txt_addr2 = view.txt_addr2
    val txt_addr_pincode = view.txt_addr_pincode
    val txt_addr_mono = view.txt_addr_mono

}
