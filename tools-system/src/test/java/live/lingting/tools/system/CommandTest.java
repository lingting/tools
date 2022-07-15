package live.lingting.tools.system;

import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting 2022/6/25 12:28
 */
class CommandTest {

	@Test
	@SneakyThrows
	void single() {
		CommandResult result = Command.instance("cmd").exec("dir").exit().result();
		String output = result.getOutputStr();
		Assertions.assertNotNull(output);
		System.out.println(output);
	}

	@Test
	@SneakyThrows
	void singleTime() {
		CommandResult result = Command.instance("adb connect 192.168.8.246").exit()
				.result(TimeUnit.SECONDS.toMillis(5));
		String output = result.getOutputStr();
		Assertions.assertNotNull(output);
		System.out.println(output);
	}

}
