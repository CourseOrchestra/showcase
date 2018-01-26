package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementSubType;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Класс, содержащий детальный контекст грида. Включает в себя основной контекст
 * плюс настройки, которые интерактивно могут изменять пользователи в процессе
 * работы с гридом. Пользовательские настройки должны восстанавливаться после
 * обновления элемента. Замечание: @XmlRootElement не может указывать на то же
 * имя context, что и CompositeContext!
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "gridContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class GridContext extends CompositeContext {
	private static final long serialVersionUID = 2005065362465664382L;

	private static final int DEF_OFFSET = 0;
	private static final int DEF_LIMIT = 50;

	public GridContext(final CompositeContext aContext) {
		super();
		assignNullValues(aContext);
	}

	@Override
	public String toString() {
		return "GridContext [gridSorting=" + gridSorting + ", liveInfo=" + liveInfo
				+ ", currentRecordId=" + currentRecordId + ", currentColumnId=" + currentColumnId
				+ ", selectedRecordIds=" + selectedRecordIds + ", isFirstLoad=" + isFirstLoad
				+ ", parentId=" + parentId + ", toString()=" + super.toString() + "]";
	}

	private GridSorting gridSorting = null;

	private LiveInfo liveInfo = new LiveInfo(DEF_OFFSET, DEF_LIMIT);

	private GridFilterInfo gridFilterInfo = new GridFilterInfo();

	@XmlTransient
	private GridListOfValuesInfo gridListOfValuesInfo = new GridListOfValuesInfo();

	private DataPanelElementSubType subtype = null;

	/**
	 * Идентификатор parent-записи. Имеет смысл только для tree-грида.
	 */
	private String parentId = null;

	/**
	 * Признак того, что выполняется обновление парентов для обновления текущего
	 * или нижнего уровня. Имеет смысл только для tree-грида.
	 */
	private Boolean updateParents = false;

	/**
	 * Отредактированные данные.
	 */
	private String editorData = null;

	/**
	 * Данные, необходимые для добавления записи.
	 */
	private String addRecordData = null;

	/**
	 * Идентификатор выделенной по клику в гриде записи.
	 */
	private String currentRecordId = null;

	/**
	 * Идентификатор выделенного по клику в гриде столбца. Имеет смысл только в
	 * режиме выделения ячеек.
	 */
	private String currentColumnId = null;

	/**
	 * Массив идентификаторов выделенных с помощью селектора записей в гриде.
	 */
	@XmlElement(name = "selectedRecordId")
	private List<String> selectedRecordIds = new ArrayList<String>();

	/**
	 * Признак того, как нужно обновлять элемент. Если isFirstLoad == true - то
	 * полностью, в противном случае - введенные пользователем данные должны
	 * сохраниться.
	 */
	@XmlTransient
	private Boolean isFirstLoad = false;

	@XmlTransient
	private String fileName = null;

	/**
	 * Сбрасывает настройки таким образом, чтобы сервер вернул все записи на
	 * первой странице.
	 */
	public void resetForReturnAllRecords() {
		liveInfo.setOffset(0);
		liveInfo.setLimit(Integer.MAX_VALUE - 1);
	}

	public GridSorting getGridSorting() {
		return gridSorting;
	}

	public void setGridSorting(final GridSorting aGridSorting) {
		gridSorting = aGridSorting;
	}

	public String getCurrentRecordId() {
		return currentRecordId;
	}

	public void setCurrentRecordId(final String aCurrentRecordId) {
		currentRecordId = aCurrentRecordId;
	}

	public String getCurrentColumnId() {
		return currentColumnId;
	}

	public void setCurrentColumnId(final String aCurrentColumnId) {
		currentColumnId = aCurrentColumnId;
	}

	public List<String> getSelectedRecordIds() {
		return selectedRecordIds;
	}

	public void setSelectedRecordIds(final List<String> aSelectedRecordIds) {
		selectedRecordIds = aSelectedRecordIds;
	}

	/**
	 * Проверка на то, что сортировка присутствует при данных настройках.
	 */
	public boolean sortingEnabled() {
		return (gridSorting != null) && (gridSorting.getSortColId() != null);
	}

	/**
	 * Создает дефолтные настройки для грида - нужны для первоначальной
	 * отрисовки грида и для тестов.
	 */
	public static GridContext createFirstLoadDefault() {
		GridContext result = new GridContext();
		result.isFirstLoad = true;
		return result;
	}

	public GridContext() {
		super();
	}

	public Boolean isFirstLoad() {
		return isFirstLoad;
	}

	public void setIsFirstLoad(final Boolean value) {
		isFirstLoad = value;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String aFileName) {
		fileName = aFileName;
	}

	public LiveInfo getLiveInfo() {
		return liveInfo;
	}

	public void setLiveInfo(final LiveInfo aLiveInfo) {
		liveInfo = aLiveInfo;
	}

	public GridFilterInfo getGridFilterInfo() {
		return gridFilterInfo;
	}

	public void setGridFilterInfo(final GridFilterInfo aGridFilterInfo) {
		gridFilterInfo = aGridFilterInfo;
	}

	public GridListOfValuesInfo getGridListOfValuesInfo() {
		return gridListOfValuesInfo;
	}

	public void setGridListOfValuesInfo(final GridListOfValuesInfo aGridListOfValuesInfo) {
		gridListOfValuesInfo = aGridListOfValuesInfo;
	}

	public DataPanelElementSubType getSubtype() {
		return subtype;
	}

	public void setSubtype(final DataPanelElementSubType aSubtype) {
		subtype = aSubtype;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(final String aParentId) {
		parentId = aParentId;
	}

	public Boolean getUpdateParents() {
		return updateParents;
	}

	public void setUpdateParents(final Boolean aUpdateParents) {
		updateParents = aUpdateParents;
	}

	public String getEditorData() {
		return editorData;
	}

	public void setEditorData(final String aEditorData) {
		editorData = aEditorData;
	}

	public String getAddRecordData() {
		return addRecordData;
	}

	public void setAddRecordData(final String aAddRecordData) {
		addRecordData = aAddRecordData;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public GridContext gwtClone() {
		GridContext res = (GridContext) super.gwtClone();

		res.currentColumnId = currentColumnId;
		res.currentRecordId = currentRecordId;
		res.isFirstLoad = isFirstLoad.booleanValue();
		res.parentId = parentId;
		res.editorData = editorData;
		res.addRecordData = addRecordData;

		res.liveInfo.setOffset(liveInfo.getOffset());
		res.liveInfo.setLimit(liveInfo.getLimit());
		res.liveInfo.setTotalCount(liveInfo.getTotalCount());

		res.gridSorting = gridSorting;

		for (String id : selectedRecordIds) {
			res.selectedRecordIds.add(id);
		}

		res.gridFilterInfo.setMaxId(gridFilterInfo.getMaxId());
		res.gridFilterInfo.getFilters().clear();
		res.gridFilterInfo.getFilters().addAll(gridFilterInfo.getFilters());

		res.gridListOfValuesInfo.setMaxId(gridListOfValuesInfo.getMaxId());
		res.gridListOfValuesInfo.getFilters().clear();
		res.gridListOfValuesInfo.getFilters().addAll(gridListOfValuesInfo.getFilters());

		return res;
	}

	@Override
	protected GridContext newInstance() {
		return new GridContext();
	}

	public void applyCompositeContext(final CompositeContext aContext) {
		if (aContext == null) {
			return;
		}
		setMain(aContext.getMain());
		setAdditional(aContext.getAdditional());
		setFilter(aContext.getFilter());
		setSession(aContext.getSession());
		setSessionParamsMap(aContext.getSessionParamsMap());
		setRelated(aContext.getRelated());
		setCurrentDatapanelWidth(aContext.getCurrentDatapanelWidth());
		setCurrentDatapanelHeight(aContext.getCurrentDatapanelHeight());
	}

}
