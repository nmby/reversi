package xyz.hotchpotch.reversi.core;

import java.util.Objects;

/**
 * リバーシゲームの手を表す不変クラス（レコード）です。<br>
 * 
 * @author nmby
 */
public record Move(Color color, Point point) {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    /**
     * 新たな {@link Move} オブジェクトを生成します。<br>
     * 
     * @param color 手番の色
     * @param point 石を打つ位置（パスの場合は {@code null}）
     * @throws NullPointerException {@code color} が {@code null} の場合
     */
    public Move {
        Objects.requireNonNull(color, "color");
    }
    
    @Override
    public String toString() {
        return "%s : %s".formatted(color, point == null ? "PASS" : point);
    }
}
