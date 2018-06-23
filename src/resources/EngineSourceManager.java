package resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import frontend.VolatileLogger;

public class EngineSourceManager
{
    private static final String LOCATIONSFILENAME;
    private static final File locationsfile;
    private static final List<File> locations;

    private static int lastAmountOfResources;
    
    static {
        LOCATIONSFILENAME = "locations.txt";
        lastAmountOfResources = 100;
        locationsfile = Resources.workingdir.resolve(LOCATIONSFILENAME).toFile();
        locations = new ArrayList<>();
        readLocations();
    }
    
    public static List<PlayerResource> collectResources(boolean includeHuman)
    {
        final List<PlayerResource> resources = new ArrayList<>(lastAmountOfResources);
        
        if (includeHuman) {
            resources.add(new PlayerResource(null, PlayerResource.HUMAN));
        }
        
        final Stack<File> walkerstack = new Stack<>();
        for (File loc : locations) {
            walkerstack.push(loc);
            while (!walkerstack.isEmpty()) {
                final File dir = walkerstack.pop();
                if (!dir.isDirectory()) {
                    continue;
                }
                
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        walkerstack.push(child);
                        continue;
                    }
                    
                    if (!child.isFile()) {
                        continue;
                    }
                    
                    final String name = child.getName();
                    final int dotloc = name.lastIndexOf('.');
                    if (dotloc == -1) {
                        continue;
                    }
                    final String extension = name.substring(dotloc);

                    if (".ak".equals(extension)) {
                        resources.add(new PlayerResource(child, PlayerResource.SAVED));
                    } else if (".akb".equals(extension)) {
                        resources.add(new PlayerResource(child, PlayerResource.ENGINE));
                    }
                }
            }
        }

        lastAmountOfResources = resources.size() + 20;
        return resources;
    }

    private static void readLocations()
    {
        locations.clear();
        if (!locationsfile.exists()) {
            return;
        }
        
        try {
            final List<String> lines = Files.readAllLines(locationsfile.toPath());
            for (String line : lines) {
                final File file = Resources.ptof(line);
                if (file != null) {
                    locations.add(file);
                }
            }
        } catch (IOException e) {
            VolatileLogger.logf(e, "reading %s file", LOCATIONSFILENAME);
        }
    }
    
    public static List<File> getLocations()
    {
        return new ArrayList<>(locations);
    }
    
    public static void setLocations(List<File> locations)
    {
        EngineSourceManager.locations.clear();
        EngineSourceManager.locations.addAll(locations);
        
        final List<String> lines = new ArrayList<>();
        for (File file : locations)
        {
            lines.add(file.getAbsolutePath());
        }
        
        try {
            Files.write(
                locationsfile.toPath(),
                lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            VolatileLogger.logf(e, "writing %s file", LOCATIONSFILENAME);
        }
    }
    
}
