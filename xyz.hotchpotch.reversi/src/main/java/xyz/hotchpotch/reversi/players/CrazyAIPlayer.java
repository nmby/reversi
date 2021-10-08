package xyz.hotchpotch.reversi.players;

import java.util.Random;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;

/**
 * しばしば
 * <ul>
 *   <li>ルール違反の手を選択する</li>
 *   <li>持ち時間を超過する</li>
 *   <li>実行時例外を発生させる</li>
 * </ul>
 * ことがある {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class CrazyAIPlayer implements Player {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    private final Player proxy = new RandomAIPlayer();
    private final Random random = new Random();
    
    /**
     * {@inheritDoc}
     * <br>
     * この実装は、通常は石を置ける位置の中からランダムに自身の手を選択しますが、
     * しばしば
     * <ul>
     *   <li>ルール違反の手を選択する</li>
     *   <li>持ち時間を超過する</li>
     *   <li>実行時例外を発生させる</li>
     * </ul>
     * ことがあります。<br>
     */
    @Override
    public Point decide(Board board, Color color, long remainingMillis) {
        float f = random.nextFloat();
        
        if (f < 0.55f) {
            // まともに打つ。
            return proxy.decide(board, color, remainingMillis);
            
        } else if (f < 0.7f) {
            // 置けるかどうかを考慮せずにめちゃくちゃに打つ
            int n = random.nextInt(Point.values().length + 1);
            return n < Point.values().length
                    ? Point.values()[n]
                    : null;
            
        } else if (f < 0.85f) {
            // 時間切れまで何もしない
            try {
                Thread.sleep(remainingMillis + 100);
            } catch (InterruptedException e) {
            }
            throw new AssertionError();
            
        } else {
            // 発狂する
            throw new RuntimeException("I'm crazy.");
        }
    }
}
