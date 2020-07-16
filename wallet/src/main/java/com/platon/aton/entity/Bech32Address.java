package com.platon.aton.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Bech32Address implements Parcelable {

    private String mainnet;
    private String testnet;


    public Bech32Address(String mainnet, String testnet) {
        this.mainnet = mainnet;
        this.testnet = testnet;
    }

    protected Bech32Address(Parcel in) {
        mainnet = in.readString();
        testnet = in.readString();
    }

    public String getMainnet() {
        return mainnet;
    }

    public void setMainnet(String mainnet) {
        this.mainnet = mainnet;
    }

    public String getTestnet() {
        return testnet;
    }

    public void setTestnet(String testnet) {
        this.testnet = testnet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Bech32Address> CREATOR = new Creator<Bech32Address>() {
        @Override
        public Bech32Address createFromParcel(Parcel in) {
            return new Bech32Address(in);
        }

        @Override
        public Bech32Address[] newArray(int size) {
            return new Bech32Address[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mainnet);
        dest.writeString(testnet);
    }
}
