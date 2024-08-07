package com.example.yummy.utils.dialogs;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.yummy.R;

//TODO: To be reffactored
public class DialogBottomButton {

    private final Button btnContinue;

    private final Button btnViewReceipt;

    private final LinearLayout viewReceiptWrapper;



    public DialogBottomButton(View view){

        btnContinue = view.findViewById(R.id.btn_continue);

        btnViewReceipt = view.findViewById(R.id.btn_view_receipt);

        viewReceiptWrapper = view.findViewById(R.id.parent_view_receipt);
    }


    public void setNegativeText(String buttonText){
        if (buttonText == null){
            Log.e("DialogBottomButton", "buttonText is null");
            return;
        }
        getLeftButton().setText(buttonText);
    }

    public void setPositiveText(String buttonText){
        if (buttonText == null){
            Log.e("DialogBottomButton", "buttonText is null");
            return;
        }
        getPositiveBtn().setText(buttonText);
    }

    public Button getLeftButton(){
        return btnContinue;
    }

    public Button getPositiveBtn(){
        return btnViewReceipt;
    }

    public void hideContinueButton(){
        btnContinue.setVisibility(View.GONE);
    }

    public void hideViewReceiptButton(){
        viewReceiptWrapper.setVisibility(View.GONE);
    }

    public void setNegativeClickListener(View.OnClickListener onClickListener){
        this.getLeftButton().setOnClickListener(onClickListener);
    }

    public void setPositiveListener(View.OnClickListener onClickListener){
        this.getPositiveBtn().setOnClickListener(onClickListener);
    }

    public void showContinueButton(){
        btnContinue.setVisibility(View.VISIBLE);
    }

    public void showDoneButton(){
        btnContinue.setVisibility(View.VISIBLE);
        btnContinue.setText("Done");
    }

    public void showViewReceiptButton(){
        viewReceiptWrapper.setVisibility(View.VISIBLE);
    }

    public void showViewTransactionsButton(){
        viewReceiptWrapper.setVisibility(View.VISIBLE);
        Button text = viewReceiptWrapper.findViewById(R.id.btn_view_receipt);
        text.setText("View transaction");
    }


}
