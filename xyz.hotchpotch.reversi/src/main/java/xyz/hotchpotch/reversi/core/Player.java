package xyz.hotchpotch.reversi.core;

/**
 * リバーシのプレーヤーを表します。<br>
 * 
 * @author nmby
 */
@FunctionalInterface
public interface Player {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    /**
     * この手番における自身の手（石を置く場所）を返します。
     * パスの場合は {@code null} を返す必要があります。<br>
     * 
     * @param board 現在のリバーシ盤
     * @param color このプレーヤーの色
     * @param remainingMillis 残り持ち時間（ミリ秒）
     * @return 石を置く場所（パスの場合は {@code null}）
     */
    Point decide(Board board, Color color, long remainingMillis);
}
