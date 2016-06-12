package com.wuwo.im.bean;

/** 
*desc
*@author 王明远
*@日期： 2016/6/11 20:17
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

public class Question {

   /* [{"Title":"你更容易喜欢或倾向哪一个词？"
            ,"DisplayIndex":57,"Dimension":3,"OptionA":"具分析力","LeftScoreA":1,"RightScoreA":0,
            "OptionB":"多愁善感","LeftScoreB":0,"RightScoreB":1,"IsSimple":null,"Id":"00b6d925-1d31-4a5f-b102-0bf7cf3baadb"}]
    */
    private  String Id;
    private  String Title;
    private  String DisplayIndex;
    private  String Dimension;
    private  String LeftScoreA;
    private  String RightScoreA;
    private  String OptionA;
    private  String OptionB;
    private  String LeftScoreB;
    private  String RightScoreB;
    private  boolean IsSimple;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDisplayIndex() {
        return DisplayIndex;
    }

    public void setDisplayIndex(String displayIndex) {
        DisplayIndex = displayIndex;
    }

    public String getDimension() {
        return Dimension;
    }

    public void setDimension(String dimension) {
        Dimension = dimension;
    }

    public String getLeftScoreA() {
        return LeftScoreA;
    }

    public void setLeftScoreA(String leftScoreA) {
        LeftScoreA = leftScoreA;
    }

    public String getRightScoreA() {
        return RightScoreA;
    }

    public void setRightScoreA(String rightScoreA) {
        RightScoreA = rightScoreA;
    }

    public String getOptionB() {
        return OptionB;
    }

    public void setOptionB(String optionB) {
        OptionB = optionB;
    }

    public String getLeftScoreB() {
        return LeftScoreB;
    }

    public void setLeftScoreB(String leftScoreB) {
        LeftScoreB = leftScoreB;
    }

    public String getOptionA() {
        return OptionA;
    }

    public void setOptionA(String optionA) {
        OptionA = optionA;
    }

    public String getRightScoreB() {
        return RightScoreB;
    }

    public void setRightScoreB(String rightScoreB) {
        RightScoreB = rightScoreB;
    }

    public boolean isSimple() {
        return IsSimple;
    }

    public void setSimple(boolean simple) {
        IsSimple = simple;
    }
}