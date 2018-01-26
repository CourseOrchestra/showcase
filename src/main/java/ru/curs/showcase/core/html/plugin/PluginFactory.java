package ru.curs.showcase.core.html.plugin;

import java.io.*;
import java.util.*;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания UI плагинов.
 * 
 * @author den
 * 
 */
public final class PluginFactory extends HTMLBasedElementFactory {

	public static final String COMPONENTS_DIR = "libraries";
	public static final String IMPORT_TXT = "import.txt";
	public static final String PLUGINS_DIR = "plugins";
	private Plugin result;

	public PluginFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public Plugin getResult() {
		return result;
	}

	@Override
	public PluginInfo getElementInfo() {
		return (PluginInfo) super.getElementInfo();
	}

	@Override
	protected void initResult() {
		result = new Plugin(getElementInfo());
	}

	@Override
	public Plugin build() throws Exception {
		return (Plugin) super.build();
	}

	@Override
	protected void transformData() {
		if (getSource().getData() == null) {
			return;
		}
		StandartXMLTransformer transformer = new StandartXMLTransformer(getSource());
		String data = transformer.transform();
		data = replaceVariables(data);
		if (getElementInfo().getPostProcessProcName() != null) {
			PluginPostProcessJythonGateway gateway =
				new PluginPostProcessJythonGateway(getCallContext(), getElementInfo(), data);
			String[] params = gateway.postProcess();
			result.getParams().addAll(Arrays.asList(params));
		} else {
			result.getParams().add(data);
		}
	}

	@Override
	protected void correctSettingsAndData() {
		result.setCreateProc("create" + TextUtils.capitalizeWord(getElementInfo().getPlugin()));
		checkImport(getPluginDir(), getElementInfo().getPlugin() + ".js",
				SettingsFileType.PLUGIN_ADAPTER);
		result.getRequiredJS().add(
				getAdapterForWebServer(getPluginDir(), getElementInfo().getPlugin() + ".js"));
		readComponents();
	}

	private void checkImport(final String dir, final String adapterFile,
			final SettingsFileType fileType) {
		String adapter = String.format("%s/%s/%s", getPluginsRoot(), dir, adapterFile);
		File file = new File(adapter);
		if (!file.exists()) {
			throw new SettingsFileOpenException(String.format("%s/%s", dir, adapterFile), fileType);
		}
	}

	private String getAdapterForWebServer(final String dir, final String adapterFile) {
		String adapter = String.format("%s/%s", dir, adapterFile);
		// String adapterOnTomcat =
		// String.format("%s/%s/%s", UserDataUtils.SOLUTIONS_DIR,
		// UserDataUtils.getUserDataId(),
		// adapter);
		String adapterOnTomcat =
			String.format("%s/%s/%s", UserDataUtils.SOLUTIONS_DIR, "general", adapter);
		return adapterOnTomcat;
	}

	private String getPluginsRoot() {
		// return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
		return AppInfoSingleton.getAppInfo().getSolutionsDirRoot() + "/"
				+ UserDataUtils.GENERAL_RES_ROOT;
	}

	private void readComponents() {
		List<String> comps = readImportFile(getPluginDir() + "/" + IMPORT_TXT);

		if ("navigator".equalsIgnoreCase(getElementInfo().getPlugin())) {
			for (String comp : comps) {
				result.getRequiredJS().add(getAdapterForWebServer(getPluginDir(), comp));
			}
		} else {
			for (String comp : comps) {
				String compDirName =
					String.format("%s/%s/%s", getPluginsRoot(), COMPONENTS_DIR, comp);
				File comDir = new File(compDirName);
				if (!comDir.exists()) {
					throw new RuntimeException(String.format("Компонент '%s' не найден", comp));
				}
				addImport(comp, "scriptList.txt", result.getRequiredJS());
				addImport(comp, "styleList.txt", result.getRequiredCSS());
			}
		}

	}

	private void addImport(final String comp, final String fileName, final List<String> list) {
		List<String> deps = readImportFile(COMPONENTS_DIR + "/" + comp + "/" + fileName);
		for (String dep : deps) {
			if (dep.trim().isEmpty()) {
				continue;
			}
			checkImport(COMPONENTS_DIR + "/" + comp, dep, SettingsFileType.LIBRARY_ADAPTER);
			list.add(getAdapterForWebServer(COMPONENTS_DIR + "/" + comp, dep));
		}
	}

	private List<String> readImportFile(final String fileName) {
		List<String> res = new ArrayList<>();
		File importFile = new File(getPluginsRoot() + "/" + fileName);
		if (!importFile.exists()) {
			return res;
		}

		String list;
		try {
			InputStream is = new FileInputStream(importFile.getAbsolutePath());
			list = TextUtils.streamToString(is);
		} catch (IOException e) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.IMPORT_LIST);
		}
		String[] compNames = list.split("\\r?\\n");
		for (String name : compNames) {
			if (name.trim().isEmpty()) {
				continue;
			}
			res.add(name);
		}
		return res;
	}

	private String getPluginDir() {
		return String.format("%s/%s", PLUGINS_DIR, getElementInfo().getPlugin());
	}

	@Override
	protected void addHandlers(final EventFactory<HTMLEvent> factory) {
		super.addHandlers(factory);

		SAXTagHandler handler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				String value;
				// Integer intValue;
				if (attrs.getIndex(WIDTH_TAG) > -1) {
					// value = attrs.getValue(WIDTH_TAG);
					// intValue = TextUtils.getIntSizeValue(value);
					// result.getSize().setWidth(intValue);

					value = attrs.getValue(WIDTH_TAG);
					if (TextUtils.isInteger(value)) {
						value = value + "px";
					}
					result.getStringSize().setWidth(value);
				}
				if (attrs.getIndex(HEIGHT_TAG) > -1) {
					// value = attrs.getValue(HEIGHT_TAG);
					// intValue = TextUtils.getIntSizeValue(value);
					// result.getSize().setHeight(intValue);

					value = attrs.getValue(HEIGHT_TAG);
					if (TextUtils.isInteger(value)) {
						value = value + "px";
					}
					result.getStringSize().setHeight(value);
				}
				return null;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags = { PROPS_TAG };
				return tags;
			}

		};
		factory.addHandler(handler);
	}
}
