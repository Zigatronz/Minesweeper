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

    Tile (Board board, int posX, int posY){
        isMine = false;
        isRevealed = false;
        isFlagged = false;
        value = 0;
        this.board = board;
        this.posX = posX;
        this.posY = posY;
    }

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
                button.setTextColor(number_colors[value - 1]);
                color = ContextCompat.getColor(btn_context, number_colors[value - 1]);
            } else {
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

    public void flag() {
        if (isRevealed || board.isLost || board.isWin) return;
        isFlagged = !isFlagged;
        UpdateVisual();
    }

    public void reveal() {
        // return unrelated
        if (isRevealed || isFlagged || board.isLost || board.isWin) return;
        // first click safe
        if (board.first_click) {
            board.first_click = false;
            board.FirstClickSafe(posX, posY);
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
        if (isMine) board.isLost = true;
        board.revealed_count++;
        if (board.revealed_count == board.width * board.height - board.mine_count) board.isWin = true;
        UpdateVisual();
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
