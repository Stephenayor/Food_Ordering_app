package com.example.yummy.utils.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.yummy.R


class ComingSoonDialog : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_coming_soon, container, false)
        val titleTv = view.findViewById<TextView>(R.id.tv_title)
        val msgTv = view.findViewById<TextView>(R.id.tv_msg)
        val iconV = view.findViewById<ImageView>(R.id.iv_hour_glass)
        if (arguments != null) {
            val comingSoonModel: ComingSoonModel = requireArguments().getParcelable(ARG_COMING_SOON)!!
            msgTv.textSize = 17f
            val spannableString = HtmlCompat.fromHtml(
                comingSoonModel.getMsg(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            titleTv.text = comingSoonModel.title
            msgTv.text = spannableString
            iconV.setImageResource(comingSoonModel.icon)
        }
        initComponents(view)
        return view
    }

    private fun initComponents(view: View) {
        val dismissLinearLayout = view.findViewById<LinearLayout>(R.id.ll_dismiss)
        dismissLinearLayout.setOnClickListener { v: View? -> dismiss() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog1: DialogInterface ->
            val d = dialog1 as BottomSheetDialog
            val bottomSheet =
                d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    companion object {
        const val ARG_COMING_SOON = "ARG_COMING_SOON"
        fun newInstance(): ComingSoonDialog {
            return ComingSoonDialog()
        }

        fun newInstance(comingSoonModel: ComingSoonModel?): ComingSoonDialog {
            val arg = Bundle()
            val comingSoon = ComingSoonDialog()
            arg.putParcelable(ARG_COMING_SOON, comingSoonModel)
            comingSoon.setArguments(arg)
            return comingSoon
        }
    }
}
