package live.lingting.tools.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import lombok.Getter;
import live.lingting.tools.core.util.FileUtils;
import live.lingting.tools.core.util.StreamUtils;
import live.lingting.tools.core.util.StringUtils;

/**
 * @author lingting 2022/6/25 12:01
 */
public class CommandResult {

	protected File outputFile;

	protected File errorFile;

	private Charset charset;

	@Getter
	protected LocalDateTime startTime;

	@Getter
	protected LocalDateTime endTime;

	protected String strOutput = null;

	protected String strError = null;

	public static CommandResult of(InputStream output, InputStream error, LocalDateTime startTime,
			LocalDateTime endTime, Charset charset) throws IOException {
		CommandResult result = new CommandResult();
		result.outputFile = FileUtils.createTemp(output);
		result.errorFile = FileUtils.createTemp(error);
		result.charset = charset;
		result.startTime = startTime;
		result.endTime = endTime;
		return result;
	}

	public String getStrOutput() throws IOException {
		if (!StringUtils.hasText(strOutput)) {
			try (FileInputStream output = new FileInputStream(outputFile)) {
				strOutput = StreamUtils.toString(output, StreamUtils.DEFAULT_SIZE, charset);
			}
		}
		return strOutput;
	}

	public String getStrError() throws IOException {
		if (!StringUtils.hasText(strError)) {
			try (FileInputStream error = new FileInputStream(errorFile)) {
				strError = StreamUtils.toString(error, StreamUtils.DEFAULT_SIZE, charset);
			}
		}
		return strError;
	}

	public InputStream getStreamOutput() throws IOException {
		return Files.newInputStream(outputFile.toPath());
	}

	public InputStream getStreamError() throws IOException {
		return Files.newInputStream(errorFile.toPath());
	}

	public void clean() {
		try {
			Files.delete(outputFile.toPath());
		}
		catch (Exception e) {
			//
		}
		try {
			Files.delete(errorFile.toPath());
		}
		catch (Exception e) {
			//
		}
	}

}
