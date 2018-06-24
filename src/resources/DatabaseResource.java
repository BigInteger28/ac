package resources;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import engines.Database;

import static common.Constants.*;

public class DatabaseResource extends Resource
{
    private final File resource;

    public DatabaseResource(File resource)
    {
        this.resource = resource;
    }
    
    @Override
    public String getName()
    {
        return this.resource.getName();
    }
    
    @Override
    public String getPath()
    {
        return this.resource.getParentFile().getAbsolutePath();
    }
    
    public Database createDatabase() throws Exception
    {
        // Element:
        //   (one of)
        //   W V A L D w v a l d
        //
        // ElementSequence:
        //   Element [ElementSequence]
        //
        // DbEntry:
        //   ElementSequence § Element
        //
        // NextDbEntry:
        //   $ DbEntry [NextDbEntry]
        //
        // Db:
        //   DbEntry [NextDbEntry]
        //
        final List<Integer> db = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(this.resource)) {
            int c = in.read();
            if ((c & 0xEF) == 0xEF) {
                // BOM
                in.read();
                in.read();
                c = in.read();
            }
            int currententry = this.ctoe(c);
            boolean cleanexit = false;
            for (;;) {
                if (c == '$') {
                    if (currententry != 0) {
                        throw new Exception("unexpected NextDbEntry");
                    }
                } else if (c == '§') {
                    if (currententry == 0) {
                        throw new Exception("invalid, expected ElementSequence");
                    }
                    db.add((currententry << 4) | ((this.ctoe(in.read()) + 1) & 0xF));
                    currententry = 0;
                    cleanexit = true;
                } else if (c == 0xC2) {
                    // ??  (legacy) all ElementSequences seem to end with this
                    c = in.read();
                    if (c == -1) {
                        throw new Exception("unexpected EOF");
                    }
                    continue;
                } else {
                    currententry <<= 4;
                    currententry |= (this.ctoe(c) & 0xF);
                }
                c = in.read();
                if (c == -1) {
                    if (!cleanexit) {
                        throw new Exception("unexpected EOF");
                    }
                    break;
                }
                cleanexit = false;
            }
        }
        return new Database(db);
    }
    
    private int ctoe(int in) throws Exception
    {
        switch (in | 0x20) {
        case -1: throw new Exception("unexpected EOF");
        case 'w': return WATER;
        case 'v': return FIRE;
        case 'a': return EARTH;
        case 'l': return AIR;
        case 'd': return DEFENSE;
        }
        throw new Exception("invalid element: " + in);
    }
}
