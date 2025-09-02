package com.zigatronz.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class DifficultySelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.difficulty_selection_menu);

        Button btn_back = findViewById(R.id.diff_btn_back);
        btn_back.setOnClickListener(this::onClickBack);

        Button btn_easy = findViewById(R.id.diff_btn_easy);
        btn_easy.setOnClickListener(this::onClickEasy);

        Button btn_intermediate = findViewById(R.id.diff_btn_intermediate);
        btn_intermediate.setOnClickListener(this::onClickIntermediate);
    }

    private void onClickBack(View view) {
        finish();
    }

    private void onClickEasy(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", "easy");
        startActivity(intent);
    }

    private void onClickIntermediate(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", "intermediate");
        startActivity(intent);
    }
}
