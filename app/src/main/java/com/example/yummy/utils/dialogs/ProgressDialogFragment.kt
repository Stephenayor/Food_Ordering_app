package com.example.yummy.utils.dialogs


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import com.example.yummy.R


class ProgressDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_progress_dialog, container, false)
        initComponents(view)
        return view
    }

    private fun initComponents(view: View) {
        val topTitleTextView = view.findViewById<TextView>(R.id.tv_top_title)
        val bottomTextView = view.findViewById<TextView>(R.id.tv_bottom_title)
        if (requireArguments().getInt(ARG_TOP_TITLE) != -1) {
            if (topTitleTextView != null) {
                topTitleTextView.visibility = View.VISIBLE
                topTitleTextView.setText(requireArguments().getInt(ARG_TOP_TITLE))
            }
        }
        if (requireArguments().getInt(ARG_BOTTOM_TITLE) != -1) {
            bottomTextView.visibility = View.VISIBLE
            bottomTextView.setText(requireArguments().getInt(ARG_BOTTOM_TITLE))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onClick(dialog: DialogInterface, which: Int) {}
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_LightStatusBar)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    companion object {
        const val TAG = "ProgressDialogFragment"
        val ARG_TOP_TITLE = (ProgressDialogFragment::class.java.getCanonicalName()
            ?.plus(".ARG_TOP_TITLE"))
        val ARG_BOTTOM_TITLE = (ProgressDialogFragment::class.java.getCanonicalName()
            ?.plus(".ARG_BOTTOM_TITLE"))
        const val NO_TITLE = -1
        fun newInstance(@IdRes topTitle: Int, @IdRes bottomTitle: Int): ProgressDialogFragment {
            val fragment = ProgressDialogFragment()
            val args = Bundle()
            args.putInt(ARG_TOP_TITLE, topTitle)
            args.putInt(ARG_BOTTOM_TITLE, bottomTitle)
            fragment.setArguments(args)
            return fragment
        }
    }
}
