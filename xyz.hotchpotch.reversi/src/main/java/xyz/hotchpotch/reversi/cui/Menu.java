package xyz.hotchpotch.reversi.cui;

import xyz.hotchpotch.reversi.cui.common.ConsoleScanner;
import xyz.hotchpotch.reversi.cui.game.GameCondition;
import xyz.hotchpotch.reversi.cui.game.GameRunner;
import xyz.hotchpotch.reversi.cui.league.LeagueCondition;
import xyz.hotchpotch.reversi.cui.league.LeagueRunner;
import xyz.hotchpotch.reversi.cui.match.MatchCondition;
import xyz.hotchpotch.reversi.cui.match.MatchRunner;

/**
 * リバーシゲームの実行メニューを表す列挙型であり、
 * このアプリケーションのエントリポイントです。<br>
 * 
 * @author nmby
 */
public enum Menu {
    
    // [static members] ********************************************************
    
    /** 1回対戦 */
    GAME("1回対戦：2プレーヤーで1回対戦します。") {
        
        @Override
        public void execute() {
            GameCondition condition = GameCondition.arrangeViaConsole();
            GameRunner runner = GameRunner.of(condition);
            runner.run();
        }
    },
    
    /** 複数回対戦 */
    MATCH("複数回対戦：2プレーヤーで黒白を交代しながら複数回対戦します。") {
        
        @Override
        public void execute() {
            MatchCondition condition = MatchCondition.arrangeViaConsole();
            MatchRunner runner = MatchRunner.of(condition);
            runner.run();
        }
    },
    
    /** 総当たり戦 */
    LEAGUE("総当たり戦：複数プレーヤーで総当たり戦を行います。") {
        
        @Override
        public void execute() {
            LeagueCondition condition = LeagueCondition.arrangeViaConsole();
            LeagueRunner runner = LeagueRunner.of(condition);
            runner.run();
        }
    };
    
    /**
     * このアプリケーションのエントリポイントです。<br>
     * 
     * @param args 未使用
     */
    public static void main(String[] args) {
        ConsoleScanner<Menu> menuScanner = ConsoleScanner.enumBuilder(Menu.class).build();
        ConsoleScanner<Boolean> repeatScanner = ConsoleScanner.booleanBuilder()
                .prompt("もう一度行いますか？(y/N) > ")
                .build();
        
        System.out.println("リバーシを開始します。");
        
        do {
            System.out.println();
            Menu menu = menuScanner.get();
            System.out.println();
            menu.execute();
            System.out.println();
        } while (repeatScanner.get());
    }
    
    // [instance members] ******************************************************
    
    private final String desc;
    
    private Menu(String desc) {
        assert desc != null;
        
        this.desc = desc;
    }
    
    /**
     * このメニューを実行します。<br>
     */
    public abstract void execute();
    
    @Override
    public String toString() {
        return desc;
    }
}
