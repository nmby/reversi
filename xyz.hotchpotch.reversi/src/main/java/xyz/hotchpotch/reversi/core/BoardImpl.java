package xyz.hotchpotch.reversi.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link Board} の標準的な実装です。
 * このクラスは不変です。<br>
 * 
 * @author nmby
 */
/*package*/ final class BoardImpl implements Board {
    
    // [static members] ********************************************************
    
    // [instance members] ******************************************************
    
    /** 石の配置を保持するマップ */
    private final Map<Point, Color> map;
    
    /*package*/ BoardImpl() {
        assert 2 <= Point.HEIGHT;
        assert 2 <= Point.WIDTH;
        
        int ci = Point.HEIGHT / 2 - 1;
        int cj = Point.WIDTH / 2 - 1;
        
        this.map = Map.of(
                Point.of(ci, cj), Color.WHITE,
                Point.of(ci + 1, cj + 1), Color.WHITE,
                Point.of(ci + 1, cj), Color.BLACK,
                Point.of(ci, cj + 1), Color.BLACK);
    }
    
    /*package*/ BoardImpl(Map<Point, Color> map) {
        assert map != null;
        
        // 防御的コピーを行うとともに、nullマッピングを除外して正規化する。
        this.map = Point.stream()
                .filter(p -> map.get(p) != null)
                .collect(Collectors.toMap(
                        Function.identity(),
                        map::get));
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException {@code point} が {@code null} の場合
     */
    @Override
    public Color colorAt(Point point) {
        Objects.requireNonNull(point, "point");
        
        return map.get(point);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException {@code move} が {@code null} の場合
     * @throws IllegalArgumentException このリバーシ盤に指定された手を適用できない場合
     */
    @Override
    public Board getApplied(Move move) {
        Objects.requireNonNull(move, "move");
        if (!Rule.canApply(this, move)) {
            throw new IllegalArgumentException("illegal move");
        }
        
        if (move.point() != null) {
            Map<Point, Color> copy = new HashMap<>(map);
            Set<Point> reversibles = Rule.reversibles(this, move);
            
            reversibles.forEach(p -> copy.put(p, move.color()));
            copy.put(move.point(), move.color());
            
            return new BoardImpl(copy);
            
        } else {
            return this;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Board b) {
            return Board.equals(this, b);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Board.hashCode(this);
    }
    
    @Override
    public String toString() {
        return Board.toString(this);
    }
}
