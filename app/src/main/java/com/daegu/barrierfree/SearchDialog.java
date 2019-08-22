package com.daegu.barrierfree;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDialog {

    private Context context = null;
    private EditText edtSearch = null;
    private TextView tvRecent1 = null;
    private TextView tvRecent2 = null;
    private TextView tvRecent3 = null;

    public SearchDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {
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

        setRecent();

        edtSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    int recent_count = preferences.getInt("recent_count", -1);

                    if(recent_count != -1) {
                        editor.putString("r" + recent_count, textView.getText().toString());

                        editor.putInt("recent_count", (recent_count%3) + 1);
                        editor.commit();
                    }

                    Toast.makeText(context, "검색중이란말이야~~~~~", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
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
