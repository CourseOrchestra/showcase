package ru.curs.showcase.core.svg;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.util.SVGConvertor;

/**
 * Команда получения PNG образа карты.
 * 
 * @author den
 * 
 */
public class PNGGetCommand extends AbstractSVGCommand {

	public PNGGetCommand(final CompositeContext aContext, final GeoMapExportSettings aSettings,
			final String aInput) {
		super(aContext, aSettings, ImageFormat.PNG, aInput);
	}

	@Override
	protected void mainProc() throws Exception {
		super.mainProc();
		SVGConvertor convertor = new SVGConvertor(getSettings());
		getResult().setData(convertor.svgStringToPNG(getInput()));
	}
}
