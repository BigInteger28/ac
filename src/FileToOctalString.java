import java.io.FileInputStream;
import java.io.InputStream;

public class FileToOctalString
{
	public static void main(String args[]) throws Throwable
	{
		try (InputStream in = new FileInputStream(args[0])) {
			System.out.print('"');
			for (int i = 0;; i++) {
				if (i % 30 == 0) {
					System.out.print("\"\n+ \"");
				}
				int value = in.read();
				if (value == -1) {
					break;
				}
				System.out.printf("\\%o", value);
			}
			System.out.print('"');
		}
	}
}
