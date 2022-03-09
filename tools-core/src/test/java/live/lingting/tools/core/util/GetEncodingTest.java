package live.lingting.tools.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting
 */
class GetEncodingTest {

	@Test
	void url() {
		String url = "http://127.0.0.1/api/cell/level1/United States";
		Assertions.assertEquals("http://127.0.0.1/api/cell/level1/United%20States", url);
	}

}
