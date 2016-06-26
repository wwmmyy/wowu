package com.wuwo.im.bean;

/**
*desc
*@author 王明远
*@日期： 2016/6/25 15:06
*@版权:Copyright    All rights reserved.
*/

public class UserCharacter {


    /**
     * Name : ISFJ
     * Title : 保护者
     * Description : SJ护卫者
     * Qualities : null
     * DisplayIndex : 16
     * Id : 6951e3af-2660-42de-8b96-e00a78e73c87
     */

    private String Name;
    private String Title;
    private String Description;
    private Object Qualities;
    private int DisplayIndex;
    private String Id;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Object getQualities() {
        return Qualities;
    }

    public void setQualities(Object Qualities) {
        this.Qualities = Qualities;
    }

    public int getDisplayIndex() {
        return DisplayIndex;
    }

    public void setDisplayIndex(int DisplayIndex) {
        this.DisplayIndex = DisplayIndex;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }
}