package com.zigatronz.minesweeper;

import android.view.View;
import android.widget.*;

import android.os.Handler;

public class Board {

    private final LinearLayout boardUI;
    private int boardUI_width;
    private int boardUI_height;

    public int width;
    public int height;
    public Tile[][] board;
    public int flag_count;
    public int mine_count;
    public int revealed_count;

    public boolean is_first_click_done;
    public boolean isLost;
    public boolean isWin;

    public int last_click_x;
    public int last_click_y;

    private final LinearLayout overlay;
    private final TextView overlay_title;
    private final TextView overlay_subtitle;
    private final String text_win_title;
    private final String text_win_subtitle;
    private final String text_lose_title;
    private final String text_lose_subtitle;

    private final TextView mine_text;
    private final TextView time_text;
    private int time = 0;
    private int maxTime = 999;
    private final Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    public void UpdateMineText() {
        mine_text.setText("x".concat(String.valueOf(mine_count - flag_count)));
    }

    public void UpdateTimeText() {
        time_text.setText(String.valueOf(time).concat("s"));
    }

    public void Stopwatch_Start() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (time < maxTime)
                    time++;
                UpdateTimeText();
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void Stopwatch_Stop() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    public void Stopwatch_Reset() {
        Stopwatch_Stop();
        time = 0;
        UpdateTimeText();
    }


    public void setWin(){
        Stopwatch_Stop();

        overlay_title.setText(text_win_title);
        overlay_subtitle.setText(text_win_subtitle);
        overlay.setVisibility(View.VISIBLE);

        // flag all mine remaining mine
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = board[x][y];
                if (tile.isMine && !tile.isRevealed && !tile.isFlagged) {
                    tile.flag();
                }
            }
        }

        isWin = true;
    }

    public void setLost(){
        Stopwatch_Stop();

        overlay_title.setText(text_lose_title);
        overlay_subtitle.setText(text_lose_subtitle);
        overlay.setVisibility(View.VISIBLE);

        // update mine tiles visual, so that 💣 is rendered
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = board[x][y];
                if (tile.isMine) {
                    tile.UpdateVisual();
                }
            }
        }

        isLost = true;
    }



    Board(LinearLayout boardUI, TextView mine_text, TextView time_text, LinearLayout overlay, TextView overlay_title, TextView overlay_subtitle,
          String text_win_title, String text_win_subtitle, String text_lose_title, String text_lose_subtitle, int width, int height) {
        this.boardUI = boardUI;
        this.width = width;
        this.height = height;

        this.mine_text = mine_text;
        this.time_text = time_text;

        this.overlay = overlay;
        this.overlay_title = overlay_title;
        this.overlay_subtitle = overlay_subtitle;
        this.text_win_title = text_win_title;
        this.text_win_subtitle = text_win_subtitle;
        this.text_lose_title = text_lose_title;
        this.text_lose_subtitle = text_lose_subtitle;

        isLost = false;
        isWin = false;
        mine_count = 0;
        flag_count = 0;
        revealed_count = 0;
        is_first_click_done = false;

        UpdateTimeText();
    }

    public void GenerateCleanBoard() {
        final int w = width;
        final int h = height;
        final int tile_margin = 2;
        board = new Tile[w][h];

        overlay.setVisibility(View.GONE);
        isLost = false;
        isWin = false;
        mine_count = 0;
        flag_count = 0;
        revealed_count = 0;
        is_first_click_done = false;

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
                if (y - 1 >= 0 && x - 1 >= 0)   board[x][y].adjacent_TL = board[x - 1][y - 1];
                if (y - 1 >= 0)                 board[x][y].adjacent_TC = board[x    ][y - 1];
                if (y - 1 >= 0 && x + 1 < w)    board[x][y].adjacent_TR = board[x + 1][y - 1];

                if (x - 1 >= 0)                 board[x][y].adjacent_ML = board[x - 1][y    ];
                if (x + 1 < w)                  board[x][y].adjacent_MR = board[x + 1][y    ];

                if (y + 1 < h && x - 1 >= 0)    board[x][y].adjacent_BL = board[x - 1][y + 1];
                if (y + 1 < h)                  board[x][y].adjacent_BC = board[x    ][y + 1];
                if (y + 1 < h && x + 1 < w)     board[x][y].adjacent_BR = board[x + 1][y + 1];
            }
        }

        Stopwatch_Reset();
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
        int total_cell_width = boardUI_width - (((board_padding * 2) + (tile_margin * width * 2)));
        int total_cell_height = boardUI_height - (((board_padding * 2) + (tile_margin * height * 2)));
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
                        board[x][y].CreateButton(row_layout.getContext(), cell_width, cell_height, tile_margin)
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
        UpdateMineText();
    }

    //  /////////////////////////////
    //  /// Make first click safe ///
    //  ///         and           ///
    //  /// Check for solvability ///
    //  /////////////////////////////

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

    public boolean SolvabilityCheck(int startX, int startY, int total_mines) {
        SolvabilityChecker.Solver_Board newBoard = get_solver_board(startX, startY);
        newBoard = SolvabilityChecker.SolveBoard(newBoard, total_mines);

        // return is possible or not
        return (newBoard.get_unrevealed_count() == total_mines);
    }


//    public SolvabilityChecker.Solver_Board get_solver_board(int startX, int startY) {
//        if (height == 0 || width == 0) return null;
//
//        SolvabilityChecker.Solver_Board newBoard = new SolvabilityChecker.Solver_Board();
//        newBoard.board = new Tile[height][width];
//
//        // copy tile one by one
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                newBoard.board[x][y] = new Tile(board[x][y]);
//            }
//        }
//
//        newBoard.pointer = new SolvabilityChecker.Solver_Board.Pointer(startX, startY);
//
//        return newBoard;
//    }
    public SolvabilityChecker.Solver_Board get_solver_board(int startX, int startY) {
        if (height == 0 || width == 0) return null;

        SolvabilityChecker.Solver_Board solver_board = new SolvabilityChecker.Solver_Board();
        // Correctly create the new board as [width][height]
        solver_board.board = new Tile[width][height];

        // copy tile one by one (this loop is now correct)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                solver_board.board[x][y] = new Tile(board[x][y]);
            }
        }

        // resolve adjacent
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (y - 1 >= 0 && x - 1 >= 0)           solver_board.board[x][y].adjacent_TL = solver_board.board[x - 1][y - 1];
                if (y - 1 >= 0)                         solver_board.board[x][y].adjacent_TC = solver_board.board[x    ][y - 1];
                if (y - 1 >= 0 && x + 1 < width)        solver_board.board[x][y].adjacent_TR = solver_board.board[x + 1][y - 1];

                if (x - 1 >= 0)                         solver_board.board[x][y].adjacent_ML = solver_board.board[x - 1][y    ];
                if (x + 1 < width)                      solver_board.board[x][y].adjacent_MR = solver_board.board[x + 1][y    ];

                if (y + 1 < height && x - 1 >= 0)       solver_board.board[x][y].adjacent_BL = solver_board.board[x - 1][y + 1];
                if (y + 1 < height)                     solver_board.board[x][y].adjacent_BC = solver_board.board[x    ][y + 1];
                if (y + 1 < height && x + 1 < width)    solver_board.board[x][y].adjacent_BR = solver_board.board[x + 1][y + 1];
            }
        }

        solver_board.pointer = new SolvabilityChecker.Solver_Board.Pointer(startX, startY);

        return solver_board;
    }

//    public void update_from_solver_board(SolvabilityChecker.Solver_Board solver_board) {
//        // copy tile one by one
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                board[x][y].copyValue(solver_board.board[x][y]);
//            }
//        }
//        last_click_x = solver_board.pointer.x;
//        last_click_y = solver_board.pointer.y;
//    }
}
