package com.example.yummy.core.user.orders.bottomsheet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.yummy.R
import com.example.yummy.core.user.UserActivity
import com.example.yummy.databinding.FragmentPaymentSuccessBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PaymentSuccessBottomSheet :  BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPaymentSuccessBottomSheetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val d = this as BottomSheetDialog
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
                val bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                bottomSheetBehaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                    override fun onStateChanged(p0: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED){
                            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                            bottomSheet.requestLayout()
                        }
                    }

                    override fun onSlide(p0: View, p1: Float) {}

                })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPaymentSuccessBottomSheetBinding.inflate(inflater, container, false)
        binding.btnContinue.setOnClickListener {
            UserActivity.start(requireContext())
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaymentSuccessBottomSheet().apply {

            }
    }
}