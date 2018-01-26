package ru.curs.showcase.app.server;

import java.io.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.core.svg.*;
import ru.curs.showcase.util.*;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Обработчик для экспорта карты в векторный или растровый формат.
 * 
 * @author den
 * 
 */
public class GeoMapExportHandler extends AbstractDownloadHandler {

	private static final String SVG_DATA_PARAM = "svg";

	private GeoMapExportSettings settings = null;
	private ImageFormat imageFormat = null;
	private String svg = null;

	private AbstractSVGCommand command = null;

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iterator = upload.getItemIterator(getRequest());
		while (iterator.hasNext()) {
			FileItemStream item = iterator.next();

			String name = item.getFieldName();
			InputStream input = item.openStream();
			// несмотря на то, что нам нужен InputStream - его приходится
			// преобразовывать в OutputStream - т.к. чтение из InputStream
			// возможно только в данном цикле
			ByteArrayOutputStream out = StreamConvertor.inputToOutputStream(input);
			String paramValue = out.toString(TextUtils.DEF_ENCODING);

			if (GeoMapExportSettings.class.getName().equals(name)) {
				settings = (GeoMapExportSettings) deserializeObject(paramValue);
			} else if (ImageFormat.class.getName().equals(name)) {
				imageFormat = ImageFormat.valueOf(paramValue);
			} else if (SVG_DATA_PARAM.equals(name)) {
				svg = paramValue;
			}
		}
	}

	@Override
	protected void processFiles() throws UnsupportedEncodingException {
		CompositeContext context = ServletUtils.prepareURLParamsContext(getRequest());
		switch (imageFormat) {
		case PNG:
			command = new PNGGetCommand(context, settings, svg);
			break;
		case JPG:
			command = new JPGGetCommand(context, settings, svg);
			break;
		case SVG:
			command = new SVGGetCommand(context, settings, svg);
			break;
		default:
			break;
		}
		setOutputFile(command.execute());
	}

	@Override
	protected void fillResponse() throws IOException {
		getResponse().setCharacterEncoding(TextUtils.DEF_ENCODING);
		super.fillResponse();
	}

	@Override
	protected void setContentType() {
		getResponse().setContentType(command.getImageFormat().getContentType());
	}

}
