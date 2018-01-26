package ru.curs.showcase.core.grid;

import java.util.*;

import org.w3c.dom.*;

import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Построитель XML документов на основе объекта грида.
 * 
 */
public class GridToExcelXMLFactory extends GeneralXMLHelper {
	/**
	 * Тэг для описания свойств столбца.
	 */
	public static final String COLUMN_TAG = "Column";
	/**
	 * Тэг для ячейки.
	 */
	public static final String CELL_TAG = "Cell";
	/**
	 * Тэг для записи.
	 */
	public static final String ROW_TAG = "Row";
	/**
	 * Корневой тэг для таблицы.
	 */
	public static final String TABLE_TAG = "Table";

	/**
	 * Результирующий документ.
	 */
	private Document result;

	private List<GridColumnConfig> columns = null;
	private List<HashMap<String, String>> records = null;

	public GridToExcelXMLFactory(final List<GridColumnConfig> aColumns,
			final List<HashMap<String, String>> aRecords) {
		super();

		columns = aColumns;
		records = aRecords;
	}

	/**
	 * Функция построителя.
	 * 
	 * @return - XML документ.
	 */
	public Document build() {
		result = createDoc();
		addColumnsData();
		addHeader();
		addRows();
		return result;
	}

	private void addRows() {
		for (HashMap<String, String> record : records) {
			addRow(false, record);
		}
	}

	private Document createDoc() {
		return XMLUtils.createEmptyDoc(TABLE_TAG);
	}

	private void addHeader() {
		addRow(true, null);
	}

	private void addRow(final boolean isHeader, final HashMap<String, String> record) {
		Element rowNode = result.createElement(ROW_TAG);
		for (GridColumnConfig col : columns) {
			Element node = result.createElement(CELL_TAG);
			if (isHeader) {
				node.setAttribute(TYPE_TAG, GridValueType.STRING.toStringForExcel());
				node.appendChild(result.createTextNode(col.getCaption()));
			} else {
				if (col.getValueType() == null) {
					node.setAttribute(TYPE_TAG, GridValueType.STRING.toStringForExcel());
				} else {
					node.setAttribute(TYPE_TAG, col.getValueType().toStringForExcel());
				}
				node.appendChild(result.createTextNode(record.get(col.getId())));
			}
			rowNode.appendChild(node);
		}
		getRoot().appendChild(rowNode);
	}

	private void addColumnsData() {
		Element node;
		for (GridColumnConfig col : columns) {
			if (col.getWidth() != null) {
				node = result.createElement(COLUMN_TAG);
				node.setAttribute(WIDTH_TAG, col.getWidth().toString());
				getRoot().appendChild(node);
			}
		}
	}

	private Element getRoot() {
		return result.getDocumentElement();
	}
}
