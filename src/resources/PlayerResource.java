package resources;

import java.awt.Color;

import backend.Player;

public abstract class PlayerResource extends Resource
{
    @Override
    public abstract String getName();
    @Override
    public abstract String getPath();
    public abstract Type getType();
    public abstract Player createPlayer(int playerNumber) throws Exception;
    
    public static enum Type
    {
        HUMAN(0x555555, "Human"),
        FIXED(0xFFF494, "Fixed engine"),
        DEPTH(0xffcd85, "Depth engine");
        
        public final Color color;
        public final String name;

        private Type(int color, String name) {
            this.color = new Color(color);
            this.name = name;
        }
    }
}
