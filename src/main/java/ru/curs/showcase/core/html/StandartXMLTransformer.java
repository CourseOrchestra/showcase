package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Класс для трансформации XML данных из HTMLBasedElementRawData. Выполняет XSL
 * трансформацию (из любого источника).
 * 
 * @author den
 * 
 */
public final class StandartXMLTransformer {
	private final HTMLBasedElementRawData source;

	public StandartXMLTransformer(final HTMLBasedElementRawData aSource) {
		source = aSource;
	}

	public String transform() {
		XSLTransformSelector selector =
			new XSLTransformSelector(source.getCallContext(), source.getElementInfo());
		DataFile<InputStream> transform = selector.getData();
		String data = XMLUtils.xsltTransform(source.getData(), transform);
		return data;
	}
}
