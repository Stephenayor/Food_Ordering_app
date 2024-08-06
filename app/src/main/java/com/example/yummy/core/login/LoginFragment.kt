package com.example.yummy.core.login

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.yummy.R
import com.example.yummy.core.admin.AdminActivity
import com.example.yummy.core.login.viewmodel.LoginViewModel
import com.example.yummy.core.onboarding.SignUpFragmentDirections
import com.example.yummy.databinding.FragmentLoginBinding
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.NavigateTo
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.dialogs.ProgressDialogFragment.Companion.TAG
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var binding: FragmentLoginBinding
    private lateinit var toolbar: Toolbar
    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var emailText: String
    private lateinit var password: String
    private val REQUEST_CODE_SIGN_IN: Int = 1
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private val cloudFireStore = Firebase.firestore
    private var isAdmin = false
    private lateinit var userDetails: Any


    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun getLayoutBinding(binding: FragmentLoginBinding) {
        this.binding = binding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
        oneTapClient = Identity.getSignInClient(requireActivity())
        createGoogleSignInRequest()
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val uid = loginViewModel.getLoginUID(AppConstants.LOGIN_UID)
        val currentUser = firebaseAuth.currentUser
        val documentReference =
            cloudFireStore.collection(AppConstants.USERS).document(uid.toString())
        documentReference.get().addOnSuccessListener {
            if (it.get("isAdmin") != null) {
                isAdmin = true
            }
        }
        if (currentUser != null && isAdmin) {
            AdminActivity.start(requireActivity())
        } else {
            AdminActivity.start(requireActivity())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = binding.toolbar
        initToolbar(requireActivity() as AppCompatActivity, toolbar)
        emailText = binding.emailEditTextField.text.toString()
        password = binding.passwordEditTextField.text.toString()

        subscribeToLiveData()
        setViewListener()
    }

    private fun subscribeToLiveData() {
        loginViewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.please_wait_dots, R.string.loading_your_fun_experience)
                }

                is Resource.Success -> {
                    hideLoading()
                    if (response.data != null) {
                        checkCategoryOfUser(response.data.uid)
                        loginViewModel.saveLoginUID(AppConstants.LOGIN_UID, response.data.uid)
                        val user = response.data
                        user.let { checkIfUserExists(it.email, it.uid, it.displayName) }
                    }
                }

                is Resource.Error -> {
                    hideLoading()
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage(response.message)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                else -> {}
            }
        }

    }

    private fun checkCategoryOfUser(uid: String) {
        val documentReference = cloudFireStore.collection(AppConstants.USERS).document(uid)
        documentReference.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val isAdmin = document.get("isAdmin")
                if (isAdmin != null) {
                    AdminActivity.start(requireContext())
                } else {
                    // Handle the case where "isAdmin" is null
                    Tools.showToast(requireActivity(), "User Activity!")
                }
            } else {
                println("No such document")
            }
        }.addOnFailureListener { exception ->
            println("get failed with $exception")
        }

    }


    private fun checkIfUserExists(email: String?, uid: String, name: String?) {
        email?.let {
            cloudFireStore.collection(AppConstants.USERS).document(uid).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        saveUserToFirestore(email, uid, name)
                    } else {
                        Log.d(TAG, "User already exists")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error checking user existence", exception)
                }
        }
    }

    private fun saveUserToFirestore(email: String, uid: String, name: String?) {
        val userDetails = HashMap<String, Any>()
        userDetails["UserEmail"] = email
        userDetails["UserName"] = email.substringBefore("@")
        userDetails["Password"] = ""
        if (email == AppConstants.TEST_ADMIN_MAIL) {
            userDetails["isAdmin"] = 1
        }

        cloudFireStore.collection(AppConstants.USERS).document(uid).set(userDetails)
            .addOnSuccessListener {
                Log.d(TAG, "User added to Firestore")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user to Firestore", e)
            }
    }


    private fun setViewListener() {
        binding.googleSignInButton.setOnClickListener {
            startOneTapSignInWithGoogle()
        }

        binding.btnSignIn.setOnClickListener {
            executeLogin(
                binding.emailEditTextField.text.toString(),
                binding.passwordEditTextField.text.toString()
            )
        }

        binding.register.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.tvForgotPassword.setOnClickListener {
            Tools.openComingSoonDialog(requireActivity())
        }

        binding.appleSignInButton.setOnClickListener {
            Tools.openComingSoonDialog(requireActivity())
        }
    }

    private fun executeLogin(emailField: String, password: String) {
        loginViewModel.executeLogin(emailField, password)
    }

    private fun startOneTapSignInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQUEST_CODE_SIGN_IN,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }.addOnFailureListener {

            }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            firebaseAuthWithGoogleAccount(idToken)
                        }

                        else -> {}
                    }
                } catch (e: ApiException) {

                }
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(idToken: String?) {
        loginViewModel.firebaseAuthWithGoogleAccount(idToken!!)
    }


    private fun createGoogleSignInRequest() {
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_application_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {

            }
    }
}