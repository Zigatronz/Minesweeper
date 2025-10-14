package com.zigatronz.minesweeper;

import java.util.ArrayList;
import java.util.List;

public class SolvabilityChecker {
    // reusable lists to avoid repeated allocations
    // use final to prevent from assignment, only declare once
    // improve performance
    private static final List<Tile> tile_adjacent_unreveal = new ArrayList<>();
    private static final List<Tile> tile_adjacent_flag = new ArrayList<>();
    private static final List<Tile> all_adjacent = new ArrayList<>();

    public static Solver_Board SolveBoard(Solver_Board solver_board, int total_mines) {
        int unrevealed_count = solver_board.get_unrevealed_count();
        int maximum_step_no_action = 5;
        int current_step_no_action = 0;

        int action_count_previous = solver_board.actionDone;
        while (unrevealed_count > total_mines && current_step_no_action < maximum_step_no_action) {
            // solve one step
            SolveOneStep(solver_board);
            // check action done
            if (action_count_previous == solver_board.actionDone){
                current_step_no_action++;
            } else {
                current_step_no_action = 0;
                action_count_previous = solver_board.actionDone;
            }
            // get unrevealed count
            unrevealed_count = solver_board.get_unrevealed_count();
        }
        return solver_board;
    }

    public static void SolveOneStep(Solver_Board solver_board) {
        int action_count_previous = solver_board.actionDone;
        int maximum_traverse = solver_board.board.length * solver_board.board[0].length;
        maximum_traverse -= (int) (maximum_traverse * 0.2f);    // reduce traverse by 20% for performance
        while (action_count_previous == solver_board.actionDone && maximum_traverse > 0){
            maximum_traverse --;

            // reveal current pointer
            solver_board.get_current_tile().reveal();
            // mark mine on adjacent tiles
            int tile_val = solver_board.get_current_tile().getValue();
            if (tile_val > 0){
                solver_board.get_adjacent_unreveal(tile_adjacent_unreveal);
                // check if adjacent tiles have mine
                if (tile_val == tile_adjacent_unreveal.size()) {
                    for (Tile cur_tile : tile_adjacent_unreveal) {
                        if (!cur_tile.isFlagged){
                            cur_tile.flag();
                            solver_board.actionDone++;
                        }
                    }
                }
            }
            // reveal safe tiles on adjacent tiles
            {
                solver_board.get_adjacent_flag(tile_adjacent_flag);
                if (tile_val == tile_adjacent_flag.size()) {
                    solver_board.get_all_adjacent(all_adjacent);
                    for (Tile cur_tile : all_adjacent) {
                        if (!cur_tile.isRevealed && !cur_tile.isFlagged){
                            cur_tile.reveal();
                            solver_board.actionDone++;
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
    }

    public static class Solver_Board {
        public Tile[][] board;
        public Pointer pointer;
        public int actionDone = 0;

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

        // use final to prevent from assignment, only declare once
        private final List<Tile> adjacent_revealed_list = new ArrayList<>();
        private final List<Tile> all_adjacent_list = new ArrayList<>();

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

        public void get_adjacent_unreveal(List<Tile> list_out) {
            list_out.clear();
            Tile tile = get_current_tile();
            if (tile.adjacent_TL != null) if (!tile.adjacent_TL.isRevealed) list_out.add(tile.adjacent_TL);
            if (tile.adjacent_TC != null) if (!tile.adjacent_TC.isRevealed) list_out.add(tile.adjacent_TC);
            if (tile.adjacent_TR != null) if (!tile.adjacent_TR.isRevealed) list_out.add(tile.adjacent_TR);
            if (tile.adjacent_ML != null) if (!tile.adjacent_ML.isRevealed) list_out.add(tile.adjacent_ML);
            if (tile.adjacent_MR != null) if (!tile.adjacent_MR.isRevealed) list_out.add(tile.adjacent_MR);
            if (tile.adjacent_BL != null) if (!tile.adjacent_BL.isRevealed) list_out.add(tile.adjacent_BL);
            if (tile.adjacent_BC != null) if (!tile.adjacent_BC.isRevealed) list_out.add(tile.adjacent_BC);
            if (tile.adjacent_BR != null) if (!tile.adjacent_BR.isRevealed) list_out.add(tile.adjacent_BR);
        }

        public List<Tile> get_adjacent_revealed() {
            Tile tile = get_current_tile();
            adjacent_revealed_list.clear();
            if (tile.adjacent_TL != null) if (tile.adjacent_TL.isRevealed)adjacent_revealed_list.add(tile.adjacent_TL);
            if (tile.adjacent_TC != null) if (tile.adjacent_TC.isRevealed)adjacent_revealed_list.add(tile.adjacent_TC);
            if (tile.adjacent_TR != null) if (tile.adjacent_TR.isRevealed)adjacent_revealed_list.add(tile.adjacent_TR);
            if (tile.adjacent_ML != null) if (tile.adjacent_ML.isRevealed)adjacent_revealed_list.add(tile.adjacent_ML);
            if (tile.adjacent_MR != null) if (tile.adjacent_MR.isRevealed)adjacent_revealed_list.add(tile.adjacent_MR);
            if (tile.adjacent_BL != null) if (tile.adjacent_BL.isRevealed)adjacent_revealed_list.add(tile.adjacent_BL);
            if (tile.adjacent_BC != null) if (tile.adjacent_BC.isRevealed)adjacent_revealed_list.add(tile.adjacent_BC);
            if (tile.adjacent_BR != null) if (tile.adjacent_BR.isRevealed)adjacent_revealed_list.add(tile.adjacent_BR);
            return adjacent_revealed_list;
        }

        public void get_adjacent_flag(List<Tile> list_out) {
            list_out.clear();
            Tile tile = get_current_tile();
            if (tile.adjacent_TL != null) if (tile.adjacent_TL.isFlagged) list_out.add(get_current_tile().adjacent_TL);
            if (tile.adjacent_TC != null) if (tile.adjacent_TC.isFlagged) list_out.add(get_current_tile().adjacent_TC);
            if (tile.adjacent_TR != null) if (tile.adjacent_TR.isFlagged) list_out.add(get_current_tile().adjacent_TR);
            if (tile.adjacent_ML != null) if (tile.adjacent_ML.isFlagged) list_out.add(get_current_tile().adjacent_ML);
            if (tile.adjacent_MR != null) if (tile.adjacent_MR.isFlagged) list_out.add(get_current_tile().adjacent_MR);
            if (tile.adjacent_BL != null) if (tile.adjacent_BL.isFlagged) list_out.add(get_current_tile().adjacent_BL);
            if (tile.adjacent_BC != null) if (tile.adjacent_BC.isFlagged) list_out.add(get_current_tile().adjacent_BC);
            if (tile.adjacent_BR != null) if (tile.adjacent_BR.isFlagged) list_out.add(get_current_tile().adjacent_BR);
        }

        public Tile get_revealed_adjacent_lower_visit_count() {
            Tile tile_with_lowest_visit = null;
            int min_visit_count = Integer.MAX_VALUE;

            get_all_adjacent(all_adjacent_list);
            for (Tile adjacent : all_adjacent_list) {
                if (adjacent.isRevealed && adjacent.visit_count < min_visit_count) {
                    min_visit_count = adjacent.visit_count;
                    tile_with_lowest_visit = adjacent;
                }
            }
            return tile_with_lowest_visit;
        }

        public void get_all_adjacent(List<Tile> list_out) {
            list_out.clear();
            Tile tile = get_current_tile();
            if (tile.adjacent_TL != null) list_out.add(tile.adjacent_TL);
            if (tile.adjacent_TC != null) list_out.add(tile.adjacent_TC);
            if (tile.adjacent_TR != null) list_out.add(tile.adjacent_TR);
            if (tile.adjacent_ML != null) list_out.add(tile.adjacent_ML);
            if (tile.adjacent_MR != null) list_out.add(tile.adjacent_MR);
            if (tile.adjacent_BL != null) list_out.add(tile.adjacent_BL);
            if (tile.adjacent_BC != null) list_out.add(tile.adjacent_BC);
            if (tile.adjacent_BR != null) list_out.add(tile.adjacent_BR);
        }
    }
}
