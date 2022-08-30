package live.lingting.tools.oss.domain;

import live.lingting.tools.core.util.FileUtils;
import live.lingting.tools.core.util.SystemUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author lingting 2021/5/10 15:34
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class StreamSize {

	private static final File TEMP_DIR = new File(SystemUtils.tmpDirLingting(), "oss");

	private final Long size;

	private final InputStream stream;

	/**
	 * 计算流大小并克隆(只读流)
	 * <p color="red">
	 * 使用后请及时关闭对应的流
	 * </p>
	 * @param stream 源流
	 * @return 记录大小和科隆流的对象
	 */
	public static StreamSize of(InputStream stream) throws IOException {
		FileUtils.createDir(TEMP_DIR);
		File file = FileUtils.createTemp("size", TEMP_DIR);

		try (final FileOutputStream out = new FileOutputStream(file)) {
			long size = 0;
			byte[] buffer = new byte[1024];
			int len;

			while ((len = stream.read(buffer)) > -1) {
				size += len;
				out.write(buffer, 0, len);
			}

			return new StreamSize(size, Files.newInputStream(file.toPath()));
		}
	}

}
