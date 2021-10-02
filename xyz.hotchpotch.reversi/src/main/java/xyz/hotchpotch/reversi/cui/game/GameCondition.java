package xyz.hotchpotch.reversi.cui.game;

import java.util.Objects;

import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.cui.common.ConditionUtil;

/**
 * ゲーム実施条件を保持するレコードです。<br>
 * 
 * @author nmby
 */
public record GameCondition(
        Class<? extends Player> playerBlack,
        Class<? extends Player> playerWhite,
        long givenMillis,
        boolean interactive) {
    
    // [static members] ********************************************************
    
    /**
     * ゲーム実施条件をコンソールから設定します。<br>
     * 
     * @return ゲーム実施条件
     */
    public static GameCondition arrangeViaConsole() {
        Class<? extends Player> playerBlack = ConditionUtil.arrangePlayer(
                "%sプレーヤー".formatted(Color.BLACK), true);
        System.out.println();
        Class<? extends Player> playerWhite = ConditionUtil.arrangePlayer(
                "%sプレーヤー".formatted(Color.WHITE), true);
        System.out.println();
        long givenMillis = ConditionUtil.arrangeMillis("");
        System.out.println();
        
        return new GameCondition(
                playerBlack,
                playerWhite,
                givenMillis,
                true);
    }
    
    // [instance members] ******************************************************
    
    /**
     * ゲーム実施条件を生成します。<br>
     * 
     * @param playerBlack 黒プレーヤークラス
     * @param playerWhite 白プレーヤークラス
     * @param givenMillis ゲームにおける1プレーヤーあたりの持ち時間（ミリ秒）
     * @param interactive ユーザーによる確認を求めながら進める場合は {@code true}
     * @throws NullPointerException {@code playerBlack}, {@code playerWhite} のいずれかが {@code null} の場合
     * @throws IllegalArgumentException {@code givenMillis} が 0 以下の場合
     */
    public GameCondition {
        Objects.requireNonNull(playerBlack, "playerBlack");
        Objects.requireNonNull(playerWhite, "playerWhite");
        if (givenMillis <= 0) {
            throw new IllegalArgumentException("givenMillis: " + givenMillis);
        }
    }
    
    @Override
    public String toString() {
        return (""
                + "%sプレーヤー : %s%n"
                + "%sプレーヤー : %s%n"
                + "各プレーヤーの持ち時間 : %d ミリ秒%n")
                        .formatted(
                                Color.BLACK, playerBlack.getName(),
                                Color.WHITE, playerWhite.getName(),
                                givenMillis);
    }
}
