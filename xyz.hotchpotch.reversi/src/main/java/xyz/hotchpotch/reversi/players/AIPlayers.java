package xyz.hotchpotch.reversi.players;

import java.util.List;

import xyz.hotchpotch.reversi.core.Player;

/**
 * AIプレーヤーに関するユーティリティクラスです。<br>
 * 
 * @author nmby
 */
public class AIPlayers {
    
    // [static members] ********************************************************
    
    /**
     * 既知のAIプレーヤークラスの一覧を返します。<br>
     * 
     * @return 既知のAIプレーヤークラスの一覧
     */
    public static List<Class<? extends Player>> list() {
        return List.of(
                SimplestAIPlayer.class,
                RandomAIPlayer.class,
                DepthFirstAIPlayer.class,
                BreadthFirstAIPlayer.class,
                MonteCarloAIPlayer.class,
                CrazyAIPlayer.class);
    }
    
    // [instance members] ******************************************************
    
    private AIPlayers() {
    }
}
