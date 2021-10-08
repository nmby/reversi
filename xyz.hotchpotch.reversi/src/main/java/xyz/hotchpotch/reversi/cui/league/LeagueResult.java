package xyz.hotchpotch.reversi.cui.league;

import java.util.Arrays;
import java.util.Objects;

import xyz.hotchpotch.reversi.cui.match.MatchResult;

/**
 * 総当たり戦実施結果を保持するレコードです。<br>
 * 
 * @author nmby
 */
public record LeagueResult(
        MatchResult[][] matchResults) {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    // [instance members] ******************************************************
    
    /**
     * 総当たり戦実施結果を生成します。<br>
     * 
     * @param matchResults ペア毎の複数回対戦結果
     * @throws NullPointerException {@code matchResults} が {@code null} の場合
     * @throws IllegalArgumentException {@code matchResults} の一次元目の長さが0の場合
     * @throws IllegalArgumentException {@code matchResults} の一次元目/二次元目の長さが異なる場合
     */
    public LeagueResult {
        Objects.requireNonNull(matchResults, "matchResults");
        if (matchResults.length == 0) {
            throw new IllegalArgumentException("matchResultsは空です。");
        }
        boolean isSquare = Arrays.stream(matchResults)
                .mapToInt(mr -> mr.length)
                .allMatch(len -> len == matchResults.length);
        if (!isSquare) {
            throw new IllegalArgumentException("matchResultsの縦横の長さが異なります。");
        }
    }
    
    @Override
    public String toString() {
        int num = matchResults.length;
        int winWidth = Arrays.stream(matchResults).flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(MatchResult::aWins)
                .map(String::valueOf)
                .mapToInt(String::length)
                .max()
                .getAsInt();
        int drawWidth = Arrays.stream(matchResults).flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(MatchResult::draws)
                .map(String::valueOf)
                .mapToInt(String::length)
                .max()
                .getAsInt();
        
        StringBuilder str = new StringBuilder();
        
        str.append("   ");
        for (int j = 0; j < num; j++) {
            str.append("  [対%c]".formatted('A' + j));
            str.append(" ".repeat(winWidth * 2 + drawWidth - 3));
        }
        str.append("   TOTAL(勝/分/負)").append(BR);
        
        for (int i = 0; i < num; i++) {
            str.append("[%c]".formatted('A' + i));
            int wins = 0;
            int draws = 0;
            int losts = 0;
            
            for (int j = 0; j < num; j++) {
                str.append("  ");
                if (i == j) {
                    str.append(" ".repeat(winWidth));
                    str.append("-".repeat(drawWidth + 2));
                    str.append(" ".repeat(winWidth));
                } else {
                    MatchResult matchResult = matchResults[i][j];
                    str.append(("%" + winWidth + "d/%" + drawWidth + "d/%" + winWidth + "d")
                            .formatted(matchResult.aWins(), matchResult.draws(), matchResult.bWins()));
                    
                    wins += matchResult.aWins();
                    draws += matchResult.draws();
                    losts += matchResult.bWins();
                }
            }
            
            int times = wins + draws + losts;
            str.append(("   %" + (winWidth + 1) + "d/%" + (drawWidth + 1) + "d/%" + (winWidth + 1) + "d")
                    .formatted(wins, draws, losts));
            str.append(" (%3d%%/%2d%%/%3d%%)"
                    .formatted(wins * 100 / times, draws * 100 / times, losts * 100 / times));
            str.append(BR);
        }
        
        return str.toString();
    }
}
