package com.daegu.barrierfree;

public class BarrierDO {

    public String businessName;
    public String tel;
    public String fax;
    public String address;
    public String opTime;
    public String closedDay;
    public String basicInfo;
    public String latitude;
    public String longitude;
    public String category;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
