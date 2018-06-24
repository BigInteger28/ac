package engines;

import java.util.Collections;
import java.util.List;

public class Database
{
    public static final Database EMPTY_DB = new Database(Collections.emptyList());

    private final List<Integer> db;
    
    public Database(List<Integer> db)
    {
        // db entries:
        // lowest 4 bits: result element
        // next 4 bits: element 1 (constants + 1)
        // next 4 bits: element 2
        // ...
        this.db = db;
    }
}
