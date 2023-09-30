package com.example.yummy.onboarding

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import co.ceryle.radiorealbutton.RadioRealButton
import com.example.yummy.R
import com.example.yummy.databinding.FragmentSignUpBinding
import com.example.yummy.utils.Validations
import com.example.yummy.utils.animations.Animations


class SignUpFragment : Fragment() {
    private var realButtonAdmin: RadioRealButton? = null
    private var realButtonPersonal: RadioRealButton? = null
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realButtonAdmin = binding.btnAdmin
        realButtonPersonal = binding.btnPersonal

        switchSignupButtonState()
        setupObservers()

    }

    private fun setupObservers() = binding.apply {
        etEmail.addTextChangedListener(textWatcher)
        signUpPassword.addTextChangedListener(passwordTextWatcher)
        confirmSignUpPassword.addTextChangedListener(confirmPasswordTextWatcher)
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
            if (!binding.confirmSignUpPassword.text?.toString()?.
                equals(binding.signUpPassword.text?.toString())!!) {
                binding.confirmSignUpPassword.error = "Check the Entered Password Again"
            }else{
                binding.confirmSignUpPassword.error = null
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private fun validatePasswordFields(password: String) {

        if (password.length < 8) {
            binding.signUpPassword.error = "Please Enter at least 8 Characters"
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
        if (email.length <= 1) {
            binding.etEmail.error = "Please Enter an Email"
        }

        if (Validations.isEmailValid(email)) {
            binding.etEmail.error = null
        } else {
            binding.etEmail.error = "Please Enter a Valid Email"
        }
    }

    private fun enableDisableButton() {
        TODO("Not yet implemented")
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