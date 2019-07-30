package com.croodstech.grocery.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.croodstech.grocery.R
import com.croodstech.grocery.api.DataStorage

class SplashActivity : AppCompatActivity(), Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {

        if (isFirst == "false")
        {
            val intent = Intent(ctx, DashBoard::class.java)
            startActivity(intent)
            (ctx as Activity).finish()
        }
        else
        {
            val intent = Intent(ctx, MainActivity::class.java)
            startActivity(intent)
            (ctx as Activity).finish()
        }

    }

    override fun onAnimationStart(p0: Animation?) {

    }

    lateinit var imgLogo: ImageView
    lateinit var linear: ConstraintLayout

    lateinit var storage: DataStorage
    lateinit var isfirst: String

    var ctx: Context = this

    var isFirst: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        imgLogo = findViewById(R.id.imgLogo)
        linear = findViewById(R.id.linear)

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.animation_fade_in)
        animation.interpolator = LinearInterpolator()
        animation.setAnimationListener(this)

        linear.startAnimation(animation)
        storage = DataStorage("loginPref", ctx)
        isfirst = storage.read("isFirst", DataStorage.STRING).toString()






    }
}
