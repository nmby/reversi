package xyz.hotchpotch.reversi.cui.match;

import java.util.List;
import java.util.Objects;

import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.cui.game.GameResult;
import xyz.hotchpotch.reversi.cui.match.MatchCondition.MatchSide;

/**
 * 複数回対戦結果を保持するレコードです。<br>
 * 
 * @author nmby
 */
public record MatchResult(
        int aWins,
        int bWins,
        int draws) {
    
    // [static members] ********************************************************
    
    /**
     * 複数回対戦結果を生成して返します。<br>
     * 
     * @param resultsA プレーヤーAが黒のときのゲーム実施結果
     * @param resultsB プレーヤーBが黒のときのゲーム実施結果
     * @return 複数回対戦結果
     * @throws NullPointerException {@code resultsA}, {@code resultsB} のいずれかが {@code null} の場合
     * @throws IllegalArgumentException {@code resultsA}, {@code resultsB} がともに長さ0の場合
     */
    public static MatchResult of(
            List<GameResult> resultsA,
            List<GameResult> resultsB) {
        
        Objects.requireNonNull(resultsA, "resultsA");
        Objects.requireNonNull(resultsB, "resultsB");
        if (resultsA.isEmpty() && resultsB.isEmpty()) {
            throw new IllegalArgumentException("一回も対戦が行われていません。");
        }
        
        int aWins = 0;
        int bWins = 0;
        int draws = 0;
        
        for (GameResult result : resultsA) {
            if (result.winner() == Color.BLACK) {
                aWins++;
            } else if (result.winner() == Color.WHITE) {
                bWins++;
            } else {
                draws++;
            }
        }
        for (GameResult result : resultsB) {
            if (result.winner() == Color.BLACK) {
                bWins++;
            } else if (result.winner() == Color.WHITE) {
                aWins++;
            } else {
                draws++;
            }
        }
        
        return new MatchResult(aWins, bWins, draws);
    }
    
    // [instance members] ******************************************************
    
    /**
     * 複数回対戦結果を生成します。<br>
     * 
     * @param aWins プレーヤーAの勝ち数
     * @param bWins プレーヤーBの勝ち数
     * @param draws 引き分け数
     * @throws IllegalArgumentException {@code aWins}, {@code bWins}, {@code draws} のいずれかが負数の場合
     */
    public MatchResult {
        if (aWins < 0 || bWins < 0 || draws < 0) {
            throw new IllegalArgumentException(
                    "マイナス値は許容されません。[aWins:%d, bWins:%d, draws:%d]"
                            .formatted(aWins, bWins, draws));
        }
    }
    
    @Override
    public String toString() {
        float times = aWins + bWins + draws;
        return "%sの勝ち：%d (%.1f%%), 引き分け：%d (%.1f%%), %sの勝ち：%d (%.1f%%)"
                .formatted(
                        MatchSide.A, aWins, aWins * 100 / times,
                        draws, draws * 100 / times,
                        MatchSide.B, bWins, bWins * 100 / times);
    }
}
