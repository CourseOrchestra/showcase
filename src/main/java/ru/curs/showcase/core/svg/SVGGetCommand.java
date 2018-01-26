package ru.curs.showcase.core.svg;

import java.io.ByteArrayOutputStream;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Команда, возвращающая исходный SVG после предварительной обработки.
 * 
 * @author den
 * 
 */
public class SVGGetCommand extends AbstractSVGCommand {

	public SVGGetCommand(final CompositeContext aContext, final GeoMapExportSettings aSettings,
			final String aInput) {
		super(aContext, aSettings, ImageFormat.SVG, aInput);
	}

	@Override
	protected void mainProc() throws Exception {
		super.mainProc();
		byte[] bytes = getInput().getBytes(TextUtils.DEF_ENCODING);
		ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
		os.write(bytes, 0, bytes.length);
		getResult().setData(os);
	}

}
