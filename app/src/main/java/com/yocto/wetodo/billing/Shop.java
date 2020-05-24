package com.yocto.wetodo.billing;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.billingclient.api.BillingClient;

public enum Shop implements Parcelable {
    ColorLite("color_lite", "color_lite_promo", BillingClient.SkuType.INAPP);

    Shop(String sku, String sku_promo, String sku_type) {
        this.sku = sku;
        this.sku_promo = sku_promo;
        this.sku_type = sku_type;
    }

    public final String sku;
    public final String sku_promo;
    public final String sku_type;

    public boolean isSubs() {
        return BillingClient.SkuType.SUBS.equals(this.sku_type);
    }

    public boolean isInApp() {
        return BillingClient.SkuType.INAPP.equals(this.sku_type);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Handling Parcelable nicely.

    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return Shop.valueOf(in.readString());
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
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

