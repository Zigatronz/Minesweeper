package com.zigatronz.minesweeper;

import java.util.ArrayList;
import java.util.List;

public class SolvabilityChecker {
    public static Solver_Board SolveBoard(Solver_Board solver_board) {
        int unrevealed_count = solver_board.get_unrevealed_count();
        int maximum_step = solver_board.board.length * solver_board.board[0].length;
        while (unrevealed_count > 0 && maximum_step > 0) {
            maximum_step --;
            // solve one step
            solver_board = SolveOneStep(solver_board);
            // get unrevealed count
            unrevealed_count = solver_board.get_unrevealed_count();
        }
        return solver_board;
    }

    public static Solver_Board SolveOneStep(Solver_Board solver_board) {
        boolean is_step_done = false;
        int maximum_traverse = solver_board.board.length * solver_board.board[0].length;
        while (!is_step_done && maximum_traverse > 0){
            maximum_traverse --;

            // reveal current pointer
            solver_board.get_current_tile().reveal();
            // mark mine on adjacent tiles
            int tile_val = solver_board.get_current_tile().getValue();
            if (tile_val > 0){
                List<Tile> tile_adjacent_unreveal = solver_board.get_adjacent_unreveal();
                // check if adjacent tiles have mine
                if (tile_val == tile_adjacent_unreveal.size()) {
                    for (Tile cur_tile : tile_adjacent_unreveal) {
                        if (!cur_tile.isFlagged){
                            cur_tile.flag();
                            is_step_done = true;
                        }
                    }
                }
            }
            // reveal safe tiles on adjacent tiles
            {
                List<Tile> tile_adjacent_flag = solver_board.get_adjacent_flag();
                if (tile_val == tile_adjacent_flag.size()) {
                    List<Tile> all_adjacent = solver_board.get_all_adjacent();
                    for (Tile cur_tile : all_adjacent) {
                        if (!cur_tile.isRevealed && !cur_tile.isFlagged){
                            cur_tile.reveal();
                            is_step_done = true;
                        }
                    }
                }
            }
            // traverse / move pointer
            //goto lower visit count
            Tile lower_visit_count = solver_board.get_revealed_adjacent_lower_visit_count();
            if (lower_visit_count == null)
                break;
            solver_board.get_current_tile().visit_count ++;
            solver_board.pointer = new Solver_Board.Pointer(lower_visit_count.posX, lower_visit_count.posY);
        }
        return solver_board;
    }

    public static class Solver_Board {
        public Tile[][] board;
        public Pointer pointer;

        public static class Pointer {
            public int x;
            public int y;

            Pointer(Pointer pointer) {
                this.x = pointer.x;
                this.y = pointer.y;
            }

            Pointer(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        public Tile get_current_tile() {
            return board[pointer.x][pointer.y];
        }

        public int get_unrevealed_count() {
            int count = 0;
            for (int y = 0; y < board[0].length; y++) {
                for (int x = 0; x < board.length; x++) {
                    if (!board[x][y].isRevealed) count++;
                }
            }
            return count;
        }

        public List<Tile> get_adjacent_unreveal() {
            Tile tile = get_current_tile();
            List<Tile> adjacent_unreveal = new ArrayList<>();
            if (tile.adjacent_TL != null) if (!tile.adjacent_TL.isRevealed) adjacent_unreveal.add(tile.adjacent_TL);
            if (tile.adjacent_TC != null) if (!tile.adjacent_TC.isRevealed) adjacent_unreveal.add(tile.adjacent_TC);
            if (tile.adjacent_TR != null) if (!tile.adjacent_TR.isRevealed) adjacent_unreveal.add(tile.adjacent_TR);
            if (tile.adjacent_ML != null) if (!tile.adjacent_ML.isRevealed) adjacent_unreveal.add(tile.adjacent_ML);
            if (tile.adjacent_MR != null) if (!tile.adjacent_MR.isRevealed) adjacent_unreveal.add(tile.adjacent_MR);
            if (tile.adjacent_BL != null) if (!tile.adjacent_BL.isRevealed) adjacent_unreveal.add(tile.adjacent_BL);
            if (tile.adjacent_BC != null) if (!tile.adjacent_BC.isRevealed) adjacent_unreveal.add(tile.adjacent_BC);
            if (tile.adjacent_BR != null) if (!tile.adjacent_BR.isRevealed) adjacent_unreveal.add(tile.adjacent_BR);
            return adjacent_unreveal;
        }

        public List<Tile> get_adjacent_revealed() {
            Tile tile = get_current_tile();
            List<Tile> adjacent_flag = new ArrayList<>();
            if (tile.adjacent_TL != null) if (tile.adjacent_TL.isRevealed)adjacent_flag.add(tile.adjacent_TL);
            if (tile.adjacent_TC != null) if (tile.adjacent_TC.isRevealed)adjacent_flag.add(tile.adjacent_TC);
            if (tile.adjacent_TR != null) if (tile.adjacent_TR.isRevealed)adjacent_flag.add(tile.adjacent_TR);
            if (tile.adjacent_ML != null) if (tile.adjacent_ML.isRevealed)adjacent_flag.add(tile.adjacent_ML);
            if (tile.adjacent_MR != null) if (tile.adjacent_MR.isRevealed)adjacent_flag.add(tile.adjacent_MR);
            if (tile.adjacent_BL != null) if (tile.adjacent_BL.isRevealed)adjacent_flag.add(tile.adjacent_BL);
            if (tile.adjacent_BC != null) if (tile.adjacent_BC.isRevealed)adjacent_flag.add(tile.adjacent_BC);
            if (tile.adjacent_BR != null) if (tile.adjacent_BR.isRevealed)adjacent_flag.add(tile.adjacent_BR);
            return adjacent_flag;
        }

        public List<Tile> get_adjacent_flag() {
            Tile tile = get_current_tile();
            List<Tile> adjacent_flag = new ArrayList<>();
            if (tile.adjacent_TL != null) if (tile.adjacent_TL.isFlagged) adjacent_flag.add(get_current_tile().adjacent_TL);
            if (tile.adjacent_TC != null) if (tile.adjacent_TC.isFlagged) adjacent_flag.add(get_current_tile().adjacent_TC);
            if (tile.adjacent_TR != null) if (tile.adjacent_TR.isFlagged) adjacent_flag.add(get_current_tile().adjacent_TR);
            if (tile.adjacent_ML != null) if (tile.adjacent_ML.isFlagged) adjacent_flag.add(get_current_tile().adjacent_ML);
            if (tile.adjacent_MR != null) if (tile.adjacent_MR.isFlagged) adjacent_flag.add(get_current_tile().adjacent_MR);
            if (tile.adjacent_BL != null) if (tile.adjacent_BL.isFlagged) adjacent_flag.add(get_current_tile().adjacent_BL);
            if (tile.adjacent_BC != null) if (tile.adjacent_BC.isFlagged) adjacent_flag.add(get_current_tile().adjacent_BC);
            if (tile.adjacent_BR != null) if (tile.adjacent_BR.isFlagged) adjacent_flag.add(get_current_tile().adjacent_BR);
            return adjacent_flag;
        }

        public Tile get_revealed_adjacent_lower_visit_count() {
            Tile tile_with_lowest_visit = null;
            int min_visit_count = Integer.MAX_VALUE;

            for (Tile adjacent : get_all_adjacent()) {
                if (adjacent.isRevealed && adjacent.visit_count < min_visit_count) {
                    min_visit_count = adjacent.visit_count;
                    tile_with_lowest_visit = adjacent;
                }
            }

            return tile_with_lowest_visit;
        }

        public List<Tile> get_all_adjacent() {
            Tile tile = get_current_tile();
            List<Tile> adjacent_tiles = new ArrayList<>();
            if (tile.adjacent_TL != null) adjacent_tiles.add(tile.adjacent_TL);
            if (tile.adjacent_TC != null) adjacent_tiles.add(tile.adjacent_TC);
            if (tile.adjacent_TR != null) adjacent_tiles.add(tile.adjacent_TR);
            if (tile.adjacent_ML != null) adjacent_tiles.add(tile.adjacent_ML);
            if (tile.adjacent_MR != null) adjacent_tiles.add(tile.adjacent_MR);
            if (tile.adjacent_BL != null) adjacent_tiles.add(tile.adjacent_BL);
            if (tile.adjacent_BC != null) adjacent_tiles.add(tile.adjacent_BC);
            if (tile.adjacent_BR != null) adjacent_tiles.add(tile.adjacent_BR);
            return adjacent_tiles;
        }
    }
}
