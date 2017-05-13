package com.wuwo.im.bean;

import java.io.Serializable;
import java.util.List;

/**
*desc {"Data":[{"UserId":"932183f73fa34220bc7f6f5a89f342c9","Name":"ranran","Disposition":"ISFP艺术家","Description":"这个人很懒，什么都没写。","Age":0,"Gender":1,"Distance":"9.1km","Before":"1小时","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/0d650dd4-859b-48ec-b587-e7250b02a4a5x480.jpg","EasemobId":"b148d99a-36b9-11e6-af54-c76407f6c700","IsVip":true}],"Total":4,"PageCount":1}
*@author 王明远
*@日期： 2016/6/18 12:13
*@版权:Copyright    All rights reserved.
*/

public class LocalUser {


    /**
     * Data : [{"UserId":"932183f73fa34220bc7f6f5a89f342c9","Name":"ranran","Disposition":"ISFP艺术家","Description":"这个人很懒，什么都没写。","Age":0,"Gender":1,"Distance":"9.1km","Before":"1小时","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/0d650dd4-859b-48ec-b587-e7250b02a4a5x480.jpg","EasemobId":"b148d99a-36b9-11e6-af54-c76407f6c700","IsVip":true}]
     * Total : 4
     * PageCount : 1
     */

    private int Total;
    private int PageCount;
    /**
     * UserId : 932183f73fa34220bc7f6f5a89f342c9
     * Name : ranran
     * Disposition : ISFP艺术家
     * Description : 这个人很懒，什么都没写。
     * Age : 0
     * Gender : 1
     * Distance : 9.1km
     * Before : 1小时
     * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/0d650dd4-859b-48ec-b587-e7250b02a4a5x480.jpg
     * EasemobId : b148d99a-36b9-11e6-af54-c76407f6c700
     * IsVip : true
     */

    private List<DataBean> Data;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int PageCount) {
        this.PageCount = PageCount;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean implements Serializable {
        private String UserId;
        private String Name;
        private String Disposition;
        private String RemarkName;
        private String Description;
        private int Age;
        private int Gender;
        private String Distance;
        private String Before;
        private String PhotoUrl;
        private String EasemobId;
        private boolean IsVip;
        private int VipType;

        public int getVipType() {
            return VipType;
        }

        public void setVipType(int vipType) {
            VipType = vipType;
        }

        public String getRemarkName() {
            return RemarkName;
        }

        public void setRemarkName(String remarkName) {
            RemarkName = remarkName;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getDisposition() {
            return Disposition;
        }

        public void setDisposition(String Disposition) {
            this.Disposition = Disposition;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public int getAge() {
            return Age;
        }

        public void setAge(int Age) {
            this.Age = Age;
        }

        public int getGender() {
            return Gender;
        }

        public void setGender(int Gender) {
            this.Gender = Gender;
        }

        public String getDistance() {
            return Distance;
        }

        public void setDistance(String Distance) {
            this.Distance = Distance;
        }

        public String getBefore() {
            return Before;
        }

        public void setBefore(String Before) {
            this.Before = Before;
        }

        public String getPhotoUrl() {
            return PhotoUrl;
        }

        public void setPhotoUrl(String PhotoUrl) {
            this.PhotoUrl = PhotoUrl;
        }

        public String getEasemobId() {
            return EasemobId;
        }

        public void setEasemobId(String EasemobId) {
            this.EasemobId = EasemobId;
        }

        public boolean isIsVip() {
            return IsVip;
        }

        public void setIsVip(boolean IsVip) {
            this.IsVip = IsVip;
        }
    }
}