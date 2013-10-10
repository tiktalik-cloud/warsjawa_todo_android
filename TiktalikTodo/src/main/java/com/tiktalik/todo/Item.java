package com.tiktalik.todo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alek on 10/10/13.
 */
public class Item implements Parcelable {
    int id;
    String todo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(todo);
    }

    public Item() {
    }

    public Item(Parcel in) {
        id = in.readInt();
        todo = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}