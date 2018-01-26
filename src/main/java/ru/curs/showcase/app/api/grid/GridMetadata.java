package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс метаданных грида.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GridMetadata extends DataPanelCompBasedElement implements SizeEstimate {

	private static final long serialVersionUID = 2492137452715570464L;

	private List<GridColumnConfig> columns = new ArrayList<GridColumnConfig>();
	private List<VirtualColumn> virtualColumns = null;

	private LiveInfo liveInfo = new LiveInfo();
	private JSInfo jsInfo = new JSInfo();

	private String textColor = null;
	private String backgroundColor = null;
	private String fontSize = null;
	private Set<FontModifier> fontModifiers = null;

	private String autoSelectRecordId = null;
	private String autoSelectColumnId = null;

	private boolean expandAllRecords = false;

	private GridSorting gridSorting = null;

	/**
	 * Настройки UI для грида. Как правило, задаются по умолчанию для всех
	 * гридов в файле настроек приложения.
	 */
	private GridUISettings uiSettings = new GridUISettings();

	public GridMetadata() {
		super();
	}

	public GridMetadata(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	public GridUISettings getUISettings() {
		return uiSettings;
	}

	public void setUISettings(final GridUISettings aSettings) {
		uiSettings = aSettings;
	}

	@Override
	public final GridEventManager getEventManager() {
		return (GridEventManager) super.getEventManager();
	}

	@Override
	protected GridEventManager initEventManager() {
		return new GridEventManager();
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		for (Event ev : getEventManager().getEvents()) {
			result += ev.sizeEstimate();
		}
		return result;
	}

	/**
	 * Возвращает действие для отрисовки зависимого элемента.
	 * 
	 * @return - действие.
	 */
	@Override
	public Action getActionForDependentElements() {
		if (autoSelectRecordId != null) {
			String columnId = null;

			if (autoSelectColumnId != null) {
				columnId = autoSelectColumnId;
			}

			List<GridEvent> events = getEventManager().getEventForCell(autoSelectRecordId,
					columnId, InteractionType.SINGLE_CLICK);
			GridEvent res = getConcreteEvent(events);
			if (res != null) {
				return res.getAction();
			}

			events = getEventManager().getEventForCell(autoSelectRecordId, columnId,
					InteractionType.DOUBLE_CLICK);
			res = getConcreteEvent(events);
			if (res != null) {
				return res.getAction();
			}
		} else {
			return getDefaultAction();
		}

		return null;
	}

	private GridEvent getConcreteEvent(final List<GridEvent> events) {
		return (GridEvent) events.toArray()[events.size() - 1];
	}

	public List<GridColumnConfig> getColumns() {
		return columns;
	}

	public void setColumns(final List<GridColumnConfig> aColumns) {
		columns = aColumns;
	}

	public List<VirtualColumn> getVirtualColumns() {
		return virtualColumns;
	}

	public void setVirtualColumns(final List<VirtualColumn> aVirtualColumns) {
		virtualColumns = aVirtualColumns;
	}

	public LiveInfo getLiveInfo() {
		return liveInfo;
	}

	public void setLiveInfo(final LiveInfo aLiveInfo) {
		liveInfo = aLiveInfo;
	}

	public JSInfo getJSInfo() {
		return jsInfo;
	}

	public void setJSInfo(final JSInfo aJsInfo) {
		jsInfo = aJsInfo;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(final String aTextColor) {
		textColor = aTextColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(final String aBackgroundColor) {
		backgroundColor = aBackgroundColor;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(final String aFontSize) {
		fontSize = aFontSize;
	}

	public Set<FontModifier> getFontModifiers() {
		return fontModifiers;
	}

	public void setFontModifiers(final Set<FontModifier> aFontModifiers) {
		fontModifiers = aFontModifiers;
	}

	/**
	 * Добавляет модификатор шрифта.
	 * 
	 * @param fontm
	 *            модификатор шрифта
	 */
	public void addFontModifier(final FontModifier fontm) {
		if (fontModifiers == null) {
			fontModifiers = new HashSet<FontModifier>();
		}

		if (!fontModifiers.contains(fontm)) {
			fontModifiers.add(fontm);
		}
	}

	/**
	 * Удаляет модификатор шрифта.
	 * 
	 * @param fontm
	 *            модификатор шрифта
	 */
	public void delFontModifier(final FontModifier fontm) {
		if (fontModifiers == null) {
			fontModifiers = new HashSet<FontModifier>();
		}

		fontModifiers.remove(fontm);
	}

	public String getAutoSelectRecordId() {
		return autoSelectRecordId;
	}

	public void setAutoSelectRecordId(final String aAutoSelectRecordId) {
		autoSelectRecordId = aAutoSelectRecordId;
	}

	public String getAutoSelectColumnId() {
		return autoSelectColumnId;
	}

	public void setAutoSelectColumnId(final String aAutoSelectColumnId) {
		autoSelectColumnId = aAutoSelectColumnId;
	}

	public GridSorting getGridSorting() {
		return gridSorting;
	}

	public void setGridSorting(final GridSorting aGridSorting) {
		gridSorting = aGridSorting;
	}

	public boolean isExpandAllRecords() {
		return expandAllRecords;
	}

	public void setExpandAllRecords(final boolean aExpandAllRecords) {
		expandAllRecords = aExpandAllRecords;
	}

}
