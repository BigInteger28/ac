package common;

public class Constants {

    public static final String[] STANDARDELEMENTS;
    public static final char[] CHARELEMENTS;
    public static final String[] BELTS;

    public static final int AIR = 0;
    public static final int EARTH = 1;
    public static final int FIRE = 2;
    public static final int WATER = 3;
    public static final int DEFENSE = 4;
    
    public static final int[][] RESULTMATRIX = {
        {0, -1, 0, 1, 0}, //Air
        {1, 0, -1, 0, 0}, //Earth
        {0, 1, 0, -1, 0}, //Fire
        {-1, 0, 1, 0, 0}, //Water
        {0, 0, 0, 0, 0}, //Defense
    };

    static {
        STANDARDELEMENTS = new String[] { "Lucht", "Aarde", "Vuur", "Water", "Defensie" }; //voorlopig in het nederlands gezet
        CHARELEMENTS = new char[] { 'L', 'A', 'V', 'W', 'D' };//voorlopig in het nederlands
        BELTS = new String[] {"White", "Grey", "Yellow", "Orange", "Lime", "Green", "Turquoise", "Blue", "Dark Blue", "Magenta", "Purple", "Brown", "Red", "Black"}; //uitbreiding voor in de toekomst
    }
}
