package frontend;

import java.awt.EventQueue;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

import common.ErrorHandler;
import frontend.util.SwingMsg;

public class ErrorHandlerFrontendImpl extends ErrorHandler
{
	@Override
	public void handleException(Throwable t, String message)
	{
		super.handleException(t, message);
		if (!Main.uiReady || EventQueue.isDispatchThread()) {
			this.showMsg(t, message);
		} else {
			SwingUtilities.invokeLater(() -> this.showMsg(t, message));
		}
	}

	private void showMsg(Throwable t, String message)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(message).append("\n");
		t.printStackTrace(new PrintStream(new OutputStream() {
			@Override
			public void write(int b)
			{
				sb.append((char) b);
			}
		}));
		SwingMsg.err_ok(null, t.getClass().getCanonicalName(), sb.toString());
	}
}
