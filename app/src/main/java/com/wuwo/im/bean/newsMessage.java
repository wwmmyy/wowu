package com.wuwo.im.bean;

import java.util.Date;
import java.util.List;

/**
 * 表示最新新闻,通知的类
* @类名: newsMessage 
* @描述: TODO 
* @作者: 王明远 
* @日期: 2015-3-26 上午10:55:24 
* @修改人: 
 * @修改时间: 
 * @修改内容:
 * @版本: V1.0
 * @版权:Copyright ©  All rights reserved.
 */
public class newsMessage {





    private String id;
    private String title;
    private String type;
    private Date time;
    private String content;
    private List<imagePath> image;
    private String infoUrl;
    private String browse;
    private String comment;
    
    private String  category;
    
    private String time0;


    private String meetingstate;
    private String meetingtitle;
    private String meetingplace;
    private String starttime;
    private String endtime;
    private String meetingintroduce;
    private String presenterid;
    private String userName;
    
//  为展示首页的通知公示，批后管理增加的字段
    private String  creater;
    private boolean  IsRead;
    private String  isweb;
    private String  projectId;
    private String  isLight;
    private String  widgetsLabel;
    private String  showurl;
     
    
    
    
    
    

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public boolean isIsRead() {
        return IsRead;
    }

    public void setIsRead(boolean isRead) {
        IsRead = isRead;
    }

    public String getIsweb() {
        return isweb;
    }

    public void setIsweb(String isweb) {
        this.isweb = isweb;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getIsLight() {
        return isLight;
    }

    public void setIsLight(String isLight) {
        this.isLight = isLight;
    }

    public String getWidgetsLabel() {
        return widgetsLabel;
    }

    public void setWidgetsLabel(String widgetsLabel) {
        this.widgetsLabel = widgetsLabel;
    }

    public String getShowurl() {
        return showurl;
    }

    public void setShowurl(String showurl) {
        this.showurl = showurl;
    }

    public String getTime0() {
        return time0;
    }

    public void setTime0(String time0) {
        this.time0 = time0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

 

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<imagePath> getImage() {
        return image;
    }

    public void setImage(List<imagePath> image) {
        this.image = image;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getBrowse() {
        return browse;
    }

    public void setBrowse(String browse) {
        this.browse = browse;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMeetingstate() {
        return meetingstate;
    }

    public void setMeetingstate(String meetingstate) {
        this.meetingstate = meetingstate;
    }

    public String getMeetingtitle() {
        return meetingtitle;
    }

    public void setMeetingtitle(String meetingtitle) {
        this.meetingtitle = meetingtitle;
    }

    public String getMeetingplace() {
        return meetingplace;
    }

    public void setMeetingplace(String meetingplace) {
        this.meetingplace = meetingplace;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getMeetingintroduce() {
        return meetingintroduce;
    }

    public void setMeetingintroduce(String meetingintroduce) {
        this.meetingintroduce = meetingintroduce;
    }

    public String getPresenterid() {
        return presenterid;
    }

    public void setPresenterid(String presenterid) {
        this.presenterid = presenterid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isRead() {
        return IsRead;
    }

    public void setRead(boolean read) {
        IsRead = read;
    }
}
