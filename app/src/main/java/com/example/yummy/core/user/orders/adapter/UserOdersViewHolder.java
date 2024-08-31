package com.example.yummy.core.user.orders.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummy.R;

public class UserOdersViewHolder extends RecyclerView.ViewHolder{

    TextView detailsTextView, statusTextView, timeTextView, dateHeaderText,
            senderAccountNumberTextView;
    ConstraintLayout dayConstraintLayout;

    public UserOdersViewHolder(View itemView) {
        super(itemView);
        dateHeaderText = itemView.findViewById(R.id.date__header_tv);
        detailsTextView = itemView.findViewById(R.id.description);
        statusTextView = itemView.findViewById(R.id.user_order_product_name_tv);
        timeTextView = itemView.findViewById(R.id.time_tv);
        dayConstraintLayout = itemView.findViewById(R.id.item_bbg_date_header);
    }

    public TextView getDetailsTextView() {
        return detailsTextView;
    }

    public TextView getStatusTextView() {
        return statusTextView;
    }


    public TextView getTimeTextView() {
        return timeTextView;
    }
    public TextView getDateHeaderTextView() {
        return dateHeaderText;
    }


    public ConstraintLayout getDayConstraintLayout() {
        return dayConstraintLayout;
    }

}
