package com.daegu.barrierfree;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naver.maps.map.overlay.InfoWindow;

import java.util.ArrayList;

public class FavoriteAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<FavoriteItem> list = new ArrayList<>();
    private ICallBack callBack = null;
    private Dialog dialog = null;

    public FavoriteAdapter(ICallBack callBack, Dialog dialog) {
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

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.favorite_listview, viewGroup, false);
        }

        TextView tvFavoriteListName = (TextView)view.findViewById(R.id.tvFavoriteListName);
        TextView tvFavoriteListTel = (TextView)view.findViewById(R.id.tvFavoriteListTel);
        TextView tvFavoriteListDistance = (TextView)view.findViewById(R.id.tvFavoriteListDistance);
        LinearLayout llSearchlv = (LinearLayout)view.findViewById(R.id.llFavoritelv);

        final FavoriteItem item = list.get(i);

        tvFavoriteListName.setText("" + item.getName());

        if(!item.getTel().equals("")) {
            tvFavoriteListTel.setText("" + item.getTel());
        }

        tvFavoriteListDistance.setText(item.getDistance() + "m");

        llSearchlv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final InfoWindow infoWindow = new InfoWindow();
                callBack.goSelect(item.getLat(), item.getLon());

                dialog.dismiss();
            }
        });

        return view;
    }

    public void addItem(String name, String tel, double lat, double lon) {
        FavoriteItem item = new FavoriteItem();

        item.setName(name);
        item.setTel(tel);
        item.setLat(lat);
        item.setLon(lon);

        list.add(item);
    }
}
