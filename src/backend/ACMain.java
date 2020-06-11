package backend;

import common.ErrorHandler;
import engines.nuwanisl.NuwaniSL;

public class ACMain
{
	public static void main()
	{
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e)
			{
				ErrorHandler.handler.handleException(e);
			}
		});

		NuwaniSL.init();
	}
}
