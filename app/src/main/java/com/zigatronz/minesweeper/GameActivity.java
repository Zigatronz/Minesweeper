package com.zigatronz.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;

public class GameActivity extends AppCompatActivity {

    private String difficulty = "";

    private Board board;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        Intent intent = getIntent();
        try {       // will not accept without difficulty
            if (intent.getStringExtra("difficulty").equals("easy")) {
                difficulty = "easy";
            } else if (intent.getStringExtra("difficulty").equals("intermediate")) {
                difficulty = "intermediate";
            } else {
                Toast.makeText(this, "Invalid Difficulty", Toast.LENGTH_SHORT).show();
                finish(); return;
            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "Invalid Difficulty", Toast.LENGTH_SHORT).show();
            finish(); return;
        }

        Button btn_exit = findViewById(R.id.game_btn_exit);
        btn_exit.setOnClickListener(this::onClickExit);

        Button btn_reset = findViewById(R.id.game_btn_reset);
        btn_reset.setOnClickListener(this::onClickReset);

        View gameView = findViewById(R.id.GameBoard);
        View gameMine = findViewById(R.id.game_text_mine);
        View gameTime = findViewById(R.id.game_text_time);

        View gameOverlay = findViewById(R.id.game_end_overlay);
        View gameOverlay_title = findViewById(R.id.game_end_overlay_title);
        View gameOverlay_subtitle = findViewById(R.id.game_end_overlay_subtitle);

        String text_win_title = getResources().getString(R.string.game_overlay_win_title);
        String text_win_subtitle = getResources().getString(R.string.game_overlay_win_subtitle);
        String text_lose_title = getResources().getString(R.string.game_overlay_lose_title);
        String text_lose_subtitle = getResources().getString(R.string.game_overlay_lose_subtitle);

        if (difficulty.equals("easy")){
            board = new Board((LinearLayout) gameView, (TextView) gameMine, (TextView) gameTime, (LinearLayout) gameOverlay, (TextView) gameOverlay_title, (TextView) gameOverlay_subtitle,
                    text_win_title, text_win_subtitle.replaceAll("\\(DIFFICULTY\\)", "Easy"),
                    text_lose_title, text_lose_subtitle.replaceAll("\\(DIFFICULTY\\)", "Easy"),
                    9, 9);
        } else {
            board = new Board((LinearLayout) gameView, (TextView) gameMine, (TextView) gameTime, (LinearLayout) gameOverlay, (TextView) gameOverlay_title, (TextView) gameOverlay_subtitle,
                    text_win_title, text_win_subtitle.replaceAll("\\(DIFFICULTY\\)", "Intermediate"),
                    text_lose_title, text_lose_subtitle.replaceAll("\\(DIFFICULTY\\)", "Intermediate"),
                    16, 16);
        }

        // create board after display is settled
        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CreateBoard();

                // Remove the listener
                gameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
        gameView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        // DEV | REMOVE THIS LATER
//        Button solve = findViewById(R.id.solve);
//        solve.setOnClickListener(this::solve);
    }

    // DEV | REMOVE THIS LATER
//    private void solve(View view) {
//        try {
//            if (!board.is_first_click_done) {
//
//            } else {
//                SolvabilityChecker.Solver_Board solver_board = board.get_solver_board(board.last_click_x, board.last_click_y);
//                solver_board = SolvabilityChecker.SolveOneStep(solver_board);
//                // find change
//                for (int y = 0; y < board.height; y++) {
//                    for (int x = 0; x < board.width; x++) {
//                        if (board.board[x][y].isFlagged != solver_board.board[x][y].isFlagged) {
//                            solver_board = solver_board;
//                        }
//                        if (board.board[x][y].isRevealed != solver_board.board[x][y].isRevealed) {
//                            solver_board = solver_board;
//                        }
//                    }
//                }
//                board.update_from_solver_board(solver_board);
//            }
//        } catch (Exception e) {
//            throw  new RuntimeException(e);
//        }
//    }

    private void CreateBoard(){
        try {
            board.GenerateCleanBoard();
            board.GenerateBoardView();
            if (difficulty.equals("easy"))
                board.PlaceMinesRandomly(10);
            else
                board.PlaceMinesRandomly(32);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to create board, please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onClickExit(View view) {
        finish();
    }

    private void onClickReset(View view) {
        CreateBoard();
    }
}
