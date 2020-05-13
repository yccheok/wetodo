package com.yocto.wetodo.billing;

import android.os.Parcel;
import android.os.Parcelable;

public enum Feature implements Parcelable {
    Color;

    ////////////////////////////////////////////////////////////////////////////
    // Handling Parcelable nicely.

    public static final Parcelable.Creator<Feature> CREATOR = new Parcelable.Creator<Feature>() {
        public Feature createFromParcel(Parcel in) {
            return Feature.valueOf(in.readString());
        }

        public Feature[] newArray(int size) {
            return new Feature[size];
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

    // Handling Parcelable nicely.
    ////////////////////////////////////////////////////////////////////////////
}

