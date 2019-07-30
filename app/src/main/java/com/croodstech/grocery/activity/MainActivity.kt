package com.croodstech.grocery.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.croodstech.grocery.R
import com.croodstech.grocery.adapter.MainPagerAdapter
import com.croodstech.grocery.fragment.LoginFragment
import com.croodstech.grocery.fragment.SignupFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() ,LoginFragment.OnFragmentInteractionListener,SignupFragment.OnFragmentInteractionListener
{
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var pager_main : ViewPager

    lateinit var tab_main : TabLayout

    var ctx: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager_main = findViewById(R.id.pager_main)
        tab_main = findViewById(R.id.tab_main)


        tab_main.addTab(tab_main.newTab().setText("Login"))
        tab_main.addTab(tab_main.newTab().setText("SignUp"))

        tab_main.tabGravity = TabLayout.GRAVITY_FILL


        val adapter = MainPagerAdapter(supportFragmentManager)
        this.pager_main.adapter = adapter

        this.tab_main.setupWithViewPager(this.pager_main)
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}
