package com.example.myapplication;

import java.util.List;

public class Forecast {
    String city,adcode,province,reporttime;
    List<Casts> casts;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public List<Casts> getCasts() {
        return casts;
    }

    public void setCasts(List<Casts> casts) {
        this.casts = casts;
    }
    public String getWeatherShow(){
        Casts today = casts.get(0);
        Casts tomorrow = casts.get(1);
        String space = "   ";
        String ret = "";
        ret += "今日天气\n";
        ret += "天气："+today.getDayweather();
        ret += space+ "温度："+today.getDaytemp();
        ret += space+ "风力："+today.getDaypower();
        ret += space+ "风向："+today.getDaywind()+"\n";
        ret += "\n明日天气\n";
        ret += "天气："+tomorrow.getDayweather();
        ret += space+ "温度："+tomorrow.getDaytemp();
        ret += space+ "风力："+tomorrow.getDaypower();
        ret += space+ "风向："+tomorrow.getDaywind();

        return ret;
    }
}
