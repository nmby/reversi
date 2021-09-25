package xyz.hotchpotch.reversi.players;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Move;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * 深さ優先探索により必勝手を探す {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class DepthFirstAIPlayer extends AIPlayerBase {
    
    // [static members] ********************************************************
    
    private static class TimeUpException extends Exception {
    }
    
    // [instance members] ******************************************************
    
    @Override
    protected Instant timelimit(Board board, long remainingMillis) {
        final long margin = 10;
        
        int blanks = (int) Point.stream().map(board::colorAt).filter(Objects::isNull).count();
        assert 1 < blanks;
        
        if (20 < blanks) {
            // 最初の40手はランダムに手を選ぶことにし、思考時間を割り当てない。
            return Instant.now().minusMillis(margin);
            
        } else {
            // 以降は残り手数に応じて持ち時間を配分する。
            long millis = remainingMillis * 4 / blanks;
            return Instant.now().plusMillis(millis - margin);
        }
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * この実装は、深さ優先探索により必勝手を探します。<br>
     */
    @Override
    protected Point decide2(Board board, Color color, List<Point> availables, Instant timelimit) {
        try {
            Point drawable = null;
            
            for (Point p : availables) {
                Color c = solve(board.getApplied(new Move(color, p)), color.reversed(), timelimit);
                if (c == color) {
                    return p;
                    
                } else if (c == null && drawable == null) {
                    drawable = p;
                }
            }
            
            return drawable != null
                    ? drawable
                    : proxy.decide(board, color, 0);
            
        } catch (TimeUpException e) {
            return proxy.decide(board, color, 0);
        }
    }
    
    /**
     * 黒白双方のプレーヤーが最善手を指した場合の勝者の色を返します。<br>
     * 
     * @param board リバーシ盤
     * @param color 手番
     * @return 黒白双方のプレーヤーが最善手を指した場合の勝者の色（引き分けの場合は {@code null}）
     * @throws TimeUpException 時間切れの場合
     */
    private Color solve(Board board, Color color, Instant timelimit) throws TimeUpException {
        if (timelimit.isBefore(Instant.now())) {
            throw new TimeUpException();
        }
        
        List<Point> availables = Point.stream()
                .filter(p -> Rule.canPutAt(board, color, p))
                .toList();
        
        if (availables.isEmpty()) {
            return Rule.canPut(board, color.reversed())
                    ? solve(board, color.reversed(), timelimit)
                    : Rule.winner(board);
        }
        
        boolean canDraw = false;
        
        for (Point p : availables) {
            Color c = solve(board.getApplied(new Move(color, p)), color.reversed(), timelimit);
            
            if (c == color) {
                return color;
                
            } else if (c == null) {
                canDraw = true;
            }
        }
        
        return canDraw ? null : color.reversed();
    }
}
