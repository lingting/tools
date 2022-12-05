package live.lingting.tools.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author lingting 2022/11/24 10:34
 */
class SystemUtilsTest {

	@Test
	void workDir() {
		File file = SystemUtils.workDir();
		System.out.println(file.getAbsolutePath());
		Assertions.assertNotNull(file);
	}

	@Test
	void homeDir() {
		File file = SystemUtils.homeDir();
		System.out.println(file.getAbsolutePath());
		Assertions.assertNotNull(file);
	}

	@Test
	void username() {
		String username = SystemUtils.username();
		System.out.println(username);
		Assertions.assertNotNull(username);
	}

}
