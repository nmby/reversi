package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class BoardTest {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    private static final Map<Point, Color> allBlack = Point.stream()
            .collect(Collectors.toMap(Function.identity(), p -> Color.BLACK));
    
    private static final Map<Point, Color> allWhite = Point.stream()
            .collect(Collectors.toMap(Function.identity(), p -> Color.WHITE));
    
    private static final Map<Point, Color> allEmpty = Map.of();
    
    private static final Map<Point, Color> initMap = Map.of(
            Point.of(3, 3), Color.WHITE,
            Point.of(4, 4), Color.WHITE,
            Point.of(3, 4), Color.BLACK,
            Point.of(4, 3), Color.BLACK);
    
    private static final Map<Point, Color> linedMap = Map.of(
            Point.of(1, 0), Color.WHITE,
            Point.of(1, 1), Color.WHITE,
            Point.of(1, 2), Color.WHITE,
            Point.of(1, 3), Color.WHITE,
            Point.of(1, 4), Color.WHITE,
            Point.of(1, 5), Color.WHITE,
            Point.of(1, 6), Color.WHITE,
            Point.of(1, 7), Color.WHITE);
    
    // [instance members] ******************************************************
    
    @Test
    void testInitBoard() {
        assertTrue(Board.initBoard() instanceof Board);
        assertEquals(""
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・○●・・・"
                + "・・・●○・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・",
                Board.toStringInline(Board.initBoard()));
    }
    
    @Test
    void testEquals() {
        Board test1a = new BoardImpl(initMap);
        Board test1b = new BoardImpl(initMap);
        Board test2 = new BoardImpl(allBlack);
        Board test3 = new BoardImpl(allEmpty);
        
        assertThrows(NullPointerException.class, () -> Board.equals(null, test1a));
        assertThrows(NullPointerException.class, () -> Board.equals(test2, null));
        assertThrows(NullPointerException.class, () -> Board.equals(null, null));
        
        assertTrue(Board.equals(test1a, test1a));
        assertTrue(Board.equals(test1a, test1b));
        
        assertFalse(Board.equals(test1a, test2));
        assertFalse(Board.equals(test2, test3));
        assertFalse(Board.equals(test3, test1a));
    }
    
    @Test
    void testHashCode() {
        assertThrows(NullPointerException.class, () -> Board.hashCode(null));
        
        assertEquals(initMap.hashCode(), Board.hashCode(new BoardImpl(initMap)));
        assertEquals(allBlack.hashCode(), Board.hashCode(new BoardImpl(allBlack)));
        assertEquals(allEmpty.hashCode(), Board.hashCode(new BoardImpl(allEmpty)));
    }
    
    @Test
    void testToString() {
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・・・・・・" + BR
                + "4 ・・・○●・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                Board.toString(new BoardImpl(initMap)));
        
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ●●●●●●●●" + BR
                + "2 ●●●●●●●●" + BR
                + "3 ●●●●●●●●" + BR
                + "4 ●●●●●●●●" + BR
                + "5 ●●●●●●●●" + BR
                + "6 ●●●●●●●●" + BR
                + "7 ●●●●●●●●" + BR
                + "8 ●●●●●●●●" + BR,
                Board.toString(new BoardImpl(allBlack)));
        
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ○○○○○○○○" + BR
                + "3 ・・・・・・・・" + BR
                + "4 ・・・・・・・・" + BR
                + "5 ・・・・・・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                Board.toString(new BoardImpl(linedMap)));
    }
    
    @Test
    void testToStringInline() {
        assertEquals(""
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・○●・・・"
                + "・・・●○・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・",
                Board.toStringInline(new BoardImpl(initMap)));
        
        assertEquals(""
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●",
                Board.toStringInline(new BoardImpl(allBlack)));
        
        assertEquals(""
                + "・・・・・・・・"
                + "○○○○○○○○"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・",
                Board.toStringInline(new BoardImpl(linedMap)));
    }
    
    @Test
    void testCounts() {
        assertEquals(Map.of(Color.BLACK, 2, Color.WHITE, 2), new BoardImpl(initMap).counts());
        assertEquals(Map.of(Color.BLACK, 64, Color.WHITE, 0), new BoardImpl(allBlack).counts());
        assertEquals(Map.of(Color.BLACK, 0, Color.WHITE, 64), new BoardImpl(allWhite).counts());
        assertEquals(Map.of(Color.BLACK, 0, Color.WHITE, 0), new BoardImpl(allEmpty).counts());
    }
}
