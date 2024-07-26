package com.example.yummy.core.onboarding

import android.R.attr.password
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import co.ceryle.radiorealbutton.RadioRealButton
import com.example.yummy.R
import com.example.yummy.core.onboarding.viewmodel.SignUpViewModel
import com.example.yummy.databinding.FragmentSignUpBinding
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.NavigateTo
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.Validations
import com.example.yummy.utils.animations.Animations
import com.example.yummy.utils.base.BaseFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    private var realButtonAdmin: RadioRealButton? = null
    private var realButtonPersonal: RadioRealButton? = null
    private lateinit var binding: FragmentSignUpBinding


    override fun getLayoutId(): Int = R.layout.fragment_sign_up

    override fun getLayoutBinding(binding: FragmentSignUpBinding) {
        this.binding = binding
    }

    private lateinit var signupButton: Button
    private var isValidEmail: Boolean = false
    private var isValidPassword: Boolean = false
    private var isUsernameValid: Boolean = false
    private lateinit var userName: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private val cloudFireStore = Firebase.firestore
    private lateinit var userEmail: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var adminCheckBox: CheckBox
    private lateinit var personalCheckBox: CheckBox
    private val signUpViewModel by viewModels<SignUpViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realButtonAdmin = binding.btnAdmin
        realButtonPersonal = binding.btnPersonal
        signupButton = binding.btnSignUp
        userName = binding.etUsername
        firebaseAuth = Firebase.auth
        userEmail = binding.etEmail
        confirmPasswordEditText = binding.confirmSignUpPassword
        adminCheckBox = binding.adminCheckbox
        personalCheckBox = binding.personalCheckbox

        switchSignupProfile()
        setupObservers()
        subscribeToLiveData()

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        // Optional: Set up navigation icon and listener
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowHomeEnabled(true)

    }



//    @Deprecated("Deprecated in Java")
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                requireActivity().onBackPressed()
//                true
//            }
//
//            R.id.hamburger_icon -> {
//                Tools.showToast(requireContext(), "Reaching")
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onStart() {
        super.onStart()
        setupObservers()
    }

    private fun subscribeToLiveData() {
        signUpViewModel.signupUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.please_wait_dots, R.string.mssg_creating_your_account)
                }

                is Resource.Success -> {
                    hideLoading()
                    Log.d("response", response.data.toString())
                    if (response.data != null) {
                        createUserDetailsOnFirebase(response.data)
                        val action =
                            SignUpFragmentDirections.actionSignUpFragmentToSuccessFragment(
                                AppConstants.ACCOUNT_CREATED,
                                "Please proceed to login to explore the goodness available",
                                AppConstants.CONTINUE,
                                NavigateTo.LOGIN.toString()
                            )
                        findNavController().navigate(action)
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

        signUpViewModel.testResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                Tools.showToast(requireContext(), it)
            }
        }
    }

    private fun setupObservers() = binding.apply {
        etEmail.addTextChangedListener(textWatcher)
        signUpPassword.addTextChangedListener(passwordTextWatcher)
        confirmSignUpPassword.addTextChangedListener(confirmPasswordTextWatcher)
        userName.addTextChangedListener(userNameTextWatcher)

        signupButton.setOnClickListener {
            onboardNewUsers()
        }

        adminCheckbox.setOnCheckedChangeListener { compoundButton: CompoundButton, _: Boolean ->
            enableDisableButton()
        }

        personalCheckbox.setOnCheckedChangeListener { compoundButton: CompoundButton, _: Boolean ->
            enableDisableButton()
            if (compoundButton.isChecked) {
                enableDisableButton()
            }
        }
    }

    private fun onboardNewUsers() {
        if (adminCheckBox.isChecked) {
            Tools.openDualOptionBottomDialog(
                requireActivity(),
                "Please Note",
                "Only Authorized Users can create an Admin account",
                true
            )
        } else {
            signUpViewModel.executeOnboardNewUser(
                userEmail.text.toString(),
                binding.confirmSignUpPassword.text.toString()
            )
        }
    }

    private fun createUserDetailsOnFirebase(user: FirebaseUser) {
        val documentReference = cloudFireStore.collection(AppConstants.USERS).document(user.uid)
        val userDetails = HashMap<String, Any>()
        userDetails["UserEmail"] = userEmail.text.toString()
        userDetails["UserName"] = userName.text.toString()
        userDetails["Password"] = confirmPasswordEditText.text.toString()
        if (adminCheckBox.isChecked) {
            userDetails["isAdmin"] = 1
        }
        documentReference.set(userDetails)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newText = s.toString()
            validateEmailField(newText)
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val password = s.toString()
            validatePasswordFields(password)
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.toString().length < 6) {
                binding.signUpPassword.error = "Please Enter at least 6 Characters"
            }
        }
    }

    private val confirmPasswordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!binding.confirmSignUpPassword.text?.toString()
                    ?.equals(binding.signUpPassword.text?.toString())!!
            ) {
                isValidPassword = false
                binding.confirmSignUpPassword.error = "Check the Entered Password Again"
            } else {
                binding.confirmSignUpPassword.error = null
                isValidPassword = true
            }
            enableDisableButton()
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private val userNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableButton()
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private fun validatePasswordFields(password: String) {

        if (password.length < 6) {
            binding.signUpPassword.error = "Please Enter at least 6 Characters"
        }

        if (!containsDigit(password)) {
            binding.signUpPassword.error = "Password must contain Digits"
        }

        if (Validations.isPasswordValid(password)) {
            binding.signUpPassword.error = null
        }


    }

    private fun containsDigit(text: String): Boolean {
        val regex = Regex("\\d")
        return regex.containsMatchIn(text)
    }


    private fun validateEmailField(email: String) {
        isValidEmail = false
        if (email.length <= 1) {
            binding.etEmail.error = "Please Enter an Email"
        }

        if (Validations.isEmailValid(email)) {
            binding.etEmail.error = null
            isValidEmail = true
        } else {
            binding.etEmail.error = "Please Enter a Valid Email"
        }
        enableDisableButton()
    }

    private fun enableDisableButton() {
        signupButton.isEnabled = isValidEmail &&
                Validations.isUsernameValid(userName.text.toString())
                && isValidPassword && adminCheckBox.isChecked || personalCheckBox.isChecked
    }

    private fun switchSignupProfile() = binding.apply {

        try {
            val drawableTint = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_business_acct, null
            )
            realButtonAdmin?.setDrawable(drawableTint)
            realButtonPersonal!!.setDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_personal_acct,
                    null
                )
            )
            realButtonPersonal!!.imageView.visibility = View.VISIBLE
            realButtonAdmin?.imageView?.visibility = View.VISIBLE

        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        }

        rgAccountType.setOnPositionChangedListener { _, currentPosition, lastPosition ->
            when (currentPosition) {
                0 -> handleAdminState()
                1 -> handlePersonalState()
            }

        }
    }

    private fun handleAdminState() {
        Animations.fadeIn(realButtonAdmin)
        Animations.objectAnimator(requireView(), 0f)
        realButtonAdmin!!.selectorColor = R.color.app_default_purple
        realButtonPersonal!!.selectorColor = R.color.app_default_purple
        changeBackground(binding.bgImage, R.drawable.password_sign_up_pattern_right)
        hidePersonalStateCheckbox()
        showAdminStateCheckBox()
        clearEditText()
    }


    private fun handlePersonalState() {
        Animations.objectAnimator(requireView(), 20f)
        val personalStateColor: Int = R.color.colorGoldenYellow
        binding.btnAdmin.selectorColor = personalStateColor
        realButtonPersonal?.selectorColor = personalStateColor
        changeBackground(binding.bgImage, R.drawable.sign_up_patten_gold)
        showCheckBox()
        hideAdminStateCheckbox()
        clearEditText()
        enableDisableButton()
    }

    private fun showCheckBox() = binding.apply {
        signUpPersonal.visibility = View.VISIBLE
        personalCheckbox.visibility = View.VISIBLE
    }

    private fun showAdminStateCheckBox() = binding.apply {
        adminCheckbox.visibility = View.VISIBLE
    }

    private fun hideAdminStateCheckbox() = binding.apply {
        adminCheckbox.visibility = View.INVISIBLE
    }

    private fun hidePersonalStateCheckbox() = binding.apply {
        signUpPersonal.visibility = View.INVISIBLE
        personalCheckbox.visibility = View.INVISIBLE
    }

    private fun clearEditText() = binding.apply {
        etEmail.requestFocus()
        etEmail.addTextChangedListener(textWatcher)

//        etEmail.text?.clear()
        signUpPassword.text?.clear()
//        confirmSignUpPassword.text?.clear()
        userName.text?.clear()
        adminCheckBox.isChecked = false
        personalCheckBox.isChecked = false
    }


    private fun changeBackground(bg: ImageView, drawableId: Int) {
        bg.setImageResource(drawableId)
        val animator = ObjectAnimator.ofFloat(
            bg, "alpha", 0f,
            1f
        )
        animator.start()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {

            }
    }

}