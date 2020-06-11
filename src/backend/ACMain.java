package backend;

import common.ErrorHandler;
import engines.nuwanisl.NuwaniSL;
import resources.Resources;

public class ACMain
{
	public static void main()
	{
		System.out.println("working dir: " + Resources.workingdir.getAbsolutePath());

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e)
			{
				ErrorHandler.handler.handleException(e);
			}
		});

		NuwaniSL.init();
	}
}
