package com.wuwo.im.bean;

/**
 * Created by wmy on 2016/11/19.
 */
public class CharacterTongLei {


    /**
     * NickName : ESTJ
     * HeroName :
     * HeroPhotoId : e7211e46-40b7-4f2c-9c00-82226a18ffcb
     * Detail :
     * Id
     * TempPhotoName : null
     * HeroPhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/dispositionnickname/d4fc3fbd-95ca-43c5-bee2-874e435da9fa.jpg
     */

    private String NickName;
    private String HeroName;
    private String HeroPhotoId;
    private String Id;
    private String Detail;
    private Object TempPhotoName;
    private String HeroPhotoUrl;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getHeroName() {
        return HeroName;
    }

    public void setHeroName(String HeroName) {
        this.HeroName = HeroName;
    }

    public String getHeroPhotoId() {
        return HeroPhotoId;
    }

    public void setHeroPhotoId(String HeroPhotoId) {
        this.HeroPhotoId = HeroPhotoId;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String Detail) {
        this.Detail = Detail;
    }

    public Object getTempPhotoName() {
        return TempPhotoName;
    }

    public void setTempPhotoName(Object TempPhotoName) {
        this.TempPhotoName = TempPhotoName;
    }

    public String getHeroPhotoUrl() {
        return HeroPhotoUrl;
    }

    public void setHeroPhotoUrl(String HeroPhotoUrl) {
        this.HeroPhotoUrl = HeroPhotoUrl;
    }
}
