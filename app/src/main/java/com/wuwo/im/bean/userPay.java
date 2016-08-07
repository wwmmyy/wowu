package com.wuwo.im.bean;

/**
*desc 用来表示用户VIP类型价格信息
*@author 王明远
*@日期： 2016/8/6 12:14
 */

public class userPay {
//    [{"Name":"一个月","Price":0.01,"Type":1},{"Name":"3个月","Price":0.01,"Type":2},{"Name":"6个月","Price":0.01,"Type":3},{"Name":"一年","Price":0.01,"Type":4}]
    /**
     * Name : 一个月
     * Price : 0.01
     * Type : 1
     */

    private String Name;
    private String Price;
    private int Type;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }
}