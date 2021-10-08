package xyz.hotchpotch.reversi.cui.league;

import java.util.List;
import java.util.Objects;

import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.cui.common.ConditionUtil;

/**
 * 総当たり戦実施条件を保持するレコードです。<br>
 * 
 * @author nmby
 */
public record LeagueCondition(
        List<Class<? extends Player>> players,
        long givenMillis,
        int times,
        boolean interactive) {
    
    // [static members] ********************************************************
    
    /**
     * 標準入力から総当たり戦実施条件を取得します。<br>
     * 
     * @return 総当たり戦実施条件
     */
    public static LeagueCondition arrangeViaConsole() {
        List<Class<? extends Player>> players = ConditionUtil.arrangePlayers();
        System.out.println();
        long givenMillis = ConditionUtil.arrangeMillis("1ゲームあたりの");
        System.out.println();
        int times = ConditionUtil.arrangeTimes("1ペアごとの");
        System.out.println();
        
        return new LeagueCondition(
                players,
                givenMillis,
                times,
                true);
    }
    
    // [instance members] ******************************************************
    
    /**
     * 総当たり戦実施条件を生成します。<br>
     * 
     * @param players プレーヤークラス
     * @param givenMillis ゲーム毎・プレーヤー毎の持ち時間（ミリ秒）
     * @param times 1ペアあたりの対戦回数
     * @param interactive ユーザーによる確認を求めながら進める場合は {@code true}
     * @throws NullPointerException {@code players} が {@code null} の場合
     * @throws IllegalArgumentException {@code players} の要素数が1以下の場合
     * @throws IllegalArgumentException {@code givenMillis}, {@code times} のいずれかが 0 以下の場合
     */
    public LeagueCondition {
        Objects.requireNonNull(players, "players");
        if (players.size() <= 1) {
            throw new IllegalArgumentException("players.size: " + players.size());
        }
        if (givenMillis <= 0) {
            throw new IllegalArgumentException("givenMillis: " + givenMillis);
        }
        if (times <= 0) {
            throw new IllegalArgumentException("times: " + times);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        
        for (int i = 0; i < players.size(); i++) {
            // FIXME: プレーヤー数が26（アルファベット文字数）を超える場合にも適切に表示されるようにする
            str.append("プレーヤー%c : %s%n".formatted('A' + i, players.get(i).getName()));
        }
        str.append("1ゲームあたりの各プレーヤーの持ち時間 : %d ミリ秒%n".formatted(givenMillis));
        str.append("1ペアごとの対戦回数 : %d%n".formatted(times));
        
        return str.toString();
    }
}
