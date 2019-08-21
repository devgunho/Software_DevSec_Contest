package com.daegu.barrierfree;

public class BarrierDO {

    public String businessName;
    public String tel;
    public String fax;
    public String address;
    public String opTime;
    public String closedDay;
    public String basicInfo;
    public String category;    // 26 : "메뉴명"
    public String latitude;    // 28 : "위도"
    public String longitude;   // 27 : "경도"

    public BarrierDO() {

    }


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }

    public String getClosedDay() {
        return closedDay;
    }

    public void setClosedDay(String closedDay) {
        this.closedDay = closedDay;
    }

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "BarrierDO{" +
                "businessName='" + businessName + '\'' +
                ", tel='" + tel + '\'' +
                ", fax='" + fax + '\'' +
                ", address='" + address + '\'' +
                ", opTime='" + opTime + '\'' +
                ", closedDay='" + closedDay + '\'' +
                ", basicInfo='" + basicInfo + '\'' +
                ", category='" + category + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
