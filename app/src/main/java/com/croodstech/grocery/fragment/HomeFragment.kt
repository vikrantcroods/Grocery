package com.croodstech.grocery.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment(),HomeDeliveryFragment.OnFragmentInteractionListener,SignupFragment.OnFragmentInteractionListener  {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var ctx:Context?=null

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
        val view = inflater.inflate(com.croodstech.grocery.R.layout.fragment_home, container, false)

       /* val tab_layout = view.findViewById<TabLayout>(com.croodstech.grocery.R.id.tab_layout)
        tab_layout.addTab(tab_layout.newTab().setText("Delivery"))
        tab_layout.addTab(tab_layout.newTab().setText("Self Pickup"))


        tab_layout.getTabAt(0)?.setIcon(com.croodstech.grocery.R.mipmap.delivery)
        tab_layout.getTabAt(1)?.setIcon(com.croodstech.grocery.R.mipmap.self_pickup)*/


        replaceFragment(HomeDeliveryFragment())

       /* tab_layout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.getPosition() == 0) {
                    replaceFragment(HomeDeliveryFragment())
                } else if (tab?.getPosition() == 1) {
                    replaceFragment(SignupFragment());
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })*/


      /*  view_pager.adapter = adapter

        tab_layout.setupWithViewPager(view_pager)*/


        return  view
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
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = fragmentManager
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(com.croodstech.grocery.R.id.fragment_container, fragment)

        transaction?.commit()
    }
}
