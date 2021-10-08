package xyz.hotchpotch.reversi.players;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * 自身の手を単純走査で決定する {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class SimplestAIPlayer implements Player {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    /**
     * {@inheritDoc}
     * <br>
     * この実装は、リバーシ盤上を左上から順に走査し、
     * 最初に見つかった石を置ける位置を自身の手とします。<br>
     */
    @Override
    public Point decide(Board board, Color color, long remainingMillis) {
        for (Point p : Point.values()) {
            if (Rule.canPutAt(board, color, p)) {
                return p;
            }
        }
        return null;
        
        // 参考：
        // 次のソースコードでも上記と同じ結果が返されます。
        // 
        // return Point.stream()
        //         .filter(p -> Rule.canPutAt(board, color, p))
        //         .findFirst()
        //         .orElse(null);
    }
}
