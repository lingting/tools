package live.lingting.tools.system;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import live.lingting.tools.core.util.SystemUtils;

/**
 * @author lingting 2022/6/25 11:55
 */
public class Command {

	public static final String NEXT_LINE = SystemUtils.line();

	public static final String EXIT = "exit";

	private final Process process;

	private final DataOutputStream out;

	private final String nextLine;

	private final String exit;

	private final Charset charset;

	private final LocalDateTime startTime;

	private Command(String init, String nextLine, String exit, Charset charset) throws IOException {
		this.process = Runtime.getRuntime().exec(init);
		this.out = new DataOutputStream(process.getOutputStream());
		this.nextLine = nextLine;
		this.exit = exit;
		this.charset = charset;
		this.startTime = LocalDateTime.now();
	}

	/**
	 * 获取命令操作实例
	 * @param init 初始命令
	 */
	public static Command instance(String init) throws IOException {
		return instance(init, NEXT_LINE, EXIT, SystemUtils.charset());
	}

	public static Command instance(String init, String nextLine, String exit, Charset charset) throws IOException {
		return new Command(init, nextLine, exit, charset);
	}

	/**
	 * 换到下一行
	 */
	public Command line() throws IOException {
		out.writeBytes(nextLine);
		out.flush();
		return this;
	}

	/**
	 * 写入通道退出指令
	 */
	public Command exit() throws IOException {
		out.writeBytes(exit);
		return line();
	}

	/**
	 * 写入并执行一行指令
	 * @param str 单行指令
	 */
	public Command exec(String str) throws IOException {
		out.writeBytes(str);
		return line();
	}

	/**
	 * 获取执行结果, 并退出
	 * <p>
	 * 注意: 如果套娃了多个通道, 则需要手动退出套娃的通道
	 * </p>
	 * <p>
	 * 例如: eg: exec("ssh ssh.lingting.live").exec("ssh ssh.lingting.live").exec("ssh
	 * ssh.lingting.live")
	 * </p>
	 * <p>
	 * 需要: eg: exit().exit().exit()
	 * </p>
	 */
	public CommandResult result() throws IOException {
		return CommandResult.of(process.getInputStream(), process.getErrorStream(), startTime, LocalDateTime.now(),
				charset);
	}

	public void close() {
		process.destroy();
	}

}
