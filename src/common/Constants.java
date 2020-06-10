package common;

public class Constants
{

	public static final String[] STANDARDELEMENTS;
	public static final char[] CHARELEMENTS;
	public static final String[] BELTS;

	public static final int WATER = 0;
	public static final int FIRE = 1;
	public static final int EARTH = 2;
	public static final int AIR = 3;
	public static final int DEFENSE = 4;

	public static final int[][] RESULTMATRIX = { { 0, 1, 0, -1, 0 }, // Water
		{ -1, 0, 1, 0, 0 }, // Fire
		{ 0, -1, 0, 1, 0 }, // Earth
		{ 1, 0, -1, 0, 0 }, // Air
		{ 0, 0, 0, 0, 0 }, // Defense
	};

	static {
		STANDARDELEMENTS = new String[] { "Water", "Vuur", "Aarde", "Lucht", "Defensie" }; // voorlopig in het
													// nederlands
													// gezet
		CHARELEMENTS = new char[] { 'W', 'V', 'A', 'L', 'D' };// voorlopig in het nederlands
		BELTS = new String[] { "White", "Grey", "Yellow", "Orange", "Lime", "Green", "Turquoise", "Blue",
			"Dark Blue", "Magenta", "Purple", "Brown", "Red", "Black" }; // uitbreiding voor in de
											// toekomst
	}
}
