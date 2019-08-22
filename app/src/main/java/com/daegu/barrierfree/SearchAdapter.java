package com.daegu.barrierfree;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<SearchItem> list = new ArrayList<>();
    private ICallBack callBack = null;
    private Dialog dialog = null;

    public SearchAdapter(ICallBack callBack, Dialog dialog) {
        this.callBack = callBack;
        this.dialog = dialog;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        context = viewGroup.getContext();

        Collections.sort(list, new Comparator<SearchItem>() {
            @Override
            public int compare(SearchItem searchItem, SearchItem t1) {
                int arg1 = searchItem.getDistance();
                int arg2 = t1.getDistance();

                if(arg1 == arg2) return 0;
                else if(arg1 > arg2) return 1;
                else return -1;
            }
        });

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search_listview, viewGroup, false);
        }

        TextView tvSearchListName = (TextView)view.findViewById(R.id.tvSearchListName);
        TextView tvSearchListTel = (TextView)view.findViewById(R.id.tvSearchListTel);
        TextView tvSearchListDistance = (TextView)view.findViewById(R.id.tvSearchListDistance);
        LinearLayout llSearchlv = (LinearLayout)view.findViewById(R.id.llSearchlv);

        final SearchItem item = list.get(i);

        tvSearchListName.setText("" + item.getName());

        if(!item.getTel().equals("")) {
            tvSearchListTel.setText("" + item.getTel());
        }

        tvSearchListDistance.setText(item.getDistance() + "m");

        llSearchlv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.goSelect(item.getLat(), item.getLon());
                dialog.dismiss();
            }
        });

        return view;
    }

    public void addItem(String name, String tel, int distance, double lat, double lon) {
        SearchItem item = new SearchItem();

        item.setName(name);
        item.setTel(tel);
        item.setDistance(distance);
        item.setLat(lat);
        item.setLon(lon);

        list.add(item);

    }
}
