package com.wuwo.im.bean;

import java.util.List;

/**
 * Created by wmy on 2016/11/10.
 */
public class BlackUser {

    /**
     * Data : [{"Id":"f631b5aa-05f0-4982-a71e-07556317e7ff","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/d83263ff-9db2-4b2b-8a36-b7e7d1c95ebbx480.jpg","Disposition":"INTP 建筑师","Name":"木头小柏","UserId":"4929c1304125494bbaca72ed4c99d842","Gender":0},{"Id":"c4188a0f-c6b1-4346-9deb-a3ef678ea7c3","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/53850785-3c9f-4124-8d38-5009866b4eb7x480.jpg","Disposition":"INTJ 策划者","Name":"苏格拉底的猫","UserId":"5285d6d1ab6c4c488ca32bfca8345877","Gender":0},{"Id":"c1e55856-5ba6-4ba8-a16c-358a90c3aa5b","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/a0837188-0613-475d-b5f6-b53f8be80453x480.jpg","Disposition":"ENTJ 霸王","Name":"baobao","UserId":"cf9a2a1b923c400395e83c288e5a33a4","Gender":1},{"Id":"b2ac2918-cf4f-4795-a862-1da0d4987c97","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/a0e92e1c-31ca-4610-b44d-917b034f85b4x480.jpg","Disposition":"INFP 化解者","Name":"慎君","UserId":"d0d5d2a30d764cb99f9ad4440a5d791c","Gender":0},{"Id":"62733c14-24ad-4f70-9b22-a08652b0df0b","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","Disposition":"ISFP 艺术家","Name":"盛世蝼蚁","UserId":"637e5acb638f46f5873ec86f0b4b49ce","Gender":1},{"Id":"4ddca646-bf97-4afe-97ed-4bfb3b002e31","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/416ccae5-3d28-422c-a08c-25a0732016bcx480.jpg","Disposition":"ISTJ 检查者","Name":"Yman","UserId":"035ab55877cf4e92994b27c20d1430eb","Gender":1},{"Id":"43130d2a-e876-45ac-95a6-a5c0cd1a0dc9","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/6efc8ce4-b0c6-4a72-8019-7e1ef4162e93x480.jpg","Disposition":"INFP 化解者","Name":"云隐","UserId":"f8f47536bdfc415ebb6acd1feef344b2","Gender":0},{"Id":"25f881b2-f6cb-4ebe-b5ba-3773372b6136","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/098893c6-655b-4621-83b6-f02595b1ead1x480.jpg","Disposition":"ISTJ 检查者","Name":"Agent_Tom","UserId":"60880f463bb34bd7b703eb4c6c523f11","Gender":0},{"Id":"1f4bf88d-7c36-4264-b8ed-e3668467707a","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/01f5f40f-d236-4198-9796-dcbb9a69e745x480.jpg","Disposition":"ISFJ 照顾者","Name":"花花","UserId":"ca220b05d0b54146aae0d0ea461e856a","Gender":1}]
     * Total : 9
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
         * Id : f631b5aa-05f0-4982-a71e-07556317e7ff
         * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/d83263ff-9db2-4b2b-8a36-b7e7d1c95ebbx480.jpg
         * Disposition : INTP 建筑师
         * Name : 木头小柏
         * UserId : 4929c1304125494bbaca72ed4c99d842
         * Gender : 0
         */

        private String Id;
        private String PhotoUrl;
        private String Disposition;
        private String Name;
        private String UserId;
        private int Gender;

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getPhotoUrl() {
            return PhotoUrl;
        }

        public void setPhotoUrl(String PhotoUrl) {
            this.PhotoUrl = PhotoUrl;
        }

        public String getDisposition() {
            return Disposition;
        }

        public void setDisposition(String Disposition) {
            this.Disposition = Disposition;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public int getGender() {
            return Gender;
        }

        public void setGender(int Gender) {
            this.Gender = Gender;
        }
    }
}