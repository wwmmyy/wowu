package com.wuwo.im.bean;

/**
*desc 推荐好友信息
*@author 王明远
*@日期： 2016/8/13 12:08
 */

public class RecommendFriends {

     /**
     * UserId : cb510cdf98ca4b9f9082051bf7190ff4
     * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/c7f13738-1768-406d-a914-53278f2a0835x480.jpg
     * Gender : 0
     * Name : 噜噜
     * Description : null
     */

    private String UserId;
    private String PhotoUrl;
    private int Gender;
    private String Name;
    private String RemarkName;
    private Object Description;

    private  boolean addState;

    public String getRemarkName() {
        return RemarkName;
    }

    public void setRemarkName(String remarkName) {
        RemarkName = remarkName;
    }

    public boolean isAddState() {
        return addState;
    }

    public void setAddState(boolean addState) {
        this.addState = addState;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int Gender) {
        this.Gender = Gender;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Object getDescription() {
        return Description;
    }

    public void setDescription(Object Description) {
        this.Description = Description;
    }
}