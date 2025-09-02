package com.zigatronz.minesweeper;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HowToPlayActivity extends AppCompatActivity {

    private int curPage = 1;
    private Drawable[] htp_img;
    private String[] htp_text;

    private Button btn_next;
    private Button btn_previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_play);

        Button btn_back = findViewById(R.id.htp_btn_back);
        btn_back.setOnClickListener(this::OnClickBack);

        htp_img = new Drawable[]{
                ContextCompat.getDrawable(this, R.drawable.htp1),
                ContextCompat.getDrawable(this, R.drawable.htp2),
                ContextCompat.getDrawable(this, R.drawable.htp3),
                ContextCompat.getDrawable(this, R.drawable.htp4),
                ContextCompat.getDrawable(this, R.drawable.htp5)
        };
        htp_text = new String[]{
                getString(R.string.htp_text1),
                getString(R.string.htp_text2),
                getString(R.string.htp_text3),
                getString(R.string.htp_text4),
                getString(R.string.htp_text5)
        };

        btn_next = findViewById(R.id.htp_btn_next);
        btn_next.setOnClickListener(this::onClickNext);

        btn_previous = findViewById(R.id.htp_btn_previous);
        btn_previous.setOnClickListener(this::onClickPrevious);
    }

    private void OnClickBack(View view) {
        finish();
    }

    private void onClickNext(View view) {
        if (curPage >= htp_img.length) return;
        curPage ++;
        this.updateHTPScreen();
    }

    private void onClickPrevious(View view) {
        if (curPage <= 1) return;
        curPage --;
        this.updateHTPScreen();
    }

    private void updateHTPScreen() {
        if (curPage > htp_img.length) curPage = htp_img.length;
        if (curPage < 1) curPage = 1;

        ImageView img = findViewById(R.id.htp_img);
        img.setImageDrawable(htp_img[curPage - 1]);

        TextView text = findViewById(R.id.htp_text_description);
        text.setText(htp_text[curPage - 1]);

        boolean y = curPage >= htp_img.length;
        btn_next.setVisibility((curPage >= htp_img.length) ? View.INVISIBLE : View.VISIBLE);
        boolean x = curPage <= 1;
        btn_previous.setVisibility((curPage <= 1) ? View.INVISIBLE : View.VISIBLE);
    }
}
