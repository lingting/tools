package live.lingting.tools.system;

import live.lingting.tools.system.exception.CommandTimeoutException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2022/6/25 12:28
 */
class CommandTest {

	@Test
	@SneakyThrows
	void single() {
		CommandResult result = Command.of("cmd").exec("dir").exit().result();
		String output = result.stdOutStr();
		Assertions.assertNotNull(output);
		System.out.println(result.startTime);
		System.out.println(result.endTime);
		System.out.println(output);
		System.out.println(result.stdErrStr());
		System.out.println("stdOut: " + result.stdOut().getAbsolutePath());
		System.out.println("stdErr: " + result.stdErr().getAbsolutePath());
	}

	@Test
	@SneakyThrows
	void singleTime() {
		try {
			CommandResult result = Command.of("cmd").exec("timeout /T 10").result(TimeUnit.SECONDS.toMillis(5));
			String output = result.stdOutStr();
			Assertions.assertNotNull(output);
		}
		catch (CommandTimeoutException e) {
			System.out.println("超时主动结束线程");
		}
	}

	@Test
	@SneakyThrows
	void bigStdOut() {
		// 大标准输出测试.
		CommandResult result = Command.of("adb shell").exec("screencap -p && sleep .1 | sed 's/^M$//'").exit().result();
		Assertions.assertNotNull(result);
		System.out.println(result.startTime);
		System.out.println(result.endTime);
		File stdOut = result.stdOut();
		File stdErr = result.stdErr();
		System.out.println("stdOut: " + stdOut.getAbsolutePath() + "; size: " + Files.size(stdOut.toPath()));
		System.out.println("stdErr: " + stdErr.getAbsolutePath() + "; size: " + Files.size(stdErr.toPath()));
	}

	/**
	 * 此方法使用 {@link Runtime#exec}构造 Process, 大标准输出会导致下面这个测试用例卡死.
	 * <p>
	 * 卡死原因详见 {@link Command#result(long)} 注释
	 * </p>
	 */
	@Test
	@SneakyThrows
	void bigStdOutStuck() {
		Process process = Runtime.getRuntime().exec("adb shell");
		DataOutputStream stdIn = new DataOutputStream(process.getOutputStream());
		stdIn.writeBytes("screencap -p && sleep .1 | sed 's/^M$//'");
		stdIn.writeBytes(Command.NEXT_LINE);
		stdIn.flush();
		stdIn.writeBytes(Command.EXIT_COMMAND);
		stdIn.writeBytes(Command.NEXT_LINE);
		stdIn.flush();
		process.waitFor();
		Assertions.assertNotNull(process);
	}

}
