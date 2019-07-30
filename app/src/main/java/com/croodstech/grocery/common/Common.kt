package com.croodstech.grocery.common

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.kaopiz.kprogresshud.KProgressHUD

object Common {

    //var companyId = "0039" // for patidaar sales
    var companyId = "0002" // for partyvilla
    fun showSnack(coordinatorLayout: View, msg: String) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show()
    }

    fun progressBar(ctx: Context): KProgressHUD {
        return KProgressHUD.create(ctx)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Loading...")
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
    }

    fun showToast(ctx: Context, msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
    }

}
