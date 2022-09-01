package live.lingting.tools.http;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author lingting 2022/6/10 17:06
 */

class HttpRequestTest {

	@Test
	@SneakyThrows
	void execSync() {
		HttpRequest request = HttpRequest.get(
				"https://search.gitee.com/?q=tools&type=repository&prec_filter=true&fork_filter=true&rec_filter=false&gvp_filter=false&lang=java");
		String body = request.execSync().body();
		Assertions.assertNotNull(body);
		System.out.println(body);
	}

	@Test
	@SneakyThrows
	void execAsync() {
		HttpRequest request = HttpRequest.get(
				"https://search.gitee.com/?q=tools&type=repository&prec_filter=true&fork_filter=true&rec_filter=false&gvp_filter=false&lang=java");
		request.execAsync(String.class, new HttpCallback<String>() {
			@Override
			public void onFailure(HttpRequest request, Exception e) {
				System.out.println("请求异常!" + e.getMessage());
			}

			@Override
			public void onResponse(HttpRequest request, HttpResponse<String> response) throws IOException {
				String body = response.body();
				Assertions.assertNotNull(body);
				System.out.println(body);
			}
		});
		Thread.sleep(1000);
	}

}
