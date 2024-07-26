package com.example.yummy.utils

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.yummy.utils.dialogs.BottomDialog2Options
import com.example.yummy.utils.dialogs.ComingSoonDialog
import com.example.yummy.utils.dialogs.ProgressDialogFragment

class Tools {

    companion object {
        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun showShortToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun showProgressDialog(
            context: FragmentActivity, topTitle: Int,
            bottomTitle: Int
        ): ProgressDialogFragment {
            val progressDialogFragment: ProgressDialogFragment =
                ProgressDialogFragment.newInstance(topTitle, bottomTitle)
            val ft = context.supportFragmentManager.beginTransaction()
            progressDialogFragment.show(ft, ProgressDialogFragment.TAG)
            return progressDialogFragment
        }

        fun openDualOptionBottomDialog(activity: FragmentActivity, title: String?, message: String?, hideButton: Boolean) {
            val args = Bundle()
            args.putString("ARG_TITLE", title)
            args.putString("ARG_MESSAGE", message)
            args.putBoolean("ARG_HIDE_BUTTON", hideButton)
            val dialog: BottomDialog2Options = BottomDialog2Options.newInstance(title, message, hideButton)
            dialog.setCancelable(false)
            dialog.setArguments(args)
            dialog.show(
                activity.supportFragmentManager,
                "SuccessErrorDialog"
            )
        }

        fun openComingSoonDialog(activity: FragmentActivity) {
            val dialog: ComingSoonDialog = ComingSoonDialog.newInstance()
            if (!activity.isFinishing) {
                dialog.show(
                    activity.supportFragmentManager,
                    dialog.tag
                )
            }
        }
    }
}