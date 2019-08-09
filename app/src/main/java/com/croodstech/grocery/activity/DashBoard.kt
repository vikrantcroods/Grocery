package com.croodstech.grocery.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.croodstech.grocery.R
import com.croodstech.grocery.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashBoard : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener,
    HomeDeliveryFragment.OnFragmentInteractionListener, SignupFragment.OnFragmentInteractionListener,
    OrderListFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var txt_tool_main: TextView
    lateinit var tool_main: Toolbar

    lateinit var navView: BottomNavigationView

    var bundle: Bundle? = null
    var fNo = 0
    var ctx: Context = this

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                txt_tool_main.text = "DashBoard"
                loadFragment(HomeDeliveryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_category -> {
                txt_tool_main.text = "Category"
                loadFragment(HomeDeliveryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_offer -> {
                txt_tool_main.text = "Offer"

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shop -> {
                txt_tool_main.text = "Order"
                loadFragment(OrderListFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                txt_tool_main.text = "Profile"
                loadFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        navView = findViewById(R.id.nav_view)
        tool_main = findViewById(R.id.tool_main)
        txt_tool_main = findViewById(R.id.txt_tool_main)


        bundle = intent.extras
        if (bundle != null) {
            fNo = bundle!!.getInt("fragmentOrder")
        }

        when (fNo) {
            0 -> {
                navView.menu.findItem(R.id.navigation_home).isChecked = true
                loadFragment(HomeDeliveryFragment())
                txt_tool_main.text = "DashBoard"
            }

            1 -> {

            }

            2 -> {
                navView.menu.findItem(R.id.navigation_shop).isChecked = true
                loadFragment(OrderListFragment())
                txt_tool_main.text = "Order"
            }
            3 -> {

            }

        }

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSupportActionBar(tool_main)
        /*loadFragment(HomeDeliveryFragment())
        txt_tool_main.text = "DashBoard"*/
    }


    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.load_container, fragment)
            .commit()
    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
         // Inflate the menu to use in the action bar
         val inflater = menuInflater
         inflater.inflate(R.menu.home_menu, menu)
         return super.onCreateOptionsMenu(menu)
     }*/

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
    }*/

}
