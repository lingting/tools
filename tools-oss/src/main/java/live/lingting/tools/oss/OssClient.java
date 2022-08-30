package live.lingting.tools.oss;

import live.lingting.tools.core.util.StringUtils;
import live.lingting.tools.oss.domain.StreamSize;
import live.lingting.tools.oss.exception.OssClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author lingting 2022/8/20 15:53
 */
public class OssClient implements Closeable {

	protected final String endpoint;

	protected final String region;

	protected final String accessKey;

	protected final String accessSecret;

	protected final String bucket;

	protected final String domain;

	protected final String objectKeyPrefix;

	protected final S3Client s3Client;

	protected final ObjectCannedACL acl;

	protected final String downloadPrefix;

	protected final boolean ready;

	protected final Exception e;

	public static OssClientBuilder builder() {
		return new OssClientBuilder();
	}

	public OssClient(OssClientBuilder builder) {
		S3Client s3Client = null;
		Exception e = null;

		try {
			s3Client = builder.s3Build();
		}
		catch (Exception ex) {
			e = ex;
		}

		this.endpoint = builder.endpoint();
		this.region = builder.region();
		this.accessKey = builder.accessKey();
		this.accessSecret = builder.accessSecret();
		this.bucket = builder.bucket();
		this.domain = builder.domain();
		this.acl = builder.acl();
		this.objectKeyPrefix = builder.objectKeyPrefix();
		this.downloadPrefix = builder.downloadPrefix();

		this.s3Client = s3Client;
		this.ready = s3Client != null;
		this.e = e;
	}

	/**
	 * oss 客户端 是否已就绪
	 * @return true 表示客户端就绪, 可以操作对象
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * oss 客户端构筑异常
	 * @return 异常时的详情. 可能为 null
	 */
	public Exception getError() {
		return e;
	}

	public S3Client getS3Client() {
		if (isReady()) {
			return s3Client;
		}
		throw new OssClientException("oss客户端未就绪!", getError());
	}

	/**
	 * 获取Oss对象的绝对key
	 * @param relativeKey 相对{@link OssClient#objectKeyPrefix}的Key
	 * @return 文件绝对路径
	 * @author lingting 2021-05-10 15:58
	 */
	public String getObjectKey(String relativeKey) {
		if (!StringUtils.hasText(relativeKey)) {
			throw new OssClientException("相对key不能为空!");
		}

		if (relativeKey.startsWith(OssConstants.SLASH)) {
			relativeKey = relativeKey.substring(1);
		}

		return objectKeyPrefix + relativeKey;
	}

	@Override
	public void close() throws IOException {
		if (isReady()) {
			getS3Client().close();
		}
	}

	/**
	 * 文件上传, 本方法会读一遍流, 计算流大小, 推荐使用 upload(stream, relativeKey, size) 方法
	 * <p color="red">
	 * 注意: 本方法不会主动关闭流. 请手动关闭传入的流
	 * </p>
	 * @param relativeKey 文件相对 {@link OssClient#objectKeyPrefix} 的路径
	 * @param stream 文件输入流
	 * @return 完整的对象key
	 * @throws IOException 流操作时异常
	 */
	public String upload(InputStream stream, String relativeKey) throws IOException {
		StreamSize streamSize = StreamSize.of(stream);

		try (final InputStream tempStream = streamSize.getStream()) {
			return upload(tempStream, relativeKey, streamSize.getSize());
		}
	}

	/**
	 * 通过流上传文件
	 * <p color="red">
	 * 注意: 本方法不会主动关闭流. 请手动关闭传入的流
	 * </p>
	 * @param stream 流
	 * @param relativeKey 相对{@link OssClient#objectKeyPrefix}的key
	 * @param size 流大小
	 * @return 完整的对象key
	 */
	public String upload(InputStream stream, String relativeKey, Long size) {
		return upload(stream, relativeKey, size, acl);
	}

	/**
	 * 通过文件对象上传文件
	 * @param file 文件
	 * @param relativeKey 相对{@link OssClient#objectKeyPrefix}的key
	 * @return 完整的对象key
	 * @throws IOException 流操作时异常
	 */
	public String upload(File file, String relativeKey) throws IOException {
		try (final FileInputStream stream = new FileInputStream(file)) {
			return upload(stream, relativeKey, Files.size(file.toPath()));
		}
	}

	/**
	 * 通过流上传文件
	 * <p color="red">
	 * 注意: 本方法不会主动关闭流. 请手动关闭传入的流
	 * </p>
	 * @param stream 流
	 * @param relativeKey 相对{@link OssClient#objectKeyPrefix}的key
	 * @param size 流大小
	 * @param acl 文件权限
	 * @return 完整的对象key
	 */
	public String upload(InputStream stream, String relativeKey, Long size, ObjectCannedACL acl) {
		final String objectKey = getObjectKey(relativeKey);
		final PutObjectRequest.Builder builder = PutObjectRequest.builder().bucket(bucket).key(objectKey);

		if (acl != null) {
			// 配置权限
			builder.acl(acl);
		}

		getS3Client().putObject(builder.build(), RequestBody.fromInputStream(stream, size));
		return objectKey;
	}

	/**
	 * 删除对象
	 * @param relativeKey 相对{@link OssClient#objectKeyPrefix}的对象key
	 */
	public void delete(String relativeKey) {
		getS3Client().deleteObject(builder -> builder.bucket(bucket).key(getObjectKey(relativeKey)));
	}

	/**
	 * 文件复制, 请确保源文件在当前bucket里面
	 * @param relativeSourceKey 相对{@link OssClient#objectKeyPrefix}的源对象key
	 * @param relativeDestinationKey 相对{@link OssClient#objectKeyPrefix}的目标对象key
	 */
	public void copy(String relativeSourceKey, String relativeDestinationKey) {
		getS3Client().copyObject(builder -> builder
				// 源
				.sourceBucket(bucket).sourceKey(getObjectKey(relativeSourceKey))
				// 目标
				.destinationKey(getObjectKey(relativeDestinationKey)).destinationBucket(bucket));
	}

	/**
	 * 获取相对{@link OssClient#objectKeyPrefix}的key的下载url
	 * @author lingting 2021-05-12 18:50
	 */
	public String getDownloadUrl(String relativeKey) {
		return String.format("%s/%s", downloadPrefix, getObjectKey(relativeKey));
	}

}
