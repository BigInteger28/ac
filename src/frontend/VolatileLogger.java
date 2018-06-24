package frontend;

import java.util.ArrayList;
import java.util.List;

public class VolatileLogger
{
    public static final List<LogEntry> exceptions = new ArrayList<>();
    
    public static void log(Throwable exception, String message)
    {
        exceptions.add(new LogEntry(exception, message));
        System.out.println("error while " + message);
        exception.printStackTrace();
    }

    public static void logf(Throwable exception, String message, Object... args)
    {
        log(exception, String.format(message, args));
    }
    
    public static class LogEntry
    {
        public final Throwable exception;
        public final String msg;

        private LogEntry(Throwable exception, String msg)
        {
            this.exception = exception;
            this.msg = msg;
        }
    }
}
