package xyz.hotchpotch.reversi.players;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Move;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * モンテカルロ法で最善手を選ぶ {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class MonteCarloAIPlayer extends AIPlayerBase {
    
    // [static members] ********************************************************
    
    /**
     * {@link Board} の実装です。
     * このクラスのオブジェクトは可変です。<br>
     * 
     * プレイアウトの高速化のため、この実装は {@link Board#getApplied(Move)} の代わりに
     * {@link #apply(Move)} を提供します。<br>
     * 
     * @author nmby
     */
    private static class MutableBoard implements Board {
        private final Map<Point, Color> map;
        
        private MutableBoard(Board board) {
            assert board != null;
            
            this.map = new HashMap<>(Board.boardToMap(board));
        }
        
        @Override
        public Color colorAt(Point point) {
            assert point != null;
            
            return map.get(point);
        }
        
        /**
         * このリバーシ盤に指定された手を適用します。<br>
         */
        public void apply(Move move) {
            assert move != null;
            assert Rule.canApply(this, move);
            
            if (move.point() != null) {
                Set<Point> reversibles = Rule.reversibles(this, move);
                reversibles.forEach(p -> map.put(p, move.color()));
                map.put(move.point(), move.color());
            }
        }
        
        @Override
        public Board getApplied(Move move) {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class Candidate implements Comparable<Candidate> {
        private final Point point;
        private final Board nextBoard;
        private int wins;
        private int draws;
        private int losses;
        
        private Candidate(Point point, Board nextBoard) {
            this.point = point;
            this.nextBoard = nextBoard;
        }
        
        private int times() {
            return wins + draws + losses;
        }
        
        private float winRatio() {
            return (float) wins / times();
        }
        
        private float drawRatio() {
            return (float) draws / times();
        }
        
        private float lossRatio() {
            return (float) losses / times();
        }
        
        @Override
        public int compareTo(Candidate o) {
            if (winRatio() != o.winRatio()) {
                return winRatio() < o.winRatio() ? -1 : 1;
                
            } else if (drawRatio() != o.drawRatio()) {
                return drawRatio() < o.drawRatio() ? -1 : 1;
                
            } else {
                return 0;
            }
        }
        
        @Override
        public String toString() {
            return "%s > wins:%d(%.1f%%), draws:%d(%.1f%%), losses:%d(%.1f%%)".formatted(
                    point,
                    wins, winRatio() * 100,
                    draws, drawRatio() * 100,
                    losses, lossRatio() * 100);
        }
    }
    
    // [instance members] ******************************************************
    
    @Override
    protected Instant timelimit(Board board, long remainingMillis) {
        final long margin = 20;
        
        int blanks = (int) Point.stream().map(board::colorAt).filter(Objects::isNull).count();
        assert 1 < blanks;
        
        if (50 < blanks) {
            // 最初の10手はランダムに手を選ぶことにし、思考時間を割り当てない。
            return Instant.now().minusMillis(margin);
            
        } else if (25 < blanks) {
            // 中盤は勝負どころなので厚めに時間を割り当てる。
            long millis = (remainingMillis - margin) * 6 / blanks;
            return Instant.now().plusMillis(millis);
            
        } else if (10 < blanks) {
            long millis = (remainingMillis - margin) * 4 / blanks;
            return Instant.now().plusMillis(millis);
            
        } else {
            long millis = (remainingMillis - margin) * 2 / blanks;
            return Instant.now().plusMillis(millis);
        }
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * この実装は、モンテカルロシミュレーションによって最善手を選択します。<br>
     */
    @Override
    protected Point decide2(Board board, Color color, List<Point> availables, Instant timelimit) {
        List<Candidate> candidates = availables.stream()
                .map(p -> new Candidate(p, board.getApplied(new Move(color, p))))
                .toList();
        
        // 本当はモンテカルロ法は並列処理と相性が良いが、
        // ここでは実装の簡略さを優先させることにする。
        while (timelimit.isAfter(Instant.now())) {
            for (Candidate candidate : candidates) {
                Color winner = playout(candidate.nextBoard, color.reversed());
                if (winner == color) {
                    candidate.wins++;
                } else if (winner == null) {
                    candidate.draws++;
                } else {
                    candidate.losses++;
                }
            }
        }
        
        return candidates.stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(AssertionError::new).point;
    }
    
    /**
     * プレイアウトを行い、勝者の色を返します。<br>
     * つまり、指定されたリバーシ盤に対してランダムな手を適用してゲーム終了まで進め、
     * 勝者の色を返します。<br>
     * 
     * @param board プレイアウト開始時点のリバーシ盤
     * @param currTurn プレイアウト開始時点の手番
     * @return 勝者の色（引き分けの場合は {@code null}）
     */
    private Color playout(Board board, Color currTurn) {
        MutableBoard b = new MutableBoard(board);
        Color c = currTurn;
        
        while (Rule.isGameOngoing(b)) {
            Point p = proxy.decide(b, c, 0);
            b.apply(new Move(c, p));
            c = c.reversed();
        }
        
        return Rule.winner(b);
    }
}
