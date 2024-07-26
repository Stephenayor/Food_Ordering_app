package com.example.yummy.utils.dialogs;

import android.os.Parcel;
import android.os.Parcelable;

public class ComingSoonModel implements Parcelable {

    public static final Creator<ComingSoonModel> CREATOR = new Creator<ComingSoonModel>() {
        @Override
        public ComingSoonModel createFromParcel(Parcel in) {
            return new ComingSoonModel(in);
        }

        @Override
        public ComingSoonModel[] newArray(int size) {
            return new ComingSoonModel[size];
        }
    };
    private String msg;
    private String title;
    private int icon;

    public ComingSoonModel() {
    }

    public ComingSoonModel(String msg, String title, int icon) {
        this.msg = msg;
        this.title = title;
        this.icon = icon;
    }

    protected ComingSoonModel(Parcel in) {
        msg = in.readString();
        title = in.readString();
        icon = in.readInt();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeString(title);
        dest.writeInt(icon);
    }
}
