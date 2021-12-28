package live.lingting.tools.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting
 */
class FileUtilsTest {

	@Test
	void getExt() {
		String path = "22.33.apk";
		Assertions.assertEquals(".apk", FileUtils.getExt(path));
	}

}
