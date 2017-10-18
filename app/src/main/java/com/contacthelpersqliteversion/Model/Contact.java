package com.contacthelpersqliteversion.Model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;


public class Contact implements Parcelable {

    private String id;
    private String name;
    private String mail;
    private String phone;
    @Nullable
    private String photo;

    public Contact() {

    }

    public Contact(String id, String name, String mail, String phone, String photo) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.mail);
        dest.writeString(this.phone);
        dest.writeString(this.photo);
    }

    protected Contact(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.mail = in.readString();
        this.phone = in.readString();
        this.photo = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
