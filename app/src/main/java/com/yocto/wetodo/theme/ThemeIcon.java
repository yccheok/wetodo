package com.yocto.wetodo.theme;

import android.os.Parcel;
import android.os.Parcelable;

public enum ThemeIcon implements Parcelable {
    Black,
    White,
    None;


    public static final Parcelable.Creator<ThemeIcon> CREATOR = new Parcelable.Creator<ThemeIcon>() {
        public ThemeIcon createFromParcel(Parcel in) {
            return ThemeIcon.valueOf(in.readString());
        }

        public ThemeIcon[] newArray(int size) {
            return new ThemeIcon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.name());
    }
}