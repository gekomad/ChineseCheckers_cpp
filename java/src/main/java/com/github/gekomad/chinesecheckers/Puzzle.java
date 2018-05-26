package com.github.gekomad.chinesecheckers;

import java.util.*;


public class Puzzle extends ChineseCheckers {

    private int MAX_PIECES;
    private long found;
    private List<Move> moves = new ArrayList<Move>(4);
    private long start;
    private int seconds = 5 * 1000;

    public Puzzle(long terrain, int maxPieces) {
        super(0, terrain);
        this.MAX_PIECES = maxPieces;
        moves.add(Move.UP);
        moves.add(Move.DOWN);
        moves.add(Move.LEFT);
        moves.add(Move.RIGHT);
    }

    static List<Long> shufflePieces(long bits) {
        List<Long> l = new ArrayList<Long>();
        while (bits != 0) {
            long from = BITScanForward(bits);
            l.add(from);
            bits &= ~from;
        }
        Collections.shuffle(l);
        return l;
    }

    FromToCaptured randomMove(long to, List<ChineseCheckers.Move> moves) {
        if (moves.isEmpty()) return null;
        ChineseCheckers.Move n = moves.get(new Random().nextInt(moves.size()));
        switch (n) {
            case RIGHT:
                if ((to >>> 1 & BOARD_to_LEFT) != 0 && (to >>> 2 & BOARD_to_LEFT) != 0)
                    return new FromToCaptured(to >>> 1, to >>> 2, to);
                else {
                    moves.remove(ChineseCheckers.Move.RIGHT);
                    randomMove(to, moves);
                }
                break;
            case UP:
                if ((to << 8 & BOARD_to_DOWN) != 0 && (to << 16 & BOARD_to_DOWN) != 0)
                    return new FromToCaptured(to << 8, to << 16, to);
                else {
                    moves.remove(ChineseCheckers.Move.UP);
                    randomMove(to, moves);
                }
                break;
            case DOWN:
                if ((to >>> 8 & BOARD_to_UP) != 0 && (to >>> 16 & BOARD_to_UP) != 0)
                    return new FromToCaptured(to >>> 8, to >>> 16, to);
                else {
                    moves.remove(Move.DOWN);
                    randomMove(to, moves);
                }
                break;
            case LEFT:
                if ((to << 1 & BOARD_to_RIGHT) != 0 && (to << 2 & BOARD_to_RIGHT) != 0)
                    return new FromToCaptured(to << 1, to << 2, to);
                else {
                    moves.remove(Move.LEFT);
                    randomMove(to, moves);
                }
            default:
                return null;
        }
        return null;
    }

    long puzzle() {
        found = 0;
        start = new Date().getTime();
        for (long m : Puzzle.shufflePieces(TERRAIN)) {
            puzzle(m, 0);
            if (found != 0)
                break;
        }
        if (found != 0) print(found);
        else System.out.println("no solutions");
        return found;
    }

    private void puzzle(long bits, int count) {
        //print(bits);
        if (count > 200 || exceedTime()) return;
        if (popCount(bits) >= MAX_PIECES) {
//            print(bits);
            found = bits;
            return;
        }
        List<Long> ll = shufflePieces(bits);

        for (long l : ll) {
            if (found == 0) {
                List<Move> moves2 = new ArrayList<Move>();
                moves2.addAll(moves);

                FromToCaptured fromTo = randomMove(l, moves2);
                if (fromTo != null) {
                    bits |= fromTo.getFrom();
                    bits |= fromTo.getCaptured();
                    bits &= ~fromTo.getTo();
                    puzzle(bits, count + 1);
                }
            }
        }
    }

    private boolean exceedTime() {
        return ((new Date()).getTime() - start) > seconds;
    }

}
