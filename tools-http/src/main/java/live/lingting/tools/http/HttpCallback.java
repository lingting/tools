package live.lingting.tools.http;

import java.io.IOException;

/**
 * @author lingting 2022/6/13 9:47
 */
public interface HttpCallback<T> {

	void onFailure(HttpRequest request, Exception e);

	void onResponse(HttpRequest request, HttpResponse<T> response) throws IOException;

}
