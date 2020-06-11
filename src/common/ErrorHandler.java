package common;

public class ErrorHandler
{
	public static ErrorHandler handler;

	public void handleException(Throwable t)
	{
		handleException(t, t.toString());
	}

	public void handleException(Throwable t, String message)
	{
		System.err.println(message);
		t.printStackTrace();
	}
}
