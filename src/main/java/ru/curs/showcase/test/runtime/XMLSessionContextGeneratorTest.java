package ru.curs.showcase.test.runtime;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.fail;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.chart.ChartGetCommand;
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Тесты для XMLSessionContextGenerator.
 * 
 * @author den
 * 
 */
public class XMLSessionContextGeneratorTest extends AbstractTest {

	@Test
	@Ignore
	// !!!
			public
			void generatorShouldAddRelatedDataToSessionContext() throws SAXException, IOException {
		CompositeContext context = CompositeContext.createCurrent();
		getExtGridContext(context);
		XMLSessionContextGenerator generator = new XMLSessionContextGenerator(context);
		String res = generator.generate();

		String example = getTestData("sessionContextWithRelated.xml");
		assertXMLEqual(example, res);
	}

	@Test
	@Ignore
	// !!!
			public
			void selfRelatedElementIsAllowed() throws SAXException, IOException {
		XFormContext context = new XFormContext(CompositeContext.createCurrent());
		context.setFormData("<model/>");
		getExtGridContext(context);
		DataPanelElementInfo elInfo = new DataPanelElementInfo("08", DataPanelElementType.XFORMS);
		elInfo.getRelated().add(elInfo.getId());

		XMLSessionContextGenerator generator = new XMLSessionContextGenerator(context, elInfo);
		String res = generator.generate();

		String example = getTestData("sessionContextWithSelfRelated.xml");
		assertXMLEqual(example, res);
	}

	@Test
	@Ignore
	// !!!
			public
			void userdataShouldBeSetToDefaultIfNoURLParamInContext() throws IOException,
					SAXException {
		Map<String, List<String>> params = new TreeMap<>();
		CompositeContext context = getTestContext3();
		context.addSessionParams(params);
		DataPanelElementInfo element = getTestChartInfo();

		ChartGetCommand command = new ChartGetCommand(context, element);
		command.execute();

		String res = context.getSession();

		String example = getTestData("sessionContextWithDefUserdata.xml");
		assertXMLEqual(example, res);

	}

	private String getTestData(final String file) throws IOException {
		InputStream is = XMLSessionContextGeneratorTest.class.getResourceAsStream(file);
		if (is == null) {
			fail(String.format("Файл '%s' с тестовыми данными не найден", file));
		}
		return TextUtils.streamToString(is);
	}

	@Test
	@Ignore
	// !!!
			public
			void urlParamsShouldBeAddedToSessionContext() throws IOException, SAXException {
		Map<String, List<String>> params = generateTestURLParams(TEST1_USERDATA);
		final int elID = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, elID);
		action.setSessionContext(params);

		DataPanelGetCommand command = new DataPanelGetCommand(action);
		command.execute();

		String example = getTestData("sessionContextWithURLParams.xml");
		assertXMLEqual(example, action.getContext().getSession());
	}
}
