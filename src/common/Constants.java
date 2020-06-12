package common;

public class Constants
{
	public static final char[] CHARELEMENTS = new char[] { 'W', 'V', 'A', 'L', 'D' };
	public static final String[] ELEMENTS = new String[] { "Water", "Vuur", "Aarde", "Lucht", "Defensie" };

	public static final int WATER = 0;
	public static final int FIRE = 1;
	public static final int EARTH = 2;
	public static final int AIR = 3;
	public static final int DEFENSE = 4;

	public static final int[][] RESULTMATRIX = {
		{ 0, 1, 0, -1, 0 }, // Water
		{ -1, 0, 1, 0, 0 }, // Fire
		{ 0, -1, 0, 1, 0 }, // Earth
		{ 1, 0, -1, 0, 0 }, // Air
		{ 0, 0, 0, 0, 0 }, // Defense
	};
}
