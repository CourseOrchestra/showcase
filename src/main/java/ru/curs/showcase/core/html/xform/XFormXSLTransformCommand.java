package ru.curs.showcase.core.html.xform;

import java.io.InputStream;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.XSLTransformSelector;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда преобразования XForm c помощью XSL.
 * 
 * @author den
 * 
 */
public final class XFormXSLTransformCommand extends XFormContextCommand<String> {

	public XFormXSLTransformCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		XSLTransformSelector selector = new XSLTransformSelector(getContext(), getElementInfo());
		DataFile<InputStream> transform = selector.getData();
		Document doc = XMLUtils.stringToDocument(getContext().getFormData());
		setResult(XMLUtils.xsltTransform(doc, transform));
	}
}
