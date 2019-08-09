package com.croodstech.grocery.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.croodstech.grocery.R
import com.croodstech.grocery.activity.DashBoard
import com.croodstech.grocery.activity.MainActivity
import com.croodstech.grocery.activity.ViewAddressActivity
import com.croodstech.grocery.api.DataStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var btn_logout : TextView
    lateinit var btn_addr : TextView
    lateinit var btn_order : TextView


    lateinit var storage : DataStorage
    lateinit var ctx : Context

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
        val root =  inflater.inflate(R.layout.fragment_profile, container, false)

        btn_logout = root.findViewById(R.id.btn_logout)
        btn_addr = root.findViewById(R.id.btn_addr)
        btn_order = root.findViewById(R.id.btn_order)

        storage = DataStorage("loginPref",ctx)

        btn_logout.setOnClickListener {
            storage.write("isFirst","true")
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            (ctx as Activity).finish()
        }

        btn_addr.setOnClickListener {
            val intent = Intent(context, ViewAddressActivity::class.java)
            intent.putExtra("isView","true")
            startActivity(intent)
            //(ctx as Activity).finish()
        }
        btn_order.setOnClickListener {
            val intent = Intent(context, DashBoard::class.java)
            intent.putExtra("fragmentOrder", 2)
            startActivity(intent)
            //(ctx as Activity).finish()
        }

        return  root
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
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
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
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
