package xyz.hotchpotch.reversi.cui.common;

import xyz.hotchpotch.reversi.core.Board;
import xyz.hotchpotch.reversi.core.Color;
import xyz.hotchpotch.reversi.core.Player;
import xyz.hotchpotch.reversi.core.Point;

/**
 * ユーザーが標準入力から手入力で手を指定する {@link Player} の実装です。<br>
 * 
 * @author nmby
 */
public class ConsolePlayer implements Player {
    
    // [static members] ********************************************************
    
    private static final ConsoleScanner<Point> scanner = ConsoleScanner.<Point> builder()
            .judge(s -> {
                if ("PASS".equals(s.toUpperCase())) {
                    return true;
                }
                try {
                    Point.of(s.toLowerCase());
                    return true;
                } catch (RuntimeException e) {
                    return false;
                }
            })
            .converter(s -> {
                return "PASS".equals(s.toUpperCase())
                        ? null
                        : Point.of(s.toLowerCase());
            })
            .prompt("%na1～h8形式で手を指定してください。パスの場合は PASS と入力してください。%n> ".formatted())
            .build();
    
    // [instance members] ******************************************************
    
    @Override
    public Point decide(Board board, Color color, long remainingMillis) {
        return scanner.get();
    }
}
