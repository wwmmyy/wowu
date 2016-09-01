package com.wuwo.im.bean;

/**
*@author 王明远
*@日期： 2016/8/27 14:21
*@版权: 人物性格
*/

public class Characters {


    /**
     * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/d421f060-f928-4568-b7ac-81869d6c51f1.png
     * Celebrity : 撒切尔
     * CelebrityDescription : 第49任英国首相
     * Name : ESTJ
     * Title : 监督者
     * Score : {"UserId":"637e5acb638f46f5873ec86f0b4b49ce","E":3,"I":2,"S":5,"N":0,"T":4,"F":1,"J":4,"P":1,"PropensityScore":12,"EI_PropensityScore":-5,"SN_PropensityScore":-19,"TF_PropensityScore":-12,"JP_PropensityScore":-14,"PropensityDescription":"轻微","Id":"2944184d-d6b3-4710-b424-b319150bfbff"}
     */

    private String PhotoUrl;
    private String Celebrity;
    private String CelebrityDescription;
    private String Name;
    private String Title;
    /**
     * UserId : 637e5acb638f46f5873ec86f0b4b49ce
     * E : 3
     * I : 2
     * S : 5
     * N : 0
     * T : 4
     * F : 1
     * J : 4
     * P : 1
     * PropensityScore : 12.0
     * EI_PropensityScore : -5.0
     * SN_PropensityScore : -19.0
     * TF_PropensityScore : -12.0
     * JP_PropensityScore : -14.0
     * PropensityDescription : 轻微
     * Id : 2944184d-d6b3-4710-b424-b319150bfbff
     */

    private ScoreBean Score;

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    public String getCelebrity() {
        return Celebrity;
    }

    public void setCelebrity(String Celebrity) {
        this.Celebrity = Celebrity;
    }

    public String getCelebrityDescription() {
        return CelebrityDescription;
    }

    public void setCelebrityDescription(String CelebrityDescription) {
        this.CelebrityDescription = CelebrityDescription;
    }

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

    public ScoreBean getScore() {
        return Score;
    }

    public void setScore(ScoreBean Score) {
        this.Score = Score;
    }

    public static class ScoreBean {
        private String UserId;
        private int E;
        private int I;
        private int S;
        private int N;
        private int T;
        private int F;
        private int J;
        private int P;
        private double PropensityScore;
        private double EI_PropensityScore;
        private double SN_PropensityScore;
        private double TF_PropensityScore;
        private double JP_PropensityScore;
        private String PropensityDescription;
        private String Id;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public int getE() {
            return E;
        }

        public void setE(int E) {
            this.E = E;
        }

        public int getI() {
            return I;
        }

        public void setI(int I) {
            this.I = I;
        }

        public int getS() {
            return S;
        }

        public void setS(int S) {
            this.S = S;
        }

        public int getN() {
            return N;
        }

        public void setN(int N) {
            this.N = N;
        }

        public int getT() {
            return T;
        }

        public void setT(int T) {
            this.T = T;
        }

        public int getF() {
            return F;
        }

        public void setF(int F) {
            this.F = F;
        }

        public int getJ() {
            return J;
        }

        public void setJ(int J) {
            this.J = J;
        }

        public int getP() {
            return P;
        }

        public void setP(int P) {
            this.P = P;
        }

        public double getPropensityScore() {
            return PropensityScore;
        }

        public void setPropensityScore(double PropensityScore) {
            this.PropensityScore = PropensityScore;
        }

        public double getEI_PropensityScore() {
            return EI_PropensityScore;
        }

        public void setEI_PropensityScore(double EI_PropensityScore) {
            this.EI_PropensityScore = EI_PropensityScore;
        }

        public double getSN_PropensityScore() {
            return SN_PropensityScore;
        }

        public void setSN_PropensityScore(double SN_PropensityScore) {
            this.SN_PropensityScore = SN_PropensityScore;
        }

        public double getTF_PropensityScore() {
            return TF_PropensityScore;
        }

        public void setTF_PropensityScore(double TF_PropensityScore) {
            this.TF_PropensityScore = TF_PropensityScore;
        }

        public double getJP_PropensityScore() {
            return JP_PropensityScore;
        }

        public void setJP_PropensityScore(double JP_PropensityScore) {
            this.JP_PropensityScore = JP_PropensityScore;
        }

        public String getPropensityDescription() {
            return PropensityDescription;
        }

        public void setPropensityDescription(String PropensityDescription) {
            this.PropensityDescription = PropensityDescription;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }
    }
}