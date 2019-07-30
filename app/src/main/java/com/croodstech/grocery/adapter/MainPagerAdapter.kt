package com.croodstech.grocery.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.croodstech.grocery.fragment.LoginFragment
import com.croodstech.grocery.fragment.SignupFragment

class MainPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm)

{
    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when(position) {
            0 -> {
               fragment =  LoginFragment()
            }

            1->{

                fragment =  SignupFragment()
            }
            else-> return null

        }

        return fragment

    }

    override fun getCount(): Int {

        return 2
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Login"
        } else if (position == 1) {
            title = "SignUp"
        }
        return title
    }


}