package com.daegu.barrierfree;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDialog {

    private Context context = null;

    public SearchDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.search_dialog);

        dialog.show();

        final EditText edtSearch = (EditText)dialog.findViewById(R.id.edtSearch);
        final TextView tvRecent1 = (TextView)dialog.findViewById(R.id.tvRecent1);
        final TextView tvRecent2 = (TextView)dialog.findViewById(R.id.tvRecent2);
        final TextView tvRecent3 = (TextView)dialog.findViewById(R.id.tvRecent3);

        edtSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(context, "검색중이란말이야~~~~~", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return true;
                }

                return false;
            }
        });
    }
}
