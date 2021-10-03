package xyz.hotchpotch.reversi.players;

import java.time.Instant;
import java.util.List;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * AIプレーヤーの個別思考ロジックに関わらない共通的な処理を提供する基底クラスです。<br>
 * 
 * @author nmby
 */
/*package*/ abstract class AIPlayerBase implements Player {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    /** 思考処理を委譲するプロキシプレーヤー */
    protected final Player proxy = new RandomAIPlayer();
    
    @Override
    public Point decide(Board board, Color color, long remainingMillis) {
        
        // 石を置ける場所を調べる。
        List<Point> availables = Point.stream()
                .filter(p -> Rule.canPutAt(board, color, p))
                .toList();
        
        // 石を置ける場所が無しまたは一箇所の場合は選択の余地が無いため、直ちに結果を返す。
        if (availables.isEmpty()) {
            return null;
            
        } else if (availables.size() == 1) {
            return availables.get(0);
        }
        
        Instant timelimit = timelimit(board, remainingMillis);
        
        // 残り時間が無い場合はランダムに手を選択する。
        if (timelimit.isBefore(Instant.now())) {
            return proxy.decide(board, color, remainingMillis);
        }
        
        return decide2(board, color, availables, timelimit);
    }
    
    /**
     * 今回の手に費やせる時間を計算し、思考処理を打ち切るべき制限時刻を返します。<br>
     * 
     * @param board 現在のリバーシ盤
     * @param remainingMillis 残り持ち時間（ミリ秒）
     * @return 思考処理を打ち切るべき制限時刻
     */
    protected abstract Instant timelimit(Board board, long remainingMillis);
    
    /**
     * この手番における自身の手（石を置く場所）を返します。<br>
     * 
     * @param board 現在のリバーシ盤
     * @param color このプレーヤーの色
     * @param availables このプレーヤーの石を置ける場所
     * @param timelimit 今回の手番の思考を打ち切る制限時刻
     * @return 石を置く場所
     */
    protected abstract Point decide2(Board board, Color color, List<Point> availables, Instant timelimit);
}
