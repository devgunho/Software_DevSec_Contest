package com.daegu.barrierfree;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchDialog {

    private Context context = null;
    private EditText edtSearch = null;
    private TextView tvRecent1 = null;
    private TextView tvRecent2 = null;
    private TextView tvRecent3 = null;
    private LinearLayout llRecentBefore = null;
    private LinearLayout llRecentAfter = null;
    private ListView lvSearch = null;
    private ArrayList<BarrierDO> lists = new ArrayList<>(); // basic
    private Location locations = null;

    public SearchDialog(Context context) {
        this.context = context;
    }

    public void showDialog(ArrayList<BarrierDO> list, Location location) {
        lists = list;
        locations = location;

        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.search_dialog);

        dialog.show();

        final SharedPreferences preferences = context.getSharedPreferences("recent", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor  = preferences.edit();

        if(preferences.getInt("recent_count", -1) == -1) {
            editor.putInt("recent_count", 1);
            editor.commit();
        }

        edtSearch = (EditText)dialog.findViewById(R.id.edtSearch);
        tvRecent1 = (TextView)dialog.findViewById(R.id.tvRecent1);
        tvRecent2 = (TextView)dialog.findViewById(R.id.tvRecent2);
        tvRecent3 = (TextView)dialog.findViewById(R.id.tvRecent3);
        llRecentBefore = (LinearLayout)dialog.findViewById(R.id.llRecentBefore);
        llRecentAfter = (LinearLayout)dialog.findViewById(R.id.llRecentAfter);
        lvSearch = (ListView)dialog.findViewById(R.id.lvSearch);

        setRecent();

        edtSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    String keyword = textView.getText().toString();
                    int recent_count = preferences.getInt("recent_count", -1);

                    if(recent_count != -1) {
                        editor.putString("r" + recent_count, keyword);

                        editor.putInt("recent_count", (recent_count%3) + 1);
                        editor.commit();
                    }

                    SearchAdapter adapter = new SearchAdapter();

                    //검색 이후에는 llRecentBefore을 gone처리하고, llRecentAfter를 visible처리 (리스트뷰)(거리순)
                    for(int c = 0; c<lists.size(); c++) {
                        // 키워드랑 맞다면 리스트뷰 추가
                        BarrierDO barrier = lists.get(c);
                        if(barrier.getCategory().contains(keyword)) {
                            Location dest = new Location("destination");
                            dest.setLatitude(Double.parseDouble(barrier.getLatitude()));
                            dest.setLongitude(Double.parseDouble(barrier.getLongitude()));

                            int distance = (int)locations.distanceTo(dest);

                            adapter.addItem(barrier.getBusinessName(), barrier.getTel(), distance);
                        }
                    }

                    lvSearch.setAdapter(adapter);

                    llRecentBefore.setVisibility(View.GONE);
                    llRecentAfter.setVisibility(View.VISIBLE);

                    //dialog.dismiss();
                    return true;
                }

                return false;
            }
        });
    }

    private void setRecent() {
        SharedPreferences preferences = context.getSharedPreferences("recent", Context.MODE_PRIVATE);

        tvRecent1.setText(preferences.getString("r1", "최근 검색어 없음"));
        tvRecent2.setText(preferences.getString("r2", "최근 검색어 없음"));
        tvRecent3.setText(preferences.getString("r3", "최근 검색어 없음"));
    }
}
