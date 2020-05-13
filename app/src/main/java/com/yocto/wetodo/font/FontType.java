package com.yocto.wetodo.font;

import android.os.Parcel;
import android.os.Parcelable;

public enum FontType implements Parcelable {
    SlabSerif(0),
    NotoSans(1);

    public final int code;

    FontType(int code) {
        this.code = code;
    }

    public static final Parcelable.Creator<FontType> CREATOR = new Parcelable.Creator<FontType>() {
        public FontType createFromParcel(Parcel in) {
            return FontType.valueOf(in.readString());
        }

        public FontType[] newArray(int size) {
            return new FontType[size];
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
