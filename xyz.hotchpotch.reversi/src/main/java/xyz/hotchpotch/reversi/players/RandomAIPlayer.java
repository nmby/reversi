package xyz.hotchpotch.reversi.players;

import java.util.List;
import java.util.Random;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * 自身の手をランダムに選択する {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class RandomAIPlayer implements Player {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    private final Random random = new Random();
    
    /**
     * {@inheritDoc}
     * <br>
     * この実装は、石を置ける位置の中からランダムに自身の手を選択します。<br>
     */
    @Override
    public Point decide(Board board, Color color, long remainingMillis) {
        List<Point> availables = Point.stream()
                .filter(p -> Rule.canPutAt(board, color, p))
                .toList();
        
        return availables.isEmpty()
                ? null
                : availables.get(random.nextInt(availables.size()));
    }
}
