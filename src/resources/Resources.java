package resources;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import frontend.VolatileLogger;

public class Resources
{
    public static Path workingdir;

    static {
        workingdir = Paths.get(".").toAbsolutePath().normalize();
    }
    
    public static File ptof(String path)
    {
        if (path == null) {
            return null;
        }
        try {
            return Paths.get(path).toAbsolutePath().normalize().toFile();
        } catch (Exception e) {
            VolatileLogger.logf(e, "path '%s' to file", path);
        }
        return null;
    }
}
