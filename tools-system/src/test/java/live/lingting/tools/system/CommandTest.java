package live.lingting.tools.system;

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

}
