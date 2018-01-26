package ru.curs.showcase.app.api.plugin;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Параметры плагина.
 * 
 * @author bogatov
 * 
 */
public class PluginParam extends JavaScriptObject {
	/**
	 * Опции плагина.
	 */
	public static class Options extends JavaScriptObject {
		protected Options() {
		};

		/**
		 * Ширина.
		 * 
		 * @return String
		 */
		public final native String dataWidth()/*-{
			return this.dataWidth;
		}-*/;

		/**
		 * Высота.
		 * 
		 * @return String
		 */
		public final native String dataHeight()/*-{
			return this.dataHeight;
		}-*/;
		
		/**
		 * Ширина.
		 * 
		 * @return String
		 */
		public final native void setDataWidth(final String dataWidth)/*-{
			this.dataWidth = dataWidth;
		}-*/;

		/**
		 * Высота.
		 * 
		 * @return String
		 */
		public final native void setDataHeight(final String dataHeight)/*-{
			this.dataHeight = dataHeight;
		}-*/;

		/**
		 * Заголовок окна для выбора из больших списков.
		 * 
		 * @return String
		 */
		public final native String windowCaption()/*-{
			return this.windowCaption;
		}-*/;

		/**
		 * onSelectionComplete.
		 * 
		 * @param ok
		 *            boolean
		 * 
		 * @param plugin
		 *            объект плагина возвращаемый адаптером плагина
		 */
		public final native void onSelectionComplete(final boolean ok,
				final JavaScriptObject plugin)/*-{
			if (this.onSelectionComplete != null) {
				this.onSelectionComplete(ok, plugin);
			}
		}-*/;

		/**
		 * Название кнопки Ok.
		 * 
		 * @return String
		 */
		public final native String buttonOkLabel()/*-{
			return this.buttonOkLabel;
		}-*/;

		/**
		 * Название кнопки Cancel.
		 * 
		 * @return String
		 */
		public final native String buttonCancelLabel()/*-{
			return this.buttonCancelLabel;
		}-*/;
	}

	protected PluginParam() {
	};

	/**
	 * Id элемента.
	 * 
	 * @return String
	 */
	public final native String id()/*-{
		return this.id;
	}-*/;

	/**
	 * Имя плагина.
	 * 
	 * @return String
	 */
	public final native String plugin()/*-{
		return this.plugin;
	}-*/;

	/**
	 * Идентификатор родительского HTML элемента.
	 * 
	 * @return String
	 */
	public final native String parentId()/*-{
		return this.parentId;
	}-*/;

	/**
	 * Установить идентификатор родительского HTML элемента.
	 * 
	 * @return String
	 */
	public final native void setParentId(final String parentId)/*-{
		this.parentId = parentId;
	}-*/;

	/**
	 * Процедура загрузки данных.
	 * 
	 * @return String
	 */
	public final native String proc()/*-{
		return this.proc;
	}-*/;

	/**
	 * Процедура пост обработки данных (POSTPROCESS).
	 * 
	 * @return String
	 */
	public final native String postProcessProc()/*-{
		return this.postProcessProc;
	}-*/;

	/**
	 * Процедура подгрузки данных.
	 * 
	 * @return String
	 */
	public final native String getDataProcName()/*-{
		return this.getDataProcName;
	}-*/;

	/**
	 * Параметры.
	 * 
	 * @return String
	 */
	public final native JavaScriptObject params()/*-{
		return this.params || {};
	}-*/;

	/**
	 * Опции.
	 * 
	 * @return Options
	 */
	public final native Options options()/*-{
		var options = this.options || {};
		options.dataWidth = options.dataWidth || '400px';
		options.dataHeight = options.dataHeight || '250px';
		options.buttonOkLabel = options.buttonOkLabel || 'Ok';
		options.buttonCancelLabel = options.buttonCancelLabel || 'Отмена';
		return options;
	}-*/;
	
	/**
	 * Дополнительные параметры передаваемые на сторону сервера.
	 * 
	 * @return String
	 */
	public final native JavaScriptObject generalFilters()/*-{
		return this.generalFilters;
	}-*/;
}
