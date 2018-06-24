package resources;

import backend.Player;

public abstract class PlayerResource extends Resource
{
    public static final int TYPE_HUMAN = 0;
    public static final int TYPE_FIXED = 1;
    public static final int TYPE_DEPTH = 2;
    public static final Type[] TYPES = {
        new Type("Human", 0x555555),
        new Type("Fixed engine", 0xFFF494),
        new Type("Depth engine", 0xFFCD85),
    };

    public abstract Player createPlayer(int playerNumber) throws Exception;
}
