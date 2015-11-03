package com.adaptris.todoekspert;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Sylwester on 2015-11-03.
 */
public class Todo implements Parcelable {

    public String content;
    public boolean done;

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(content);
        out.writeByte((byte) (done ? 1 : 0));
    }

    public static final Parcelable.Creator<Todo> CREATOR
            = new Parcelable.Creator<Todo>() {
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    public Todo() {

    }

    private Todo(Parcel in) {
        content = in.readString();
        done = in.readByte() > 0;
    }
}
