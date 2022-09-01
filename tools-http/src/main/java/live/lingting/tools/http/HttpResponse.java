package live.lingting.tools.http;

import live.lingting.tools.core.util.StreamUtils;
import live.lingting.tools.json.JacksonUtils;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @author lingting
 */
public class HttpResponse<T> implements Closeable {

	private final Response response;

	private final Class<T> cls;

	private T body;

	public HttpResponse(Response response, Class<T> cls) {
		this.response = response;
		this.cls = cls;
	}

	public int code() {
		return response.code();
	}

	public String message() {
		return response.message();
	}

	public Headers headers() {
		return response.headers();
	}

	public byte[] bytes() {
		try {
			return response.body().bytes();
		}
		catch (IOException e) {
			return new byte[0];
		}
	}

	public T body() {
		if (body == null) {
			try {
				String string = response.body().string();
				if (cls.isAssignableFrom(String.class)) {
					body = (T) string;
				}
				else {
					body = JacksonUtils.toObj(string, cls);
				}
			}
			catch (IOException e) {
				body = null;
			}
		}

		return body;
	}

	public void accept(Consumer<Response> consumer) {
		consumer.accept(response);
	}

	public void write(OutputStream out) throws IOException {
		StreamUtils.write(response.body().byteStream(), out);
	}

	@Override
	public void close() {
		StreamUtils.close(response);
	}

}
