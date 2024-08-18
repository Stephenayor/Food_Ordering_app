package com.example.yummy.core.admin.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.yummy.R
import com.example.yummy.core.admin.AdminSharedViewModel
import com.example.yummy.core.user.UserActivity
import com.example.yummy.core.view.IntroSliderActivity
import com.example.yummy.databinding.FragmentProfileBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.dialogs.NotificationSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(),
            NotificationSheetDialog.OnButtonsClickListener{
    private lateinit var binding: FragmentProfileBinding
    private val adminViewModel by viewModels<AdminSharedViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun getLayoutBinding(binding: FragmentProfileBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rlLogout.setOnClickListener {
            openLogoutDialog()
        }

        binding.rlSwitchAccount.setOnClickListener {
            UserActivity.start(requireContext())
        }

        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        adminViewModel.signOutResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.signing_out, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    IntroSliderActivity.start(requireContext())
                }

                is Resource.Error -> {
                    hideLoading()
                    Tools.openSuccessErrorDialog(
                        requireActivity(),
                        response.message,
                        "Failed",
                        false,
                        false
                    )
                }

                else -> {}
            }
        }
    }

   private fun openLogoutDialog() {
        val alertBuilder = AlertDialog.Builder(requireContext())
        alertBuilder.setTitle("Are you sure you want to logout?")
        alertBuilder.setPositiveButton(R.string.logout) { _, _ ->
            adminViewModel.signOut()
        }
        alertBuilder.setNegativeButton(R.string.action_stay) { dialogInterface, _ ->
            dialogInterface.dismiss() }
        if (!requireActivity().isFinishing) {
            alertBuilder.show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {

            }
    }

    override fun onNegativeClick() {
        TODO("Not yet implemented")
    }

    override fun onPositiveClick() {
        TODO("Not yet implemented")
    }
}