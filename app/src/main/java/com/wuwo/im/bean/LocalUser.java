package com.wuwo.im.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
*desc {"Data":[{"UserId":"437ca552-ca12-4542-98e0-b2011399b849","Name":"ran","Disposition":"ESTP创业者","Description":null,"Age":0,"Gender":1,"Distance":"7074.1km","Before":"19小时","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/cd5b98f6-439b-4e8c-be3f-a9cd437a34be.jpg","EasemobId":"296fb1da-338d-11e6-8c1d-87ddb1f2ae6f"},{"UserId":"a3a58155-34e2-4c6c-8201-135b830411dd","Name":"today","Disposition":"ISFP创作者","Description":null,"Age":0,"Gender":0,"Distance":"7074.1km","Before":"10分钟","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/66e1b7e7-ea6c-4635-86ce-d62c43162431.jpg","EasemobId":"d9a27a4a-32cc-11e6-88d3-0bca4779fbf6"}],"Total":4,"PageCount":1}
*@author 王明远
*@日期： 2016/6/18 12:13
*@版权:Copyright    All rights reserved.
*/

public class LocalUser {


    /**
     * Data : [{"UserId":"437ca552-ca12-4542-98e0-b2011399b849","Name":"ran","Disposition":"ESTP创业者","Description":null,"Age":0,"Gender":1,"Distance":"7074.1km","Before":"19小时","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/cd5b98f6-439b-4e8c-be3f-a9cd437a34be.jpg","EasemobId":"296fb1da-338d-11e6-8c1d-87ddb1f2ae6f"},{"UserId":"a3a58155-34e2-4c6c-8201-135b830411dd","Name":"today","Disposition":"ISFP创作者","Description":null,"Age":0,"Gender":0,"Distance":"7074.1km","Before":"10分钟","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/66e1b7e7-ea6c-4635-86ce-d62c43162431.jpg","EasemobId":"d9a27a4a-32cc-11e6-88d3-0bca4779fbf6"}]
     * Total : 4
     * PageCount : 1
     */

    private int Total;
    private int PageCount;
    /**
     * UserId : 437ca552-ca12-4542-98e0-b2011399b849
     * Name : ran
     * Disposition : ESTP创业者
     * Description : null
     * Age : 0
     * Gender : 1
     * Distance : 7074.1km
     * Before : 19小时
     * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/cd5b98f6-439b-4e8c-be3f-a9cd437a34be.jpg
     * EasemobId : 296fb1da-338d-11e6-8c1d-87ddb1f2ae6f
     */

    private ArrayList<DataBean> Data;

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
    public ArrayList<DataBean> getData() {
        return Data;
    }

    public void setData(ArrayList<DataBean> Data) {
        this.Data = Data;
    }

    public   class DataBean  implements Serializable {
        private String UserId;
        private String Name;
        private String Disposition;
        private String Description;
        private int Age;
        private Object Gender;
        private String Distance;
        private String Before;
        private String PhotoUrl;
        private String EasemobId;
        private static final long serialVersionUID = 9060527069391618394L;
        public DataBean(String userId, String name, String disposition, String description, int age, Object gender, String distance, String before, String photoUrl, String easemobId) {
            UserId = userId;
            Name = name;
            Disposition = disposition;
            Description = description;
            Age = age;
            Gender = gender;
            Distance = distance;
            Before = before;
            PhotoUrl = photoUrl;
            EasemobId = easemobId;
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

        public Object getGender() {
            return Gender;
        }

        public void setGender(Object Gender) {
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
    }
}