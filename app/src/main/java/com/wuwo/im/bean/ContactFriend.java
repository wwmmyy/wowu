package com.wuwo.im.bean;

/**
*desc 通讯录好友
*@author 王明远
*@日期： 2016/8/13 17:22
 */

public class ContactFriend {


    /**
     * PhoneNumber : +85282038863
     * UserId : 637e5acb638f46f5873ec86f0b4b49ce
     * ContactsUserId : null
     * Name : 索尼愛立信香港客戶服務熱線
     * CreateOnUtc : 2016-08-13T09:21:01.083
     * IsFriend : false
     * Id : 0c6284ea-c18d-4336-a0c3-2363266c531b
     */

    private String PhoneNumber;
    private String UserId;
    private Object ContactsUserId;
    private String Name;
    private String CreateOnUtc;
    private boolean IsFriend;
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

    public boolean isIsFriend() {
        return IsFriend;
    }

    public void setIsFriend(boolean IsFriend) {
        this.IsFriend = IsFriend;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }
}