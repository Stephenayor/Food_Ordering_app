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
import com.google.firebase.auth.auth
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
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {

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
                        Tools.showToast(requireContext(), response.data.email)
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


    private fun setViewListener() {
        binding.googleSignInButton.setOnClickListener {
            startOneTapSignInWithGoogle()
        }

        binding.btnSignIn.setOnClickListener {
            executeLogin(binding.emailEditTextField.text.toString(),
                binding.passwordEditTextField.text.toString())
        }

        binding.register.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.tvForgotPassword.setOnClickListener {
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