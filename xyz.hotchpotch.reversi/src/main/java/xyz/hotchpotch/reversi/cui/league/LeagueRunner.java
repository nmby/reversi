package xyz.hotchpotch.reversi.cui.league;

import java.util.Objects;

import xyz.hotchpotch.reversi.cui.common.ConsoleScanner;
import xyz.hotchpotch.reversi.cui.match.MatchCondition;
import xyz.hotchpotch.reversi.cui.match.MatchResult;
import xyz.hotchpotch.reversi.cui.match.MatchRunner;

/**
 * 総当たり戦実行クラスです。<br>
 * 
 * @author nmby
 */
public class LeagueRunner {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    private static final ConsoleScanner<String> waiter = ConsoleScanner.waiter();
    
    /**
     * 与えられた条件で総当たり戦を実行する {@link LeagueRunner} インスタンスを生成して返します。<br>
     * 
     * @param leagueCondition 総当たり戦実施条件
     * @return 総当たり戦実行器
     * @throws NullPointerException {@code leagueCondition} が {@code null} の場合
     */
    public static LeagueRunner of(LeagueCondition leagueCondition) {
        Objects.requireNonNull(leagueCondition, "leagueCondition");
        
        return new LeagueRunner(leagueCondition);
    }
    
    // [instance members] ******************************************************
    
    private final LeagueCondition leagueCondition;
    
    private LeagueRunner(LeagueCondition leagueCondition) {
        assert leagueCondition != null;
        
        this.leagueCondition = leagueCondition;
    }
    
    /**
     * 総当たり戦を実施して結果を返します。<br>
     * 
     * @return 総当たり戦実施結果
     */
    public LeagueResult run() {
        print("次の条件で総当たり戦を行います。" + BR);
        print(leagueCondition.toString().indent(4));
        waitUser();
        print(BR);
        
        int num = leagueCondition.players().size();
        int tw = String.valueOf(leagueCondition.times()).length();
        MatchResult[][] matchResults = new MatchResult[num][num];
        
        for (int i = 0; i < num - 1; i++) {
            for (int j = i + 1; j < num; j++) {
                MatchCondition matchCondition = new MatchCondition(
                        leagueCondition.players().get(i),
                        leagueCondition.players().get(j),
                        leagueCondition.givenMillis(),
                        leagueCondition.times(),
                        false);
                
                MatchResult matchResult = MatchRunner.of(matchCondition).run();
                matchResults[i][j] = matchResult;
                matchResults[j][i] = new MatchResult(matchResult.bWins(), matchResult.aWins(), matchResult.draws());
                
                print(("[%c vs %c] %cの勝ち：%" + tw + "d, %cの勝ち：%" + tw + "d, 引き分け：%" + tw + "d%n")
                        .formatted(
                                'A' + i, 'A' + j,
                                'A' + i, matchResult.aWins(),
                                'A' + j, matchResult.bWins(),
                                matchResult.draws()));
            }
        }
        
        LeagueResult leagueResult = new LeagueResult(matchResults);
        print(BR + leagueResult);
        
        return leagueResult;
    }
    
    private void print(Object o) {
        if (leagueCondition.interactive()) {
            System.out.print(o);
        }
    }
    
    private void waitUser() {
        if (leagueCondition.interactive()) {
            waiter.get();
        }
    }
}
