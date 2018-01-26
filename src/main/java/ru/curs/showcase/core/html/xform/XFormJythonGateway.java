package ru.curs.showcase.core.html.xform;

import java.io.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;

/**
 * Шлюз для XForms для работы с Jython. Некоторые функции - работа с файлами -
 * пока не реализованы.
 * 
 * @author den
 * 
 */
public class XFormJythonGateway implements HTMLAdvGateway {
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;
	private String procName;
	private String data;

	/**
	 * Класс Jython шлюза для сохранения данных XForm.
	 * 
	 * @author den
	 * 
	 */
	class XFormSaveJythonGateway extends JythonQuery<Void> {

		@Override
		protected Object execute() {
			return getProc().save(context, elementInfo.getId().getString(), data);
		}

		@Override
		protected String getJythonProcName() {
			return elementInfo.getSaveProc().getName();
		}

		public XFormSaveJythonGateway() {
			super(Void.class);
		}
	}

	/**
	 * Класс Jython шлюза для сохранения данных XForm.
	 * 
	 * @author den
	 * 
	 */
	class XFormTransformJythonGateway extends JythonQuery<JythonDTO> {

		@Override
		protected Object execute() {
			return getProc().transform(context, data);
		}

		@Override
		protected String getJythonProcName() {
			return procName;
		}

		public XFormTransformJythonGateway() {
			super(JythonDTO.class);
		}
	}

	@Override
	public String scriptTransform(final String aProcName, final XFormContext aContext) {
		context = aContext;
		procName = aProcName;
		data = aContext.getFormData();
		XFormTransformJythonGateway gateway = new XFormTransformJythonGateway();
		gateway.runTemplateMethod();
		context.setOkMessage(gateway.getResult().getUserMessage());
		return gateway.getResult().getData();
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext aContext,
			final DataPanelElementInfo aElementInfo, final ID aLinkId) {
		XFormJythonDownloadHelper xjdh =
			new XFormJythonDownloadHelper(aContext, aElementInfo, aLinkId);
		xjdh.runTemplateMethod();
		JythonDownloadResult jythonResult = xjdh.getResult();

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
	public void uploadFile(final XFormContext aContext, final DataPanelElementInfo aElementInfo,
			final ID aLinkId, final DataFile<InputStream> aFile) {
		XFormJythonUploadHelper xjuh;
		try {
			xjuh = new XFormJythonUploadHelper(aContext, aElementInfo, aLinkId, aFile);
			xjuh.runTemplateMethod();
			JythonErrorResult error = xjuh.getResult();
			if (error != null && error.getErrorCode() != 0) {
				UserMessageFactory factory = new UserMessageFactory();
				UserMessage um = factory.build(error.getErrorCode(), error.getMessage());
				if (um.getType() == MessageType.ERROR) {
					throw new ValidateException(um);
				} else {
					aContext.setOkMessage(um);
				}
			}
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}

	}

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		HTMLGateway gateway = new HTMLJythonGateway();
		return gateway.getRawData(aContext, aElementInfo);
	}

	@Override
	public void saveData(final CompositeContext aContext, final DataPanelElementInfo aElementInfo,
			final String aData) {
		context = aContext;
		elementInfo = aElementInfo;
		data = aData;
		XFormSaveJythonGateway gateway = new XFormSaveJythonGateway();

		gateway.runTemplateMethod();
		context.setOkMessage(gateway.getUserMessage());

	}

}
