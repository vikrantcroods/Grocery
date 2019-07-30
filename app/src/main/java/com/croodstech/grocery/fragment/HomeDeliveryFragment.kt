package com.croodstech.grocery.fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.croodstech.grocery.R
import com.croodstech.grocery.activity.SubCategoryActivity
import com.croodstech.grocery.activity.VerificationActivity
import com.croodstech.grocery.activity.ViewCartListActivity
import com.croodstech.grocery.adapter.HomeDeliveryAdapter
import com.croodstech.grocery.adapter.HomeDeliveryListAdapter
import com.croodstech.grocery.adapter.ProductListAdapter
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CategoryVo
import com.croodstech.grocery.model.HomeDeliveryListResponse
import com.croodstech.grocery.model.HomeDeliveryResponse
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_home_delivery.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeDeliveryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var apiINterface: ApiInterface? = null
    var ctx: Context? = null
    var homeDeliverryList: ArrayList<CategoryVo> = ArrayList()

    lateinit var lst_home_delivery: GridView
    lateinit var lst_home_offer: RecyclerView

    var storage: DataStorage? = null

    private val width: Int = 0
    val height: Int = 0
    var remaining: Int = 0
    private val displayMetrics: DisplayMetrics? = null

    var token: String = ""
    var tokenType: String = ""
    var progressBar: KProgressHUD? = null

    lateinit var searchView: SearchView
    lateinit var queryTextListener: SearchView.OnQueryTextListener
    lateinit var adapter: HomeDeliveryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(com.croodstech.grocery.R.layout.fragment_home_delivery, container, false)

        lst_home_offer = view.findViewById<RecyclerView>(com.croodstech.grocery.R.id.lst_home_offer)
        lst_home_delivery = view.findViewById<GridView>(com.croodstech.grocery.R.id.lst_home_delivery)

        apiINterface = UtilApi.apiService
        progressBar = ctx?.let { Common.progressBar(it) }
        storage = ctx?.let { DataStorage("loginPref", it) }


        token = storage?.read("token", DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType", DataStorage.STRING).toString()

        getHomeDeliveryList()

        lst_home_delivery.setOnItemClickListener { p0, p1, position, p3 ->
            val intent = Intent(ctx, SubCategoryActivity::class.java)
            intent.putExtra("categoryName", homeDeliverryList[position].categoryName)
            intent.putExtra("categoryId", homeDeliverryList[position].categoryId)
            startActivity(intent)
        }

        return view
    }

    fun getHomeDeliveryList() {
        progressBar?.show()
        apiINterface?.getHomeDeliveryList(Common.companyId, "$tokenType $token")
            ?.enqueue(object : Callback<HomeDeliveryResponse> {
                override fun onFailure(call: Call<HomeDeliveryResponse>, t: Throwable) {
                    progressBar?.dismiss()
                    Toast.makeText(ctx, "Something Went wrong Please try again latter", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<HomeDeliveryResponse>, response: Response<HomeDeliveryResponse>) {
                    progressBar?.dismiss()
                    if (response.body() != null) {
                        val homeDeliveryResponse: HomeDeliveryResponse = response.body()!!
                        homeDeliverryList = homeDeliveryResponse.response!!

                        adapter = HomeDeliveryAdapter(ctx!!, homeDeliverryList)
                        lst_home_delivery.adapter = adapter
                    }
                }

            })
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle presses on the action bar menu items
        when (item!!.itemId) {
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater!!.inflate(R.menu.home_menu, menu)

        val searchItem = menu!!.findItem(R.id.home_search)

        val searchManager = ctx!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView

            val icon = searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView

            val iconclose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
            val line = searchView.findViewById(androidx.appcompat.R.id.search_plate) as View


            icon.setImageDrawable(ContextCompat.getDrawable(ctx!!, R.mipmap.search))
            iconclose.setImageDrawable(ContextCompat.getDrawable(ctx!!, R.drawable.ic_clear_black_24dp))
            line.setBackgroundColor(Color.parseColor("#00000000"))

            searchView.setLayoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            searchView.setQueryHint(Html.fromHtml("<font color=#ffffff>Search...</font>"))
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))

            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i("onQueryTextChange", newText)
                    adapter.filter(newText)
                    adapter.notifyDataSetChanged()
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)

                    return true
                }
            }
            searchView.setOnQueryTextListener(queryTextListener)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        ctx = context
        super.onAttach(context)
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
            HomeDeliveryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
