package xyz.hotchpotch.reversi.core;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * リバーシ盤上の位置を表す不変クラスです。<br>
 * 同じ位置を表すインスタンスは同一であることが保証されます。<br>
 * 
 * @author nmby
 */
public class Point {
    
    // [static members] ********************************************************
    
    /** 座標平面（リバーシ盤）の高さ */
    public static final int HEIGHT = 8;
    
    /** 座標平面（リバーシ盤）の幅 */
    public static final int WIDTH = 8;
    
    private static final Point[] points = IntStream.range(0, HEIGHT * WIDTH)
            .mapToObj(n -> new Point(n / WIDTH, n % WIDTH))
            .toArray(Point[]::new);
    
    /**
     * 全ての {@link Point} オブジェクトを含む配列を返します。<br>
     * 
     * @return 全ての {@link Point} オブジェクトが格納された配列
     */
    public static Point[] values() {
        return Arrays.copyOf(points, points.length);
    }
    
    /**
     * 全ての {@link Point} オブジェクトを含む直列ストリームを返します。<br>
     * 
     * @return 全ての {@link Point} オブジェクトを含む直列ストリーム
     */
    public static Stream<Point> stream() {
        return Stream.of(points);
    }
    
    /**
     * 指定された位置を表す {@link Point} インスタンスを返します。<br>
     * 
     * @param i 縦座標
     * @param j 横座標
     * @return 指定された位置を表す {@link Point} インスタンス
     * @throws IndexOutOfBoundsException 指定された座標が範囲外の場合
     */
    public static Point of(int i, int j) {
        if (!isValidIdx(i, j)) {
            throw new IndexOutOfBoundsException("(%d, %d)".formatted(i, j));
        }
        return points[idxToOrd(i, j)];
    }
    
    /**
     * 指定された位置を表す {@link Point} インスタンスを返します。<br>
     * 
     * @param pos {@code "a1"}～{@code "h8"} 形式の座標
     * @return 指定された位置を表す {@link Point} インスタンス
     * @throws NullPointerException {@code pos} が {@code "null"} の場合
     * @throws IllegalArgumentException {@code pos} の形式が不正な場合 または 範囲外の場合
     */
    public static Point of(String pos) {
        Objects.requireNonNull(pos, "pos");
        
        int n = posToOrd(pos);
        return points[n];
    }
    
    private static boolean isValidIdx(int i, int j) {
        return 0 <= i && i < HEIGHT && 0 <= j && j < WIDTH;
    }
    
    private static int posToOrd(String pos) {
        assert pos != null;
        
        if (pos.length() != 2) {
            throw new IllegalArgumentException(pos);
        }
        
        int i = pos.charAt(1) - '1';
        int j = pos.charAt(0) - 'a';
        
        if (!isValidIdx(i, j)) {
            throw new IllegalArgumentException(pos);
        }
        
        return idxToOrd(i, j);
    }
    
    private static int idxToOrd(int i, int j) {
        assert isValidIdx(i, j);
        
        return WIDTH * i + j;
    }
    
    private static String idxToPos(int i, int j) {
        assert isValidIdx(i, j);
        
        return "%c%d".formatted('a' + j, 1 + i);
    }
    
    // [instance members] ******************************************************
    
    private final int i;
    private final int j;
    
    private Point(int i, int j) {
        this.i = i;
        this.j = j;
    }
    
    /**
     * 縦座標を返します。<br>
     * 
     * @return 縦座標
     */
    public int i() {
        return i;
    }
    
    /**
     * 横座標を返します。<br>
     * 
     * @return 横座標
     */
    public int j() {
        return j;
    }
    
    /**
     * {@code "a1"}～{@code "h8"} 形式の座標を返します。<br>
     * 
     * @return {@code "a1"}～{@code "h8"} 形式の座標
     */
    public String pos() {
        return idxToPos(i, j);
    }
    
    /**
     * 指定された方向に次の {@link Point} が存在するかを返します。<br>
     * 
     * @param direction 方向
     * @return 指定された方向に次の {@link Point} が存在する場合は {@code true}
     * @throws NullPointerException {@code direction} が {@code null} の場合
     */
    public boolean hasNext(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        
        int ni = i + direction.di;
        int nj = j + direction.dj;
        
        return isValidIdx(ni, nj);
    }
    
    /**
     * 指定された方向の次の {@link Point} を返します。<br>
     * 
     * @param direction 方向
     * @return 指定された方向の次の {@link Point}
     * @throws NullPointerException {@code direction} が {@code null} の場合
     * @throws NoSuchElementException 指定された方向に次の {@link Point} が存在しない場合
     */
    public Point next(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        
        int ni = i + direction.di;
        int nj = j + direction.dj;
        
        if (!isValidIdx(ni, nj)) {
            throw new NoSuchElementException("%s -> %s".formatted(this, direction));
        }
        
        return of(ni, nj);
    }
    
    @Override
    public String toString() {
        return idxToPos(i, j);
    }
}
