package ru.curs.showcase.security.esia;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.jcajce.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.*;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.json.*;

import ru.curs.showcase.util.TextUtils;

/**
 * Класс интеграции Showcase и ESIA.
 * 
 */
public final class ESIAManager {

	private static final String BC = "BC";
	private static final String UTF8 = "UTF-8";

	private static final String PARAM_SCOPE = "scope";
	private static final String PARAM_TIMESTAMP = "timestamp";
	private static final String PARAM_CLIENT_ID = "client_id";
	private static final String PARAM_STATE = "state";
	private static final String PARAM_CLIENT_SECRET = "client_secret";
	private static final String PARAM_REDIRECT_URI = "redirect_uri";
	private static final String PARAM_RESPONSE_TYPE = "response_type";
	private static final String PARAM_ACCESS_TYPE = "access_type";
	private static final String PARAM_CODE = "code";
	private static final String PARAM_GRANT_TYPE = "grant_type";
	private static final String PARAM_TOKEN_TYPE = "token_type";

	private static final String VALUE_RESPONSE_TYPE = "code";
	private static final String VALUE_GRANT_TYPE = "authorization_code";
	private static final String VALUE_TOKEN_TYPE = "Bearer";
	private static final String VALUE_ACCESS_TYPE = "online";

	private static final String URL_AUTHORIZATION = "/aas/oauth2/ac";
	private static final String URL_LOGOUT = "/idp/ext/Logout";
	private static final String URL_TOKEN_EXCHANGE = "/aas/oauth2/te";
	private static final String URL_USER_INFO = "/rs/prns/%s";
	private static final String URL_USER_CONTACTS = "/rs/prns/%s/ctts?embed=(elements)";

	private static CMSSignedDataGenerator generator = null;

	private ESIAManager() {
	}

	public static void init() {
		if (EsiaSettings.isEsiaEnable()) {

			try {

				if (Security.getProvider(BC) != null) {
					Security.removeProvider(BC);
				}
				Security.addProvider(new BouncyCastleProvider());

				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BC);
				JcaX509CertificateConverter certconv =
					new JcaX509CertificateConverter().setProvider(BC);

				PrivateKey privateKey = null;
				X509CertificateHolder x509CertificateHolder = null;

				PEMParser parser = null;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(EsiaSettings.CERT_FILE_NAME);
					parser = new PEMParser(new InputStreamReader(fis));
					Object objCertificate = parser.readObject();
					parser.close();
					fis.close();
					x509CertificateHolder = (X509CertificateHolder) parsePemObject(objCertificate,
							null, converter, certconv);

					fis = new FileInputStream(EsiaSettings.KEY_FILE_NAME);
					parser = new PEMParser(new InputStreamReader(fis));
					Object objPrivateKey = parser.readObject();
					parser.close();
					fis.close();
					privateKey = (PrivateKey) parsePemObject(objPrivateKey, EsiaSettings.KEY_PASS,
							converter, certconv);

					generator = new CMSSignedDataGenerator();
					ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256withRSA")
							.setProvider(BC).build(privateKey);

					generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
							new JcaDigestCalculatorProviderBuilder().setProvider(BC).build())
									.build(sha256Signer, x509CertificateHolder));

					generator.addCertificate(x509CertificateHolder);

				} finally {
					if (fis != null) {
						fis.close();
					}
					if (parser != null) {
						parser.close();
					}
				}
			} catch (Exception e) {
				EsiaSettings.setEsiaEnable(false);
				throw new ESIAException(e);
			}

		}
	}

	private static Object parsePemObject(final Object in, final String pphrase,
			final JcaPEMKeyConverter converter, final JcaX509CertificateConverter certconv) {
		Object o = in;
		try {
			if (o instanceof PEMEncryptedKeyPair) {
				o = ((PEMEncryptedKeyPair) o).decryptKeyPair(
						new JcePEMDecryptorProviderBuilder().build(pphrase.toCharArray()));
			} else if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
				InputDecryptorProvider pkcs8decoder =
					new JceOpenSSLPKCS8DecryptorProviderBuilder().build(pphrase.toCharArray());
				o = converter.getPrivateKey(
						((PKCS8EncryptedPrivateKeyInfo) o).decryptPrivateKeyInfo(pkcs8decoder));
			}
		} catch (Throwable t) {
			throw new ESIAException("Failed to decode private key", t);
		}

		if (o instanceof PEMKeyPair) {
			try {
				return converter.getKeyPair((PEMKeyPair) o);
			} catch (PEMException e) {
				throw new ESIAException("Failed to construct public/private key pair", e);
			}
		} else if (o instanceof RSAPrivateCrtKey) {
			return o;
		} else if (o instanceof X509CertificateHolder) {
			return o;
		} else {
			return o;
		}
	}

	private static void putClientSecret(final HashMap<String, String> params) {

		try {
			CMSTypedData msg =
				new CMSProcessableByteArray((params.get(PARAM_SCOPE) + params.get(PARAM_TIMESTAMP)
						+ params.get(PARAM_CLIENT_ID) + params.get(PARAM_STATE)).getBytes());

			synchronized (generator) {
				CMSSignedData sigData = generator.generate(msg, false);
				params.put(PARAM_CLIENT_SECRET,
						new String(Base64.getUrlEncoder().encode(sigData.getEncoded()), UTF8));
			}

		} catch (Exception e) {
			throw new ESIAException(e);
		}

	}

	private static String getTimeStamp() {
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		return utc.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss Z"));
	}

	public static boolean isAllowAuthenticateOnlyTrustedUser() {
		return EsiaSettings.ALLOW_AUTHENTICATE_ONLY_TRUSTED_USER;
	}

	public static String getLogoutURL() {
		String url = EsiaSettings.URL_BASE + URL_LOGOUT + "?" + PARAM_CLIENT_ID + "="
				+ EsiaSettings.VALUE_CLIENT_ID;
		if (EsiaSettings.VALUE_LOGOUT_REDIRECT_URI != null) {
			url = url + "&redirect_url=" + EsiaSettings.VALUE_LOGOUT_REDIRECT_URI;
		}
		return url;
	}

	public static String getAuthorizationURL() {
		try {

			HashMap<String, String> params = new HashMap<String, String>();

			params.put(PARAM_CLIENT_ID, EsiaSettings.VALUE_CLIENT_ID);
			params.put(PARAM_REDIRECT_URI, EsiaSettings.VALUE_REDIRECT_URI);
			params.put(PARAM_SCOPE, EsiaSettings.VALUE_SCOPE.replaceAll(";", " "));
			params.put(PARAM_RESPONSE_TYPE, VALUE_RESPONSE_TYPE);
			params.put(PARAM_STATE, UUID.randomUUID().toString());
			params.put(PARAM_TIMESTAMP, getTimeStamp());
			params.put(PARAM_ACCESS_TYPE, VALUE_ACCESS_TYPE);

			putClientSecret(params);

			String url = EsiaSettings.URL_BASE + URL_AUTHORIZATION + "?";

			String query = "";
			for (String key : params.keySet()) {
				if (!query.isEmpty()) {
					query = query + "&";
				}
				query = query + key + "=" + URLEncoder.encode(params.get(key), UTF8);
			}

			url = url + query;

			return url;

		} catch (Exception e) {
			throw new ESIAException(e);
		}
	}

	// CHECKSTYLE:OFF
	public static ESIAUserInfo getUserInfo(final String code) {

		ESIAUserInfo ui = new ESIAUserInfo();

		HttpsURLConnection conn = null;
		try {
			try {

				HashMap<String, String> params = new HashMap<String, String>();

				params.put(PARAM_CLIENT_ID, EsiaSettings.VALUE_CLIENT_ID);
				params.put(PARAM_CODE, code);
				params.put(PARAM_GRANT_TYPE, VALUE_GRANT_TYPE);
				params.put(PARAM_REDIRECT_URI, EsiaSettings.VALUE_REDIRECT_URI);
				params.put(PARAM_TIMESTAMP, getTimeStamp());
				params.put(PARAM_TOKEN_TYPE, VALUE_TOKEN_TYPE);
				params.put(PARAM_SCOPE, EsiaSettings.VALUE_SCOPE.replaceAll(";", " "));
				params.put(PARAM_STATE, UUID.randomUUID().toString());

				putClientSecret(params);

				// --------------------------------------------------

				long oid = 0;
				String accessToken = null;

				StringBuilder postData = new StringBuilder();
				for (Entry<String, String> param : params.entrySet()) {
					if (postData.length() != 0) {
						postData.append("&");
					}
					postData.append(URLEncoder.encode(param.getKey(), UTF8));
					postData.append("=");
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), UTF8));
				}
				byte[] postDataBytes = postData.toString().getBytes(UTF8);

				URL url = new URL(EsiaSettings.URL_BASE + URL_TOKEN_EXCHANGE);

				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.getOutputStream().write(postDataBytes);

				conn.connect();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

					String resContent = TextUtils.streamToString(conn.getInputStream());

					JSONObject jo = new JSONObject(resContent);

					String idToken = jo.getString("id_token");
					accessToken = jo.getString("access_token");

					String[] idTokenParts = idToken.split("\\.");

					String payload =
						new String(Base64.getUrlDecoder().decode(idTokenParts[1]), UTF8);

					jo = new JSONObject(payload);

					oid = jo.getJSONObject("urn:esia:sbj").getLong("urn:esia:sbj:oid");

				} else {
					throw new ESIAException("Ошибка при получении маркера доступа, responseCode = "
							+ conn.getResponseCode());
				}

				conn.disconnect();

				// --------------------------------------------------

				url = new URL(EsiaSettings.URL_BASE + String.format(URL_USER_INFO, oid));

				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				conn.connect();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

					String resContent = TextUtils.streamToString(conn.getInputStream());

					JSONObject jo = new JSONObject(resContent);

					ui.setOid(oid);
					if (jo.has("snils")) {
						ui.setSnils(jo.getString("snils"));
					}
					if (jo.has("trusted")) {
						ui.setTrusted(jo.getBoolean("trusted"));
					}
					if (jo.has("firstName")) {
						ui.setFirstName(jo.getString("firstName"));
					}
					if (jo.has("lastName")) {
						ui.setLastName(jo.getString("lastName"));
					}
					if (jo.has("middleName")) {
						ui.setMiddleName(jo.getString("middleName"));
					}
					if (jo.has("gender")) {
						ui.setGender(jo.getString("gender"));
					}
					if (jo.has("birthDate")) {
						ui.setBirthDate(jo.getString("birthDate"));
					}
					if (jo.has("birthPlace")) {
						ui.setBirthPlace(jo.getString("birthPlace"));
					}

				} else {
					throw new ESIAException(
							"Ошибка при получении данных о пользователе, responseCode = "
									+ conn.getResponseCode());
				}

				conn.disconnect();

				// --------------------------------------------------

				url = new URL(EsiaSettings.URL_BASE + String.format(URL_USER_CONTACTS, oid));

				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				conn.connect();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

					String resContent = TextUtils.streamToString(conn.getInputStream());

					JSONObject jo = new JSONObject(resContent);
					int size = 0;
					if (jo.has("size")) {
						size = jo.getInt("size");
					}

					if (size > 0) {
						String phn = null;
						JSONArray ja = jo.getJSONArray("elements");
						for (int i = 0; i < size; i++) {
							String type = ja.getJSONObject(i).getString("type");
							String value = ja.getJSONObject(i).getString("value");
							if ("EML".equalsIgnoreCase(type)) {
								ui.setEmail(value);
							}
							if ("MBT".equalsIgnoreCase(type)) {
								ui.setPhone(value);
							}
							if ("PHN".equalsIgnoreCase(type)) {
								phn = value;
							}
						}
						if (ui.getPhone() == null) {
							ui.setPhone(phn);
						}
					}

				} else {
					throw new ESIAException(
							"Ошибка при получении данных о пользователе, responseCode = "
									+ conn.getResponseCode());
				}

			} catch (Exception e) {
				throw new ESIAException(e);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return ui;
	}
	// CHECKSTYLE:ON

}
