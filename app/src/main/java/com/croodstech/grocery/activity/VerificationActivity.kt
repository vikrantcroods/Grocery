package com.croodstech.grocery.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.croodstech.grocery.R
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CheckMobileNoResponse
import com.croodstech.grocery.model.CommonResponse
import com.croodstech.grocery.model.SignUpResponse
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit


class VerificationActivity : AppCompatActivity() {

    var b: Bundle? = null
    var mobileno: String? = ""
    var otp: String? = ""
    var ctx: Context = this
    var apiInterface: ApiInterface? = null
    var progressBar: KProgressHUD? = null


    lateinit var txt_verification_mobno: TextView
    lateinit var lbl_resend_otp_timer: TextView

    lateinit var txt_otp1: TextView
    lateinit var txt_otp2: TextView
    lateinit var txt_otp3: TextView
    lateinit var txt_otp4: TextView
    lateinit var btn_login: TextView

    var storage: DataStorage? = null

    lateinit var countDownTimer: CountDownTimer

    var otp1 = ""
    var otp2 = ""
    var otp3 = ""
    var otp4 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        allocateMemory()

        if (b != null) {
            mobileno = b?.getString("mobileno")
            txt_verification_mobno.text = mobileno
        }

        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
//Convert milliseconds into hour,minute and seconds
                val hms = String.format(
                    "%02d",
                    TimeUnit.MILLISECONDS.toSeconds(
                        millisUntilFinished
                    ) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                )
                lbl_resend_otp_timer.text = hms//set text
                lbl_resend_otp.isClickable = false
                lbl_resend_otp.setTextColor(ctx.resources.getColor(R.color.colorDark))

            }

            override fun onFinish() {

                lbl_resend_otp_timer.text = ""
                //lbl_resend_otp.visibility = View.VISIBLE
                lbl_resend_otp.isClickable = true
                lbl_resend_otp.setTextColor(ctx.resources.getColor(R.color.colorBlue))
            }
        }.start()

        lbl_resend_otp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                sendOtp()
                lbl_resend_otp.isClickable = false
                lbl_resend_otp.setTextColor(ctx.resources.getColor(R.color.colorDark))
                countDownTimer.start()
            }

        })


        setOtpEvents()

        btn_login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                progressBar?.show()
                otp1 = txt_otp1.text.toString()
                otp2 = txt_otp2.text.toString()
                otp3 = txt_otp3.text.toString()
                otp4 = txt_otp4.text.toString()

                if (otp1 == "" || otp2 == "" || otp3 == "" || otp4 == "") {
                    progressBar?.dismiss()
                    Common.showToast(ctx, "Otp should not blank")
                } else {
                    otp =
                        txt_otp1.text.toString() + txt_otp2.text.toString() + txt_otp3.text.toString() + txt_otp4.text.toString()
                        verifyOtp(otp!!)
                }

            }

        })


    }

    fun verifyOtp(otp: String) {
        apiInterface?.verifyOtp(mobileno.toString(), otp, Common.companyId)?.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                progressBar?.dismiss()
                Common.showToast(ctx, "Please try again latter")
            }

            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {

                if(response.body() != null)
                {
                    if (response.body()!!.status) {
                        progressBar?.dismiss()
                        Log.d("trace response","-----------------"+ response.body()!!.response)
                        var res : SignUpResponse? = response.body()!!.response
                        storage?.write("token", res?.accessToken.toString())
                        storage?.write("tokenType", res?.tokenType.toString())
                        storage?.write("isFirst","false")
                        val intent = Intent(ctx, DashBoard::class.java)
                        startActivity(intent)
                        (ctx as Activity).finish()
                    } else {
                        progressBar?.dismiss()
                        val msg = response.body()!!.message
                        msg?.let { Common.showToast(ctx, it) }


                    }
                }
            }

        })
    }

    fun allocateMemory() {

        txt_verification_mobno = findViewById<TextView>(R.id.txt_verification_mobno)
        lbl_resend_otp_timer = findViewById<TextView>(R.id.lbl_resend_otp_timer)

        txt_otp1 = findViewById<TextView>(R.id.txt_otp1)
        txt_otp2 = findViewById<TextView>(R.id.txt_otp2)
        txt_otp3 = findViewById<TextView>(R.id.txt_otp3)
        txt_otp4 = findViewById<TextView>(R.id.txt_otp4)

        btn_login = findViewById<Button>(R.id.btn_login)


        storage = DataStorage("loginPref", ctx)
        apiInterface = UtilApi.apiService
        progressBar = com.croodstech.grocery.common.Common.progressBar(ctx)



        b = intent.extras
    }

    fun sendOtp() {
        apiInterface?.signIn(mobileno.toString(), Common.companyId)?.enqueue(object :
            Callback<CheckMobileNoResponse> {
            override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                progressBar?.dismiss()
            }

            override fun onResponse(
                call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>
            ) {
                progressBar?.dismiss()
                /* if (response.body()?.status!!)
                 {
                     val intent = Intent(ctx, VerificationActivity::class.java)
                     intent.putExtra("mobileno",txt_login_mobileno.text.toString())
                     startActivity(intent)
                 }*/
            }

        })
    }

    private fun setOtpEvents() {
        txt_otp1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (txt_otp1.text.toString().trim().length == 1) {
                    txt_otp2.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        txt_otp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (txt_otp2.text.toString().trim().length == 1) {
                    txt_otp3.requestFocus()
                } else if (txt_otp2.text.toString().trim().length == 0) {
                    txt_otp1.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        txt_otp3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (txt_otp3.text.toString().trim().length == 1) {
                    txt_otp4.requestFocus()
                } else if (txt_otp3.text.toString().trim().length == 0) {
                    txt_otp2.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        txt_otp4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (txt_otp4.text.toString().trim().length == 0) {
                    txt_otp3.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

    }
}
