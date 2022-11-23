package live.lingting.tools.http.core;

import live.lingting.tools.core.util.CollectionUtils;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingting 2022/11/17 16:42
 */
public class HttpKeepCookieJar implements CookieJar {

	private final Map<String, List<Cookie>> cache = new HashMap<>();

	@NotNull
	@Override
	public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
		List<Cookie> cookies = cache.get(httpUrl.host());

		if (CollectionUtils.isEmpty(cookies)) {
			cookies = new ArrayList<>();
		}

		return cookies;
	}

	@Override
	public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
		for (Cookie cookie : list) {
			addCookie(cookie.domain(), cookie);
		}
	}

	public void addCookie(String host, Cookie cookie) {
		cache.computeIfAbsent(host, k -> new ArrayList<>()).add(cookie);
	}

}
