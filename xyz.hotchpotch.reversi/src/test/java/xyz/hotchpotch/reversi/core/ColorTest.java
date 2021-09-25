package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ColorTest {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    @Test
    void testColor() {
        assertArrayEquals(new Color[] { Color.BLACK, Color.WHITE }, Color.values());
        
        assertSame(Color.BLACK, Color.valueOf("BLACK"));
        assertSame(Color.WHITE, Color.valueOf("WHITE"));
    }
    
    @Test
    void testReversed() {
        assertSame(Color.WHITE, Color.BLACK.reversed());
        assertSame(Color.BLACK, Color.WHITE.reversed());
    }
    
    @Test
    void testToString() {
        assertEquals("●", Color.BLACK.toString());
        assertEquals("○", Color.WHITE.toString());
    }
}
