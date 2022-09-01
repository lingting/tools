package live.lingting.tools.http;

import live.lingting.tools.core.util.CollectionUtils;
import live.lingting.tools.http.enums.HttpContentType;
import live.lingting.tools.http.enums.HttpHeader;
import live.lingting.tools.http.enums.HttpMethod;
import live.lingting.tools.http.exception.HttpException;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lingting
 */
public class HttpRequest {

	@Getter
	private HttpMethod method;

	@Getter
	private HttpUrl url;

	private final Map<String, List<String>> headers = new HashMap<>();

	@Getter
	private RequestBody body;

	/**
	 * 代理
	 */
	@Getter
	private Proxy proxy;

	/**
	 * 连接超时, 单位: 毫秒
	 */
	@Getter
	private long connectTimeout = 0;

	/**
	 * 读取超时, 单位: 毫秒
	 */
	@Getter
	private long readTimeout = 0;

	/**
	 * HostnameVerifier，用于HTTPS安全连接
	 */
	@Getter
	private HostnameVerifier hostnameVerifier;

	/**
	 * SSLSocketFactory，用于HTTPS安全连接
	 */
	@Getter
	private SSLSocketFactory ssf;

	@Getter
	private X509TrustManager trustManager;

	protected HttpRequest(HttpMethod method, HttpUrl url) {
		this.url = url;
		this.method = method;
	}

	public static HttpRequest create(HttpMethod method, String url) {
		return new HttpRequest(method, HttpUrl.parse(url));
	}

	public static HttpRequest post(String url) {
		return create(HttpMethod.POST, url);
	}

	public static HttpRequest get(String url) {
		return create(HttpMethod.GET, url);
	}

	public HttpRequest method(HttpMethod method) {
		this.method = method;
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

	/**
	 * 设置body
	 * @param val 如果不是{@link java.io.File} 或 {@link byte[]} 或 {@link ByteString} 则会调用
	 * .toString() 方法赋值
	 */
	public HttpRequest body(HttpContentType contentType, Object val) {
		return body(contentType.getValue(), val);
	}

	public HttpRequest body(String contentType, Object val) {
		RequestBody requestBody;
		// 其他类型请求
		MediaType mediaType = MediaType.parse(contentType);
		if (val instanceof File) {
			requestBody = RequestBody.create(mediaType, (File) val);
		}
		else if (val instanceof byte[]) {
			requestBody = RequestBody.create(mediaType, (byte[]) val);
		}
		else if (val instanceof ByteString) {
			requestBody = RequestBody.create(mediaType, (ByteString) val);
		}
		else {
			requestBody = RequestBody.create(mediaType, val.toString());
		}
		return body(requestBody);
	}

	/**
	 * form表单
	 */
	public HttpRequest form(Map<String, ?> map) {
		FormBody.Builder builder = new FormBody.Builder();

		map.forEach((key, val) -> {
			if (val == null) {
				//
			}
			else if (val instanceof Collection) {
				((Collection<?>) val).forEach(v -> builder.add(key, v.toString()));
			}
			else if (val instanceof Iterable) {
				((Iterable<?>) val).forEach(v -> builder.add(key, v.toString()));
			}
			else if (val instanceof Iterator) {
				((Iterator<?>) val).forEachRemaining(v -> builder.add(key, v.toString()));
			}
			else if (val.getClass().isArray()) {
				for (Object v : (Object[]) val) {
					builder.add(key, v.toString());
				}
			}
			else {
				builder.add(key, val.toString());
			}
		});

		return form(builder.build());
	}

	public HttpRequest form(FormBody formBody) {
		return body(formBody);
	}

	public HttpRequest formMultipart(String contentType, Map<String, ?> map) {
		MultipartBody.Builder builder = new MultipartBody.Builder();
		final MediaType mediaType = MediaType.get(contentType);
		builder.setType(mediaType);
		map.forEach((key, val) -> {
			if (val == null) {
				//
			}
			else if (val instanceof Collection) {
				((Collection<?>) val).forEach(v -> addFormDataPart(builder, mediaType, key, v));
			}
			else if (val instanceof Iterable) {
				((Iterable<?>) val).forEach(v -> addFormDataPart(builder, mediaType, key, v));
			}
			else if (val instanceof Iterator) {
				((Iterator<?>) val).forEachRemaining(v -> addFormDataPart(builder, mediaType, key, v));
			}
			else if (val.getClass().isArray()) {
				for (Object v : (Object[]) val) {
					addFormDataPart(builder, mediaType, key, v);
				}
			}
			else {
				addFormDataPart(builder, mediaType, key, val);
			}
		});

		return formMultipart(builder.build());
	}

	private void addFormDataPart(MultipartBody.Builder builder, MediaType mediaType, String key, Object o) {
		if (o instanceof File) {
			builder.addFormDataPart(key, ((File) o).getName(), RequestBody.create(mediaType, (File) o));
		}
		else {
			builder.addFormDataPart(key, o.toString());
		}
	}

	public HttpRequest formMultipart(MultipartBody multipartBody) {
		return body(multipartBody);
	}

	/**
	 * 设置url参数
	 */
	public HttpRequest urlParams(Map<String, ?> map) {
		if (!CollectionUtils.isEmpty(map)) {
			HttpUrl.Builder builder = url.newBuilder();

			for (Map.Entry<String, ?> entry : map.entrySet()) {
				String key = entry.getKey();
				Object val = entry.getValue();

				if (val == null) {
					builder.addQueryParameter(key, null);
				}
				else if (val instanceof Collection) {
					((Collection<?>) val).forEach(v -> builder.addQueryParameter(key, v.toString()));
				}
				else if (val instanceof Iterable) {
					((Iterable<?>) val).forEach(v -> builder.addQueryParameter(key, v.toString()));
				}
				else if (val instanceof Iterator) {
					((Iterator<?>) val).forEachRemaining(v -> builder.addQueryParameter(key, v.toString()));
				}
				else if (val.getClass().isArray()) {
					for (Object v : (Object[]) val) {
						builder.addQueryParameter(key, v.toString());
					}
				}
				else {
					builder.addQueryParameter(key, val.toString());
				}
			}

			url = builder.build();

		}
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
			if (CollectionUtils.isEmpty(v)) {
				return;
			}
			for (String s : v) {
				if (s != null) {
					builder.addHeader(k, s);
				}
			}
		});

		return builder;
	}

}
