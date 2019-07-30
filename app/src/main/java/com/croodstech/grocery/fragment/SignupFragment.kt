package com.croodstech.grocery.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.croodstech.grocery.R
import com.croodstech.grocery.activity.MainActivity
import com.croodstech.grocery.activity.VerificationActivity
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.model.CheckMobileNoResponse
import com.croodstech.grocery.model.CommonResponse
import com.croodstech.grocery.model.SignUpRequest
import com.croodstech.grocery.model.SignUpResponse
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var progressBar: KProgressHUD? = null
    private var apiInterface: ApiInterface? = null
    var ctx: Context? = null

    var storage: DataStorage? = null

    lateinit var txt_rfname : EditText
    lateinit var txt_rlname : EditText
    lateinit var txt_rmobile_no : EditText
    lateinit var txt_remail : EditText
    lateinit var btn_register : Button


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
        val root = inflater.inflate(R.layout.fragment_signup, container, false)
        apiInterface = UtilApi.apiService
        progressBar = ctx?.let { Common.progressBar(it) }

        storage = ctx?.let { DataStorage("loginPref", it) }


        txt_rfname = root.findViewById<EditText>(R.id.txt_rfname)
        txt_rlname = root.findViewById<EditText>(R.id.txt_rlname)
        txt_rmobile_no = root.findViewById<EditText>(R.id.txt_rmobile_no)
        txt_remail = root.findViewById<EditText>(R.id.txt_remail)
        btn_register = root.findViewById<Button>(R.id.btn_register)

        txt_rmobile_no.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                progressBar?.show()
               checkMobileNo(txt_rmobile_no.text.toString())


                return true
            }
        })
        btn_register.setOnClickListener(object : View.OnClickListener {


            override fun onClick(p0: View?) {
                val fname = txt_rfname.text.toString()
                val lname = txt_rlname.text.toString()
                val mobile = txt_rmobile_no.text.toString()
                val email = txt_remail.text.toString()


                if (isValidate(fname, lname, email, mobile)) {
                    signUp(email, fname, lname, mobile)
                }
            }
        }
        )


        return root
    }


    fun isValidate(fname: String, lname: String, mobile: String, email: String): Boolean {
        if (fname == "" || lname == "" || email == "" || mobile == "") {
            ctx?.let { Common.showToast(it, "All fields are require") }
            return false
        } else {
            return true
        }
    }

    fun signUp(
        customerEmail: String,
        customerFirstName: String,
        customerLastName: String,
        customerMobile: String
    ) {

        progressBar?.show()
        val call: Call<CommonResponse>? =
            apiInterface?.signUp(
                SignUpRequest(
                    Common.companyId,
                    customerEmail,
                    customerFirstName,
                    customerLastName,
                    customerMobile
                )
            )
        call?.enqueue(object : retrofit2.Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                progressBar?.dismiss()
                ctx?.let { Common.showToast(it, "Server error please try again latter") }
            }

            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                progressBar?.dismiss()

                if (response.body() != null)
                {
                    if (!response.body()?.status!!)
                    {
                        val msg: String = response.body()!!.message.toString()
                        ctx?.let { Common.showToast(it, msg) }
                    }
                    else
                    {
                        Log.d("false  loop","------------------")
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        (ctx as Activity).finish()
                        val res: SignUpResponse = response.body()!!.response as SignUpResponse
                        storage?.write("token",res.accessToken.toString())
                        storage?.write("tokenType",res.tokenType.toString())
                    }
                }

            }

        })

    }

    fun checkMobileNo(mobileNo: String) {

        val call: Call<CheckMobileNoResponse>? = apiInterface?.checkMobileNo(mobileNo, Common.companyId)
        val enqueue = call?.enqueue(object : retrofit2.Callback<CheckMobileNoResponse> {
            override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                progressBar?.dismiss()

            }

            override fun onResponse(call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>) {
                progressBar?.dismiss()
                var msg: String? = response.body()?.message
                if (response.body()?.status!!)
                {

                }
                else
                {
                    txt_rmobile_no.error = "Mobile number already exist"
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
            SignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
