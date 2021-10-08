package xyz.hotchpotch.reversi.core;

import java.util.stream.Stream;

/**
 * リバーシ盤上の方向を表す列挙型です。<br>
 * 
 * @author nmby
 */
public enum Direction {
    
    // [static members] ********************************************************
    
    /** 上 */
    UPPER(-1, 0),
    
    /** 右上 */
    UPPER_RIGHT(-1, 1),
    
    /** 右 */
    RIGHT(0, 1),
    
    /** 右下 */
    LOWER_RIGHT(1, 1),
    
    /** 下 */
    LOWER(1, 0),
    
    /** 左下 */
    LOWER_LEFT(1, -1),
    
    /** 左 */
    LEFT(0, -1),
    
    /** 左上 */
    UPPER_LEFT(-1, -1);
    
    /**
     * 全ての {@link Direction} オブジェクトを含む直列ストリームを返します。<br>
     * 
     * @return 全ての {@link Direction} オブジェクトを含む直列ストリーム
     */
    public static Stream<Direction> stream() {
        return Stream.of(values());
    }
    
    // [instance members] ******************************************************
    
    /*package*/ final int di;
    /*package*/ final int dj;
    
    private Direction(int di, int dj) {
        this.di = di;
        this.dj = dj;
    }
}
