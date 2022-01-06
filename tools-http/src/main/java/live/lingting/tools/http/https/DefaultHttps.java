package live.lingting.tools.http.https;

import static live.lingting.tools.http.constant.HttpConstants.SSL_V3;
import static live.lingting.tools.http.constant.HttpConstants.TLS_V1;
import static live.lingting.tools.http.constant.HttpConstants.TLS_V11;
import static live.lingting.tools.http.constant.HttpConstants.TLS_V12;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.experimental.UtilityClass;
import live.lingting.tools.http.exception.HttpException;

/**
 * @author lingting
 */
@UtilityClass
public class DefaultHttps {

	public static final String DALVIK = "dalvik";

	public static final String VM_NAME = "java.vm.name";

	/**
	 * Android低版本不重置的话某些SSL访问就会失败
	 */
	private static final String[] ANDROID_PROTOCOLS = { SSL_V3, TLS_V1, TLS_V11, TLS_V12 };

	/**
	 * 默认信任全部的域名校验器
	 */
	public static final HostnameVerifier HOSTNAME_VERIFIER = (s, sslSession) -> true;

	public static final KeyManager[] KEY_MANAGERS = null;

	public static final TrustManager[] TRUST_MANAGERS = { new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	} };

	/**
	 * 默认的SSLSocketFactory，区分安卓
	 */
	public static final SSLSocketFactory SSF;

	static {
		try {
			if (DALVIK.equalsIgnoreCase(System.getProperty(VM_NAME))) {
				// 兼容android低版本SSL连接
				SSF = new SSLFactory(ANDROID_PROTOCOLS);
			}
			else {
				SSF = new SSLFactory();
			}
		}
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new HttpException("ssf 创建失败!", e);
		}
	}

}
