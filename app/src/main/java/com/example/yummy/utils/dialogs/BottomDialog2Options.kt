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
import android.widget.TextView
import com.example.yummy.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomDialog2Options : BottomSheetDialogFragment(), View.OnClickListener {
    private var tvProceed: TextView? = null
    private var tvCancel: TextView? = null
    private var tvTitle: TextView? = null
    private var tvMessage: TextView? = null
    private var onSelectMenuItemListener: OnSelectMenuItemListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSelectMenuItemListener = try {
            context as OnSelectMenuItemListener
        } catch (ce: ClassCastException) {
            throw ClassCastException("OnSelectMenuItemListener must be implemented in $context")
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
        val view: View = inflater.inflate(R.layout.double_options_dialog, container, false)
        initComponents(view)
        bindClickListener()
        return view
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

    private fun initComponents(view: View) {
        tvTitle = view.findViewById(R.id.tv_title)
        tvMessage = view.findViewById(R.id.tv_message)
        tvCancel = view.findViewById(R.id.tv_cancel)
        tvProceed = view.findViewById(R.id.tv_proceed)
        var msg: String? = null
        if (arguments != null) {
            msg = requireArguments().getString("ARG_MESSAGE")
            val mTitle = requireArguments().getString("ARG_TITLE")
            val hideButton = requireArguments().getBoolean("ARG_HIDE_BUTTON")
            tvTitle?.text = mTitle
            tvMessage?.text = msg
            if (hideButton){
                tvCancel?.visibility = View.GONE
            }
        }
    }

    private fun bindClickListener() {
        tvProceed!!.setOnClickListener(this)
        tvCancel!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (onSelectMenuItemListener == null) {
            Log.e("BottomDialog3Options", "OnSelectMenuItemListener has not been implemented")
            return
        }
        if (v === tvCancel) {
            onSelectMenuItemListener!!.onCancel()
        } else if (v === tvProceed) {
            onSelectMenuItemListener!!.onProceed()
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    interface OnSelectMenuItemListener {
        fun onProceed()
        fun onCancel()
    }

    companion object {
        fun newInstance(title: String?, message: String?, hideButton: Boolean): BottomDialog2Options {
            val bottomDialog2Options = BottomDialog2Options()
            val bundle = Bundle()
            bundle.putString("ARG_TITLE", title)
            bundle.putString("ARG_MESSAGE", message)
            bundle.putBoolean("ARG_HIDE_BUTTON", hideButton)
            bottomDialog2Options.setArguments(bundle)
            return bottomDialog2Options
        }
    }
}
