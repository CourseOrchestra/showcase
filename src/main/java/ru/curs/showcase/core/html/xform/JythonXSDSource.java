package ru.curs.showcase.core.html.xform;

import java.io.InputStream;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.core.html.ElementPartsJythonGateway;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.*;

/**
 * Jython источник для XSD схем. По сути, переходник от XSDSource к
 * ElementPartsJythonGateway.
 * 
 * @author den
 * 
 */
public class JythonXSDSource extends ElementPartsJythonGateway implements XSDSource {

	private final DataPanelElementContext context;

	public JythonXSDSource(final DataPanelElementContext aContext, final String aSourceName) {
		super();
		context = aContext;
		setSource(aSourceName);
	}

	@Override
	public Schema getSchema(final String aSourceName) throws SAXException {
		DataFile<InputStream> file =
			getRawData(context.getCompositeContext(), context.getElementInfo());
		return XMLUtils.createSchemaForStream(file.getData());
	}

	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.USER;
	}

}
