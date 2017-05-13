package com.wuwo.im.bean;

/**
 * Created by wmy on 2016/11/13.
 */
public class CharacterPopInfo {


    /**
     * Title :
     * Text :
     * Id;
     * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/dispositionmanualdialog/bc7dfa58-40c1-40b4-8d30-176e74e2c337.jpg
     * NickName
     */

    private String Title;
    private String Text;
    private String PhotoUrl;
    private String Id;
    private String Content;
    private String HeroPhotoUrl;
    private String Detail;
    private String NickName;

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public String getHeroPhotoUrl() {
        return HeroPhotoUrl;
    }

    public void setHeroPhotoUrl(String heroPhotoUrl) {
        HeroPhotoUrl = heroPhotoUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
