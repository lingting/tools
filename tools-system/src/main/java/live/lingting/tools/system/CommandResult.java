package live.lingting.tools.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import lombok.Getter;
import live.lingting.tools.core.util.StreamUtils;
import live.lingting.tools.core.util.StringUtils;

/**
 * @author lingting 2022/6/25 12:01
 */
public class CommandResult {

	protected InputStream output;

	protected InputStream error;

	private Charset charset;

	@Getter
	protected LocalDateTime startTime;

	@Getter
	protected LocalDateTime endTime;

	protected String strOutput = null;

	protected byte[] bytesOutput = null;

	protected String strError = null;

	protected byte[] bytesError = null;

	public static CommandResult of(InputStream output, InputStream error, LocalDateTime startTime,
			LocalDateTime endTime, Charset charset) {
		CommandResult result = new CommandResult();
		result.output = output;
		result.error = error;
		result.charset = charset;
		result.startTime = startTime;
		result.endTime = endTime;
		return result;
	}

	/**
	 * 本方法会读取流, 调用本方法后调用其他output处理方法可能会导致异常
	 */
	public String getStrOutput() throws IOException {
		if (!StringUtils.hasText(strOutput)) {
			StreamUtils.toString(output, StreamUtils.DEFAULT_SIZE, charset);
		}
		return strOutput;
	}

	/**
	 * 本方法会读取流, 调用本方法后调用其他output处理方法可能会导致异常
	 */
	private byte[] getBytesOutput() throws IOException {
		if (bytesOutput == null) {
			bytesOutput = StreamUtils.read(output);
		}
		return bytesOutput;
	}

	/**
	 * 本方法会读取流, 调用本方法后调用其他output处理方法可能会导致异常
	 */
	public void writeOutput(OutputStream out) throws IOException {
		StreamUtils.write(output, out);
	}

	/**
	 * 本方法会读取流, 调用本方法后调用其他error处理方法可能会导致异常
	 */
	public String getStrError() throws IOException {
		if (!StringUtils.hasText(strError)) {
			StreamUtils.toString(error, StreamUtils.DEFAULT_SIZE, charset);
		}
		return strError;
	}

	/**
	 * 本方法会读取流, 调用本方法后调用其他error处理方法可能会导致异常
	 */
	public byte[] getBytesError() throws IOException {
		if (bytesError == null) {
			bytesError = StreamUtils.read(error);
		}
		return bytesError;
	}

	/**
	 * 本方法会读取流, 调用本方法后调用其他error处理方法可能会导致异常
	 */
	public void writeError(OutputStream out) throws IOException {
		StreamUtils.write(error, out);
	}

}
