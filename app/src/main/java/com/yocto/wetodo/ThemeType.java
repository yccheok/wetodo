package com.yocto.wetodo;

import android.os.Parcel;
import android.os.Parcelable;

public enum ThemeType implements Parcelable {
    Main;

    public static final Parcelable.Creator<ThemeType> CREATOR = new Parcelable.Creator<ThemeType>() {
        public ThemeType createFromParcel(Parcel in) {
            return ThemeType.valueOf(in.readString());
        }

        public ThemeType[] newArray(int size) {
            return new ThemeType[size];
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
