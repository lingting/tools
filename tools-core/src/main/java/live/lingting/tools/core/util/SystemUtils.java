package live.lingting.tools.core.util;

import java.io.File;
import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;
import sun.awt.OSInfo;

/**
 * @author lingting 2022/6/25 12:10
 */
@UtilityClass
public class SystemUtils {

	public static boolean isWindows() {
		return OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS);
	}

	/**
	 * 获取系统字符集
	 */
	public static Charset charset() {
		return Charset.forName(System.getProperty("sun.jnu.encoding"));
	}

	public static String line() {
		return System.lineSeparator();
	}

	public static String file() {
		return File.separator;
	}

}
