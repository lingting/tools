package live.lingting.tools.core.util;

import live.lingting.tools.core.constant.CharConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting
 */

class RandomUtilsTest {

	@Test
	void nextInt() {
		final int i = RandomUtils.nextInt(10, 100);
		Assertions.assertTrue(i >= 10 && i < 100);
	}

	@Test
	void nextStr() {
		final String str1 = RandomUtils.nextStr(10);
		Assertions.assertEquals(10, str1.length());
		final String str2 = RandomUtils.nextStr("123456789", 100);
		Assertions.assertFalse(str2.contains("0"));
	}

	@Test
	void nextHex() {
		final String hex = RandomUtils.nextHex(10);
		Assertions.assertNotEquals(CharConstants.ZERO, hex.charAt(0));
		final long l = Long.parseLong(hex, 16);
		Assertions.assertTrue(l > 0);
	}

}
