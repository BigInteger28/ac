package frontend.util;

import java.io.InputStream;

public class RawStringInputStream extends InputStream
{
	private char[] value;
	private int pos;

	public InputStream resetSetSrc(String value)
	{
		this.value = value.toCharArray();
		this.pos = 0;
		return this;
	}

	@Override
	public int read()
	{
		if (this.pos >= this.value.length) {
			return -1;
		}
		return this.value[this.pos++];
	}
}
