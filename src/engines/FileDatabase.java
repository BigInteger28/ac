package engines;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import static common.Constants.*;

public class FileDatabase extends Database
{
	private File file;

	public FileDatabase(File file)
	{
		super(file.getName());
		this.file = file;
	}

	@Override
	public String getPath()
	{
		return this.file.getParentFile().getAbsolutePath();
	}

	@Override
	public void load() throws Exception
	{
		// Element:
		// (one of)
		// W V A L D w v a l d
		//
		// ElementSequence:
		// Element [ElementSequence]
		//
		// DbEntry:
		// ElementSequence \u00a7 Element
		//
		// NextDbEntry:
		// $ DbEntry [NextDbEntry]
		//
		// Db:
		// DbEntry [NextDbEntry]
		//
		this.db = new ArrayList<>();
		try (FileInputStream in = new FileInputStream(this.file)) {
			int c = in.read();
			if ((c & 0xEF) == 0xEF) {
				// BOM
				if (in.read() == -1) {
					return;
				}
				if (in.read() == -1) {
					return;
				}
				c = in.read();
			}
			if (c == -1) {
				return;
			}
			int currententry = 0x77777777;
			boolean cleanexit = false;
			for (;;) {
				if (c == '$') {
					if (currententry != 0x77777777) {
						throw new Exception("unexpected NextDbEntry");
					}
				} else if (c == '\u00A7') {
					if (currententry == 0x77777777) {
						throw new Exception("invalid, expected ElementSequence");
					}
					this.db.add((currententry << 4) | (this.ctoe(in.read()) & 0xF));
					currententry = 0x77777777;
					cleanexit = true;
				} else if (c == 0xC2) {
					// ?? (legacy) all ElementSequences seem to end with this
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
