package live.lingting.tools.core.util;

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

}
