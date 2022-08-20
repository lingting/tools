package live.lingting.tools.core.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

/**
 * @author lingting
 */
class FileUtilsTest {

	@Test
	void getExt() {
		String path = "22.33.apk";
		Assertions.assertEquals(".apk", FileUtils.getExt(path));
	}

	@Test
	@SneakyThrows
	void create() {
		File dir = new File(SystemUtils.tmpDirLingting(), "create");
		if (dir.exists()) {
			dir.delete();
		}

		FileUtils.createDir(dir);
		Assertions.assertTrue(Files.exists(dir.toPath()));
		dir.delete();
		File file = new File(dir, "123");
		FileUtils.createFile(file);
		Assertions.assertTrue(Files.exists(file.toPath()));
	}

}
