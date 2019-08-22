package com.daegu.barrierfree;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoriteDialog {

    private Context context = null;
    private ICallBack callBack = null;
    private Location locations = null;
    private ListView lvFavorite = null;

    public FavoriteDialog(Context context, ICallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    public void showDialog(ArrayList<BarrierDO> list, Location location) {
        locations = location;

        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.favorite_dialog);

        dialog.show();

        lvFavorite = (ListView)dialog.findViewById(R.id.lvSearch);

        FavoriteAdapter adapter = new FavoriteAdapter(callBack, dialog);

        for(int c = 0; c<13; c++) {
            BarrierDO barrier = list.get(c);

            adapter.addItem(barrier.getBusinessName(), barrier.getTel(), Double.parseDouble(barrier.getLatitude()), Double.parseDouble(barrier.getLongitude()));
        }

        lvFavorite.setAdapter(adapter);
    }

}
