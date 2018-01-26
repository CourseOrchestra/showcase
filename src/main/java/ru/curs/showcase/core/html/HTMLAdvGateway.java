package ru.curs.showcase.core.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.*;

/**
 * Шлюз для получения данных, необходимых для построения элемента панели типа
 * XForms.
 * 
 * @author den
 * 
 */
public interface HTMLAdvGateway extends HTMLGateway {

	/**
	 * Сохраняет данные, введенные в карточке на основе XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @param data
	 *            - данные для сохранения.
	 * 
	 */
	void saveData(CompositeContext context, DataPanelElementInfo element, String data);

	/**
	 * Возвращает файл для XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param aLinkId
	 *            - идентификатор ссылки на файл
	 * @return - файл.
	 */
	OutputStreamDataFile downloadFile(XFormContext context, DataPanelElementInfo elementInfo,
			ID aLinkId);

	/**
	 * Загружает (или модифицирует) дополнительные данные через Submission.
	 * 
	 * @param procName
	 *            - идентификатор процедуры.
	 * @param context
	 *            - дополнительные данные, требуемые для загрузки (как правило в
	 *            формате XML).
	 * @return - требуемые данные (как правило в формате XML).
	 * 
	 */
	String scriptTransform(String procName, XFormContext context);

	/**
	 * Загружает файл в хранилище.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param aLinkId
	 *            - ссылка на файл.
	 * @param aFile
	 *            - файл.
	 * **/
	void uploadFile(XFormContext context, DataPanelElementInfo elementInfo, ID aLinkId,
			DataFile<InputStream> aFile);
}
