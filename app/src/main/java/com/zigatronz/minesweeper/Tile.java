package com.zigatronz.minesweeper;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;

import androidx.core.content.ContextCompat;

public class Tile {

    public boolean isMine;
    public boolean isRevealed;
    public boolean isFlagged;
    private int value;
    private Board board;
    public int posX;
    public int posY;

    private Context btn_context;
    public Button button;
    public int margin;

    public Tile adjacent_TL;
    public Tile adjacent_TC;
    public Tile adjacent_TR;

    public Tile adjacent_ML;
    public Tile adjacent_MR;

    public Tile adjacent_BL;
    public Tile adjacent_BC;
    public Tile adjacent_BR;

    public int visit_count = 0; // for solvability check

    private static final int[] number_colors = {
            R.color.tile_num_1,
            R.color.tile_num_2,
            R.color.tile_num_3,
            R.color.tile_num_4,
            R.color.tile_num_5,
            R.color.tile_num_6,
            R.color.tile_num_7,
            R.color.tile_num_8
    };

    Tile(Board board, int posX, int posY){
        isMine = false;
        isRevealed = false;
        isFlagged = false;
        value = 0;
        this.board = board;
        this.posX = posX;
        this.posY = posY;
    }

    // Copy constructor for solvability check
    Tile(Tile tile) {
        posX = tile.posX;
        posY = tile.posY;
        value = tile.value;

        isMine = tile.isMine;
        isRevealed = tile.isRevealed;
        isFlagged = tile.isFlagged;

        visit_count = tile.visit_count;
    }

//    public void copyValue(Tile tile) {
//        if (isFlagged != tile.isFlagged || isRevealed != tile.isRevealed){
//            isFlagged = tile.isFlagged;
//            isRevealed = tile.isRevealed;
//        }
//        visit_count = tile.visit_count;
//        UpdateVisual();
//    }

    public Button CreateButton(Context context, int width, int height, int margin){
        btn_context = context;
        button = new Button(btn_context);

        // visual
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, width - (int) (width * 0.25f));
        UpdateVisual();

        // spacing
        button.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams tile_btn_params = new LinearLayout.LayoutParams(
                width,
                height
        );
        tile_btn_params.setMargins(margin, margin, margin, margin);
        button.setLayoutParams(tile_btn_params);

        // listener
        button.setOnClickListener(this::OnClickTile);
        button.setOnLongClickListener(this::OnHoldTile);

        return button;
    }

    private void OnClickTile(View view){
        reveal();
    }

    private boolean OnHoldTile(View view){
        flag();
        return true;    // tell the lister that the action was handled by the method
    }

    private void UpdateVisual(){
        int color = ContextCompat.getColor(btn_context, R.color.white);
        if (isRevealed)
        {
            button.setBackgroundResource(R.drawable.tile_revealed);
            if (isMine) {
                button.setText("💥");
            } else if (value > 0) {
                button.setText(String.valueOf(value));
//                button.setText(String.valueOf(visit_count));    ///////////////// devvvvvvvvvvvvvv
                button.setTextColor(number_colors[value - 1]);
                color = ContextCompat.getColor(btn_context, number_colors[value - 1]);
            } else {
//                button.setText(String.valueOf(visit_count));    ///////////////// devvvvvvvvvvvvvv
                button.setText("");
            }
        } else {
            button.setBackgroundResource(R.drawable.tile_unreveal);
            if (isFlagged)  button.setText("🚩");
            else            button.setText("");
        }
        button.setTextColor(color);
    }

    public int getValue() {
        return value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    // checkBoard == true; for normal gameplay
    // checkBoard == false; for solvability check
    public void flag() {
        if (board != null)
            if (isRevealed || board.isLost || board.isWin) return;
        else
            if (isRevealed) return;
        isFlagged = !isFlagged;
        if (board != null){
            board.flag_count += isFlagged ? 1 : -1;
            UpdateVisual();
        }
    }

    public void reveal() {
        // return unrelated
        if (board != null){
            if (isRevealed || isFlagged || board.isLost || board.isWin) return;
        } else {
            if (isRevealed || isFlagged) return;
        }
        // set last click
        if (board != null) {
            board.last_click_x = posX;
            board.last_click_y = posY;
        }
        // first click safe
        if (board != null && !board.is_first_click_done) {
//            board.FirstClickSafe(posX, posY);
            boolean canSolve = false;
            int totalMines = board.mine_count;
            while (!canSolve) {
                board.FirstClickSafe(posX, posY);
                canSolve = board.SolvabilityCheck(posX, posY, totalMines);
                if (!canSolve) {
                    // regenerate board
                    board.GenerateCleanBoard();
                    board.GenerateBoardView();
                    board.PlaceMinesRandomly(totalMines);
                }
            }
            board.Stopwatch_Start();
            board.is_first_click_done = true;
        }
        // reveal the tile(s)
        isRevealed = true;
        if (value == 0) {
            if(adjacent_TL != null) adjacent_TL.reveal();
            if(adjacent_TC != null) adjacent_TC.reveal();
            if(adjacent_TR != null) adjacent_TR.reveal();

            if(adjacent_ML != null) adjacent_ML.reveal();
            if(adjacent_MR != null) adjacent_MR.reveal();

            if(adjacent_BL != null) adjacent_BL.reveal();
            if(adjacent_BC != null) adjacent_BC.reveal();
            if(adjacent_BR != null) adjacent_BR.reveal();
        }
        if (board != null) {
            if (isMine) board.setLost();
            board.revealed_count++;
            if (board.revealed_count == board.width * board.height - board.mine_count)
                board.setWin();
            UpdateVisual();
        }
    }

    public void setMine(boolean mine){
        if (isMine && !mine) {
            // remove mine
            if (adjacent_TL != null) adjacent_TL.addValue(-1);
            if (adjacent_TC != null) adjacent_TC.addValue(-1);
            if (adjacent_TR != null) adjacent_TR.addValue(-1);

            if (adjacent_ML != null) adjacent_ML.addValue(-1);
            if (adjacent_MR != null) adjacent_MR.addValue(-1);

            if (adjacent_BL != null) adjacent_BL.addValue(-1);
            if (adjacent_BC != null) adjacent_BC.addValue(-1);
            if (adjacent_BR != null) adjacent_BR.addValue(-1);
            board.mine_count--;
        }
        if (!isMine && mine) {
            // add mine
            if (adjacent_TL != null) adjacent_TL.addValue(1);
            if (adjacent_TC != null) adjacent_TC.addValue(1);
            if (adjacent_TR != null) adjacent_TR.addValue(1);

            if (adjacent_ML != null) adjacent_ML.addValue(1);
            if (adjacent_MR != null) adjacent_MR.addValue(1);

            if (adjacent_BL != null) adjacent_BL.addValue(1);
            if (adjacent_BC != null) adjacent_BC.addValue(1);
            if (adjacent_BR != null) adjacent_BR.addValue(1);
            board.mine_count++;
        }
        isMine = mine;
    }
}
