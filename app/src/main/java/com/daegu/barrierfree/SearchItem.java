package com.daegu.barrierfree;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchItem {

    private String name;
    private String tel;
    private int distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
