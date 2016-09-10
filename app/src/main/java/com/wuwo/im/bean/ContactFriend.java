package com.wuwo.im.bean;

/**
*desc 通讯录好友
*@author 王明远
*@日期： 2016/8/13 17:22
 */

public class ContactFriend {
    /**
     * PhoneNumber : *0*
     * UserId : 637e5acb638f46f5873ec86f0b4b49ce
     * ContactsUserId : null
     * Ninkname : null
     * Name : *0*
     * CreateOnUtc : 2016-09-06T15:47:29.7
     * Registered : false
     * PhotoUrl : null
     * DisplayIndex : 0
     * Id : 368ad08d-90b3-407a-a769-54d425b05313
     */

    private String PhoneNumber;
    private String UserId;
    private Object ContactsUserId;
    private Object Ninkname;
    private String Name;
    private String CreateOnUtc;
    private boolean Registered;
    private Object PhotoUrl;
    private int DisplayIndex;
    private String Id;

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public Object getContactsUserId() {
        return ContactsUserId;
    }

    public void setContactsUserId(Object ContactsUserId) {
        this.ContactsUserId = ContactsUserId;
    }

    public Object getNinkname() {
        return Ninkname;
    }

    public void setNinkname(Object Ninkname) {
        this.Ninkname = Ninkname;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCreateOnUtc() {
        return CreateOnUtc;
    }

    public void setCreateOnUtc(String CreateOnUtc) {
        this.CreateOnUtc = CreateOnUtc;
    }

    public boolean isRegistered() {
        return Registered;
    }

    public void setRegistered(boolean Registered) {
        this.Registered = Registered;
    }

    public Object getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(Object PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    public int getDisplayIndex() {
        return DisplayIndex;
    }

    public void setDisplayIndex(int DisplayIndex) {
        this.DisplayIndex = DisplayIndex;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }







}