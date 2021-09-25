package xyz.hotchpotch.reversi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

class PointTest {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    @Test
    void testPoint() {
        assertEquals(8, Point.HEIGHT);
        assertEquals(8, Point.WIDTH);
    }
    
    @Test
    void testValues() {
        Point[] values = Point.values();
        
        assertEquals(Point.HEIGHT * Point.WIDTH, values.length);
        
        for (int i = 0; i < Point.HEIGHT; i++) {
            for (int j = 0; j < Point.WIDTH; j++) {
                int n = Point.WIDTH * i + j;
                Point p = values[n];
                
                assertEquals(i, p.i());
                assertEquals(j, p.j());
            }
        }
    }
    
    @Test
    void testStream() {
        List<Point> values = List.of(Point.values());
        
        assertEquals(values, Point.stream().toList());
        assertFalse(Point.stream().isParallel());
    }
    
    @Test
    void testOfIntInt() {
        assertThrows(IndexOutOfBoundsException.class, () -> Point.of(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> Point.of(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> Point.of(Point.HEIGHT, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> Point.of(0, Point.WIDTH));
        
        for (int i = 0; i < Point.HEIGHT; i++) {
            for (int j = 0; j < Point.WIDTH; j++) {
                Point p = Point.of(i, j);
                
                assertEquals(i, p.i());
                assertEquals(j, p.j());
            }
        }
    }
    
    @Test
    void testOfString() {
        assertThrows(NullPointerException.class, () -> Point.of(null));
        
        assertThrows(IllegalArgumentException.class, () -> Point.of(""));
        assertThrows(IllegalArgumentException.class, () -> Point.of("a"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("a0"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("a9"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("i1"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("i8"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("A1"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("H8"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("xyz"));
        assertThrows(IllegalArgumentException.class, () -> Point.of("12345"));
        
        for (char c = 'a'; c < 'a' + Point.WIDTH; c++) {
            for (int i = 1; i <= Point.HEIGHT; i++) {
                String pos = "" + c + i;
                Point p = Point.of(pos);
                
                assertEquals(pos, p.pos());
            }
        }
    }
    
    @Test
    void testHasNext() {
        for (int i = 0; i < Point.HEIGHT; i++) {
            for (int j = 0; j < Point.WIDTH; j++) {
                Point p = Point.of(i, j);
                
                assertThrows(NullPointerException.class, () -> p.hasNext(null));
                
                assertEquals(0 < i, p.hasNext(Direction.UPPER));
                assertEquals(0 < i && j < Point.WIDTH - 1, p.hasNext(Direction.UPPER_RIGHT));
                assertEquals(j < Point.WIDTH - 1, p.hasNext(Direction.RIGHT));
                assertEquals(i < Point.HEIGHT - 1 && j < Point.WIDTH - 1, p.hasNext(Direction.LOWER_RIGHT));
                assertEquals(i < Point.HEIGHT - 1, p.hasNext(Direction.LOWER));
                assertEquals(i < Point.HEIGHT - 1 && 0 < j, p.hasNext(Direction.LOWER_LEFT));
                assertEquals(0 < j, p.hasNext(Direction.LEFT));
                assertEquals(0 < i && 0 < j, p.hasNext(Direction.UPPER_LEFT));
            }
        }
    }
    
    @Test
    void testNext() {
        for (int i = 0; i < Point.HEIGHT; i++) {
            for (int j = 0; j < Point.WIDTH; j++) {
                Point p = Point.of(i, j);
                
                assertThrows(NullPointerException.class, () -> p.next(null));
                
                for (Direction direction : Direction.values()) {
                    if (p.hasNext(direction)) {
                        assertSame(Point.of(i + direction.di, j + direction.dj), p.next(direction));
                    } else {
                        assertThrows(NoSuchElementException.class, () -> p.next(direction));
                    }
                }
            }
        }
    }
    
    @Test
    void testToString() {
        assertEquals("a1", Point.of(0, 0).toString());
        assertEquals("a8", Point.of(7, 0).toString());
        assertEquals("h1", Point.of(0, 7).toString());
        assertEquals("h8", Point.of(7, 7).toString());
        
        for (int i = 0; i < Point.HEIGHT; i++) {
            for (int j = 0; j < Point.WIDTH; j++) {
                Point p = Point.of(i, j);
                String pos = "%c%d".formatted('a' + j, 1 + i);
                
                assertEquals(pos, p.toString());
            }
        }
    }
}
