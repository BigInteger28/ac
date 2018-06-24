package engines;

import java.util.Collections;
import java.util.List;

import backend.Game;

public class Database
{
    public static final Database EMPTY_DB = new Database(Collections.emptyList());

    private final List<Integer> db;
    
    public Database(List<Integer> db)
    {
        // db entries:
        // lowest 4 bits: result element
        // next 4 bits: element 1
        // next 4 bits: element 2
        // ...
        // unused nibbles: 0x77
        this.db = db;
    }
    
    public int findEntry(Game.Data data, int playerNumber)
    {
        final int currentMove = data.getCurrentMove();
        if (currentMove == 0 || 7 < currentMove) {
            return -1;
        }
        
        final int p = playerNumber ^ 1;
        int hash = 0x77777777;
        for (int i = 0; i < currentMove; i++) {
            hash = hash << 4 | (data.getMove(p, i) & 0xF);
        }
        hash <<= 4;
        
        for (int v : db) {
            if ((v & 0xFFFFFFF0) == hash) {
                return v & 0xF;
            }
        }
        
        return -1;
    }
}
