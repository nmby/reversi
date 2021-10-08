package xyz.hotchpotch.reversi.core;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * リバーシのルールに関わる関数を集めたユーティリティクラスです。<br>
 * 
 * @author nmby
 */
public class Rule {
    
    // [static members] ********************************************************
    
    /**
     * 指定されたリバーシ盤がゲーム継続中かを返します。<br>
     * 
     * @param board リバーシ盤
     * @return ゲーム継続中の場合は {@code true}
     * @throws NullPointerException {@code board} が {@code null} の場合
     */
    public static boolean isGameOngoing(Board board) {
        Objects.requireNonNull(board, "board");
        
        return canPut(board, Color.BLACK) || canPut(board, Color.WHITE);
    }
    
    /**
     * 指定されたリバーシ盤の勝者の色を返します。
     * 引き分けの場合は {@code null} を返します。<br>
     * 
     * @param board リバーシ盤
     * @return 勝者の色（引き分けの場合は {@code null}）
     * @throws IllegalStateException ゲーム継続中の場合
     */
    public static Color winner(Board board) {
        Objects.requireNonNull(board, "board");
        if (isGameOngoing(board)) {
            throw new IllegalStateException("game is ongoing.");
        }
        
        Map<Color, Integer> counts = board.counts();
        int black = counts.get(Color.BLACK);
        int white = counts.get(Color.WHITE);
        
        return black == white
                ? null
                : black < white ? Color.WHITE : Color.BLACK;
    }
    
    /**
     * 指定されたリバーシ盤に指定された色の石を置ける場所があるかを返します。<br>
     * 
     * @param board リバーシ盤
     * @param color 石の色
     * @return 石を置ける場所がある場合は {@code true}
     * @throws NullPointerException {@code board}, {@code color} のいずれかが {@code null} の場合
     */
    public static boolean canPut(Board board, Color color) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(color, "color");
        
        return Point.stream().anyMatch(p -> canPutAt(board, color, p));
    }
    
    /**
     * 指定されたリバーシ盤の指定された位置に指定された色の石を置けるかを返します。<br>
     * 
     * @param board リバーシ盤
     * @param color 石の色
     * @param point リバーシ盤上の位置
     * @return 石を置ける場合は {@code true}
     * @throws NullPointerException
     *      {@code board}, {@code color}, {@code point} のいずれかが {@code null} の場合
     */
    public static boolean canPutAt(Board board, Color color, Point point) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(color, "color");
        Objects.requireNonNull(point, "point");
        
        if (board.colorAt(point) != null) {
            return false;
        }
        
        return Direction.stream().anyMatch(d -> 0 < numOfReversibles(board, color, point, d));
    }
    
    /**
     * 指定されたリバーシ盤に指定された手を適用できるかを返します。<br>
     * 手を適用できるとは、
     * <ul>
     *   <li>指定された手がパスのとき、指定された色の石を置ける位置が皆無であるか</li>
     *   <li>指定された手がパス以外のとき、指定された色の石を指定された位置に置けるか</li>
     * </ul>
     * を意味します。石の色が手番通りであるかは考慮しません。<br>
     * 
     * @param board リバーシ盤
     * @param move 手
     * @return 手を適用できる場合は {@code true}
     * @throws NullPointerException {@code board}, {@code move} のいずれかが {@code null} の場合
     */
    public static boolean canApply(Board board, Move move) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(move, "move");
        
        return move.point() == null
                ? !canPut(board, move.color())
                : canPutAt(board, move.color(), move.point());
    }
    
    /**
     * 指定されたリバーシ盤に指定された手を適用したときに
     * ひっくり返すことのできる石の位置を返します。<br>
     * 
     * @param board リバーシ盤
     * @param move 適用する手
     * @return ひっくり返すことのできる石の位置
     * @throws NullPointerException {@code board}, {@code move} のいずれかが {@code null} の場合
     * @throws IllegalArgumentException
     *      適用できない手が指定された場合もしくは指定された手がパスの場合
     */
    public static Set<Point> reversibles(Board board, Move move) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(move, "move");
        if (move.point() == null || !canPutAt(board, move.color(), move.point())) {
            throw new IllegalArgumentException("move: %s, board: %s".formatted(move, board));
        }
        
        return Direction.stream()
                .<Point> mapMulti((d, sink) -> {
                    int n = numOfReversibles(board, move.color(), move.point(), d);
                    Point p = move.point();
                    
                    while (0 < n--) {
                        p = p.next(d);
                        sink.accept(p);
                    }
                }).collect(Collectors.toSet());
    }
    
    private static int numOfReversibles(
            Board board,
            Color color,
            Point point,
            Direction direction) {
        
        assert board != null;
        assert color != null;
        assert point != null;
        assert direction != null;
        assert board.colorAt(point) == null;
        
        int n = 0;
        Point p = point;
        
        while (p.hasNext(direction)) {
            p = p.next(direction);
            Color c = board.colorAt(p);
            
            if (c == null) {
                return 0;
            } else if (c == color) {
                return n;
            } else {
                n++;
            }
        }
        return 0;
    }
    
    // [instance members] ******************************************************
    
    private Rule() {
    }
}
