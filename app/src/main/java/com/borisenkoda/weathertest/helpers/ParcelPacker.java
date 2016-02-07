package com.borisenkoda.weathertest.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by BDA on 10.12.2015.
 */
public class ParcelPacker {
    static ParcelPacker instance;
    Gson gson;

    public static void writeToParcel(Parcel parcel, Object object) {
        if (instance == null) {
            instance = new ParcelPacker();
        }

        parcel.writeString(instance.gson.toJson(object));
    }

    public static <T> Creator<T> getCreator(Class<T> type) {
        if (instance == null) {
            instance = new ParcelPacker();
        }
        return new Creator<T>(type, instance.gson);
    }

    private ParcelPacker() {
        gson = new Gson();  // We have to use simple Gson instance.
    }

    static class Creator<T> implements Parcelable.Creator<T> {
        final Class<T> tClass;
        final Gson gson;

        public Creator(Class<T> tClass, Gson gson) {
            this.tClass = tClass;
            this.gson = gson;
        }

        @Override
        public T createFromParcel(Parcel parcel) {
            return gson.fromJson(parcel.readString(), tClass);
        }

        @Override
        public T[] newArray(int i) {
            throw new RuntimeException("Not implemented");
        }
    }
}
