package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class RuleTest {
    
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
    
    // [instance members] ******************************************************
    
    @Test
    void testIsGameOngoing() {
        assertTrue(Rule.isGameOngoing(new BoardImpl(initMap)));
        
        assertFalse(Rule.isGameOngoing(new BoardImpl(allBlack)));
        assertFalse(Rule.isGameOngoing(new BoardImpl(allEmpty)));
        
        assertThrows(NullPointerException.class, () -> Rule.isGameOngoing(null));
    }
    
    @Test
    void testWinner() {
        assertSame(Color.BLACK, Rule.winner(new BoardImpl(allBlack)));
        assertSame(Color.WHITE, Rule.winner(new BoardImpl(allWhite)));
        assertNull(Rule.winner(new BoardImpl(allEmpty)));
        
        assertThrows(IllegalStateException.class, () -> Rule.winner(new BoardImpl()));
        assertThrows(NullPointerException.class, () -> Rule.winner(null));
    }
    
    @Test
    void testCanPut() {
        assertTrue(Rule.canPut(new BoardImpl(), Color.BLACK));
        assertTrue(Rule.canPut(new BoardImpl(), Color.WHITE));
        assertFalse(Rule.canPut(new BoardImpl(allBlack), Color.BLACK));
        assertFalse(Rule.canPut(new BoardImpl(allBlack), Color.WHITE));
        
        assertThrows(NullPointerException.class, () -> Rule.canPut(null, Color.BLACK));
        assertThrows(NullPointerException.class, () -> Rule.canPut(new BoardImpl(), null));
    }
    
    @Test
    void testCanPutAt() {
        Board testBoard = new BoardImpl();
        
        for (Point p : Point.values()) {
            if (List.of("e3", "f4", "c5", "d6").contains(p.pos())) {
                assertTrue(Rule.canPutAt(testBoard, Color.WHITE, p));
            } else {
                assertFalse(Rule.canPutAt(testBoard, Color.WHITE, p));
            }
        }
        
        assertThrows(NullPointerException.class, () -> Rule.canPutAt(null, Color.BLACK, Point.of("a1")));
        assertThrows(NullPointerException.class, () -> Rule.canPutAt(testBoard, null, Point.of("a1")));
        assertThrows(NullPointerException.class, () -> Rule.canPutAt(testBoard, Color.BLACK, null));
    }
    
    @Test
    void testCanApply() {
        Board testBoard1 = new BoardImpl();
        Board testBoard2 = new BoardImpl(allEmpty);
        
        assertTrue(Rule.canApply(testBoard1, new Move(Color.BLACK, Point.of("d3"))));
        assertTrue(Rule.canApply(testBoard1, new Move(Color.WHITE, Point.of("e3"))));
        assertFalse(Rule.canApply(testBoard1, new Move(Color.BLACK, Point.of("a1"))));
        assertFalse(Rule.canApply(testBoard1, new Move(Color.WHITE, null)));
        
        assertTrue(Rule.canApply(testBoard2, new Move(Color.BLACK, null)));
        assertTrue(Rule.canApply(testBoard2, new Move(Color.WHITE, null)));
        assertFalse(Rule.canApply(testBoard2, new Move(Color.BLACK, Point.of("a1"))));
        assertFalse(Rule.canApply(testBoard2, new Move(Color.WHITE, Point.of("h8"))));
    }
    
    @Test
    void testReversibles() {
        Board testBoard = new BoardImpl();
        Move move = new Move(Color.BLACK, Point.of("d3"));
        
        assertEquals(
                Set.of(Point.of("d4")),
                Rule.reversibles(testBoard, move));
        
        assertThrows(NullPointerException.class, () -> Rule.reversibles(null, move));
        assertThrows(NullPointerException.class, () -> Rule.reversibles(testBoard, null));
        
        assertThrows(
                IllegalArgumentException.class,
                () -> Rule.reversibles(testBoard, new Move(Color.WHITE, Point.of("d3"))));
        assertThrows(
                IllegalArgumentException.class,
                () -> Rule.reversibles(testBoard, new Move(Color.BLACK, null)));
    }
}
