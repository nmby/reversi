package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class BoardTest {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    private static class TestBoard1 extends BoardBase {
        
        private TestBoard1(Map<Point, Color> map) {
            super(map);
        }
        
        @Override
        public Board getApplied(Move move) {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class TestBoard2 implements Board {
        
        private final Map<Point, Color> map;
        
        private TestBoard2(Map<Point, Color> map) {
            this.map = map;
        }
        
        @Override
        public Color colorAt(Point point) {
            return map.get(point);
        }
        
        @Override
        public Board getApplied(Move move) {
            throw new UnsupportedOperationException();
        }
    }
    
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
        Board test1a = new TestBoard1(initMap);
        Board test1b = new TestBoard1(allBlack);
        Board test1c = new TestBoard1(allEmpty);
        
        Board test2a = new TestBoard2(initMap);
        Board test2b = new TestBoard2(allBlack);
        Board test2c = new TestBoard2(allEmpty);
        
        assertThrows(NullPointerException.class, () -> Board.equals(null, test2a));
        assertThrows(NullPointerException.class, () -> Board.equals(test1a, null));
        assertThrows(NullPointerException.class, () -> Board.equals(null, null));
        
        assertTrue(Board.equals(test1a, test1a));
        
        assertTrue(Board.equals(test1a, test2a));
        assertTrue(Board.equals(test1b, test2b));
        assertTrue(Board.equals(test1c, test2c));
        
        assertFalse(Board.equals(test1a, test2b));
        assertFalse(Board.equals(test1b, test2c));
        assertFalse(Board.equals(test1c, test2a));
    }
    
    @Test
    void testHashCode() {
        assertThrows(NullPointerException.class, () -> Board.hashCode(null));
        
        assertEquals(initMap.hashCode(), Board.hashCode(new TestBoard1(initMap)));
        assertEquals(allBlack.hashCode(), Board.hashCode(new TestBoard1(allBlack)));
        assertEquals(allEmpty.hashCode(), Board.hashCode(new TestBoard1(allEmpty)));
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
                Board.toString(new TestBoard1(initMap)));
        
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
                Board.toString(new TestBoard1(allBlack)));
        
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
                Board.toString(new TestBoard1(linedMap)));
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
                Board.toStringInline(new TestBoard1(initMap)));
        
        assertEquals(""
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●"
                + "●●●●●●●●",
                Board.toStringInline(new TestBoard1(allBlack)));
        
        assertEquals(""
                + "・・・・・・・・"
                + "○○○○○○○○"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・"
                + "・・・・・・・・",
                Board.toStringInline(new TestBoard1(linedMap)));
    }
    
    @Test
    void testCounts() {
        assertEquals(Map.of(Color.BLACK, 2, Color.WHITE, 2), new TestBoard1(initMap).counts());
        assertEquals(Map.of(Color.BLACK, 64, Color.WHITE, 0), new TestBoard1(allBlack).counts());
        assertEquals(Map.of(Color.BLACK, 0, Color.WHITE, 64), new TestBoard1(allWhite).counts());
        assertEquals(Map.of(Color.BLACK, 0, Color.WHITE, 0), new TestBoard1(allEmpty).counts());
    }
}
