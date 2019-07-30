package com.croodstech.grocery.fragment

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.croodstech.grocery.activity.DashBoard
import com.croodstech.grocery.activity.MainActivity
import com.croodstech.grocery.activity.VerificationActivity
import com.croodstech.grocery.api.ApiInterface
import com.croodstech.grocery.api.DataStorage
import com.croodstech.grocery.common.Common
import com.croodstech.grocery.api.UtilApi
import com.croodstech.grocery.model.CheckMobileNoResponse
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


open class LoginFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var apiInterface: ApiInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var ctx: Context
    val RC_SIGN_IN: Int = 1
    lateinit var name: String
    lateinit var email: String
    lateinit var idToken: String
    lateinit var googleApiClient: GoogleApiClient


    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authStareListener: FirebaseAuth.AuthStateListener
    lateinit var callBackManager: CallbackManager

    var progressBar: KProgressHUD? = null
    var storage : DataStorage? = null

    var token : String=""
    var tokenType : String=""
    var isFirst : String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(com.croodstech.grocery.R.layout.fragment_login, container, false)


        val txt_login_mobileno = root.findViewById<EditText>(com.croodstech.grocery.R.id.txt_login_mobileno)
        val btn_login = root.findViewById<Button>(com.croodstech.grocery.R.id.btn_login)
        val btn_google_login = root.findViewById<ImageView>(com.croodstech.grocery.R.id.btn_google_login)
        val btn_facebook_login = root.findViewById<ImageView>(com.croodstech.grocery.R.id.btn_facebook_login)

        apiInterface = UtilApi.apiService
        storage = DataStorage("loginPref",ctx)
        progressBar = Common.progressBar(ctx)

        token = storage?.read("token",DataStorage.STRING).toString()
        tokenType = storage?.read("tokenType",DataStorage.STRING).toString()
        isFirst = storage?.read("isFirst",DataStorage.STRING).toString()

        Log.d("trace accesstoken","-------"+token)

        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()

        authStareListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

            //get signin user
            val user = firebaseAuth.currentUser

            //if user is signed in, we call a helper method to save the user details to Firebase
            if (user != null) {
                Log.d("trace user id", user.uid)
            } else {
                Log.d("trace user sign out---", "user sign out")
            }
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.croodstech.grocery.R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleApiClient = this.activity?.let {
            GoogleApiClient.Builder(ctx)
                .enableAutoManage(it, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        }!!

        btn_google_login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                progressBar?.show()
                val googleIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(googleIntent, RC_SIGN_IN)
            }

        })

        FacebookSdk.sdkInitialize(ctx)
        /*var info = ctx.getPackageManager().getPackageInfo("com.croodstech.grocery", PackageManager.GET_SIGNATURES)

        for (signature in info?.signatures!!) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }*/


        btn_facebook_login.setOnClickListener {
            progressBar?.show()

            var fbLoginManager = LoginManager.getInstance()

            fbLoginManager.logInWithReadPermissions(
                activity,
                Arrays.asList("email", "public_profile", "user_birthday")
            )


            fbLoginManager.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    Log.d("trace response", "--------" + result)
                    getUserDetails(result!!)
                }

                override fun onError(error: FacebookException?) {
                    Log.d("trace response", "--------" + error)
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }


                override fun onCancel() {
                }
            })
        }


        if (isFirst == "false")
        {
            val intent = Intent(context, DashBoard::class.java)
            startActivity(intent)
            (ctx as Activity).finish()
        }

        btn_login.setOnClickListener( object : View.OnClickListener{
            override fun onClick(p0: View?) {
                progressBar?.show()
                if (txt_login_mobileno.text.toString() == "")
                {
                    progressBar?.dismiss()
                    Common.showToast(ctx,"Should not blanck ")
                }
                else if (txt_login_mobileno.text.toString().length != 10)
                {
                    progressBar?.dismiss()
                    Common.showToast(ctx,"Mobile number is not valid")
                }
                else
                {
                    checkMobileNo(txt_login_mobileno.text.toString())
                }
            }
        })



        return root
    }


    protected fun getUserDetails(loginResult: LoginResult) {

        Log.d("hello fb json???", "----------------------" + loginResult)
        val data_request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { json_object, response ->
            Log.d("hello fb json???", "-------------------" + json_object)

        }
        val permission_param = Bundle()
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)")
        data_request.parameters = permission_param
        data_request.executeAsync()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInData(result)
        } else {
            progressBar?.dismiss()
            Toast.makeText(
                ctx, "Authentication failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->

            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
            if (task.isSuccessful) {
                Toast.makeText(ctx, "Login successful", Toast.LENGTH_SHORT).show()
                //gotoProfile()
            } else {
                Log.w(TAG, "signInWithCredential" + task.exception!!.message)
                task.exception!!.printStackTrace()
                Toast.makeText(
                    ctx, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    fun checkMobileNo(mobileNo: String) {
        val call: Call<CheckMobileNoResponse>? = apiInterface?.signIn(mobileNo, Common.companyId)
        call?.enqueue(object  : retrofit2.Callback<CheckMobileNoResponse>
        {

            override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                progressBar?.dismiss()
                Common.showToast(ctx,"Server error, Please try again latter")
            }

            override fun onResponse(call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>) {

                if(response.body()!=null)
                {
                    if(response.body()?.status!!)
                    {
                        apiInterface?.signIn(txt_login_mobileno.text.toString(),Common.companyId)?.enqueue(object  : Callback<CheckMobileNoResponse>
                        {
                            override fun onFailure(call: Call<CheckMobileNoResponse>, t: Throwable) {
                                progressBar?.dismiss()
                            }

                            override fun onResponse(call: Call<CheckMobileNoResponse>, response: Response<CheckMobileNoResponse>
                            )
                            {
                                progressBar?.dismiss()
                                if (response.body()?.status!!)
                                {
                                    val intent = Intent(context, VerificationActivity::class.java)
                                    intent.putExtra("mobileno",txt_login_mobileno.text.toString())
                                    startActivity(intent)
                                }
                            }

                        })

                    }
                    else
                    {
                        progressBar?.dismiss()
                        Common.showToast(ctx,"Something went wrong.Please try again latter")
                    }
                }

            }

        })

    }

    fun handleSignInData(result: GoogleSignInResult) {
        progressBar?.dismiss()
        if (result.isSuccess) {

            val account = result.signInAccount

            idToken = account?.idToken.toString()
            name = account?.displayName.toString()
            email = account?.email.toString()

            val credential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuthWithGoogle(credential)

        } else {
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. -------------" + result.status)
            Toast.makeText(ctx, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
