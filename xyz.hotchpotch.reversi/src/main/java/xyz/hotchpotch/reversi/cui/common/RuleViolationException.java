package xyz.hotchpotch.reversi.cui.common;

import xyz.hotchpotch.reversi.cui.game.GameResult;

/**
 * ルール違反が発生したことを表す例外です。<br>
 * 
 * @author nmby
 */
public class RuleViolationException extends Exception {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    private final GameResult gameResult;
    
    /**
     * 例外オブジェクトを生成します。<br>
     * 
     * @param gameResult ルール違反によって確定したゲーム結果
     */
    public RuleViolationException(GameResult gameResult) {
        this.gameResult = gameResult;
    }
    
    /**
     * このルール違反によって確定したゲーム結果を返します。<br>
     * 
     * @return このルール違反によって確定したゲーム結果
     */
    public GameResult gameResult() {
        return gameResult;
    }
}
