package com.zigatronz.minesweeper;

import android.view.View;
import android.widget.*;

public class Board {

    private LinearLayout boardUI;
    private int boardUI_width;
    private int boardUI_height;


    public int width;
    public int height;
    public Tile[][] board;
    public int mine_count;
    public int revealed_count;

    public boolean first_click;
    public boolean isLost;
    public boolean isWin;

    Board (LinearLayout boardUI, int width, int height) {
        this.boardUI = boardUI;
        this.width = width;
        this.height = height;

        isLost = false;
        isWin = false;
        mine_count = 0;
        revealed_count = 0;
        first_click = true;
    }

    public void GenerateCleanBoard() {
        final int w = width;
        final int h = height;
        final int tile_margin = 2;
        board = new Tile[w][h];

        isLost = false;
        isWin = false;
        mine_count = 0;
        revealed_count = 0;
        first_click = true;

        // initialize all tiles
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                board[x][y] = new Tile(this, x, y);
                board[x][y].margin = tile_margin;
            }
        }

        // set tile adjacent pointers
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (y - 1 >= 0 && x - 1 >= 0) board[y][x].adjacent_TL = board[y - 1][x - 1];
                if (y - 1 >= 0              ) board[y][x].adjacent_TC = board[y - 1][x    ];
                if (y - 1 >= 0 && x + 1 <  w) board[y][x].adjacent_TR = board[y - 1][x + 1];

                if (x - 1 >= 0              ) board[y][x].adjacent_ML = board[y    ][x - 1];
                if (x + 1 <  w              ) board[y][x].adjacent_MR = board[y    ][x + 1];

                if (y + 1 <  h && x - 1 >= 0) board[y][x].adjacent_BL = board[y + 1][x - 1];
                if (y + 1 <  h              ) board[y][x].adjacent_BC = board[y + 1][x    ];
                if (y + 1 <  h && x + 1 <  w) board[y][x].adjacent_BR = board[y + 1][x + 1];
            }
        }
    }

    public void GenerateBoardView() {
        // clear all tiles from board
        boardUI.removeAllViews();

        // board sizing
        int board_padding = 10;
        View parent = (View) boardUI.getParent();
        boardUI_width = parent.getWidth();
        boardUI_height = parent.getWidth();
        boardUI.getLayoutParams().width = boardUI_width;
        boardUI.getLayoutParams().height = boardUI_height;
        boardUI.setPadding(board_padding, board_padding, board_padding, board_padding);

        // cell size
        int tile_margin = 2;
        int total_cell_width = boardUI_width - ( ( ( board_padding * 2) + ( tile_margin * width * 2 ) ) );
        int total_cell_height = boardUI_height - ( ( ( board_padding * 2) + ( tile_margin * height * 2 ) ) );
        int cell_width = total_cell_width / width;
        int cell_height = total_cell_height / height;

        // add row by row
        for (int y = 0; y < height; y++) {
            // add row layout
            LinearLayout row_layout = new LinearLayout(boardUI.getContext());
            row_layout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            row_layout.setLayoutParams(rowParams);
            boardUI.addView(row_layout);

            // add cell by cell
            for (int x = 0; x < width; x++) {
                int screen_width = boardUI.getWidth();
                row_layout.addView(
                        board[x][y].CreateButton( row_layout.getContext(), cell_width, cell_height, tile_margin)
                );
            }
        }
    }

    public void PlaceMinesRandomly(int total_mines) {
        PlaceMinesRandomly(total_mines, new Tile[0]);
    }

    public void PlaceMinesRandomly(int total_mines, Tile[] avoid_tile) {
        int mine_placed = 0;
        while (mine_placed < total_mines) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            // check it is not already mine
            if (board[x][y].isMine) continue;
            // avoid putting mine on avoid_tile
            boolean tile_is_avoid = false;
            for (int i = 0; i < avoid_tile.length; i++) {
                if (avoid_tile[i] == null) continue;
                if (board[x][y] == avoid_tile[i]) tile_is_avoid = true;
            }
            if (tile_is_avoid) continue;
            // place mine
            board[x][y].setMine(true);
            mine_placed++;
        }
    }

    public void FirstClickSafe(int x, int y) {
        Tile[] tiles_to_avoid = {
                board[x][y].adjacent_TL,
                board[x][y].adjacent_TC,
                board[x][y].adjacent_TR,

                board[x][y].adjacent_ML,
                board[x][y],
                board[x][y].adjacent_MR,

                board[x][y].adjacent_BL,
                board[x][y].adjacent_BC,
                board[x][y].adjacent_BR
        };

        for (int i = 0; i < tiles_to_avoid.length; i++) {
            if (tiles_to_avoid[i] == null) continue;
            while (tiles_to_avoid[i].isMine) {
                tiles_to_avoid[i].setMine(false);
                PlaceMinesRandomly(1, tiles_to_avoid);
            }
        }
    }
}
