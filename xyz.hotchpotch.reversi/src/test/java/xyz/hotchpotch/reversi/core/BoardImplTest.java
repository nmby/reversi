package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class BoardImplTest {
    
    // [static members] ********************************************************
    
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
    
    private static final String BR = System.lineSeparator();
    
    // [instance members] ******************************************************
    
    @Test
    void testConstructor() {
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
                new BoardImpl().toString());
        
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
                new BoardImpl(linedMap).toString());
    }
    
    @Test
    void testColorAt() {
        Board testee = new BoardImpl();
        
        for (Point p : Point.values()) {
            if ("d4".equals(p.pos()) || "e5".equals(p.pos())) {
                assertSame(Color.WHITE, testee.colorAt(p));
            } else if ("d5".equals(p.pos()) || "e4".equals(p.pos())) {
                assertSame(Color.BLACK, testee.colorAt(p));
            } else {
                assertNull(testee.colorAt(p));
            }
        }
    }
    
    @Test
    void testGetApplied1() {
        Board testee1 = new BoardImpl();
        
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・●・・・・" + BR
                + "4 ・・・●●・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                testee1.getApplied(new Move(Color.BLACK, Point.of("d3"))).toString());
        
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・・・・・・" + BR
                + "4 ・・●●●・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                testee1.getApplied(new Move(Color.BLACK, Point.of("c4"))).toString());
        
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・・・・・・" + BR
                + "4 ・・・○●・・・" + BR
                + "5 ・・・●●●・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                testee1.getApplied(new Move(Color.BLACK, Point.of("f5"))).toString());
        
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・・・・・・" + BR
                + "4 ・・・○●・・・" + BR
                + "5 ・・・●●・・・" + BR
                + "6 ・・・・●・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                testee1.getApplied(new Move(Color.BLACK, Point.of("e6"))).toString());
    }
    
    @Test
    void testGetApplied2() {
        Board testee1 = new BoardImpl();
        
        assertThrows(
                NullPointerException.class,
                () -> testee1.getApplied(null));
        
        // ルール違反
        assertThrows(
                IllegalArgumentException.class,
                () -> testee1.getApplied(new Move(Color.BLACK, Point.of("a1"))));
        assertThrows(
                IllegalArgumentException.class,
                () -> testee1.getApplied(new Move(Color.BLACK, null)));
    }
    
    @Test
    void testGetApplied3() {
        Board testee1 = new BoardImpl();
        
        Board next = testee1.getApplied(new Move(Color.BLACK, Point.of("d3")));
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・●・・・・" + BR
                + "4 ・・・●●・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                next.toString());
        
        next = next.getApplied(new Move(Color.WHITE, Point.of("e3")));
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・●○・・・" + BR
                + "4 ・・・●○・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                next.toString());
        
        next = next.getApplied(new Move(Color.BLACK, Point.of("f3")));
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・・・・・" + BR
                + "3 ・・・●●●・・" + BR
                + "4 ・・・●●・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                next.toString());
        
        next = next.getApplied(new Move(Color.WHITE, Point.of("e2")));
        assertEquals(""
                + "  a b c d e f g h " + BR
                + "1 ・・・・・・・・" + BR
                + "2 ・・・・○・・・" + BR
                + "3 ・・・●○●・・" + BR
                + "4 ・・・●○・・・" + BR
                + "5 ・・・●○・・・" + BR
                + "6 ・・・・・・・・" + BR
                + "7 ・・・・・・・・" + BR
                + "8 ・・・・・・・・" + BR,
                next.toString());
    }
    
    @Test
    void testGetApplied4() {
        Board testee1 = new BoardImpl(linedMap);
        
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
                testee1.getApplied(new Move(Color.BLACK, null)).toString());
    }
    
    @Test
    void testEquals() {
        Board test1a = new BoardImpl(initMap);
        Board test1b = new BoardImpl(initMap);
        Board test2 = new BoardImpl(allBlack);
        
        assertTrue(test1a.equals(test1a));
        assertTrue(test1a.equals(test1b));
        
        assertFalse(test1a.equals(test2));
        assertFalse(test1a.equals(null));
        assertFalse(test1a.equals("abc"));
    }
    
    @Test
    void testHashCode() {
        assertEquals(initMap.hashCode(), new BoardImpl().hashCode());
        assertEquals(allEmpty.hashCode(), new BoardImpl(Map.of()).hashCode());
        assertEquals(allBlack.hashCode(), new BoardImpl(allBlack).hashCode());
        assertEquals(allWhite.hashCode(), new BoardImpl(allWhite).hashCode());
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
                new BoardImpl().toString());
        
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
                new BoardImpl(allBlack).toString());
        
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
                new BoardImpl(linedMap).toString());
    }
}
