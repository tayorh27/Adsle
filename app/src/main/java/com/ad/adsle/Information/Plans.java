package com.ad.adsle.Information;

import android.os.Parcel;
import android.os.Parcelable;

public class Plans implements Parcelable {

    String id, title, description, validity, price, data, bonus_data, product_id;

    public Plans() {
    }

    public Plans(String id, String title, String description, String validity, String price, String data, String bonus_data, String product_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.validity = validity;
        this.price = price;
        this.data = data;
        this.bonus_data = bonus_data;
        this.product_id = product_id;
    }

    protected Plans(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        validity = in.readString();
        price = in.readString();
        data = in.readString();
        bonus_data = in.readString();
        product_id = in.readString();
    }

    public static final Creator<Plans> CREATOR = new Creator<Plans>() {
        @Override
        public Plans createFromParcel(Parcel in) {
            return new Plans(in);
        }

        @Override
        public Plans[] newArray(int size) {
            return new Plans[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getValidity() {
        return validity;
    }

    public String getPrice() {
        return price;
    }

    public String getData() {
        return data;
    }

    public String getBonus_data() {
        return bonus_data;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setBonus_data(String bonus_data) {
        this.bonus_data = bonus_data;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(validity);
        dest.writeString(price);
        dest.writeString(data);
        dest.writeString(bonus_data);
        dest.writeString(product_id);
    }

}
