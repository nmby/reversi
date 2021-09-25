package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MoveTest {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    @Test
    void testMove() {
        assertThrows(NullPointerException.class, () -> new Move(null, Point.of(0, 0)));
        
        assertDoesNotThrow(() -> new Move(Color.BLACK, Point.of(0, 0)));
        assertDoesNotThrow(() -> new Move(Color.WHITE, null));
    }
    
    @Test
    void testToString() {
        assertEquals("● : PASS", new Move(Color.BLACK, null).toString());
        assertEquals("○ : a1", new Move(Color.WHITE, Point.of("a1")).toString());
    }
}
