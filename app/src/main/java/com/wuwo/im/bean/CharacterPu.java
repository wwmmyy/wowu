package com.wuwo.im.bean;

/**
 * Created by wmy on 2016/11/9.
 * 性格谱
 * [{"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":1,"Position":"优点",
 * "Code":"Fi","FunctionModule":"自我","Age":"6-12岁","FunctionDegree":1,"Consciousness":true,"BlackOrWhite":true,"CreateOn":"2016-10-20T14:37:46.26","Id":"4dbaf797-be31-42b0-8561-1061be540b94"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":2,"Position":"创造点","Code":"Se",
 * "FunctionModule":"自我","Age":"12-20岁","FunctionDegree":1,"Consciousness":true,"BlackOrWhite":true,"CreateOn":"2016-10-20T14:38:08.837","Id":"839bf0f7-6ef3-4017-b6d4-9bdddc91e4b8"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":3,"Position":"激活点","Code":"Ni",
 * "FunctionModule":"超级本我","Age":"20-35岁","FunctionDegree":2,"Consciousness":false,"BlackOrWhite":true,"CreateOn":"2016-10-20T14:42:12.773","Id":"3f6ee92c-0436-481f-a067-fbd9ea0bbf6f"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":4,"Position":"充电点","Code":"Te",
 * "FunctionModule":"超级本我","Age":"35-50岁","FunctionDegree":2,"Consciousness":false,"BlackOrWhite":true,"CreateOn":"2016-10-20T14:42:40.523","Id":"865ab4c2-44e8-4676-aabc-088e470c99ec"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":5,"Position":"盲点","Code":"Fe",
 * "FunctionModule":"本我","Age":"50岁后","FunctionDegree":1,"Consciousness":false,"BlackOrWhite":false,"CreateOn":"2016-10-20T14:43:10.043","Id":"bf1235e0-b414-4944-8080-d6fd65c4b1b6"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":6,"Position":"攻击点","Code":"Si",
 * "FunctionModule":"本我","Age":"50岁后","FunctionDegree":1,"Consciousness":false,"BlackOrWhite":false,"CreateOn":"2016-10-20T14:43:35.4","Id":"1b2d5b0e-dec2-46fc-8c9d-7e98783f3923"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":7,"Position":"痛点","Code":"Ne",
 * "FunctionModule":"超级自我","Age":"50岁后","FunctionDegree":2,"Consciousness":true,"BlackOrWhite":false,"CreateOn":"2016-10-20T14:44:00.103","Id":"6b3d3b08-1770-4b4e-816c-4a26a951b0f9"},
 * {"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","FunctionLevel":8,"Position":"缺点","Code":"Ti",
 * "FunctionModule":"超级自我","Age":"50岁后","FunctionDegree":2,"Consciousness":true,"BlackOrWhite":false,"CreateOn":"2016-10-20T14:44:20.513","Id":"6fc4e0f0-afa4-4c14-802b-5b2533740dde"}];
 */
public class CharacterPu {
    /**
     * DispositionId : d9d1a6a2-0b7e-4063-814e-26beb081a078
     * FunctionLevel : 1
     * Position : 优点
     * Code : Fi
     * FunctionModule : 自我
     * Age : 6-12岁
     * FunctionDegree : 1
     * Consciousness : true
     * BlackOrWhite : true
     * CreateOn : 2016-10-20T14:37:46.26
     * Id : 4dbaf797-be31-42b0-8561-1061be540b94
     */

    private String DispositionId;
    private int FunctionLevel;
    private String Position;
    private String Code;
    private String FunctionModule;
    private String Age;
    private int FunctionDegree;
    private boolean Consciousness;
    private boolean BlackOrWhite;
    private String CreateOn;
    private String Id;

    public String getDispositionId() {
        return DispositionId;
    }

    public void setDispositionId(String DispositionId) {
        this.DispositionId = DispositionId;
    }

    public int getFunctionLevel() {
        return FunctionLevel;
    }

    public void setFunctionLevel(int FunctionLevel) {
        this.FunctionLevel = FunctionLevel;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String Position) {
        this.Position = Position;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getFunctionModule() {
        return FunctionModule;
    }

    public void setFunctionModule(String FunctionModule) {
        this.FunctionModule = FunctionModule;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String Age) {
        this.Age = Age;
    }

    public int getFunctionDegree() {
        return FunctionDegree;
    }

    public void setFunctionDegree(int FunctionDegree) {
        this.FunctionDegree = FunctionDegree;
    }

    public boolean isConsciousness() {
        return Consciousness;
    }

    public void setConsciousness(boolean Consciousness) {
        this.Consciousness = Consciousness;
    }

    public boolean isBlackOrWhite() {
        return BlackOrWhite;
    }

    public void setBlackOrWhite(boolean BlackOrWhite) {
        this.BlackOrWhite = BlackOrWhite;
    }

    public String getCreateOn() {
        return CreateOn;
    }

    public void setCreateOn(String CreateOn) {
        this.CreateOn = CreateOn;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }
}
