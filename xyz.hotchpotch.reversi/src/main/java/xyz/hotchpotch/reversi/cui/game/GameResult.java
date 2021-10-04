package xyz.hotchpotch.reversi.cui.game;

import java.util.Map;
import java.util.Objects;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Rule;

/**
 * ゲーム（1回対戦）実施結果を保持するレコードです。<br>
 * 
 * @author nmby
 */
public record GameResult(
        Board board,
        Color winner,
        Throwable exception,
        String desc) {
    
    // [static members] ********************************************************
    
    /**
     * 正常にゲーム終了した場合のゲーム実施結果を生成して返します。<br>
     * 
     * @param board ゲーム終了時のリバーシ盤
     * @return ゲーム実施結果
     * @throws NullPointerException {@code board} が {@code null} の場合
     */
    public static GameResult ofNormalEnd(Board board) {
        Objects.requireNonNull(board, "board");
        
        Color winner = Rule.winner(board);
        Map<Color, Integer> counts = board.counts();
        
        return new GameResult(
                board,
                winner,
                null,
                "ゲームが終了しました。"
                        + (winner == null ? "引き分けです。" : "%sの勝ちです。".formatted(winner))
                        + "（%s:%d, %s:%d）".formatted(
                                Color.BLACK, counts.get(Color.BLACK),
                                Color.WHITE, counts.get(Color.WHITE)));
    }
    
    /**
     * ルール違反の手によりゲームが終了した場合のゲーム実施結果を生成して返します。<br>
     * 
     * @param board ゲーム終了時のリバーシ盤
     * @param violator ルール違反を犯したプレーヤーの色
     * @return ゲーム実施結果
     * @throws NullPointerException {@code board}, {@code violator} のいずれかが {@code null} の場合
     */
    public static GameResult ofRuleViolation(Board board, Color violator) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(violator, "violator");
        
        return new GameResult(
                board,
                violator.reversed(),
                null,
                "ルール違反の手が指定されました。%sの負けです。".formatted(violator));
    }
    
    /**
     * 片方のプレーヤークラスのインスタンス化に失敗した場合の不戦敗を表すゲーム実施結果を生成して返します。<br>
     * 
     * @param failed インスタンス化に失敗したプレーヤーの色
     * @param e インスタンス化失敗時に発生した例外
     * @return ゲーム実施結果
     * @throws NullPointerException {@code failed}, {@code e} のいずれかが {@code null} の場合
     */
    public static GameResult ofFailToCreatePlayer(Color failed, Throwable e) {
        Objects.requireNonNull(failed, "failed");
        Objects.requireNonNull(e, "e");
        
        return new GameResult(
                null,
                failed.reversed(),
                e,
                "%sプレーヤークラスのインスタンス化に失敗しました。%sの不戦敗です。%n%s%n"
                        .formatted(failed, failed, e));
    }
    
    /**
     * 双方のプレーヤークラスのインスタンス化に失敗した場合のゲーム不成立を表すゲーム実施結果を生成して返します。<br>
     * 
     * @param eBlack 黒プレーヤーのインスタンス化失敗時に発生した例外
     * @param eWhite 白プレーヤーのインスタンス化失敗時に発生した例外
     * @return ゲーム実施結果
     * @throws NullPointerException {@code eBlack}, {@code eWhite} のいずれかが {@code null} の場合
     */
    public static GameResult ofFailToCreatePlayers(Throwable eBlack, Throwable eWhite) {
        Objects.requireNonNull(eBlack, "eBlack");
        Objects.requireNonNull(eWhite, "eWhite");
        
        eBlack.addSuppressed(eWhite);
        
        return new GameResult(
                null,
                null,
                eBlack,
                "%s%s双方のプレーヤークラスのインスタンス化に失敗しました。ゲーム不成立です。%n"
                        + "%s: %s%n"
                        + "%s: %s%n".formatted(
                                Color.BLACK, Color.WHITE,
                                Color.BLACK, eBlack,
                                Color.WHITE, eWhite));
    }
    
    // [instance members] ******************************************************
    
    /**
     * ゲーム（1回対戦）実施結果を生成します。<br>
     * 
     * @param board ゲーム終了時点のリバーシ盤
     * @param winner 勝者の色（引き分けの場合は {@code null}）
     * @param exception ゲーム終了の原因となった例外
     * @param desc ゲーム実施結果の説明
     * @throws NullPointerException {@code desc} が {@code null} の場合
     */
    public GameResult {
        Objects.requireNonNull(desc, "desc");
    }
    
    @Override
    public String toString() {
        return desc;
    }
}
