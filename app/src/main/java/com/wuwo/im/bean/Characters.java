package com.wuwo.im.bean;

/**
*@author 王明远
*@日期： 2016/8/27 14:21
*@版权: 人物性格
*/

public class Characters  {

//    "DispositionId":"3c78a948-10ff-4012-a01f-46a539b9695f","Name":"ENFP","Title":"奋斗者",
    /**
     * PhotoUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/fd3d8302-ba1c-4798-bdf8-d9c29e7c519b.jpg
     * IconUrl : http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/0224bdd1-b880-4691-ab5f-568321e5dd14.jpg
     * Celebrity : 杩堝厠灏斺�㈡澃鍏嬮��
     * CelebrityDescription : 缇庡浗娴佽闊充箰涔嬬帇
     * Name : ISFP
     * Title : 鑹烘湳瀹�
     * Score : {"UserId":"637e5acb638f46f5873ec86f0b4b49ce","E":10,"I":11,"S":15,"N":11,"T":10,"F":14,"J":10,"P":12,"CreateOn":"2016-11-12T22:42:51.393","IsCurrent":true,"DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","QuestionType":2,"PropensityScore":12,"EI_PropensityScore":5,"SN_PropensityScore":-15,"TF_PropensityScore":17,"JP_PropensityScore":9,"PropensityDescription":"杞诲井","CreateOnString":"2016/11/12","Id":"c002ffd2-228d-496d-bd4a-df8b2ff83db2"}
     */

    private String DispositionId;
    private String IconUrl;
    private String Celebrity;
    private String CelebrityDescription;
    private String Name;
    private String Title;
    private ScoreBean Score;
    private String PhotoUrl;

    public String getDispositionId() {
        return DispositionId;
    }

    public void setDispositionId(String dispositionId) {
        DispositionId = dispositionId;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String PhotoUrl) {
        this.PhotoUrl = PhotoUrl;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String IconUrl) {
        this.IconUrl = IconUrl;
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
        /**
         * UserId : 637e5acb638f46f5873ec86f0b4b49ce
         * E : 10
         * I : 11
         * S : 15
         * N : 11
         * T : 10
         * F : 14
         * J : 10
         * P : 12
         * CreateOn : 2016-11-12T22:42:51.393
         * IsCurrent : true
         * DispositionId : d9d1a6a2-0b7e-4063-814e-26beb081a078
         * QuestionType : 2
         * PropensityScore : 12.0
         * EI_PropensityScore : 5.0
         * SN_PropensityScore : -15.0
         * TF_PropensityScore : 17.0
         * JP_PropensityScore : 9.0
         * PropensityDescription : 杞诲井
         * CreateOnString : 2016/11/12
         * Id : c002ffd2-228d-496d-bd4a-df8b2ff83db2
         */

        private String UserId;
        private int E;
        private int I;
        private int S;
        private int N;
        private int T;
        private int F;
        private int J;
        private int P;
        private String CreateOn;
        private boolean IsCurrent;
        private String DispositionId;
        private int QuestionType;
        private double PropensityScore;
        private double EI_PropensityScore;
        private double SN_PropensityScore;
        private double TF_PropensityScore;
        private double JP_PropensityScore;
        private String PropensityDescription;
        private String CreateOnString;
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

        public String getCreateOn() {
            return CreateOn;
        }

        public void setCreateOn(String CreateOn) {
            this.CreateOn = CreateOn;
        }

        public boolean isIsCurrent() {
            return IsCurrent;
        }

        public void setIsCurrent(boolean IsCurrent) {
            this.IsCurrent = IsCurrent;
        }

        public String getDispositionId() {
            return DispositionId;
        }

        public void setDispositionId(String DispositionId) {
            this.DispositionId = DispositionId;
        }

        public int getQuestionType() {
            return QuestionType;
        }

        public void setQuestionType(int QuestionType) {
            this.QuestionType = QuestionType;
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

        public String getCreateOnString() {
            return CreateOnString;
        }

        public void setCreateOnString(String CreateOnString) {
            this.CreateOnString = CreateOnString;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }
    }
}