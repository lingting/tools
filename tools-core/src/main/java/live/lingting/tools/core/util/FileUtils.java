package live.lingting.tools.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;
import live.lingting.tools.core.constant.FileConstants;

/**
 * @author lingting
 */
@UtilityClass
public class FileUtils {

	/**
	 * 根据文件扩展名获得MimeType
	 * @param path 文件路径或文件名
	 * @return MimeType
	 */
	public static String getMimeType(String path) throws IOException {
		String contentType = URLConnection.getFileNameMap().getContentTypeFor(path);
		if (null == contentType) {
			if (path.endsWith(FileConstants.CSS_END)) {
				return FileConstants.CSS_MIME;
			}
			else if (path.endsWith(FileConstants.JS_END)) {
				return FileConstants.JS_MIME;
			}
			else {
				return Files.probeContentType(Paths.get(path));
			}
		}

		return contentType;
	}

	public static FileInputStream getInputStream(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

}
