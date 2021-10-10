package xyz.hotchpotch.reversi.cui.game;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Move;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;
import xyz.hotchpotch.reversi.core.Rule;
import xyz.hotchpotch.reversi.cui.common.ConsolePlayer;
import xyz.hotchpotch.reversi.cui.common.ConsoleScanner;
import xyz.hotchpotch.reversi.cui.common.RuleViolationException;

/**
 * ゲーム（1回対戦）実行クラスです。<br>
 * 
 * @author nmby
 */
public class GameRunner {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    private static final ConsoleScanner<String> waiter = ConsoleScanner.waiter();
    
    private static record PlayerInstance(Player player, Exception e) {
    }
    
    private static record Decision(Point point, long elapsed) {
    }
    
    /**
     * 与えられた条件でゲーム（1回対戦）を実行する {@link GameRunner} インスタンスを生成して返します。<br>
     * 
     * @param gameCondition ゲーム実施条件
     * @return ゲーム実行器
     * @throws NullPointerException {@code gameCondition} が {@code null} の場合
     */
    public static GameRunner of(GameCondition gameCondition) {
        Objects.requireNonNull(gameCondition, "gameCondition");
        
        return new GameRunner(gameCondition);
    }
    
    // [instance members] ******************************************************
    
    private final GameCondition gameCondition;
    
    private GameRunner(GameCondition gameCondition) {
        assert gameCondition != null;
        
        this.gameCondition = gameCondition;
    }
    
    /**
     * ゲーム（1回対戦）を実行して結果を返します。<br>
     * 
     * @return ゲーム実施結果
     */
    public GameResult run() {
        GameResult gameResult = null;
        
        try {
            print("次の条件で対戦を行います。" + BR);
            print(gameCondition.toString().indent(4));
            waitUser();
            
            Map<Color, Player> players = createPlayers();
            Map<Color, Long> remainingMillis = new HashMap<>(Map.of(
                    Color.BLACK, gameCondition.givenMillis(),
                    Color.WHITE, gameCondition.givenMillis()));
            
            Board board = Board.initBoard();
            Color currTurn = Color.BLACK;
            
            while (Rule.isGameOngoing(board)) {
                Player player = players.get(currTurn);
                long millis = remainingMillis.get(currTurn);
                
                print(BR + board + BR);
                print("%sの番（残り %d ミリ秒） ... ".formatted(currTurn, millis));
                
                Decision decision = getDecision(player, board, currTurn, millis);
                if (!(player instanceof ConsolePlayer)) {
                    print(decision.point == null ? "PASS" : decision.point);
                    print(BR);
                    waitUser();
                }
                
                Move move = new Move(currTurn, decision.point);
                if (!Rule.canApply(board, move)) {
                    throw new RuleViolationException(GameResult.ofRuleViolation(board, currTurn));
                }
                
                remainingMillis.put(currTurn, millis - decision.elapsed);
                board = board.getApplied(move);
                currTurn = currTurn.reversed();
            }
            
            print(BR + board);
            gameResult = GameResult.ofNormalEnd(board);
            
        } catch (RuleViolationException e) {
            gameResult = e.gameResult();
        }
        
        print(BR + gameResult + BR);
        
        return gameResult;
    }
    
    private Map<Color, Player> createPlayers() throws RuleViolationException {
        PlayerInstance black = createPlayer(gameCondition.playerBlack());
        PlayerInstance white = createPlayer(gameCondition.playerWhite());
        
        if (black.player == null && white.player == null) {
            throw new RuleViolationException(
                    GameResult.ofFailToCreatePlayers(black.e, white.e));
            
        } else if (black.player == null) {
            throw new RuleViolationException(
                    GameResult.ofFailToCreatePlayer(Color.BLACK, black.e));
            
        } else if (white.player == null) {
            throw new RuleViolationException(
                    GameResult.ofFailToCreatePlayer(Color.WHITE, white.e));
        }
        
        return Map.of(
                Color.BLACK, black.player,
                Color.WHITE, white.player);
    }
    
    private PlayerInstance createPlayer(Class<? extends Player> playerClass) {
        assert playerClass != null;
        
        try {
            return new PlayerInstance(playerClass.getConstructor().newInstance(), null);
        } catch (Exception e) {
            return new PlayerInstance(null, e);
        }
    }
    
    private Decision getDecision(
            Player player,
            Board board,
            Color color,
            long remainingMillis)
            throws RuleViolationException {
        
        assert player != null;
        assert board != null;
        assert color != null;
        assert Rule.isGameOngoing(board);
        assert 0 < remainingMillis;
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        try {
            Instant start = Instant.now();
            Point point = executor
                    .submit(() -> player.decide(board, color, remainingMillis))
                    .get(remainingMillis, TimeUnit.MILLISECONDS);
            Instant end = Instant.now();
            
            long elapsed = Math.min(
                    remainingMillis,
                    Duration.between(start, end).toMillis());
            
            if (elapsed < remainingMillis) {
                return new Decision(point, elapsed);
                
            } else {
                throw new TimeoutException();
            }
            
        } catch (TimeoutException e) {
            throw new RuleViolationException(new GameResult(
                    board,
                    color.reversed(),
                    e,
                    "持ち時間が無くなりました。%sの負けです。".formatted(color)));
            
        } catch (ExecutionException e) {
            throw new RuleViolationException(new GameResult(
                    board,
                    color.reversed(),
                    e.getCause(),
                    "%sの思考中に例外が発生しました。%sの負けです。%n%s"
                            .formatted(color, color, e.getCause())));
            
        } catch (InterruptedException e) {
            throw new RuleViolationException(new GameResult(
                    board,
                    null,
                    e,
                    "%sの思考中に予期せぬ割り込みが発生しました。ノーコンテストです。"
                            .formatted(color)));
            
        } finally {
            executor.shutdownNow();
        }
    }
    
    private void print(Object o) {
        if (gameCondition.interactive()) {
            System.out.print(o);
        }
    }
    
    private void waitUser() {
        if (gameCondition.interactive()) {
            waiter.get();
        }
    }
}
