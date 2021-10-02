package xyz.hotchpotch.reversi.cui.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.players.AIPlayers;

/**
 * 実施条件設定に関するユーティリティクラスです。<br>
 * 
 * @author nmby
 */
public class ConditionUtil {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    /**
     * プレーヤーの設定を行います。<br>
     * 
     * @param target 選択するプレーヤーの呼び名
     * @param allowHuman 人間プレーヤーを参加させる場合は {@code true}
     * @return プレーヤークラス
     * @throws NullPointerException {@code prompt} が {@code null} の場合
     */
    public static Class<? extends Player> arrangePlayer(
            String target,
            boolean allowHuman) {
        
        Objects.requireNonNull(target, "target");
        
        List<Class<? extends Player>> aiPlayers = AIPlayers.list();
        StringBuilder str = new StringBuilder();
        
        str.append(target).append("を次の中から選択してください。").append(BR);
        for (int i = 0; i < aiPlayers.size(); i++) {
            str.append("\t%d: %s%n".formatted(i + 1, aiPlayers.get(i).getName()));
        }
        if (allowHuman) {
            str.append("\t%d: 手動（自分で手を指定する）%n".formatted(aiPlayers.size() + 1));
        }
        str.append("\t0: その他（自作クラス）").append(BR);
        str.append("> ");
        
        ConsoleScanner<Integer> idxScanner = ConsoleScanner
                .intBuilder(0, aiPlayers.size() + (allowHuman ? 1 : 0))
                .prompt(str.toString())
                .build();
        
        while (true) {
            int idx = idxScanner.get();
            
            if (idx == 0) {
                Class<? extends Player> playerClass = playerScanner.get();
                if (playerClass != null) {
                    return playerClass;
                }
                
            } else if (idx <= aiPlayers.size()) {
                return aiPlayers.get(idx - 1);
                
            } else if (idx == aiPlayers.size() + 1 && allowHuman) {
                return ConsolePlayer.class;
                
            } else {
                throw new AssertionError(idx);
            }
        }
    }
    
    /**
     * 総当たり戦参加プレーヤーの設定を行います。<br>
     * 
     * @return プレーヤークラスのリスト
     */
    public static List<Class<? extends Player>> arrangePlayers() {
        Set<Integer> selected = new HashSet<>();
        List<Class<? extends Player>> candidates = new ArrayList<>(AIPlayers.list());
        
        while (true) {
            StringBuilder str = new StringBuilder(
                    "総当たり戦に参加させるプレーヤーを次の中から選択してください。" + BR);
            for (int i = 0; i < candidates.size(); i++) {
                str.append("\t%d: %s%s%n".formatted(
                        i + 1,
                        selected.contains(i) ? "【選択済み】" : "",
                        candidates.get(i).getName()));
            }
            str.append("\t0: その他（自作クラス）").append(BR);
            str.append("\t-1: 選択終了").append(BR);
            str.append("> ");
            
            ConsoleScanner<Integer> idxScanner = ConsoleScanner.intBuilder(-1, candidates.size())
                    .prompt(str.toString())
                    .build();
            
            int idx = idxScanner.get();
            
            switch (idx) {
            case -1:
                if (selected.size() < 2) {
                    System.out.println("複数のプレーヤーを選択してください。");
                } else {
                    return selected.stream()
                            .sorted()
                            .<Class<? extends Player>> map(i -> candidates.get(i))
                            .toList();
                }
                break;
            
            case 0:
                Class<? extends Player> playerClass = playerScanner.get();
                if (playerClass != null) {
                    selected.add(candidates.size());
                    candidates.add(playerClass);
                }
                break;
            
            default:
                if (selected.contains(idx - 1)) {
                    selected.remove(idx - 1);
                } else {
                    selected.add(idx - 1);
                }
                break;
            }
        }
    }
    
    private static final ConsoleScanner<Class<? extends Player>> playerScanner = ConsoleScanner
            .<Class<? extends Player>> builder()
            .judge(s -> {
                if (s.isEmpty()) {
                    return true;
                }
                try {
                    Class<?> playerClass = Class.forName(s);
                    return Player.class.isAssignableFrom(playerClass);
                } catch (ClassNotFoundException e) {
                    return false;
                }
            })
            .converter(s -> {
                if (s.isEmpty()) {
                    return null;
                }
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Player> playerClass = (Class<? extends Player>) Class.forName(s);
                    return playerClass;
                } catch (ClassNotFoundException e) {
                    throw new AssertionError(e);
                }
            })
            .prompt("プレーヤークラスの完全修飾クラス名を指定してください。"
                    + "（例：com.example.MyAIPlayer）" + BR
                    + "中断して戻る場合は何も入力せず Enter を押下してください。" + BR
                    + "> ")
            .complaint("クラスが見つからないか、%s を implements していません。%n"
                    .formatted(Player.class.getName()))
            .build();
    
    /**
     * 1ゲームあたりの持ち時間の設定を行います。<br>
     * 
     * @param prefix ユーザー向け説明
     * @return 1ゲームあたりの持ち時間
     * @throws NullPointerException {@code prefix} が {@code null} の場合
     */
    public static long arrangeMillis(String prefix) {
        Objects.requireNonNull(prefix, "prefix");
        
        final long MIN_MILLIS = 50;
        final long MAX_MILLIS = 1000 * 60 * 60;
        
        ConsoleScanner<Long> millisScanner = ConsoleScanner
                .longBuilder(MIN_MILLIS, MAX_MILLIS)
                .prompt("%s各プレーヤーの持ち時間を %dミリ秒～%dミリ秒 の範囲でミリ秒で指定してください。%n> "
                        .formatted(prefix, MIN_MILLIS, MAX_MILLIS))
                .build();
        
        return millisScanner.get();
    }
    
    /**
     * 対戦回数の設定を行います。<br>
     * 
     * @param prefix ユーザー向け説明
     * @return 対戦回数
     * @throws NullPointerException {@code prefix} が {@code null} の場合
     */
    public static int arrangeTimes(String prefix) {
        Objects.requireNonNull(prefix, "prefix");
        
        final int MAX_TIMES = 1000;
        
        ConsoleScanner<Integer> timesScanner = ConsoleScanner
                .intBuilder(1, MAX_TIMES)
                .prompt("%s対戦回数を 1～%d の範囲で指定してください。%n> "
                        .formatted(prefix, MAX_TIMES))
                .build();
        
        return timesScanner.get();
    }
    
    // [instance members] ******************************************************
    
    private ConditionUtil() {
    }
}
