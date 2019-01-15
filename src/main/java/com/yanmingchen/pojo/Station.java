package com.yanmingchen.pojo;

public class Station {

    private String stationName; //车站名称
    private String stationCode; //车站码
    private String pinyin;      //拼音
    private String suoxie;      //拼音缩写
    private String no;       //编号

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSuoxie() {
        return suoxie;
    }

    public void setSuoxie(String suoxie) {
        this.suoxie = suoxie;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationName='" + stationName + '\'' +
                ", stationCode='" + stationCode + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", suoxie='" + suoxie + '\'' +
                ", no='" + no + '\'' +
                '}';
    }
}
