package live.lingting.tools.core.util;

import lombok.experimental.UtilityClass;
import sun.awt.OSInfo;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author lingting 2022/6/25 12:10
 */
@UtilityClass
public class SystemUtils {

	/**
	 * 当前系统是否为Windows系统, 参考以下系统API
	 * @see OSInfo#getOSType()
	 * @return boolean
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
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

	public static File tmpDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	public static File tmpDirLingting() {
		return new File(System.getProperty("java.io.tmpdir"), "lingting.live");
	}

}
