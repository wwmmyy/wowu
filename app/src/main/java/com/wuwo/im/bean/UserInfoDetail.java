package com.wuwo.im.bean;

import java.util.List;

/**
*desc UserInfoDetail
*@author 王明远
*@日期： 2016/7/2 12:10
*@版权:Copyright    All rights reserved.
*/

public class UserInfoDetail {


    /**
     * Id : 637e5acb638f46f5873ec86f0b4b49ce
     * Name : wmy
     * PhoneNumber : 18565398524
     * EnglishName : null
     * Disposition : null
     * EasemobId : e6026dba-35f4-11e6-8ecc-01a33478b711
     * Birthday : null
     * Age : 0
     * Gender : 0
     * Description : null
     * MaritalStatus : 0
     * Job : null
     * Company : null
     * School : null
     * Home : null
     * City : null
     * JobAddress : null
     * LifeAddress : null
     * DailyAddress : null
     * VisitedAttractions : null
     * IsVip : false
     * VipLevel : 0
     * Star : 0
     * UserNumber : 54412332
     * Foucs : false
     * Tag : null
     * CreateTime : 2016-06-19 08:07:42
     * Status : 1
     * Photos : [{"Url":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg","FullUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4e.jpg","Id":"dc3df506-e657-4643-bdbc-71b09e7b1afe","IsIcon":true}]
     * Icon : {"Url":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg","FullUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4e.jpg","Id":"dc3df506-e657-4643-bdbc-71b09e7b1afe","IsIcon":true}
     */

    private String Id;
    private String Name;
    private String PhoneNumber;
    private String EnglishName;
    private String Disposition;
    private String EasemobId;
    private String Birthday;
    private int Age;
    private int Gender;
    private String Description;
    private int MaritalStatus;
    private String Job;
    private String Company;
    private String School;
    private String Home;
    private String City;
    private String JobAddress;
    private String LifeAddress;
    private String DailyAddress;
    private String VisitedAttractions;
    private boolean IsVip;
    private int VipLevel;
    private int Star;
    private String UserNumber;
    private boolean Foucs;
    private String Tag;
    private String CreateTime;
    private int Status;
    /**
     * Url : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg
     * FullUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4e.jpg
     * Id : dc3df506-e657-4643-bdbc-71b09e7b1afe
     * IsIcon : true
     */

    private IconBean Icon;
    /**
     * Url : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg
     * FullUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4e.jpg
     * Id : dc3df506-e657-4643-bdbc-71b09e7b1afe
     * IsIcon : true
     */

    private List<PhotosBean> Photos;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String EnglishName) {
        this.EnglishName = EnglishName;
    }

    public String getDisposition() {
        return Disposition;
    }

    public void setDisposition(String Disposition) {
        this.Disposition = Disposition;
    }

    public String getEasemobId() {
        return EasemobId;
    }

    public void setEasemobId(String EasemobId) {
        this.EasemobId = EasemobId;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String Birthday) {
        this.Birthday = Birthday;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public int getMaritalStatus() {
        return MaritalStatus;
    }

    public void setMaritalStatus(int MaritalStatus) {
        this.MaritalStatus = MaritalStatus;
    }

    public String getJob() {
        return Job;
    }

    public void setJob(String Job) {
        this.Job = Job;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String Company) {
        this.Company = Company;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String School) {
        this.School = School;
    }

    public String getHome() {
        return Home;
    }

    public void setHome(String Home) {
        this.Home = Home;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getJobAddress() {
        return JobAddress;
    }

    public void setJobAddress(String JobAddress) {
        this.JobAddress = JobAddress;
    }

    public String getLifeAddress() {
        return LifeAddress;
    }

    public void setLifeAddress(String LifeAddress) {
        this.LifeAddress = LifeAddress;
    }

    public String getDailyAddress() {
        return DailyAddress;
    }

    public void setDailyAddress(String DailyAddress) {
        this.DailyAddress = DailyAddress;
    }

    public String getVisitedAttractions() {
        return VisitedAttractions;
    }

    public void setVisitedAttractions(String VisitedAttractions) {
        this.VisitedAttractions = VisitedAttractions;
    }

    public boolean isIsVip() {
        return IsVip;
    }

    public void setIsVip(boolean IsVip) {
        this.IsVip = IsVip;
    }

    public int getVipLevel() {
        return VipLevel;
    }

    public void setVipLevel(int VipLevel) {
        this.VipLevel = VipLevel;
    }

    public int getStar() {
        return Star;
    }

    public void setStar(int Star) {
        this.Star = Star;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String UserNumber) {
        this.UserNumber = UserNumber;
    }

    public boolean isFoucs() {
        return Foucs;
    }

    public void setFoucs(boolean Foucs) {
        this.Foucs = Foucs;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String Tag) {
        this.Tag = Tag;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public IconBean getIcon() {
        return Icon;
    }

    public void setIcon(IconBean Icon) {
        this.Icon = Icon;
    }

    public List<PhotosBean> getPhotos() {
        return Photos;
    }

    public void setPhotos(List<PhotosBean> Photos) {
        this.Photos = Photos;
    }

    public static class IconBean {
        private String Url;
        private String FullUrl;
        private String Id;
        private boolean IsIcon;

        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url = Url;
        }

        public String getFullUrl() {
            return FullUrl;
        }

        public void setFullUrl(String FullUrl) {
            this.FullUrl = FullUrl;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public boolean isIsIcon() {
            return IsIcon;
        }

        public void setIsIcon(boolean IsIcon) {
            this.IsIcon = IsIcon;
        }
    }

    public static class PhotosBean {
        private String Url;
        private String FullUrl;
        private String Id;
        private boolean IsIcon;

        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url = Url;
        }

        public String getFullUrl() {
            return FullUrl;
        }

        public void setFullUrl(String FullUrl) {
            this.FullUrl = FullUrl;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public boolean isIsIcon() {
            return IsIcon;
        }

        public void setIsIcon(boolean IsIcon) {
            this.IsIcon = IsIcon;
        }
    }
}