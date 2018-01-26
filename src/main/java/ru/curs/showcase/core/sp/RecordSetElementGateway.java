package ru.curs.showcase.core.sp;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;

/**
 * Единый шлюз для элементов инф. панели, основанных на компоненте, данные для
 * которой загружаются в виде RecordSet.
 * 
 * @author den
 * 
 * @param <T>
 */
public interface RecordSetElementGateway<T> {

	RecordSetElementRawData getRawData(T context, DataPanelElementInfo element);

}
