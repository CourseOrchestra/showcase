package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Возможные подтипы элементов информационной панели (пока только для гридов).
 * 
 */
public enum DataPanelElementSubType implements SerializableElement {

	/**
	 * JS-Грид. Live.
	 */
	JS_LIVE_GRID,
	/**
	 * JS-Грид. Page.
	 */
	JS_PAGE_GRID,
	/**
	 * JS-Грид. Tree.
	 */
	JS_TREE_GRID,
	/**
	 * JS-Грид. Lyra.
	 */
	JS_LYRA_GRID;

}
