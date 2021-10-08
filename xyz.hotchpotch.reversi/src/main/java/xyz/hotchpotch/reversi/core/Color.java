package xyz.hotchpotch.reversi.core;

/**
 * リバーシの石の色を表す列挙型です。<br>
 * 
 * @author nmby
 */
public enum Color {
    
    // [static members] ********************************************************
    
    /** 黒 */
    BLACK("●"),
    
    /** 白 */
    WHITE("○");
    
    // [instance members] ******************************************************
    
    private final String symbol;
    
    private Color(String symbol) {
        assert symbol != null;
        
        this.symbol = symbol;
    }
    
    /**
     * 自身と反対の色を返します。<br>
     * 
     * @return 自身と反対の色
     */
    public Color reversed() {
        return this == BLACK ? WHITE : BLACK;
    }
    
    @Override
    public String toString() {
        return symbol;
    }
}
