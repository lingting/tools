package live.lingting.tools.http;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import live.lingting.tools.http.enums.HttpHeader;
import live.lingting.tools.http.enums.HttpMethod;
import live.lingting.tools.http.exception.HttpException;

/**
 * @author lingting
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HttpRequest {

	@Getter
	private HttpMethod method;

	private String url = "";

	private final Map<String, List<String>> headers = new HashMap<>();

	private RequestBody body;

	/**
	 * 代理
	 */
	private Proxy proxy;

	/**
	 * 连接超时, 单位: 毫秒
	 */
	private long connectTimeout = 0;

	/**
	 * 读取超时, 单位: 毫秒
	 */
	private long readTimeout = 0;

	/**
	 * HostnameVerifier，用于HTTPS安全连接
	 */
	private HostnameVerifier hostnameVerifier;

	/**
	 * SSLSocketFactory，用于HTTPS安全连接
	 */
	private SSLSocketFactory ssf;

	private X509TrustManager trustManager;

	public static HttpRequest create(HttpMethod method) {
		return new HttpRequest().method(Objects.requireNonNull(method));
	}

	public static HttpRequest post() {
		return create(HttpMethod.POST);
	}

	public static HttpRequest get() {
		return create(HttpMethod.GET);
	}

	public HttpRequest method(HttpMethod method) {
		this.method = method;
		return this;
	}

	public HttpRequest url(String url) {
		this.url = url;
		return this;
	}

	public List<String> header(HttpHeader header) {
		return header(header.getVal());
	}

	public List<String> header(String name) {
		headers.computeIfAbsent(name, k -> new ArrayList<>());
		return headers.get(name);
	}

	public HttpRequest header(HttpHeader header, String val) {
		return header(header.getVal(), val, true);
	}

	public HttpRequest header(String name, String val) {
		return header(name, val, true);
	}

	public HttpRequest header(String name, String val, boolean isOverride) {
		if (isOverride) {
			List<String> list = new ArrayList<>();
			list.add(val);
			headers.put(name, list);
		}
		else {
			headers.computeIfAbsent(name, k -> new ArrayList<>());
			headers.get(name).add(val);
		}
		return this;
	}

	public HttpRequest headers(Map<String, String> map) {
		return headers(map, true);
	}

	public HttpRequest headers(Map<String, String> map, boolean isOverride) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (isOverride) {
				List<String> list = new ArrayList<>();
				list.add(entry.getValue());
				headers.put(entry.getKey(), list);
			}
			else {
				headers.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
				headers.get(entry.getKey()).add(entry.getValue());
			}
		}
		return this;
	}

	public HttpRequest headersMap(Map<String, List<String>> map) {
		return headersMap(map, true);
	}

	public HttpRequest headersMap(Map<String, List<String>> map, boolean isOverride) {
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			if (isOverride) {
				headers.put(entry.getKey(), entry.getValue());
			}
			else {
				headers.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
				headers.get(entry.getKey()).addAll(entry.getValue());
			}
		}
		return this;
	}

	public HttpRequest headersClean() {
		headers.clear();
		return this;
	}

	public HttpRequest body(RequestBody body) {
		this.body = body;
		return this;
	}

	public HttpRequest proxy(Proxy proxy) {
		this.proxy = proxy;
		return this;
	}

	/**
	 * 连接超时时间. 单位 毫秒
	 * @param connectTimeout 超时时间
	 * @return live.lingting.tools.http.HttpRequest
	 */
	public HttpRequest connectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	/**
	 * 读取超时时间. 单位 毫秒
	 * @param readTimeout 超时时间
	 * @return live.lingting.tools.http.HttpRequest
	 */
	public HttpRequest readTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	/**
	 * https连接配置
	 */
	public HttpRequest https(HostnameVerifier hostnameVerifier, SSLSocketFactory ssf, X509TrustManager trustManager) {
		this.hostnameVerifier = hostnameVerifier;
		this.ssf = ssf;
		this.trustManager = trustManager;
		return this;
	}

	public HttpResponse<String> execSync() throws HttpException {
		return execSync(String.class);
	}

	public <T> HttpResponse<T> execSync(Class<T> cls) throws HttpException {
		try {
			Response response = clientBuilder().build().newCall(requestBuilder().build()).execute();
			return new HttpResponse<>(response, cls);
		}
		catch (Exception e) {
			throw new HttpException("请求异常!", e);
		}
	}

	public <T> void execAsync(Class<T> cls, HttpCallback<T> callback) {
		try {
			clientBuilder().build().newCall(requestBuilder().build()).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callback.onFailure(HttpRequest.this, e);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					callback.onResponse(HttpRequest.this, new HttpResponse<>(response, cls));
				}
			});
		}
		catch (Exception e) {
			throw new HttpException("请求异常!", e);
		}
	}

	public OkHttpClient.Builder clientBuilder() {
		OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
				.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS).readTimeout(readTimeout, TimeUnit.MILLISECONDS)
				.proxy(proxy);

		if (ssf != null) {
			builder.sslSocketFactory(ssf, trustManager).hostnameVerifier(hostnameVerifier);
		}
		return builder;
	}

	public Request.Builder requestBuilder() {
		Request.Builder builder = new Request.Builder().method(method.toString(), body).url(url);

		headers.forEach((k, v) -> {
			for (String s : v) {
				builder.addHeader(k, s);
			}
		});

		return builder;
	}

}
