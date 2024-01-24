package com.kits.kowsarapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    public final String name;
    public int id;
    public int ChildNo;


    public Product(String name, Integer id, Integer ChildNo) {
        this.name = name;
        this.id = id;
        this.ChildNo = ChildNo;
    }

    protected Product(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
