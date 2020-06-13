package backend;

public class ResourceType
{
	public static ResourceType
		HUMAN = new ResourceType(0, "Human", 0x555555),
		FIXED = new ResourceType(1, "Fixed engine", 0xFFF494),
		DEPTH = new ResourceType(2, "Depth engine", 0xFFCD85),
		BUILTIN = new ResourceType(3, "Built-in engine", 0xE3B7EB);

	public static ResourceType
		DATABASE = new ResourceType(0, "Database", 0xFFCD85);

	public static ResourceType[] PLAYERTYPES = { HUMAN, FIXED, DEPTH, BUILTIN };
	public static ResourceType[] DATABASETYPES = { DATABASE };

	public final int index;
	public final String name;
	public final int color;

	private ResourceType(int index, String name, int color)
	{
		this.index = index;
		this.name = name;
		this.color = color;
	}
}
