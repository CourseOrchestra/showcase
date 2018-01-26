package ru.curs.showcase.core.primelements;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.DataFile;

/**
 * Интерфейс шлюза данных для навигатора и инф. панели.
 * 
 * @author den
 * 
 */
public interface PrimElementsGateway extends Closeable {

	/**
	 * Базовая функция возврата данных.
	 * 
	 * @return - данные в формате xml.
	 * @param aContext
	 *            - контекст.
	 */
	DataFile<InputStream> getRawData(CompositeContext aContext);

	/**
	 * Расширенная функция возврата данных.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param aSourceName
	 *            - имя источника - файла или процедуры.
	 */
	DataFile<InputStream> getRawData(CompositeContext aContext, String aSourceName);

	/**
	 * В переопределенной версии убрано контролируемое исключение.
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	void close();

	void setSourceName(String aSourceName);
}