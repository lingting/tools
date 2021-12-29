package live.lingting.tools.http;

import static live.lingting.tools.core.constant.StringConstants.CR_LF;
import static live.lingting.tools.core.util.StringUtils.hasText;
import static live.lingting.tools.http.constant.HttpConstants.CONTENT_TYPE_TEMPLATE;
import static live.lingting.tools.http.constant.HttpConstants.QUERY_DELIMITER;
import static live.lingting.tools.http.constant.HttpConstants.QUERY_PARAMS_DELIMITER;
import static live.lingting.tools.http.constant.HttpConstants.UA;
import static live.lingting.tools.http.constant.HttpConstants.URL_DELIMITER;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import live.lingting.tools.core.util.CollectionUtils;
import live.lingting.tools.core.util.FileUtils;
import live.lingting.tools.core.util.HttpUtils;
import live.lingting.tools.core.util.RandomUtils;
import live.lingting.tools.core.util.StreamUtils;
import live.lingting.tools.core.util.StringUtils;
import live.lingting.tools.http.constant.HttpConstants;
import live.lingting.tools.http.domain.MultiVal;
import live.lingting.tools.http.enums.HttpContentType;
import live.lingting.tools.http.enums.HttpHeader;
import live.lingting.tools.http.enums.HttpMethod;
import live.lingting.tools.http.exception.HttpException;
import live.lingting.tools.json.JacksonUtils;

/**
 * @author lingting
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HttpRequest {

	@Getter
	private HttpMethod method;

	private String protocol = HttpConstants.HTTPS;

	private String uri = "";

	private final Map<String, List<String>> headers = new HashMap<>();

	private final Map<String, Object> form = new HashMap<>();

	private byte[] body;

	private Charset charset = StandardCharsets.UTF_8;

	private Proxy proxy;

	private int connectTimeout = -1;

	private int readTimeout = -1;

	private int blockSize = 0;

	private boolean useCache = false;

	private boolean keepAlive = false;

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

	public HttpRequest protocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public HttpRequest url(String url) {
		if (hasText(url)) {
			if (url.contains(URL_DELIMITER)) {
				final String[] split = url.split(URL_DELIMITER);
				protocol = split[0];
				uri = split[1];
			}
			else {
				protocol = HttpConstants.HTTP;
				uri = url;
			}
		}
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

	public HttpRequest contentType(HttpContentType type) {
		if (type == null) {
			return contentType("");
		}
		return contentType(type.getValue());
	}

	public HttpRequest contentType(String type) {
		return header(HttpHeader.CONTENT_TYPE, type);
	}

	/**
	 * 如果未配置 contentType. 则设置为指定值. 已配置则忽略
	 * @param type type
	 * @return live.lingting.tools.http.HttpRequest
	 */
	public HttpRequest contentTypeIfAbsent(HttpContentType type) {
		List<String> contentType = headers.get(HttpHeader.CONTENT_TYPE.getVal());
		if (CollectionUtils.isEmpty(contentType) && !hasText(contentType.get(0))) {
			return contentType(type);
		}
		return this;
	}

	public HttpRequest formAppend(String name, Object val) {
		form.computeIfAbsent(name, k -> new MultiVal());
		final Object obj = form.get(name);
		if (obj instanceof MultiVal) {
			((MultiVal) obj).add(val);
		}
		else {
			final MultiVal multiVal = new MultiVal();
			multiVal.add(obj);
			multiVal.add(val);
			form.put(name, multiVal);
		}
		return this;
	}

	public HttpRequest form(String key, Object val) {
		form.put(key, val);
		return contentTypeIfAbsent(HttpContentType.APPLICATION_FORM_URLENCODED);
	}

	public HttpRequest form(Map<String, Object> map) {
		form.putAll(map);
		return contentTypeIfAbsent(HttpContentType.APPLICATION_FORM_URLENCODED);
	}

	public HttpRequest formClean() {
		form.clear();
		return this;
	}

	public HttpRequest body(byte[] body) {
		this.body = body;
		return this;
	}

	public HttpRequest body(String body) {
		this.body = body.getBytes(charset);
		if (StringUtils.isJson(body)) {
			contentTypeIfAbsent(HttpContentType.APPLICATION_JSON);
		}
		else if (StringUtils.isXml(body)) {
			contentTypeIfAbsent(HttpContentType.APPLICATION_XML);
		}
		return this;
	}

	/**
	 * body中写入map - 如果当前 content Type 不是 form表单样式. contentType会被设置为 json
	 * @param map map
	 * @return live.lingting.tools.http.HttpRequest
	 */
	public HttpRequest body(Map<String, Object> map) {
		if (CollectionUtils.isEmpty(map)) {
			return this;
		}
		if (isForm() || method.equals(HttpMethod.GET)) {
			return body(HttpUtils.urlParamBuild(map));
		}
		else {
			return body(JacksonUtils.toJson(map));
		}
	}

	public HttpRequest charset(String charsetName) throws UnsupportedCharsetException {
		return charset(Charset.forName(charsetName));
	}

	public HttpRequest charset(Charset charset) {
		this.charset = charset;
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
	public HttpRequest connectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	/**
	 * 读取超时时间. 单位 毫秒
	 * @param readTimeout 超时时间
	 * @return live.lingting.tools.http.HttpRequest
	 */
	public HttpRequest readTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public HttpRequest blockSize(int blockSize) {
		this.blockSize = blockSize;
		return this;
	}

	/**
	 * 是否使用缓存
	 * @param useCache true 使用
	 * @return live.lingting.tools.http.HttpRequest
	 */
	public HttpRequest useCache(boolean useCache) {
		this.useCache = useCache;
		return this;
	}

	/**
	 * 是否保持连接
	 */
	public HttpRequest keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	public HttpResponse<String> exec() throws HttpException {
		return exec(String.class);
	}

	public <T> HttpResponse<T> exec(Class<T> cls) throws HttpException {
		if (!hasText(uri)) {
			throw new HttpException("请配置正确的请求地址!");
		}

		HttpURLConnection connection = null;
		try {
			// 初始化 url
			final URL url = urlBuild();
			// 初始化 连接
			connection = connectionBuild(url);
			// 发送参数
			send(connection);

			// 获取返回值
			return new HttpResponse<>(connection, charset, cls);
		}
		catch (Exception e) {
			// 出错端口连接
			if (connection != null) {
				connection.disconnect();
			}
			throw new HttpException("请求发送异常!", e);
		}
	}

	public String getUrl() {
		return protocol + URL_DELIMITER + uri;
	}

	/**
	 * body 转 查询参数
	 * @return java.lang.String
	 */
	protected String queryBuild() {
		if (body == null) {
			return null;
		}
		return new String(body, charset);
	}

	/**
	 * 构建实际请求的url
	 */
	protected URL urlBuild() throws MalformedURLException {
		// get 请求将 参数放入 url中
		if (method.equals(HttpMethod.GET)) {
			String uri = this.uri;
			String query = "";

			if (uri.contains(QUERY_DELIMITER)) {
				final String[] split = uri.split(HttpConstants.QUERY_DELIMITER_SPLIT);
				uri = split[0];
				query = split[1];
				if (query.endsWith(QUERY_PARAMS_DELIMITER)) {
					query = query.substring(0, query.length() - 1);
				}
			}

			// 组装
			final String qb = queryBuild();
			if (StringUtils.hasText(qb)) {
				if (StringUtils.hasText(query) && !query.endsWith(QUERY_DELIMITER)) {
					query = query + QUERY_DELIMITER;
				}
				query = query + qb;
			}

			this.uri = uri + QUERY_DELIMITER + query;
		}

		final String url = protocol + URL_DELIMITER + uri;
		// 不可见字符转为 %20
		return new URL(null, url.replace("\\s", "%20"));
	}

	protected HttpURLConnection connectionBuild(URL url) throws IOException {
		final HttpURLConnection connection = (HttpURLConnection) (proxy == null ? url.openConnection()
				: url.openConnection(proxy));
		connection.setDoInput(true);

		if (blockSize > 0) {
			connection.setChunkedStreamingMode(blockSize);
		}

		if (connectTimeout > 0) {
			connection.setConnectTimeout(connectTimeout);
		}

		if (readTimeout > 0) {
			connection.setReadTimeout(readTimeout);
		}

		connection.setRequestMethod(method.name());
		connection.setUseCaches(useCache);

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			for (String val : entry.getValue()) {
				if (val == null || val.length() == 0) {
					continue;
				}

				// 设备 charset
				if (val.equalsIgnoreCase(HttpHeader.CONTENT_TYPE.getVal())) {
					val = String.format(HttpConstants.HEADER_CONTENT_TYPE_CHARSET, val, charset.name());
				}

				connection.addRequestProperty(entry.getKey(), val);
			}
		}

		if (CollectionUtils.isEmpty(header(HttpHeader.USER_AGENT))) {
			connection.addRequestProperty(HttpHeader.USER_AGENT.getVal(), UA);
		}

		// 不保持连接
		if (!keepAlive) {
			connection.addRequestProperty(HttpHeader.CONNECTION.getVal(), HttpConstants.CONNECTION_CLOSE);
		}

		return connection;
	}

	protected void send(HttpURLConnection connection) throws IOException {
		if (method.equals(HttpMethod.GET)) {
			connection.connect();
			return;
		}
		connection.setDoOutput(true);

		if (isMultipart()) {
			String boundary = "LingTingFormBoundary" + RandomUtils.nextStr(9);
			connection.setRequestProperty(HttpHeader.CONTENT_TYPE.getVal(),
					String.format(HttpConstants.HEADER_CONTENT_TYPE_BOUNDARY,
							HttpContentType.MULTIPART_FORM_DATA.getValue(), charset.name(), boundary));

			multipartSend(connection.getOutputStream(), boundary);
		}
		else {
			// 未配置. 使用默认表单
			if (CollectionUtils.isEmpty(header(HttpHeader.CONTENT_TYPE))) {
				connection.setRequestProperty(HttpHeader.CONTENT_TYPE.getVal(),
						String.format(HttpConstants.HEADER_CONTENT_TYPE_CHARSET,
								HttpContentType.APPLICATION_FORM_URLENCODED.getValue(), charset.name()));
			}

			final OutputStream out = connection.getOutputStream();
			// content type 是 form 且 form 不为空 使用 form 表单
			if (isForm() && !CollectionUtils.isEmpty(form)) {
				out.write(HttpUtils.urlParamBuild(form).getBytes(charset));
			}
			// body 不为空 使用 body
			else {
				out.write(body == null ? new byte[0] : body);
			}
		}
	}

	protected void multipartSend(OutputStream out, String boundary) throws IOException {
		for (Map.Entry<String, Object> entry : form.entrySet()) {
			final List<Object> list = entry.getValue() instanceof MultiVal ? ((MultiVal) entry.getValue()).values()
					: CollectionUtils.toList(entry.getValue());

			for (Object obj : list) {
				if (obj == null) {
					continue;
				}

				write(out, "--");
				write(out, boundary);
				write(out, CR_LF);

				if (obj instanceof File) {
					File file = (File) obj;
					write(out, String.format(HttpConstants.DISPOSITION_FILE_TEMPLATE, entry.getKey(), file.getName()));

					final String mimeType = FileUtils.getMimeType(file.getName());
					write(out, String.format(CONTENT_TYPE_TEMPLATE,
							hasText(mimeType) ? mimeType : HttpConstants.MULTIPART_DEFAULT_CONTENT_TYPE));

					final FileInputStream inputStream = FileUtils.getInputStream(file);
					StreamUtils.write(inputStream, out);
				}
				else {
					write(out, String.format(HttpConstants.DISPOSITION_TEMPLATE, entry.getKey()));
					write(out, obj.toString());
				}

				write(out, CR_LF);
			}
		}
		write(out, "--" + boundary + "--\r\n");
	}

	public void write(OutputStream out, String str) throws IOException {
		out.write(str.getBytes(charset));
	}

	public boolean isForm() {
		final List<String> list = header(HttpHeader.CONTENT_TYPE);
		return !CollectionUtils.isEmpty(list) && list.get(0).contains("form-");
	}

	public boolean isMultipart() {
		final List<String> list = header(HttpHeader.CONTENT_TYPE);
		return !CollectionUtils.isEmpty(list) && list.get(0).startsWith(HttpContentType.MULTIPART_FORM_DATA.getValue());
	}

}
