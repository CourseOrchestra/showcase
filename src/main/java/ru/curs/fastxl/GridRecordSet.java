package ru.curs.fastxl;

/**
 * Интерфейс набора данных грида.
 * 
 */
public interface GridRecordSet {

	/**
	 * Переходит к следующей записи. Возвращает false, если переход не
	 * осуществлён по причине достижения конца набора.
	 */
	boolean next() throws EFastXLRuntime;

	/**
	 * Является ли колонка i целочисленной.
	 * 
	 * @param i
	 *            номер колонки.
	 */
	boolean isInteger(int i) throws EFastXLRuntime;

	/**
	 * Является ли колонка i колонкой с плавающей запятой.
	 * 
	 * @param i
	 *            номер колонки.
	 */
	boolean isFloat(int i) throws EFastXLRuntime;

	/**
	 * Возвращает название колонки.
	 * 
	 * @param i
	 *            номер колонки.
	 */
	String getColumnName(int i) throws EFastXLRuntime;

	/**
	 * Возвращает количество колонок.
	 */
	int getColumnCount() throws EFastXLRuntime;

	/**
	 * Возвращает значение колонки в виде числа с плавающей точкой.
	 * 
	 * @param i
	 *            номер колонки
	 */
	double getDouble(int i) throws EFastXLRuntime;

	/**
	 * Возвращает значение колонки в виде целого числа.
	 * 
	 * @param i
	 *            номер колонки
	 */
	int getInt(int i) throws EFastXLRuntime;

	/**
	 * Возвращает значение колонки в виде строки.
	 * 
	 * @param i
	 *            номер колонки
	 */
	String getString(int i) throws EFastXLRuntime;

}
