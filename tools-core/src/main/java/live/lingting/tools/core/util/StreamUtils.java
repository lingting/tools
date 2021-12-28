package live.lingting.tools.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class StreamUtils {

	public static final int DEFAULT_SIZE = 1024 * 1024;

	public static void write(InputStream in, OutputStream out) throws IOException {
		write(in, out, DEFAULT_SIZE);
	}

	public static void write(InputStream in, OutputStream out, int size) throws IOException {
		byte[] bytes = new byte[size];
		int len;

		while (true) {
			len = in.read(bytes);
			if (len <= 0) {
				break;
			}

			out.write(bytes, 0, len);
		}
	}

}
