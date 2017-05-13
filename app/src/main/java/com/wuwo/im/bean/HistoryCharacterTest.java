package com.wuwo.im.bean;

import java.util.List;

/**
 * Created by wmy on 2016/11/11.
 * 历史评测数据
 */
public class HistoryCharacterTest {


    /**
     * Data : [{"Id":"03f74913-8367-46a8-9509-4bfd02724bcd","DispositionName":"ISFP 艺术家","Time":"2016-06-19 08:07:42","PropensityScore":15,"QuestionType":1}]
     * Total : 1
     * PageCount : 1
     * PageIndex : 0
     */

    private int Total;
    private int PageCount;
    private int PageIndex;
    private List<DataBean> Data;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int PageCount) {
        this.PageCount = PageCount;
    }

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int PageIndex) {
        this.PageIndex = PageIndex;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * Id : 03f74913-8367-46a8-9509-4bfd02724bcd
         * DispositionName : ISFP 艺术家
         * Time : 2016-06-19 08:07:42
         * PropensityScore : 15.0
         * QuestionType : 1
         */

        private String Id;
        private String DispositionName;
        private String Time;
        private double PropensityScore;
        private int QuestionType;

        private boolean selectState;

        public boolean isSelectState() {
            return selectState;
        }

        public void setSelectState(boolean selectState) {
            this.selectState = selectState;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getDispositionName() {
            return DispositionName;
        }

        public void setDispositionName(String DispositionName) {
            this.DispositionName = DispositionName;
        }

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }

        public double getPropensityScore() {
            return PropensityScore;
        }

        public void setPropensityScore(double PropensityScore) {
            this.PropensityScore = PropensityScore;
        }

        public int getQuestionType() {
            return QuestionType;
        }

        public void setQuestionType(int QuestionType) {
            this.QuestionType = QuestionType;
        }
    }
}
