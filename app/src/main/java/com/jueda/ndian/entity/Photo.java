package com.jueda.ndian.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 图片。
 * <p/>
 * Created by yanglw on 2014/8/17.
 */
public class Photo extends Bean implements Parcelable,Serializable
{
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(path);
    }

    public static final Creator<Photo> CREATOR
            = new Creator<Photo>()
    {
        public Photo createFromParcel(Parcel in)
        {
            Photo photo = new Photo();
            photo.id = in.readString();
            photo.text = in.readString();
            photo.path = in.readString();
            return photo;
        }

        public Photo[] newArray(int size)
        {
            return new Photo[size];
        }
    };

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || ((Object) this).getClass() != o.getClass())
        {
            return false;
        }

        Photo photo = (Photo) o;

        return path.equals(photo.path);
    }

    @Override
    public int hashCode()
    {
        return path.hashCode();
    }
}
