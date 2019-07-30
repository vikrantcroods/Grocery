package com.croodstech.grocery.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.croodstech.grocery.R
import com.croodstech.grocery.adapter.HomeDeliveryAdapter
import com.croodstech.grocery.adapter.OrderListAdapter
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.*
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_home_delivery.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OrderListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [OrderListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class OrderListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var  apiINterface : ApiInterface? = null
    var ctx : Context? = null
    var orderList : List<OrderVo> = ArrayList()

    lateinit var lst_order : RecyclerView
    lateinit var lbl_noorder : TextView

    var storage : DataStorage?=null

    var token : String=""
    var tokenType : String=""
    var progressBar : KProgressHUD?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_order_list, container, false)

        lst_order = root.findViewById(R.id.lst_order)
        lbl_noorder = root.findViewById(R.id.lbl_noorder)

        apiINterface = UtilApi.apiService
        progressBar = ctx?.let { Common.progressBar(it) }
        storage = ctx?.let { DataStorage("loginPref", it) }


        token = storage?.read("token",DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType",DataStorage.STRING).toString()

        getHomeDeliveryList()

        return root
    }


    fun getHomeDeliveryList()
    {
        progressBar?.show()
        apiINterface?.orderList(Common.companyId, "$tokenType $token")?.enqueue(object :
            Callback<OrderListResponse> {
            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                progressBar?.dismiss()
                Toast.makeText(ctx,"Something Went wrong Please try again latter", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>)
            {
                progressBar?.dismiss()
                if(response.body() != null)
                {
                    if (response.body()!!.status!!)
                    {
                        val response : OrderListResponse = response.body()!!
                        orderList = response.response!!

                        if (orderList.isNotEmpty())
                        {
                            lbl_noorder.visibility = View.GONE
                            lst_order.adapter = ctx?.let { OrderListAdapter( orderList,it) }
                        }
                        else
                        {
                            lbl_noorder.visibility = View.VISIBLE
                        }

                    }

                }
            }

        })
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
