package ru.curs.showcase.runtime;

import java.io.File;
import java.util.*;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.util.exception.ServerLogicError;

//import java.io.File;

/**
 * Пул интерпретаторов Jython.
 * 
 * @author den
 * 
 */
public final class JythonIterpretatorFactory extends PoolByUserdata<PythonInterpreter> {
	private static final String PYTHON_SCRIPTS_DIR_NOT_FOUND =
		"Каталог со стандартными python скриптами '%s' не найден";
	public static final String LIB_JYTHON_PATH = "/WEB-INF/libJython";
	public static final String SCRIPTS_JYTHON_PATH = "scripts/jython";

	private static final JythonIterpretatorFactory INSTANCE = new JythonIterpretatorFactory();

	private String libDir = LIB_JYTHON_PATH;

	public void setLibDir(final String aLibDir) {
		libDir = aLibDir;
	}

	public void resetLibDir() {
		libDir = LIB_JYTHON_PATH;
	}

	private JythonIterpretatorFactory() {
		super();
	}

	public static JythonIterpretatorFactory getInstance() {
		return INSTANCE;
	}

	@Override
	protected void cleanReusable(final PythonInterpreter aReusable) {
		super.cleanReusable(aReusable);
		aReusable.cleanup();
	}

	@Override
	protected PythonInterpreter createReusableItem() {
		PySystemState state = new PySystemState();

		if (getJarList().size() > 0) {
			for (String jarName : getJarList()) {
				state.path.append(new PyString(jarName));
			}
		}

		// PySystemState state = Py.getSystemState();
		state.path.append(new PyString(getUserDataScriptDir()));
		// File genScriptDir = new File(getGeneralScriptDir());
		// if (genScriptDir.exists()) {
		if (getGeneralScriptDir().size() > 0) {
			for (String path : getGeneralScriptDir()) {
				state.path.append(new PyString(path));
			}
		}
		// }

		if (getGeneralScriptDirFromWebInf("libJython").size() > 0) {
			for (String path : getGeneralScriptDirFromWebInf("libJython")) {
				state.path.append(new PyString(path));
			}
		}
		File jythonLibPath = new File(AppInfoSingleton.getAppInfo().getWebAppPath() + libDir);
		if (!jythonLibPath.exists()) {
			throw new ServerLogicError(String.format(PYTHON_SCRIPTS_DIR_NOT_FOUND, libDir));
		}
		state.path.append(new PyString(jythonLibPath.getAbsolutePath()));

		jythonLibPath =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + libDir + "/site-packages");
		if (jythonLibPath.exists()) {
			state.path.append(new PyString(jythonLibPath.getAbsolutePath()));
		}

		return new PythonInterpreter(null, state);
	}

	public static String getUserDataScriptDir() {
		if (AppInfoSingleton.getAppInfo().getCurUserDataId() == null)
			AppInfoSingleton.getAppInfo().setCurUserDataId("default");
		return AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/"
				+ SCRIPTS_JYTHON_PATH;
	}

	public static List<String> getGeneralScriptDir() {
		List<String> pathList = new ArrayList<String>();
		File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());

		File[] files = fileRoot.listFiles();
		for (File f : files) {
			if (f.getName().startsWith("common.")) {
				File fileN =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName()
							+ "/" + SCRIPTS_JYTHON_PATH);
				if (fileN.exists())
					pathList.add(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
							+ f.getName() + "/" + SCRIPTS_JYTHON_PATH);
			}
		}

		return pathList;
		// return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
		// + UserDataUtils.GENERAL_RES_ROOT + "/" + SCRIPTS_JYTHON_PATH;
	}

	public static List<String> getGeneralScriptDirFromWebInf(String libFolder) {
		List<String> pathList = new ArrayList<String>();
		File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());

		File[] files = fileRoot.listFiles();
		for (File f : files) {
			// if (f.getName().startsWith("common.")) {
			File fileN =
				new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName()
						+ "/WEB-INF/" + libFolder);
			if (fileN.exists())
				pathList.add(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName()
						+ "/WEB-INF/" + libFolder);
			if (libFolder.equals("libJython")) {
				fileN =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName()
							+ "/WEB-INF/" + libFolder + "/site-packages");
				if (fileN.exists())
					pathList.add(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
							+ f.getName() + "/WEB-INF/" + libFolder + "/site-packages");
			}
			// }
		}

		return pathList;
	}

	public static List<String> getJarList() {
		List<String> jarList = new ArrayList<String>();
		for (String path : getGeneralScriptDirFromWebInf("lib")) {
			String[] jarArray = (new File(path)).list();
			if (jarArray != null && jarArray.length > 0) {
				for (String jar : jarArray) {
					if (jar.endsWith(".jar")) {
						jarList.add(path + "/" + jar);
					}
				}
			}
		}
		return jarList;
	}

	public String getLibJythonDir() {
		return AppInfoSingleton.getAppInfo().getWebAppPath() + libDir;
	}

	@Override
	protected Pool<PythonInterpreter> getLock() {
		return INSTANCE;
	}
}
