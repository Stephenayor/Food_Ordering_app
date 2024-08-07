package com.example.yummy.utils.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import com.example.yummy.R
import com.example.yummy.utils.Tools
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.Serializable

class NotificationSheetDialog() : BottomSheetDialogFragment() {
    private var isSuccess = false
    var callback: NotificationSheetDialog.ActionButtonClickedListener? =
        null
    private var onButtonsClickListener: NotificationSheetDialog.OnButtonsClickListener? =
        null
    private var isShowViewButton = false

    interface ActionButtonClickedListener {
        fun onNotificationActionButtonClicked()
    }

    interface OnButtonsClickListener {
        fun onNegativeClick()
        fun onPositiveClick()
    }

    enum class States : Serializable {
        SUCCESS,
        FAILED,
        NEUTRAL
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onButtonsClickListener =
                context as OnButtonsClickListener

            callback =
                context as NotificationSheetDialog.ActionButtonClickedListener
        } catch (e: ClassCastException) {
            Log.d(
                "$context must implement ActionButtonClickedListener",
                " both"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_notification, container, false)
        initComponents(view)
        return view
    }

    private fun initComponents(view: View) {
        val bottomButton = DialogBottomButton(view.findViewById(R.id.bottom_btns))
        val titleTextView = view.findViewById<TextView>(R.id.tv_title)
        val cardView = view.findViewById<CardView>(R.id.cv_main)
        val imageView = view.findViewById<ImageView>(R.id.iv_icon)
        val messageTextView = view.findViewById<TextView>(R.id.tv_message)


        var states: States? = null
        if (arguments != null) {
            states =
                requireArguments().getSerializable(ARG_STATE) as States?
        }
        val msg: String?
        if (arguments != null) {
            msg = requireArguments().getString(ARG_MESSAGE)
            isSuccess = requireArguments().getBoolean(ARG_IS_SUCCESS_OR_ERROR)
            isShowViewButton = requireArguments().getBoolean(ARG_ACTION_SHOW_VIEW_BUTTON)
            val mTitle = requireArguments().getString(ARG_TITLE)
            titleTextView.text = mTitle
            messageTextView.text = msg


            if (isShowViewButton) {
                bottomButton.showViewReceiptButton()
            } else {
                bottomButton.hideViewReceiptButton()
            }

        }
        val actionTitle = requireArguments().getString(ARG_ACTION_TITLE)


        if (!isSuccess) {
            cardView.setCardBackgroundColor(resources.getColor(R.color.red))
            Tools.setImageViewDrawableSrc(
                requireActivity(),
                imageView,
                R.drawable.ic_warning_white_24dp
            )
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog1: DialogInterface ->
            val d: BottomSheetDialog = dialog1 as BottomSheetDialog
            val bottomSheet: FrameLayout? =
                d.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    companion object {
        private val ARG_MESSAGE =
            NotificationSheetDialog::class.java.getCanonicalName()?.plus(".ARG_MESSAGE")
        private val ARG_TITLE =
            NotificationSheetDialog::class.java.getCanonicalName()?.plus(".ARG_TITLE")
        private val ARG_ACTION_TITLE =
            NotificationSheetDialog::class.java.getCanonicalName()?.plus(".ARG_ACTION_TITLE")
        private val ARG_ACTION_SHOW_VIEW_BUTTON =
            NotificationSheetDialog::class.java.getCanonicalName()
                ?.plus(".ARG_ACTION_SHOW_VIEW_BUTTON")
        private val ARG_IS_SUCCESS_OR_ERROR =
            NotificationSheetDialog::class.java.getCanonicalName()?.plus(".ARG_IS_SUCCESS_OR_ERROR")
        private val ARG_STATE =
            NotificationSheetDialog::class.java.getCanonicalName()?.plus(".ARG_STATE")


//        fun newInstance(
//            message: String?, title: String?,
//            isSuccessOrError: Boolean
//        ): NotificationSheetDialog {
//            return newInstance(message, title, showViewButton = , isSuccessOrError)
//        }

        fun newInstance(
            message: String?, title: String?,
            showViewButton: Boolean,
            isSuccessOrError: Boolean
        ): NotificationSheetDialog {
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            args.putString(ARG_TITLE, title)
            args.putBoolean(ARG_IS_SUCCESS_OR_ERROR, isSuccessOrError)
            args.putBoolean(ARG_ACTION_SHOW_VIEW_BUTTON, showViewButton)
            val dialogFrag = NotificationSheetDialog()
            dialogFrag.setArguments(args)
            return dialogFrag
        }

    }
}
