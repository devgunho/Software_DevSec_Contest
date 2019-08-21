/*

0 : "순번"
1 : "업소명"
2 : "연락처"
3 : "팩스"
4 : "주소"
5 : "영업시간"
6 : "휴무일"
7 : "기본정보"
8 : "장애인 편의 시설"
9 : "주출입구"
10 : "장애인전용주차구역"
11 : "출입문 문 폭"
12 : "복도/통로"
13 : "장애인용 엘리베이터"
14 : "장애인용 화장실"
15 : "음식점 좌석/테이블"
16 : "장애인용 관람석"
17 : "접수대 작업대"
18 : "샤워실 탈의실"
19 : "테이블/진열대 등 통로 폭"
20 : "장애인용 객실"
21 : "여객시설"
22 : "비치용품"
23 : "이용가능댓수"
24 : "턱 치수 *삭제된 항목"
25 : "단차 cm"
26 : "메뉴명"
27 : "경도"
28 : "위도"
29 : "다음 경도"
30 : "다음 위도"

*/

package com.daegu.barrierfree;

class BFDataSample {
    //private String num;
    private String businessName;
    private String tel;
    private String fax;
    private String address;
    private String opTime;
    private String closedDay;
    private String basicInfo;
    private String category;    // 26 : "메뉴명"
    private String latitude;    // 28 : "위도"
    private String longitude;   // 27 : "경도"

    /*
    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
    */

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        return "BFDataSample{" +
                //"num='" + num + '\'' +
                "\"businessName\" : \"" + businessName + '\"' +
                ", \"tel\" : \"" + tel + '\"' +
                ", \"fax\" : \"" + fax + '\"' +
                ", \"address\" : \"" + address + '\"' +
                ", \"opTime\" : \"" + opTime + '\"' +
                ", \"closedDay\" : \"" + closedDay + '\"' +
                ", \"basicInfo\" : \"" + basicInfo + '\"' +
                ", \"category\" : \"" + category + '\"' +
                ", \"latitude\" : \"" + latitude + '\"' +
                ", \"longitude\" : \"" + longitude + '\"' +
                '}';
    }
}
