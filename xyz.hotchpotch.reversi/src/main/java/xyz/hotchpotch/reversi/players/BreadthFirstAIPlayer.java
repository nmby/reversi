package xyz.hotchpotch.reversi.players;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Move;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * 幅優先探索により最善手を探す {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class BreadthFirstAIPlayer extends AIPlayerBase {
    
    // [static members] ********************************************************
    
    private static class Node {
        private final Node parent;
        private final Board board;
        private final Color color;
        private int score;
        private List<Node> children;
        
        private Node(Node parent, Board board, Color color) {
            this.parent = parent;
            this.board = board;
            this.color = color;
            score = evaluator.applyAsInt(board);
        }
        
        private void setScore(int newVal) {
            if (newVal != score) {
                score = newVal;
                if (parent != null) {
                    parent.updateScore();
                }
            }
        }
        
        private void updateScore() {
            int newVal = (color == Color.BLACK)
                    ? children.stream().mapToInt(n -> n.score).max().getAsInt()
                    : children.stream().mapToInt(n -> n.score).min().getAsInt();
            setScore(newVal);
        }
        
        private void solve() {
            if (!Rule.isGameOngoing(board)) {
                children = List.of();
                return;
            }
            
            List<Point> availables = Point.stream()
                    .filter(p -> Rule.canPutAt(board, color, p))
                    .toList();
            
            children = availables.isEmpty()
                    ? List.of(new Node(this, board, color.reversed()))
                    : availables.stream()
                            .map(p -> new Node(this, board.getApplied(new Move(color, p)), color.reversed()))
                            .toList();
            updateScore();
        }
    }
    
    /** 石を置ける場所の数の差で有利不利を判定する評価関数 */
    private static final ToIntFunction<Board> evaluator1 = board -> {
        int black = (int) Point.stream().filter(p -> Rule.canPutAt(board, Color.BLACK, p)).count();
        int white = (int) Point.stream().filter(p -> Rule.canPutAt(board, Color.WHITE, p)).count();
        
        return black - white;
    };
    
    /** 置かれている石の数の差で有利不利を判定する評価関数 */
    private static final ToIntFunction<Board> evaluator2 = board -> {
        Map<Color, Integer> counts = board.counts();
        
        return counts.get(Color.BLACK) - counts.get(Color.WHITE);
    };
    
    /** リバーシ盤に対する評価関数。黒の有利を正の値、白の有利を負の値として算出します。 */
    private static final ToIntFunction<Board> evaluator = board -> {
        int eval1 = evaluator1.applyAsInt(board);
        int eval2 = evaluator2.applyAsInt(board);
        int blanks = (int) Point.stream().map(board::colorAt).filter(Objects::isNull).count();
        
        return eval1 * blanks + eval2 * (Point.HEIGHT * Point.WIDTH - blanks);
    };
    
    // [instance members] ******************************************************
    
    @Override
    protected Instant timelimit(Board board, long remainingMillis) {
        final long margin = 20;
        
        int blanks = (int) Point.stream().map(board::colorAt).filter(Objects::isNull).count();
        assert 1 < blanks;
        
        if (50 < blanks) {
            // 最初の10手はランダムに手を選ぶことにし、思考時間を割り当てない。
            return Instant.now().minusMillis(margin);
            
        } else {
            long millis = (remainingMillis - margin) * 4 / blanks;
            return Instant.now().plusMillis(millis);
        }
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * この実装は、幅優先探索により最善手を探します。<br>
     */
    @Override
    protected Point decide2(Board board, Color color, List<Point> availables, Instant timelimit) {
        Map<Node, Point> candidates = availables.stream()
                .collect(Collectors.toMap(
                        p -> new Node(null, board.getApplied(new Move(color, p)), color.reversed()),
                        Function.identity()));
        
        Deque<Node> queue = new ArrayDeque<>(candidates.keySet());
        
        while (!queue.isEmpty() && timelimit.isAfter(Instant.now())) {
            Node node = queue.removeFirst();
            node.solve();
            queue.addAll(node.children);
        }
        
        Node best = (color == Color.BLACK)
                ? candidates.keySet().stream().max(Comparator.comparing(n -> n.score)).get()
                : candidates.keySet().stream().min(Comparator.comparing(n -> n.score)).get();
        
        return candidates.get(best);
    }
}
