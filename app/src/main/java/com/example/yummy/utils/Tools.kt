package com.example.yummy.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.BoringLayout
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import com.example.yummy.R
import com.example.yummy.utils.dialogs.BottomDialog2Options
import com.example.yummy.utils.dialogs.ComingSoonDialog
import com.example.yummy.utils.dialogs.NotificationSheetDialog
import com.example.yummy.utils.dialogs.ProgressDialogFragment
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.util.Locale

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

        fun openDualOptionBottomDialog(
            activity: FragmentActivity,
            title: String?,
            message: String?,
            hideButton: Boolean
        ) {
            val args = Bundle()
            args.putString("ARG_TITLE", title)
            args.putString("ARG_MESSAGE", message)
            args.putBoolean("ARG_HIDE_BUTTON", hideButton)
            val dialog: BottomDialog2Options =
                BottomDialog2Options.newInstance(title, message, hideButton)
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

        fun convertBitmapToBase64(bitmap: Bitmap?): String {
            val stream = ByteArrayOutputStream()
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                val fileSize = bitmap.getByteCount() / 1048576
                Log.d("convertBitmapToBase64", "File size: $fileSize")
            }
            val byteFormat = stream.toByteArray()
            return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
        }

        fun formatToCommaNaira(context: Context?, amount: String): String {
            return try {
                formatToCommaNaira(context!!, BigDecimal(amount))
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
                amount
            }
        }

        private fun formatToCommaNaira(context: Context, amount: BigDecimal): String {
            return String.format(
                Locale.US, context.resources.getString(R.string.money_naira_double_format),
                amount.toDouble()
            )
        }

        fun setImageViewDrawableSrc(context: Context, imageView: ImageView, drawableId: Int) {
            val drawable = AppCompatResources.getDrawable(context, drawableId)
            imageView.setImageDrawable(
                context.resources
                    .getDrawable(drawableId, context.applicationContext.theme)
            )
        }

        fun openSuccessErrorDialog(
            activity: FragmentActivity,
            message: String?, title: String?,
            showViewButton: Boolean,
            isSuccess: Boolean,
        ) {
            val dialog: NotificationSheetDialog =
                NotificationSheetDialog.newInstance(message, title, showViewButton, isSuccess)
            dialog.setCancelable(false)
            if (!dialog.isVisible) {
                performVibrationAction(activity)
                if (!activity.isFinishing
                    && !activity.isDestroyed
                    && !activity.supportFragmentManager.isStateSaved
                ) {
                    dialog.show(
                        activity.supportFragmentManager,
                        "SuccessErrorDialog"
                    )
                }
            }
        }

        private fun performVibrationAction(activity: FragmentActivity) {
            val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(200)
            }
        }
    }
}