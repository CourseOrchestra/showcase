package ru.curs.showcase.test.runtime;

import static org.junit.Assert.*;

import java.io.*;
import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;

import org.junit.Test;

import ru.curs.showcase.app.server.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.FileUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Тесты класса AppProps.
 */
public final class UserDataUtilsTest extends AbstractTestWithDefaultUserData {

	private static final String GE_KEY_NAME = "ymapsKey";
	private static final String YM_KEY =
		"AMOPgE4BAAAA9Y-BUwMAonjZ5NBRJDj54c-cDVPzQcYlLNAAAAAAAAAAAACPSuKS9WyCiMuXm9An1ZKCx5Pk-A==";

	/**
	 * Тест ф-ции loadResToStream.
	 */
	@Test
	public void testLoadResToStream() {
		assertNotNull(FileUtils.loadClassPathResToStream(FileUtils.GENERAL_PROPERTIES));
	}

	/**
	 * Тест ф-ции loadUserDataToStream и получение пути к каталогу с
	 * пользовательскими данными.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadUserDataToStream() throws IOException {
		assertNotNull(UserDataUtils.loadUserDataToStream(UserDataUtils.PROPFILENAME));
	}

	/**
	 * Тест ф-ции getValueByName.
	 * 
	 */
	@Test
	public void testGetValueByName() {
		assertNotNull(UserDataUtils
				.getGeneralRequiredProp(SecurityParamsFactory.AUTH_SERVER_URL_PARAM));
		assertNotNull(UserDataUtils.getRequiredProp(ConnectionFactory.CONNECTION_URL_PARAM));
		assertNotNull(UserDataUtils.getOptionalProp(ConnectionFactory.CONNECTION_URL_PARAM));

		assertEquals("group_icon_default1.png",
				UserDataUtils.getOptionalProp("navigator.def.icon.name", TEST1_USERDATA));

		assertEquals("ora:pg:test1:test2", UserDataUtils.getGeneralOptionalProp("copy.userdatas"));
		assertNotNull(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication"));
		assertEquals("false", UserDataUtils.getGeneralRequiredProp("activiti.enable"));
	}

	@Test
	public void testDirExists() {
		checkDir(SettingsFileType.XSLT.getFileDir());
		checkDir(UserDataUtils.XSLTTRANSFORMSFORGRIDDIR);
		checkDir(SettingsFileType.DATAPANEL.getFileDir());
		checkDir(SettingsFileType.NAVIGATOR.getFileDir());
		checkDir(UserDataUtils.SCHEMASDIR);
		checkDir(SettingsFileType.XFORM.getFileDir());

		assertTrue("Папка с XSD схемами не найдена", (new File(AppInfoSingleton.getAppInfo()
				.getWebAppPath() + "/WEB-INF/classes/" + UserDataUtils.SCHEMASDIR)).exists());
	}

	private void checkDir(final String dirName) {
		File dir = new File(UserDataUtils.getUserDataCatalog() + File.separator + dirName);
		assertTrue(dir.exists());
	}

	/**
	 * Проверка чтения информации о главном окне из app.properties.
	 */
	@Test
	public void testReadMainPageInfo() {
		assertEquals("100px",
				UserDataUtils.getOptionalProp(UserDataUtils.HEADER_HEIGHT_PROP, TEST1_USERDATA));
		assertEquals("50px",
				UserDataUtils.getOptionalProp(UserDataUtils.FOOTER_HEIGHT_PROP, TEST1_USERDATA));
		assertNull(UserDataUtils.getOptionalProp(UserDataUtils.HEADER_HEIGHT_PROP, TEST2_USERDATA));
		assertNull(UserDataUtils.getOptionalProp(UserDataUtils.FOOTER_HEIGHT_PROP, TEST2_USERDATA));
	}

	@Test(expected = NoSuchUserDataException.class)
	public void testAppPropsExists() {
		UserDataUtils.checkAppPropsExists("test33");
	}

	@Test(expected = SettingsFileOpenException.class)
	public void testCheckUserdatas() {
		try {
			AppInfoSingleton.getAppInfo().getUserdatas().put("test34", new UserData("c:\\"));
			UserDataUtils.checkUserdatas();
		} finally {
			AppInfoSingleton.getAppInfo().getUserdatas().clear();
			AppInitializer.finishUserdataSetupAndCheckLoggingOverride();
		}
	}

	@Test
	public void testGeoMapKeys() {
		assertEquals(YM_KEY, UserDataUtils.getGeoMapKey(GE_KEY_NAME, "localhost"));
		assertEquals(YM_KEY, UserDataUtils.getGeoMapKey(GE_KEY_NAME, "127.0.0.1"));
		assertEquals(YM_KEY, UserDataUtils.getGeoMapKey(GE_KEY_NAME, "mail.ru"));
		assertEquals("", UserDataUtils.getGeoMapKey("", "localhost"));
	}

	@Test
	public void testJXMEnable() throws MalformedObjectNameException {
		final String pathToTest = "/WEB-INF/classes/ru/curs/showcase/test/runtime/";
		try {
			UserDataUtils.setGeneralPropFile(AppInfoSingleton.getAppInfo().getWebAppPath()
					+ pathToTest + "jmxdisable.properties");
			JMXBeanRegistrator.register();
			assertFalse(ManagementFactory.getPlatformMBeanServer().isRegistered(
					JMXBeanRegistrator.getShowcaseMBeanName()));

			UserDataUtils.setGeneralPropFile(AppInfoSingleton.getAppInfo().getWebAppPath()
					+ pathToTest + "jmxenable.properties");
			JMXBeanRegistrator.register();
			assertTrue(ManagementFactory.getPlatformMBeanServer().isRegistered(
					JMXBeanRegistrator.getShowcaseMBeanName()));
			JMXBeanRegistrator.unRegister();
		} finally {
			UserDataUtils.setGeneralPropFile(null);
		}
	}

	@Test
	public void isGeneralappPropertiesExist() {
		assertTrue((new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
				+ UserDataUtils.GENERALPROPFILENAME)).exists());
	}

	@Test
	public void isCommonSysExist() {
		assertTrue((new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + "common.sys"))
				.exists());
	}
}
