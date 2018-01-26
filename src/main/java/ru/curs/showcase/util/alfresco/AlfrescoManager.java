package ru.curs.showcase.util.alfresco;

import java.io.*;
import java.nio.charset.Charset;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.json.*;
import org.python.core.*;

/**
 * Класс интеграции Showcase и Alfresco.
 * 
 */
public final class AlfrescoManager {

	private static final String DEF_ENCODING = "UTF-8";
	private static final int HTTP_OK = 200;
	private static final int RESULT_OK = 0;
	private static final int RESULT_ERROR = 1;

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	private static final String ALF_TICKET = "alf_ticket";

	private AlfrescoManager() {
	}

	public static AlfrescoLoginResult login(final String alfURL, final String alfUser,
			final String alfPass) {

		AlfrescoLoginResult ar = new AlfrescoLoginResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost = new HttpPost(alfURL + "/service/api/login");
			httppost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			httppost.setEntity(new StringEntity("{\"username\" : \"" + alfUser
					+ "\",\"password\" : \"" + alfPass + "\"}"));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					ar.setResult(RESULT_OK);
					ar.setTicket(jo.getJSONObject("data").getString("ticket"));
				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос логирования вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);

		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;
	}

	public static AlfrescoUploadFileResult uploadFile(final String fileName,
			final InputStream file, final String alfURL, final String alfTicket,
			final PyDictionary alfUploadParams) {

		AlfrescoUploadFileResult ar = new AlfrescoUploadFileResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost =
				new HttpPost(alfURL + "/service/api/upload?" + ALF_TICKET + "=" + alfTicket);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.setCharset(Charset.forName(DEF_ENCODING));

			builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, fileName);

			for (int i = 0; i < alfUploadParams.items().__len__(); i++) {
				PyTuple tup = (PyTuple) alfUploadParams.items().__getitem__(i);
				builder.addPart(tup.__getitem__(0).toString(), new StringBody(tup.__getitem__(1)
						.toString()));
			}

			httppost.setEntity(builder.build());

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					ar.setResult(RESULT_OK);
					ar.setNodeRef(jo.getString("nodeRef"));
				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос загрузки файла в Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}

	public static AlfrescoDeleteFileResult deleteFile(final String alfFileId, final String alfURL,
			final String alfTicket) {

		AlfrescoDeleteFileResult ar = new AlfrescoDeleteFileResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpDelete httpdelete =
				new HttpDelete(alfURL
						+ "/service/slingshot/doclib/action/file/node/workspace/SpacesStore/"
						+ alfFileId + "?" + ALF_TICKET + "=" + alfTicket);

			HttpResponse response = httpclient.execute(httpdelete);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					boolean overallSuccess = jo.getBoolean("overallSuccess");
					if (overallSuccess) {
						ar.setResult(RESULT_OK);
					} else {
						ar.setErrorMessage(resContent);
					}

				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос удаления файла из Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}

	public static AlfrescoGetFileMetaDataResult getFileMetaData(final String alfFileId,
			final String alfURL, final String alfTicket, final String acceptLanguage) {

		AlfrescoGetFileMetaDataResult ar = new AlfrescoGetFileMetaDataResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpGet httpget =
				new HttpGet(alfURL + "/service/api/metadata?nodeRef=workspace://SpacesStore/"
						+ alfFileId + "&" + ALF_TICKET + "=" + alfTicket);

			if (!((acceptLanguage == null) || (acceptLanguage.trim().isEmpty()))) {
				httpget.setHeader("Accept-Language", acceptLanguage);
			}

			HttpResponse response = httpclient.execute(httpget);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					ar.setMetaData(resContent);
					ar.setResult(RESULT_OK);
				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос получения метаданных файла из Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}

	public static AlfrescoSetFileMetaDataResult setFileMetaData(final String alfFileId,
			final String metaData, final String alfURL, final String alfTicket,
			final String acceptLanguage) {

		AlfrescoSetFileMetaDataResult ar = new AlfrescoSetFileMetaDataResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost =
				new HttpPost(alfURL + "/service/api/metadata/node/workspace/SpacesStore/"
						+ alfFileId + "?" + ALF_TICKET + "=" + alfTicket);
			httppost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			if (!((acceptLanguage == null) || (acceptLanguage.trim().isEmpty()))) {
				httppost.setHeader("Accept-Language", acceptLanguage);
			}

			StringEntity entity = new StringEntity(metaData);
			entity.setContentType(APPLICATION_JSON);

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					boolean success = jo.getBoolean("success");
					if (success) {
						ar.setResult(RESULT_OK);
					} else {
						ar.setErrorMessage(resContent);
					}

				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос задания метаданных файла из Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}

	public static AlfrescoGetFileVersionsResult getFileVersions(final String alfFileId,
			final String alfURL, final String alfTicket) {

		AlfrescoGetFileVersionsResult ar = new AlfrescoGetFileVersionsResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpGet httpget =
				new HttpGet(alfURL + "/service/api/version?nodeRef=workspace://SpacesStore/"
						+ alfFileId + "&" + ALF_TICKET + "=" + alfTicket);

			HttpResponse response = httpclient.execute(httpget);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					ar.setVersions(resContent);
					ar.setResult(RESULT_OK);
				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос получения версий файла из Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}

	public static AlfrescoCreateFolderResult createFolder(final String alfParentFolderId,
			final String alfCreateFolderParams, final String alfURL, final String alfTicket) {

		AlfrescoCreateFolderResult ar = new AlfrescoCreateFolderResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost =
				new HttpPost(alfURL + "/service/api/node/folder/workspace/SpacesStore/"
						+ alfParentFolderId + "?" + ALF_TICKET + "=" + alfTicket);
			httppost.setHeader(CONTENT_TYPE, APPLICATION_JSON);

			StringEntity entity = new StringEntity(alfCreateFolderParams);
			entity.setContentType(APPLICATION_JSON);

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					ar.setResult(RESULT_OK);
					ar.setNodeRef(jo.getString("nodeRef"));
				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос создания директории в Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}

}
