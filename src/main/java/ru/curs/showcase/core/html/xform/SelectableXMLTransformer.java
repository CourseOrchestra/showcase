package ru.curs.showcase.core.html.xform;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.html.XSLTransformSelector;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Оболочка над UserXMLTransformer.
 * 
 * @author den
 * 
 */
public class SelectableXMLTransformer {

	private OutputStreamDataFile subject = null;
	private final DataPanelElementProc proc;
	private final CompositeContext context;
	private final DataPanelElementInfo elementInfo;
	private UserXMLTransformer internal;
	private String strSubject = null;

	public SelectableXMLTransformer(final OutputStreamDataFile aSubject,
			final DataPanelElementProc aProc, final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		super();
		subject = aSubject;
		proc = aProc;
		context = aContext;
		elementInfo = aElInfo;
	}

	public SelectableXMLTransformer(final String aStrSubject,
			final DataPanelElementProc aProc, final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		super();
		strSubject = aStrSubject;
		proc = aProc;
		context = aContext;
		elementInfo = aElInfo;
	}

	public void transform() throws IOException {
		XSLTransformSelector selector = new XSLTransformSelector(context, elementInfo, proc);
		DataFile<InputStream> transform = selector.getData();

		XSDSelector xsdSelector = new XSDSelector(context, elementInfo, proc);
		XSDSource xsdSource = xsdSelector.getGateway();

		if (subject != null) {
			internal = new UserXMLTransformer(subject, proc, transform, xsdSource);
		} else {
			internal = new UserXMLTransformer(strSubject, proc, transform, xsdSource);
		}
		internal.checkAndTransform();
	}

	public DataFile<InputStream> getInputStreamResult() {
		return internal.getInputStreamResult();
	}

	public OutputStreamDataFile getOutputStreamResult() throws IOException {
		return internal.getOutputStreamResult();
	}

	public String getStringResult() throws IOException {
		return internal.getStringResult();
	}

}
