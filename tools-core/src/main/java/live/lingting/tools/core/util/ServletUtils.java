package live.lingting.tools.core.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

/**
 * @author lingting 2022/10/28 17:54
 */
@UtilityClass
public class ServletUtils {

	public static String getUri(ServletRequest servletRequest) {
		if (servletRequest instanceof HttpServletRequest request) {
			return request.getRequestURI();
		}
		return null;
	}

}
