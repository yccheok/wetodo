package com.yocto.wetodo;

import android.os.Parcel;
import android.os.Parcelable;

public enum FragmentType implements Parcelable {
    Todo,
    Trash;

    public static final Parcelable.Creator<FragmentType> CREATOR = new Parcelable.Creator<FragmentType>() {
        public FragmentType createFromParcel(Parcel in) {
            return FragmentType.valueOf(in.readString());
        }

        public FragmentType[] newArray(int size) {
            return new FragmentType[size];
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
