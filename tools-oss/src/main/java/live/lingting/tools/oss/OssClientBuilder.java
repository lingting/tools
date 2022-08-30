package live.lingting.tools.oss;

import live.lingting.tools.core.util.StringUtils;
import live.lingting.tools.oss.exception.OssBuildException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

/**
 * @author lingting 2021/5/12 22:01
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OssClientBuilder {

	protected String endpoint;

	protected String regionStr;

	protected String accessKey;

	protected String accessSecret;

	protected String bucket;

	protected String domain;

	private ObjectCannedACL acl;

	protected String downloadPrefix;

	protected String objectKeyPrefix;

	public static OssClientBuilder builder() {
		return new OssClientBuilder();
	}

	public String endpoint() {
		return endpoint;
	}

	public OssClientBuilder endpoint(String endpoint) {
		this.endpoint = endpoint;
		return this;
	}

	public String region() {
		return regionStr;
	}

	public OssClientBuilder region(String region) {
		this.regionStr = region;
		return this;
	}

	public String accessKey() {
		return accessKey;
	}

	public OssClientBuilder accessKey(String accessKey) {
		this.accessKey = accessKey;
		return this;
	}

	public String accessSecret() {
		return accessSecret;
	}

	public OssClientBuilder accessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
		return this;
	}

	public String bucket() {
		return bucket;
	}

	public OssClientBuilder bucket(String bucket) {
		this.bucket = bucket;
		return this;
	}

	public String domain() {
		return domain;
	}

	public OssClientBuilder domain(String domain) {
		this.domain = domain;
		return this;
	}

	public String downloadPrefix() {
		// 不以 / 结尾
		if (StringUtils.hasText(downloadPrefix) && downloadPrefix.endsWith(OssConstants.SLASH)) {
			return downloadPrefix.substring(0, downloadPrefix.length() - 1);
		}
		return downloadPrefix;
	}

	public OssClientBuilder downloadPrefix(String downloadPrefix) {
		this.downloadPrefix = downloadPrefix;
		return this;
	}

	public String objectKeyPrefix() {
		// 不存在或者是 / 直接返回
		if (!StringUtils.hasText(objectKeyPrefix) || objectKeyPrefix.equals(OssConstants.SLASH)) {
			return "";
		}

		// 保证 root key 以 / 结尾
		if (!objectKeyPrefix.endsWith(OssConstants.SLASH)) {
			objectKeyPrefix = objectKeyPrefix + OssConstants.SLASH;
		}

		// 保证 root key 不以 / 开头
		if (objectKeyPrefix.startsWith(OssConstants.SLASH)) {
			objectKeyPrefix = objectKeyPrefix.substring(1);
		}

		return objectKeyPrefix;
	}

	public OssClientBuilder objectKeyPrefix(String objectKeyPrefix) {
		this.objectKeyPrefix = objectKeyPrefix;
		return this;
	}

	public ObjectCannedACL acl() {
		return acl;
	}

	public OssClientBuilder acl(ObjectCannedACL acl) {
		this.acl = acl;
		return this;
	}

	private S3ClientBuilder create() throws OssBuildException {
		S3ClientBuilder builder = S3Client.builder();

		// 关闭路径形式
		builder.serviceConfiguration(sb -> sb.pathStyleAccessEnabled(false).chunkedEncodingEnabled(false));

		String uriStr = domain;

		// 未使用自定义域名
		if (!StringUtils.hasText(uriStr)) {
			uriStr = endpoint;

			// 亚马逊节点
			if (endpoint.contains(OssConstants.AWS_INTERNATIONAL)
					// 不是s3节点
					&& !endpoint.startsWith(OssConstants.S3)) {
				uriStr = OssConstants.S3 + endpoint;
			}

			final Region region;
			if (StringUtils.hasText(regionStr)) {
				// 配置了区域
				region = Region.of(regionStr);
			}
			else {
				// 未配置, 通过节点解析
				regionStr = uriStr.startsWith(OssConstants.S3)
						// 亚马逊s3节点
						? uriStr.substring(OssConstants.S3.length(),
								uriStr.indexOf(OssConstants.DOT, OssConstants.S3.length() + 1))
						// 其他节点
						: uriStr.substring(0, uriStr.indexOf(OssConstants.DOT));

				region = Region.of(regionStr);
			}

			builder.region(region);

			// 使用托管形式 参考文档
			// https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/VirtualHosting.html
			uriStr = "https://" + bucket + "." + uriStr;
		}
		else {
			// 使用自定义域名
			if (!StringUtils.hasText(regionStr)) {
				throw new OssBuildException("使用自定义域名时, 区域不能为空!");
			}
			builder.region(Region.of(regionStr));
		}

		if (!StringUtils.hasText(downloadPrefix)) {
			// 未指定下载地址时配置下载地址前缀
			downloadPrefix = uriStr;
		}

		try {
			builder.endpointOverride(new URI(uriStr)).credentialsProvider(
					StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, accessSecret)));
		}
		catch (URISyntaxException e) {
			throw new OssBuildException("URI语法异常!", e);
		}

		return builder;
	}

	public S3Client s3Build() throws OssBuildException {
		return create().build();
	}

	/**
	 * 覆写一些配置
	 * @author lingting 2021-05-12 22:37
	 */
	public S3Client s3Build(Consumer<S3ClientBuilder> consumer) throws OssBuildException {
		final S3ClientBuilder builder = create();
		consumer.accept(builder);
		return builder.build();
	}

	public OssClient build() {
		return new OssClient(this);
	}

}
