package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class DirectionTest {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    @Test
    void testDirection() {
        assertArrayEquals(
                new Direction[] {
                        Direction.UPPER,
                        Direction.UPPER_RIGHT,
                        Direction.RIGHT,
                        Direction.LOWER_RIGHT,
                        Direction.LOWER,
                        Direction.LOWER_LEFT,
                        Direction.LEFT,
                        Direction.UPPER_LEFT
                },
                Direction.values());
        
        assertSame(Direction.UPPER, Direction.valueOf("UPPER"));
        assertSame(Direction.UPPER_RIGHT, Direction.valueOf("UPPER_RIGHT"));
        assertSame(Direction.RIGHT, Direction.valueOf("RIGHT"));
        assertSame(Direction.LOWER_RIGHT, Direction.valueOf("LOWER_RIGHT"));
        assertSame(Direction.LOWER, Direction.valueOf("LOWER"));
        assertSame(Direction.LOWER_LEFT, Direction.valueOf("LOWER_LEFT"));
        assertSame(Direction.LEFT, Direction.valueOf("LEFT"));
        assertSame(Direction.UPPER_LEFT, Direction.valueOf("UPPER_LEFT"));
    }
    
    @Test
    void testStream() {
        List<Direction> values = List.of(Direction.values());
        
        assertEquals(values, Direction.stream().toList());
        assertFalse(Direction.stream().isParallel());
    }
}
