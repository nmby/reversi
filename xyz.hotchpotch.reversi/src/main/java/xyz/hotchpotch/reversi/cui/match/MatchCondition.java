package xyz.hotchpotch.reversi.cui.match;

import java.util.Objects;

import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.cui.common.ConditionUtil;

/**
 * 複数回対戦条件を保持するレコードです。<br>
 * 
 * @author nmby
 */
public record MatchCondition(
        Class<? extends Player> playerA,
        Class<? extends Player> playerB,
        long givenMillis,
        int times,
        boolean interactive) {
    
    // [static members] ********************************************************
    
    /**
     * 対戦者の側を表す列挙型です。<br>
     * 
     * @author nmby
     */
    public static enum MatchSide {
        /** A側 */
        A,
        /** B側 */
        B;
        
        /**
         * 自身と反対の側を返します。<br>
         * 
         * @return 自身と反対の側
         */
        public MatchSide opposite() {
            return this == A ? B : A;
        }
    }
    
    /**
     * 標準入力から複数回対戦条件を取得します。<br>
     * 
     * @return 複数回対戦条件
     */
    public static MatchCondition arrangeViaConsole() {
        Class<? extends Player> playerA = ConditionUtil
                .arrangePlayer("プレーヤー%s".formatted(MatchSide.A), false);
        System.out.println();
        Class<? extends Player> playerB = ConditionUtil
                .arrangePlayer("プレーヤー%s".formatted(MatchSide.B), false);
        System.out.println();
        long givenMillis = ConditionUtil.arrangeMillis("1ゲームあたりの");
        System.out.println();
        int times = ConditionUtil.arrangeTimes("");
        System.out.println();
        
        return new MatchCondition(
                playerA,
                playerB,
                givenMillis,
                times,
                true);
    }
    
    // [instance members] ******************************************************
    
    /**
     * 複数回対戦条件を生成します。<br>
     * 
     * @param playerA プレーヤーAクラス
     * @param playerB プレーヤーBクラス
     * @param givenMillis ゲーム毎・プレーヤー毎の持ち時間（ミリ秒）
     * @param times 対戦回数
     * @param interactive ユーザーによる確認を求めながら進める場合は {@code true}
     * @throws NullPointerException {@code playerA}, {@code playerB} のいずれかが {@code null} の場合
     * @throws IllegalArgumentException {@code givenMillis}, {@code times} のいずれかが 0 以下の場合
     */
    public MatchCondition {
        Objects.requireNonNull(playerA, "playerA");
        Objects.requireNonNull(playerB, "playerB");
        if (givenMillis <= 0) {
            throw new IllegalArgumentException("givenMillis: " + givenMillis);
        }
        if (times <= 0) {
            throw new IllegalArgumentException("times: " + times);
        }
    }
    
    @Override
    public String toString() {
        return (""
                + "プレーヤー%s : %s%n"
                + "プレーヤー%s : %s%n"
                + "1ゲームあたりの各プレーヤーの持ち時間 : %d ミリ秒%n"
                + "対戦回数 : %d%n")
                        .formatted(
                                MatchSide.A, playerA.getName(),
                                MatchSide.B, playerB.getName(),
                                givenMillis,
                                times);
    }
}
