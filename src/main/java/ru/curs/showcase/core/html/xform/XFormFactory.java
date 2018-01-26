package ru.curs.showcase.core.html.xform;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.slf4j.*;
import org.w3c.dom.Document;
import org.xml.sax.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика по созданию объектов XForms.
 * 
 * @author den
 * 
 */
public final class XFormFactory extends HTMLBasedElementFactory {
	protected static final Logger LOGGER = LoggerFactory.getLogger(XFormFactory.class);

	private static final String XFORMS_CREATE_ERROR =
		"Ошибка при формировании XForms для элемента '%s'";

	/**
	 * Результат работы фабрики.
	 */
	private XForm result;

	/**
	 * Промежуточный результат работы фабрики.
	 */
	private String html;

	public XFormFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public XForm build() throws Exception {
		return (XForm) super.build();
	}

	@Override
	protected void transformData() {

		Date dt1 = new Date();

		DocumentBuilder db = XMLUtils.createBuilder();
		Document template = null;
		XFormTemplateSelector selector = new XFormTemplateSelector(getElementInfo());
		DataFile<InputStream> data =
			selector.getGateway().getRawData(getCallContext(), getElementInfo());

		String stringTemplate = null;
		try {
			// Было
			// template = db.parse(data.getData());

			// Переход на новую версию XForms. Begin
			stringTemplate = TextUtils.streamToString(data.getData(), data.getEncoding());
			stringTemplate = TextUtils.removeUTF8BOM(stringTemplate);
			stringTemplate = UserDataUtils.replaceVariables(stringTemplate);
			stringTemplate = stringTemplate.replace("xformId", getElementInfo().getId());
			StringReader reader = new StringReader(stringTemplate);
			try {
				InputSource inputSource = new InputSource(reader);
				template = db.parse(inputSource);
			} finally {
				reader.close();
			}
			// Переход на новую версию XForms. End

		} catch (SAXException | IOException e1) {
			throw new XMLFormatException(getElementInfo().getTemplateName(), e1);
		}

		String subformId = XFormProducer.getSubformId(template);
		result.setSubformId(subformId);

		Date dt2 = new Date();
		LoggerHelper.profileToLog(
				getElementInfo().getFullId() + ". Формирование документа шаблона.", dt1, dt2,
				getElementInfo().getType().toString(), "");

		dt1 = new Date();

		template = XFormTemplateModificator.modify(template, getSource().getCallContext(),
				getElementInfo(), result.getSubformId());
		logInput(template);

		dt2 = new Date();
		LoggerHelper.profileToLog(
				getElementInfo().getFullId() + ". Модификация документа шаблона.", dt1, dt2,
				getElementInfo().getType().toString(), "");

		try {
			dt1 = new Date();

			html = XFormProducer.getHTML(template, getSource().getData());
			// html = XFormProducer.getTemplateWithData(template,
			// getSource().getData());

			dt2 = new Date();
			LoggerHelper.profileToLog(
					getElementInfo().getFullId()
							+ ". XSL-преобразование документа шаблона в HTML-строку.",
					dt1, dt2, getElementInfo().getType().toString(), "");

			dt1 = new Date();

			logOutput();
			replaceVariables();

			dt2 = new Date();
			LoggerHelper.profileToLog(getElementInfo().getFullId() + ". Замена переменных.", dt1,
					dt2, getElementInfo().getType().toString(), "");

			dt1 = new Date();

			List<String> lst = new ArrayList<String>(2);
			lst.add(html);

			List<String> cuttered = XFormCutter.xFormParts(stringTemplate);
			if (cuttered.size() > 1) {
				lst.add(cuttered.get(1));
			}

			dt2 = new Date();
			LoggerHelper.profileToLog(
					getElementInfo().getFullId()
							+ ". Разрезание на части для отображения на странице.",
					dt1, dt2, getElementInfo().getType().toString(), "");

			result.setXFormParts(lst);
		} catch (TransformerException | XMLStreamException | IOException e) {
			throw new XSLTTransformException(
					String.format(XFORMS_CREATE_ERROR, getElementInfo().getFullId()), e);
		}
	}

	private void logOutput() {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			Marker marker = MarkerFactory.getDetachedMarker(XMLUtils.XSL_MARKER);
			marker.add(HandlingDirection.OUTPUT.getMarker());
			marker.add(MarkerFactory.getMarker(
					String.format("xslTransform=%s", XSLTransformerPoolFactory.XSLTFORMS_XSL)));
			LOGGER.info(marker, html);
		}
	}

	private void logInput(final Document template) {
		if (!(LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo())) {
			return;
		}
		Marker marker = MarkerFactory.getDetachedMarker(XMLUtils.XSL_MARKER);
		marker.add(HandlingDirection.INPUT.getMarker());
		marker.add(MarkerFactory.getMarker(
				String.format("xslTransform=%s", XSLTransformerPoolFactory.XSLTFORMS_XSL)));
		LOGGER.info(marker, XMLUtils.documentToString(template));
	}

	private String replaceVariables() {
		html = super.replaceVariables(html);
		html = html.replace("xformId", getElementInfo().getId());
		addUserDataToSubmissions();

		html = html.replace("&lt;", "<");
		html = html.replace("&gt;", ">");
		html = html.replace("&amp;", "&");

		html = html.replace("&nbsp;", " ");

		html = html.replace("_figurnayaskobka_", "{");

		return html;
	}

	private void addUserDataToSubmissions() {
		String servletQuery = ExchangeConstants.SECURED_SERVLET_PREFIX + "/"
				+ ExchangeConstants.SUBMIT_SERVLET + "?";
		String userDataParam =
			"userdata=" + AppInfoSingleton.getAppInfo().getCurUserDataId() + "&amp;";

		html = html.replace(servletQuery, servletQuery + userDataParam);
		servletQuery = ExchangeConstants.SECURED_SERVLET_PREFIX + "/xslttransformer?";
		html = html.replace(servletQuery, servletQuery + userDataParam);
		servletQuery = ExchangeConstants.SECURED_SERVLET_PREFIX + "/xslTransform?";
		html = html.replace(servletQuery, servletQuery + userDataParam);
	}

	@Override
	public XForm getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new XForm(getElementInfo());
	}

	@Override
	protected void correctSettingsAndData() {
		// ничего не нужно

	}
}
