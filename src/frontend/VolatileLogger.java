package frontend;

import java.util.ArrayList;
import java.util.List;

public class VolatileLogger
{
    public static final List<LogEntry> exceptions = new ArrayList<>();
    
    public static void logf(Throwable exception, String message, Object... args)
    {
        final String msg = String.format(message, args);
        exceptions.add(new LogEntry(exception, msg));
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
