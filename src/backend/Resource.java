package backend;

public interface Resource
{
	ResourceType getType();
	String getName();
	String getPath();

	default String getNameWithoutExtension()
	{
		String value = this.getName();
		if (value.endsWith(".ak")) {
			return value.substring(0, value.length() - 3);
		}
		if (value.endsWith(".akb") || value.endsWith(".adb")) {
			return value.substring(0, value.length() - 4);
		}
		return value;
	}
}
