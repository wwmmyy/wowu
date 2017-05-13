package com.wuwo.im.bean;

import java.util.List;

/**
 * Created by wmy on 2016/11/10.
 */
public class MatchOrVisitedMe {
    /**
     * Data : [{"UserId":"9f4ec37ff675438cb812e86ab6f1b4ea","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/caa6e89d-9f1a-4682-95de-5d3c6eaa3d77x480.jpg","Gender":0,"Name":"卡尔蚂蚁","Disposition":"INFP 化解者","EventTime":"2016-10-29 13:52:10","IsVip":true},{"UserId":"e5eb2a20b6e24712bff151cfb6837d3c","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/bef376f6-9ed9-49d9-a6e7-65a216e5931ax480.jpg","Gender":0,"Name":"不知不觉","Disposition":"INFP 化解者","EventTime":"2016-10-30 13:20:22","IsVip":false},{"UserId":"4d9a428725184bfc88d14c057705b023","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/b15a89ea-1302-4b58-aac8-493c7d860833x480.jpg","Gender":1,"Name":"逆光飞翔","Disposition":"INTJ 策划者","EventTime":"2016-11-01 20:22:13","IsVip":false},{"UserId":"b45ada1464f040aba0897893c4f26ee0","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/3bae545f-ba55-4125-b49a-b2d82f1ea59fx480.jpg","Gender":1,"Name":"爱德华��","Disposition":"INFP 化解者","EventTime":"2016-11-05 11:18:57","IsVip":true},{"UserId":"50c60d1b2d854409a708ab4afc6ec290","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/05090244-b237-4d91-8f9f-f551a0146f0bx480.jpg","Gender":0,"Name":"珂壳可克","Disposition":"ENFJ 教导者","EventTime":"2016-11-10 11:58:55","IsVip":false}]
     * Total : 5
     * PageCount : 1
     * PageIndex : 0
     */

    private int Total;
    private int PageCount;
    private int PageIndex;
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

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int PageIndex) {
        this.PageIndex = PageIndex;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * UserId : 9f4ec37ff675438cb812e86ab6f1b4ea
         * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/caa6e89d-9f1a-4682-95de-5d3c6eaa3d77x480.jpg
         * Gender : 0
         * Name : 卡尔蚂蚁
         * Disposition : INFP 化解者
         * EventTime : 2016-10-29 13:52:10
         * IsVip : true
         */

        private String UserId;
        private String PhotoUrl;
        private int Gender;
        private String Name;
        private String Disposition;
        private String EventTime;
        private boolean IsVip;
        private int VipType;

        public int getVipType() {
            return VipType;
        }

        public void setVipType(int vipType) {
            VipType = vipType;
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

        public String getDisposition() {
            return Disposition;
        }

        public void setDisposition(String Disposition) {
            this.Disposition = Disposition;
        }

        public String getEventTime() {
            return EventTime;
        }

        public void setEventTime(String EventTime) {
            this.EventTime = EventTime;
        }

        public boolean isIsVip() {
            return IsVip;
        }

        public void setIsVip(boolean IsVip) {
            this.IsVip = IsVip;
        }
    }

}
