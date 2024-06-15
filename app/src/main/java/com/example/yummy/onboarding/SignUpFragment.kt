package com.example.yummy.onboarding

import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import co.ceryle.radiorealbutton.RadioRealButton
import com.example.yummy.R
import com.example.yummy.databinding.FragmentSignUpBinding
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.Tools
import com.example.yummy.utils.Validations
import com.example.yummy.utils.animations.Animations
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class SignUpFragment : Fragment() {
    private var realButtonAdmin: RadioRealButton? = null
    private var realButtonPersonal: RadioRealButton? = null
    private lateinit var binding: FragmentSignUpBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
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

        switchSignupButtonState()
        setupObservers()

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
        firebaseAuth.createUserWithEmailAndPassword(
            userEmail.text.toString(),
            binding.confirmSignUpPassword.text.toString()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        createUserDetailsOnFirebase(user)
                    }
                    Tools.showToast(requireContext(), "ACCOUNT CREATED SUCCESSFULLY")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Tools.showShortToast(
                        requireContext(),
                        "Authentication failed for Account Creation!."
                    )
                }
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
            if (s.toString().length < 8) {
                binding.signUpPassword.error = "Please Enter at least 8 Characters"
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

    private fun switchSignupButtonState() = binding.apply {

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
    }


    private fun handlePersonalState() {
        Animations.objectAnimator(requireView(), 20f)
        val personalStateColor: Int = R.color.colorGoldenYellow
        binding.btnAdmin.selectorColor = personalStateColor
        realButtonPersonal?.selectorColor = personalStateColor
        changeBackground(binding.bgImage, R.drawable.sign_up_patten_gold)
        showCheckBox()
        hideAdminStateCheckbox()
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