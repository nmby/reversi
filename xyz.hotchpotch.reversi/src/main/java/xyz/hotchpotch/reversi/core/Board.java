package xyz.hotchpotch.reversi.core;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * リバーシ盤を表します。<br>
 * 
 * @author nmby
 */
public interface Board {
    
    // [static members] ********************************************************
    
    /**
     * ゲーム開始時状態のリバーシ盤を返します。<br>
     * 
     * @return ゲーム開始時状態のリバーシ盤
     */
    public static Board initBoard() {
        return new FunctionalBoard();
    }
    
    /**
     * 2つのリバーシ盤が等価か否かを返します。
     * ここで言う等価であるとは、石の配置が同じであることを意味します。<br>
     * 
     * @param board1 リバーシ盤1
     * @param board2 リバーシ盤2
     * @return 2つのリバーシ盤が等価の場合は {@code true}
     * @throws NullPointerException {@code board1}, {@code board2} のいずれかが {@code null} の場合
     */
    public static boolean equals(Board board1, Board board2) {
        Objects.requireNonNull(board1, "board1");
        Objects.requireNonNull(board2, "board2");
        
        return board1 == board2
                || Point.stream().allMatch(p -> board1.colorAt(p) == board2.colorAt(p));
    }
    
    /**
     * 指定されたリバーシ盤のハッシュ値を返します。<br>
     * 
     * @param board リバーシ盤
     * @return 指定されたリバーシ盤のハッシュ値
     * @throws NullPointerException {@code board} が {@code null} の場合
     */
    public static int hashCode(Board board) {
        Objects.requireNonNull(board, "board");
        
        return boardToMap(board).hashCode();
    }
    
    /**
     * 指定されたリバーシ盤と同じ内容を保持するマップを返します。<br>
     * 
     * @param board リバーシ盤
     * @return 指定されたリバーシ盤と同じ内容を保持するマップ
     * @throws NullPointerException {@code board} が {@code null} の場合
     */
    public static Map<Point, Color> boardToMap(Board board) {
        Objects.requireNonNull(board, "board");
        
        return Point.stream()
                .filter(p -> board.colorAt(p) != null)
                .collect(Collectors.toMap(
                        Function.identity(),
                        board::colorAt));
    }
    
    /**
     * 指定されたリバーシ盤の文字列表現（複数行）を返します。<br>
     * 
     * @param board リバーシ盤
     * @return 指定されたリバーシ盤の文字列表現（複数行）
     * @throws NullPointerException {@code board} が {@code null} の場合
     */
    public static String toString(Board board) {
        Objects.requireNonNull(board, "board");
        
        String BR = System.lineSeparator();
        StringBuilder str = new StringBuilder("  ");
        
        for (int j = 0; j < Point.WIDTH; j++) {
            str.append((char) ('a' + j)).append(" ");
        }
        str.append(BR);
        
        for (int i = 0; i < Point.HEIGHT; i++) {
            str.append(i + 1).append(" ");
            
            for (int j = 0; j < Point.WIDTH; j++) {
                Color c = board.colorAt(Point.of(i, j));
                str.append(c == null ? "・" : c);
            }
            str.append(BR);
        }
        
        return str.toString();
    }
    
    /**
     * 指定されたリバーシ盤の文字列表現（単一行）を返します。<br>
     * 
     * @param board リバーシ盤
     * @return 指定されたリバーシ盤の文字列表現（単一行）
     * @throws NullPointerException {@code board} が {@code null} の場合
     */
    public static String toStringInline(Board board) {
        Objects.requireNonNull(board, "board");
        
        return Point.stream()
                .map(board::colorAt)
                .map(c -> c == null ? "・" : c.toString())
                .collect(Collectors.joining());
    }
    
    // [instance members] ******************************************************
    
    /**
     * このリバーシ盤上の指定された位置の石の色を返します。<br>
     * 
     * @param point リバーシ盤上の位置
     * @return 指定された位置の石の色（石が置かれていない場合は {@code null}）
     */
    Color colorAt(Point point);
    
    /**
     * このリバーシ盤上の黒白それぞれの石の数を返します。<br>
     * 
     * @return 黒白それぞれの石の数
     */
    default Map<Color, Integer> counts() {
        int black = (int) Point.stream().filter(p -> colorAt(p) == Color.BLACK).count();
        int white = (int) Point.stream().filter(p -> colorAt(p) == Color.WHITE).count();
        
        return Map.of(
                Color.BLACK, black,
                Color.WHITE, white);
    }
    
    /**
     * このリバーシ盤に指定された手を適用して得られるリバーシ盤を返します。<br>
     * 
     * @param move 適用する手
     * @return 新たなリバーシ盤
     */
    Board getApplied(Move move);
}
