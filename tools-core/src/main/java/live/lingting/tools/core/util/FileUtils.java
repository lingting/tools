package live.lingting.tools.core.util;

import static live.lingting.tools.core.constant.FileConstants.POINT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import live.lingting.tools.core.constant.FileConstants;

/**
 * @author lingting
 */
@UtilityClass
public class FileUtils {

	private static final Map<String, String> MIME_TYPE;

	static {
		MIME_TYPE = new HashMap<>(16);
		MIME_TYPE.put(FileConstants.CSS_END, FileConstants.CSS_MIME);
		MIME_TYPE.put(FileConstants.JS_END, FileConstants.JS_MIME);
		MIME_TYPE.put(FileConstants.APK_END, FileConstants.APK_MIME);
	}

	/**
	 * 文件路径获取文件扩展名
	 * @param path 文件路径
	 * @return java.lang.String
	 */
	public static String getExt(String path) {
		if (!path.contains(POINT)) {
			return null;
		}

		return path.substring(path.lastIndexOf(POINT));
	}

	/**
	 * 根据文件扩展名获得MimeType
	 * @param path 文件路径或文件名
	 * @return MimeType
	 */
	public static String getMimeType(String path) throws IOException {
		String contentType = URLConnection.getFileNameMap().getContentTypeFor(path);
		if (!StringUtils.hasText(contentType)) {
			String ext = getExt(path);
			if (ext != null && MIME_TYPE.containsKey(ext)) {
				return MIME_TYPE.get(ext);
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

	/**
	 * 扫描指定路径下所有文件
	 * @param path 指定路径
	 * @param recursive 是否递归
	 * @return java.util.List<java.lang.String>
	 */
	public static List<String> scanFile(String path, boolean recursive) {
		List<String> list = new ArrayList<>();
		File file = new File(path);
		if (!file.exists()) {
			return list;
		}

		if (file.isFile()) {
			list.add(file.getAbsolutePath());
		}
		// 文件夹
		else {
			File[] files = file.listFiles();
			for (File childFile : files) {
				// 如果递归
				if (recursive && childFile.isDirectory()) {
					list.addAll(scanFile(childFile.getAbsolutePath(), true));
				}
				// 是文件
				else if (childFile.isFile()) {
					list.add(childFile.getAbsolutePath());
				}
			}
		}

		return list;
	}

}
