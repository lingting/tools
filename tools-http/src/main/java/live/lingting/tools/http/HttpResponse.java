package live.lingting.tools.http;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import live.lingting.tools.core.util.CollectionUtils;
import live.lingting.tools.core.util.StreamUtils;
import live.lingting.tools.http.constant.HttpConstants;
import live.lingting.tools.http.enums.HttpHeader;
import live.lingting.tools.json.JacksonUtils;

/**
 * @author lingting
 */
public class HttpResponse<T> {

	private final int code;

	private final Map<String, List<String>> headers;

	private final Charset charset;

	private final InputStream in;

	private final String message;

	private final HttpURLConnection connection;

	private byte[] bytes;

	private final Class<T> cls;

	private T body;

	public HttpResponse(HttpURLConnection connection, Charset charset, Class<T> cls) throws IOException {
		this.connection = connection;
		code = connection.getResponseCode();
		headers = connection.getHeaderFields();
		this.charset = charset;
		this.cls = cls;
		message = connection.getResponseMessage();

		InputStream stream = null;
		try {
			if (code == HttpConstants.SUCCESS_CODE) {
				stream = connection.getInputStream();
			}
			else {
				stream = connection.getErrorStream();
			}
		}
		catch (FileNotFoundException e) {
			throw e;
		}
		catch (IOException e) {
			// 忽略.无返回内容
		}

		// 返回的流有可能为null
		if (stream != null) {
			in = stream(stream);
		}
		else {
			in = null;
		}
	}

	protected InputStream stream(InputStream in) throws IOException {
		// gzip 处理
		if (isGzip() && !(in instanceof GZIPInputStream)) {
			try {
				in = new GZIPInputStream(in);
			}
			catch (IOException e) {
				// 无返回值时. 会出错, 忽略
				return null;
			}
		}
		// deflate 处理
		else if (isDeflate() && !(in instanceof InflaterInputStream)) {
			in = new InflaterInputStream(in, new Inflater(true));
		}

		return in;
	}

	public int code() {
		return code;
	}

	public Map<String, List<String>> headers() {
		return headers;
	}

	public Charset charset() {
		return charset;
	}

	public String message() {
		return message;
	}

	/**
	 * 流转字节
	 * @return byte[]
	 */
	public byte[] bytes() {
		if (bytes == null && in != null) {
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				StreamUtils.write(in, out);
				bytes = out.toByteArray();
			}
			catch (IOException e) {
				bytes = new byte[0];
			}
			finally {
				try {
					in.close();
					connection.disconnect();
				}
				catch (IOException e) {
					//
				}
			}
		}
		return bytes;
	}

	public T body() {
		byte[] bytes = bytes();
		if (body == null && bytes != null && bytes.length > 0) {
			final String str = new String(bytes, charset);
			if (cls.isAssignableFrom(String.class)) {
				body = (T) str;
			}
			else {
				body = JacksonUtils.toObj(str, cls);
			}
		}
		return body;
	}

	public void writeBody(OutputStream out) throws IOException {
		try {
			StreamUtils.write(in, out);
		}
		finally {
			try {
				in.close();
			}
			catch (IOException e) {
				//
			}
		}
	}

	protected boolean isGzip() {
		final List<String> list = headers.get(HttpHeader.CONTENT_ENCODING.getVal());
		return !CollectionUtils.isEmpty(list) && list.get(0).equalsIgnoreCase(HttpConstants.GZIP);
	}

	protected boolean isDeflate() {
		final List<String> list = headers.get(HttpHeader.CONTENT_ENCODING.getVal());
		return !CollectionUtils.isEmpty(list) && list.get(0).equalsIgnoreCase(HttpConstants.DEFLATE);
	}

}
