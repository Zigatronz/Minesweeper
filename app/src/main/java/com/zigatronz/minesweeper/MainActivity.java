package com.zigatronz.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        Button btn_start = findViewById(R.id.menu_btn_start);
        btn_start.setOnClickListener(this::onClickStart);

        Button btn_htp = findViewById(R.id.menu_btn_htp);
        btn_htp.setOnClickListener(this::onClickHTP);

        Button btn_exit = findViewById(R.id.menu_btn_exit);
        btn_exit.setOnClickListener(this::onClickExit);
    }

    private void onClickStart(View view){
        Intent intent = new Intent(this, DifficultySelectionActivity.class);
        startActivity(intent);
    }

    private void onClickHTP(View view){
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }

    private void onClickExit(View view){
        finishAndRemoveTask();
    }
}
