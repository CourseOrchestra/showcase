package ru.curs.showcase.core.html.xform;

import java.io.*;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;

/**
 * Шлюз для XForms для работы с Celesta.
 * 
 * @author bogatov
 * 
 */
public class XFormCelestaGateway implements HTMLAdvGateway {
	private static final String NO_UPLOAD_PROC_ERROR =
		"Не задана процедура загрузки файлов на сервер для linkId=";
	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура скачивания файлов с сервера для linkId=";

	@Override
	public String scriptTransform(final String procName, final XFormContext context) {
		CelestaHelper<String> helper = new CelestaHelper<String>(context, String.class);
		String data = context.getFormData();

		// String dataJson = XMLUtils.convertXmlToJson(data);
		String dataJson = null;
		try {
			dataJson = XMLJSONConverter.xmlToJson(data);
		} catch (SAXException | IOException e) {
			throw new XMLJSONConverterException(e);
		}

		String result = helper.runPython(procName, dataJson);
		return result;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final ID aLinkId) {
		CelestaHelper<JythonDownloadResult> helper =
			new CelestaHelper<JythonDownloadResult>(context, JythonDownloadResult.class);
		DataPanelElementProc proc = elementInfo.getProcs().get(aLinkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR + aLinkId);
		}
		String data = context.getFormData();
		// String dataJson = XMLUtils.convertXmlToJson(data);
		String dataJson = null;
		try {
			dataJson = XMLJSONConverter.xmlToJson(data);
		} catch (SAXException | IOException e1) {
			throw new XMLJSONConverterException(e1);
		}
		JythonDownloadResult jythonResult =
			helper.runPython(proc.getName(), elementInfo.getId().getString(), dataJson);

		JythonErrorResult error = jythonResult.getError();
		if (error != null && error.getErrorCode() != 0) {
			UserMessageFactory factory = new UserMessageFactory();
			throw new ValidateException(factory.build(error.getErrorCode(), error.getMessage()));
		}

		InputStream is = jythonResult.getInputStream();
		if (is == null) {
			throw new FileIsAbsentInDBException();
		}
		String fileName = jythonResult.getFileName();
		try {
			StreamConvertor dup = new StreamConvertor(is);
			ByteArrayOutputStream os = dup.getOutputStream();
			OutputStreamDataFile result = new OutputStreamDataFile(os, fileName);
			result.setEncoding(TextUtils.JDBC_ENCODING);
			return result;
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
	}

	@Override
	public void uploadFile(final XFormContext context, final DataPanelElementInfo elementInfo,
			final ID aLinkId, final DataFile<InputStream> file) {
		CelestaHelper<Void> helper = new CelestaHelper<Void>(context, Void.class);
		DataPanelElementProc proc = elementInfo.getProcs().get(aLinkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_UPLOAD_PROC_ERROR + aLinkId);
		}
		String data = context.getFormData();
		// String dataJson = XMLUtils.convertXmlToJson(data);
		String dataJson = null;
		try {
			dataJson = XMLJSONConverter.xmlToJson(data);
		} catch (SAXException | IOException e) {
			throw new XMLJSONConverterException(e);
		}
		helper.runPython(proc.getName(), elementInfo.getId().getString(), dataJson, file.getName(),
				file.getData());
	}

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		HTMLGateway gateway = new HTMLCelestaGateway();
		return gateway.getRawData(aContext, aElementInfo);
	}

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String data) {
		final String elementId = elementInfo.getId().getString();
		CelestaHelper<Void> helper = new CelestaHelper<Void>(context, Void.class);
		// String dataJson = XMLUtils.convertXmlToJson(data);
		String dataJson = null;
		try {
			dataJson = XMLJSONConverter.xmlToJson(data);
		} catch (SAXException | IOException e) {
			throw new XMLJSONConverterException(e);
		} // convertXmlToJson(data);
		helper.runPython(elementInfo.getSaveProc().getName(), elementId, dataJson);
	}

}
