package xyz.hotchpotch.reversi.cui.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.cui.common.ConsoleScanner;
import xyz.hotchpotch.reversi.cui.game.GameCondition;
import xyz.hotchpotch.reversi.cui.game.GameResult;
import xyz.hotchpotch.reversi.cui.game.GameRunner;
import xyz.hotchpotch.reversi.cui.match.MatchCondition.MatchSide;

/**
 * 複数回対戦実行クラスです。<br>
 * 
 * @author nmby
 */
public class MatchRunner {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    private static final ConsoleScanner<String> waiter = ConsoleScanner.waiter();
    
    /**
     * 与えられた条件で複数回対戦を実行する {@link MatchRunner} インスタンスを生成して返します。<br>
     * 
     * @param matchCondition 複数回対戦条件
     * @return 複数回対戦実行器
     * @throws NullPointerException {@code matchCondition} が {@code null} の場合
     */
    public static MatchRunner of(MatchCondition matchCondition) {
        Objects.requireNonNull(matchCondition, "matchCondition");
        
        return new MatchRunner(matchCondition);
    }
    
    // [instance members] ******************************************************
    
    private final MatchCondition matchCondition;
    
    private MatchRunner(MatchCondition matchCondition) {
        assert matchCondition != null;
        
        this.matchCondition = matchCondition;
    }
    
    /**
     * 複数回対戦を実行して結果を返します。<br>
     * 
     * @return 複数回対戦結果
     */
    public MatchResult run() {
        print("次の条件で複数回対戦を行います。" + BR);
        print(matchCondition.toString().indent(4));
        waitUser();
        print(BR);
        
        Map<MatchSide, GameRunner> gameRunners = Map.of(
                MatchSide.A, GameRunner.of(new GameCondition(
                        matchCondition.playerA(),
                        matchCondition.playerB(),
                        matchCondition.givenMillis(),
                        false)),
                MatchSide.B, GameRunner.of(new GameCondition(
                        matchCondition.playerB(),
                        matchCondition.playerA(),
                        matchCondition.givenMillis(),
                        false)));
        
        Map<MatchSide, List<GameResult>> gameResults = Map.of(
                MatchSide.A, new ArrayList<>(),
                MatchSide.B, new ArrayList<>());
        
        MatchSide currBlack = MatchSide.A;
        
        for (int i = 0; i < matchCondition.times(); i++, currBlack = currBlack.opposite()) {
            GameRunner gameRunner = gameRunners.get(currBlack);
            GameResult gameResult = gameRunner.run();
            
            String str = gameResult.desc();
            str = str.replace(Color.BLACK.toString(), "%s=%s".formatted(Color.BLACK, currBlack));
            str = str.replace(Color.WHITE.toString(), "%s=%s".formatted(Color.WHITE, currBlack.opposite()));
            
            print(str + BR);
            gameResults.get(currBlack).add(gameResult);
        }
        
        MatchResult matchResult = MatchResult.of(
                gameResults.get(MatchSide.A),
                gameResults.get(MatchSide.B));
        
        print(BR + matchResult + BR);
        
        return matchResult;
    }
    
    private void print(Object o) {
        if (matchCondition.interactive()) {
            System.out.print(o);
        }
    }
    
    private void waitUser() {
        if (matchCondition.interactive()) {
            waiter.get();
        }
    }
}
