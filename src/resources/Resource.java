package resources;

import java.awt.Color;

public abstract class Resource
{
	public abstract String getName();

	public abstract String getPath();

	public abstract int getType();

	public String getNameWithoutExtension()
	{
		String name = this.getName();
		int dot = name.lastIndexOf('.');
		if (dot == -1) {
			return name;
		}
		return name.substring(0, dot);
	}

	@Override
	public String toString()
	{
		return this.getName();
	}

	public static class Type
	{
		public final String name;
		public final Color color;

		Type(String name, int color)
		{
			this.name = name;
			this.color = new Color(color);
		}
	}
}
