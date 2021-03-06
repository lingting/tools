package live.lingting.tools.core.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class StreamUtils {

	public static final int DEFAULT_SIZE = 1024 * 1024;

	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(in, out);
		return out.toByteArray();
	}

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

	public static String toString(InputStream in) throws IOException {
		return toString(in, DEFAULT_SIZE, StandardCharsets.UTF_8);
	}

	public static String toString(InputStream in, int size, Charset charset) throws IOException {
		byte[] bytes = new byte[size];
		int len;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		while (true) {
			len = in.read(bytes);
			if (len <= 0) {
				break;
			}
			outputStream.write(bytes, 0, len);
		}

		return outputStream.toString(charset.name());
	}

	/**
	 * 从流中读取 int
	 * @author lingting 2021-07-22 14:54
	 */
	public static int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
		int ret = 0;
		int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
		int cnt = bigEndian ? -8 : 8;
		for (int i = 0; i < noOfBytes; i++) {
			ret |= is.read() << sv;
			sv += cnt;
		}
		return ret;
	}

	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		}
		catch (Exception e) {
			//
		}
	}

}
