package common;

public class Constants {

    public static final String[] STANDARDELEMENTS;
    public static final char[] CHARELEMENTS;

    public static final int AIR = 0;
    public static final int EARTH = 1;
    public static final int FIRE = 2;
    public static final int WATER = 3;
    public static final int DEFENSE = 4;

    static {
        STANDARDELEMENTS = new String[] { "Air", "Earth", "Fire", "Water", "Defense" };
        CHARELEMENTS = new char[] { 'A', 'E', 'F', 'W', 'D' };
    }

}
