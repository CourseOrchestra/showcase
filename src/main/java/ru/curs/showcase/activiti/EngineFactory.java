/**
 * 
 */
package ru.curs.showcase.activiti;

import org.activiti.engine.ProcessEngine;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Класс для получения в скриптах jython (или celesta) созданного объекта движка
 * activiti.
 * 
 * * @author anlug
 * 
 */
public final class EngineFactory {

	private EngineFactory() {
		// never called
	}

	public static ProcessEngine getActivitiProcessEngine() {
		return AppInfoSingleton.getAppInfo().getActivitiProcessEngine();

	}

}
