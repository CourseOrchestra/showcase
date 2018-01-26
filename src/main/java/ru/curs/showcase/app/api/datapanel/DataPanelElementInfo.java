package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс с описанием элемента информационной панели. Примечание: свойство
 * {@link #tab} в классе носит справочный характер и поэтому не учитывается при
 * сравнении и не сериализуется в XML!
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "element")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPanelElementInfo extends TransferableElement
		implements SerializableElement, ru.curs.showcase.app.api.SelfCheckObject {
	private static final String KEEP_USER_SETTINGS_ERROR =
		"Невозможно получить значение keepUserSettings для действия, не содержащего блока для работы с инф. панелью";
	public static final int DEF_TIMER_INTERVAL = 600;

	private static final long serialVersionUID = -6461216659708261808L;

	/**
	 * Идентификатор элемента.
	 */
	private ID id;

	/**
	 * Позиция элемента на вкладке.
	 */
	private Integer position;

	/**
	 * Тип элемента панели управления.
	 */
	private DataPanelElementType type;

	/**
	 * Подтип элемента панели управления.
	 */
	private DataPanelElementSubType subtype = null;

	/**
	 * Признак редактируемости элемента (пока только для гридов из GXT).
	 */
	private Boolean editable = false;

	/**
	 * Наименование хранимой процедуры, которая загружает данные в элемент. Для
	 * тестовых файловых шлюзов - имя файла, из которого загружаются данные.
	 */
	private String procName;

	/**
	 * Наименование файла XSLT трансформации, используемой для преобразования
	 * данных, полученных из БД. Требуется только для элементов типа "WEBTEXT".
	 * 
	 */
	private String transformName;

	/**
	 * Наименование файла шаблона, используемого для отображения элемента.
	 * Шаблон преобразуется в HTML с помощью XSLT. Требуется только для
	 * элементов типа "XFORMS", причем является обязательным для этих элементов.
	 */
	private String templateName;

	/**
	 * Признак того, что при первой отрисовке элемента он прячется и не
	 * наполняется реальными данными. Необходим для оптимизации быстродействия.
	 */
	private Boolean hideOnLoad = false;

	/**
	 * Признак того, что элемент никогда не будет показываться внутри вкладки, а
	 * будет отрываться в внешнем по отношению к вкладке окне.
	 */
	private Boolean neverShowInPanel = false;

	/**
	 * Стандартные атрибуты для HTML элемента, созданного на основе данное
	 * элемента панели. Включают в себя описания стилей.
	 */
	private HTMLAttrs htmlAttrs = new HTMLAttrs();

	/**
	 * Признак того, что нужно сохранять данные элемента и не обращаться к
	 * серверу повторно при возврате на вкладку или панель элемента. На
	 * выполнение действий с типом RELOAD_ELEMENTS данная опция не влияет. В
	 * режиме cacheData = true должна быть возможность принудительного
	 * обновления элемента. Элементы кэшируются по составному ключу, включающему
	 * в себя FullId элемента и main context.
	 */
	private Boolean cacheData = false;

	/**
	 * Признак того, нужно ли обновлять содержимое элемента по таймеру. При этом
	 * время отсчитывается от последнего из 3 событий: 1) последней загрузки
	 * данных элемента, инициированной из UI, 2) выполнения действия,
	 * обновившего данные текущего элемента, 3) последнего обновления по
	 * таймеру.
	 */
	private Boolean refreshByTimer = false;
	/**
	 * Интервал обновления панели в секундах. Используется только если
	 * refreshByTimer=true.
	 */
	private Integer refreshInterval = DEF_TIMER_INTERVAL;

	/**
	 * Дополнительные процедуры для элемента панели управления. Используются для
	 * элементов XForms.
	 */
	private Map<ID, DataPanelElementProc> procs = new TreeMap<ID, DataPanelElementProc>();

	/**
	 * Идентификаторы связанных с данным элементов. Контексты связанных
	 * элементов передаются в БД.
	 */
	private List<ID> related = new ArrayList<ID>();

	/**
	 * Ссылка на вкладку панели, на которой расположен элемент.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private DataPanelTab tab;

	/**
	 * Определяет, показывать ли сообщение о загрузке элемента. Запрет на показ
	 * сообщения приводит к том, что элемент будет обновлен только после
	 * получения данных от сервера. Это нужно, если вы хотите избежать мигания
	 * панели. Но это плохо, если загрузка элемента происходит достаточно долго.
	 */
	private Boolean showLoadingMessage = false;

	private Boolean showLoadingMessageForFirstTime = true;

	private List<String> group = null;

	/**
	 * Определяет, встраивать ли в темплейт иксформы дополнительные части,
	 * определённые в других файлах (xml-, питон- или челеста-файлах, лежащих в
	 * соответствующих местах юзердаты).
	 */
	private boolean buildTemplate = true;

	public DataPanelElementInfo(final Integer aPosition, final DataPanelTab aTab) {
		super();
		position = aPosition;
		tab = aTab;
	}

	public DataPanelElementInfo() {
		super();
	}

	@Override
	public boolean isCorrect() {
		if ((id == null) || !checkRelatedExistances()) {
			return false;
		}
		switch (type) {
		case WEBTEXT:
			return (procName != null) || (transformName != null);
		case GRID:
		case CHART:
		case GEOMAP:
			return procName != null;
		case XFORMS:
			return templateName != null;
		case JSFORM:
			return templateName != null;
		default:
			return true;
		}
	}

	private boolean checkRelatedExistances() {
		for (ID key : related) {
			if (tab.getElementInfoById(key) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Метод для определения того что у элемента датапанели, может быть свойство
	 * related, ссылающееся на элемент, у которого атрибут
	 * neverShowInPanel="true".
	 * 
	 * @return булевское значение
	 */
	public Boolean wrongRelated() {
		if (checkRelatedExistances()) {
			for (ID key : related) {
				if (tab.getElementInfoById(key).getNeverShowInPanel()) {
					return true;
				}
			}
		}
		return false;
	}

	public DataPanelElementInfo(final String aId, final DataPanelElementType aType) {
		super();
		id = new ID(aId);
		type = aType;
	}

	public final ID getId() {
		return id;
	}

	public final void setId(final ID aId) {
		this.id = aId;
	}

	public final void setId(final String aId) {
		this.id = new ID(aId);
	}

	public final DataPanelElementType getType() {
		return type;
	}

	public final DataPanelElementSubType getSubtype() {
		return subtype;
	}

	public final void setSubtype(final DataPanelElementSubType aSubtype) {
		this.subtype = aSubtype;
	}

	public final Boolean getEditable() {
		return editable;
	}

	public final void setEditable(final Boolean aEditable) {
		this.editable = aEditable;
	}

	public final void setType(final DataPanelElementType aType) {
		this.type = aType;
	}

	public final String getProcName() {
		return procName;
	}

	public final void setProcName(final String aProcName) {
		this.procName = aProcName;
	}

	public final String getTransformName() {
		return transformName;
	}

	public final void setTransformName(final String aTransformName) {
		this.transformName = aTransformName;
	}

	public final Integer getPosition() {
		return position;
	}

	public final void setPosition(final Integer aPosition) {
		position = aPosition;
	}

	/**
	 * Возвращает процедуру для сохранения данных.
	 */
	public DataPanelElementProc getSaveProc() {
		return getProcByType(DataPanelElementProcType.SAVE);
	}

	/**
	 * Возвращает процедуру для получения метаданных. Если данная процедура
	 * отсутствует - значит для загрузки данных и метаданных используется одна и
	 * та же процедура - getProcName().
	 */
	public DataPanelElementProc getMetadataProc() {
		return getProcByType(DataPanelElementProcType.METADATA);
	}

	/**
	 * Возвращает процедуру для получения панели инструментов.
	 */
	public DataPanelElementProc getToolBarProc() {
		return getProcByType(DataPanelElementProcType.TOOLBAR);
	}

	/**
	 * Возвращает процедуру для получения данных экспорта в Excel.
	 */
	public DataPanelElementProc getExportDataProc() {
		return getProcByType(DataPanelElementProcType.EXPORTDATA);
	}

	/**
	 * @return true если задана TOOLBAR процедура, иначе false
	 */
	public boolean isToolBarProc() {
		return getToolBarProc() != null;
	}

	public final Boolean getHideOnLoad() {
		return hideOnLoad;
	}

	public final void setHideOnLoad(final Boolean aHideOnLoad) {
		hideOnLoad = aHideOnLoad;
	}

	/**
	 * Возвращает процедуру определенного типа. Использовать данную функция
	 * имеет смысл только с теми типами процедур, который должны быть в одном
	 * экземпляре в элементе.
	 * 
	 * @param procType
	 *            - тип процедуры.
	 */
	public DataPanelElementProc getProcByType(final DataPanelElementProcType procType) {
		for (DataPanelElementProc cur : procs.values()) {
			if (cur.getType() == procType) {
				return cur;
			}
		}
		return null;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(final String aTemplateName) {
		templateName = aTemplateName;
	}

	public Map<ID, DataPanelElementProc> getProcs() {
		return procs;
	}

	public void setProcs(final Map<ID, DataPanelElementProc> aProcs) {
		procs = aProcs;
	}

	public DataPanelTab getTab() {
		return tab;
	}

	public void setTab(final DataPanelTab aTab) {
		tab = aTab;
	}

	public Boolean getNeverShowInPanel() {
		return neverShowInPanel;
	}

	public void setNeverShowInPanel(final Boolean aNeverShowInPanel) {
		neverShowInPanel = aNeverShowInPanel;
	}

	/**
	 * Возвращает значение keepUserSettings, заданное в действии для данного
	 * элемента.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - keepUserSettings.
	 */
	public Boolean getKeepUserSettings(final Action ac) {
		if (ac.getDataPanelLink() != null) {
			for (DataPanelElementLink link : ac.getDataPanelLink().getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getKeepUserSettings();
				}
			}

			return ac.getKeepUserSettings();
		}
		throw new AppLogicError(KEEP_USER_SETTINGS_ERROR);
	}

	/**
	 * Возвращает значение partialUpdate, заданное в действии для данного
	 * элемента.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - partialUpdate.
	 */
	public Boolean getPartialUpdate(final Action ac) {
		if (ac.getDataPanelLink() != null) {
			for (DataPanelElementLink link : ac.getDataPanelLink().getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getPartialUpdate();
				}
			}

			// return ac.getPartialUpdate();
			return false;
		}
		return false;
	}

	/**
	 * Возвращает значение currentLevelUpdate, заданное в действии для данного
	 * элемента.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - currentLevelUpdate.
	 */
	public Boolean getCurrentLevelUpdate(final Action ac) {
		if (ac.getDataPanelLink() != null) {
			for (DataPanelElementLink link : ac.getDataPanelLink().getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getCurrentLevelUpdate();
				}
			}

			// return ac.getPartialUpdate();
			return false;
		}
		return false;
	}

	/**
	 * Возвращает значение childLevelUpdate, заданное в действии для данного
	 * элемента.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - childLevelUpdate.
	 */
	public Boolean getChildLevelUpdate(final Action ac) {
		if (ac.getDataPanelLink() != null) {
			for (DataPanelElementLink link : ac.getDataPanelLink().getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getChildLevelUpdate();
				}
			}

			// return ac.getPartialUpdate();
			return false;
		}
		return false;
	}

	/**
	 * Возвращает текущий контекст для элемента из переданного действия.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - контекст.
	 */
	public CompositeContext getContext(final Action ac) {
		DataPanelLink dpLink = ac.getDataPanelLink();
		if (dpLink != null) {
			for (DataPanelElementLink link : dpLink.getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getContext();
				}
			}
			return ac.getContext();
		}
		return null;
	}

	/**
	 * Возвращает полный уникальный идентификатор элемента, включающий в себя
	 * идентификатор панели.
	 * 
	 * @return - идентификатор.
	 */
	public String getFullId() {
		String dataPanelId;
		if (tab != null && tab.getDataPanel() != null && tab.getDataPanel().getId() != null) {
			dataPanelId = tab.getDataPanel().getId().toString();
		} else {
			dataPanelId = "none";
		}
		return "dpe_" + dataPanelId + "__" + id;
	}

	/**
	 * Возвращает ключ, который может быть использован для кэширования элементов
	 * на клиентской стороне.
	 * 
	 * @param context
	 *            - контекст для элемента.
	 * @return - ключ.
	 */
	public String getKeyForCaching(final CompositeContext context) {
		return getFullId() + "_" + context.getMain();
	}

	public Integer getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(final Integer aRefreshInterval) {
		refreshInterval = aRefreshInterval;
	}

	public Boolean getRefreshByTimer() {
		return refreshByTimer;
	}

	public void setRefreshByTimer(final Boolean aRefreshByTimer) {
		refreshByTimer = aRefreshByTimer;
	}

	public Boolean getCacheData() {
		return cacheData;
	}

	public void setCacheData(final Boolean aCacheData) {
		cacheData = aCacheData;
	}

	public List<ID> getRelated() {
		return related;
	}

	public void setRelated(final List<ID> aRelated) {
		related = aRelated;
	}

	public HTMLAttrs getHtmlAttrs() {
		return htmlAttrs;
	}

	public void setHtmlAttrs(final HTMLAttrs aHtmlAttrs) {
		htmlAttrs = aHtmlAttrs;
	}

	public String getUploaderId(final String procId) {
		return getFullId() + "__proc_" + procId + "__uploader";
	}

	public Boolean getShowLoadingMessage() {
		return showLoadingMessage;
	}

	public void setShowLoadingMessage(final Boolean aShowLoadingMessage) {
		showLoadingMessage = aShowLoadingMessage;
	}

	public Boolean getShowLoadingMessageForFirstTime() {
		return showLoadingMessageForFirstTime;
	}

	public void setShowLoadingMessageForFirstTime(final Boolean aShowLoadingMessageForFirstTime) {
		showLoadingMessageForFirstTime = aShowLoadingMessageForFirstTime;
	}

	public DataPanelElementProc getProcById(final String procId) {
		return procs.get(new ID(procId));
	}

	public List<String> getGroup() {
		return group;
	}

	public void setGroup(final String aGroup) {
		group = new ArrayList<String>();
		String str = aGroup.trim();
		String[] result = str.split("\\s+");
		for (int i = 0; i < result.length; i++) {
			group.add(result[i]);
		}
	}

	public boolean getBuildTemplate() {
		return buildTemplate;
	}

	public void setBuildTemplate(final Boolean aBuildTemplate) {
		buildTemplate = aBuildTemplate;
	}

	public void addDataAndMetaDataProcs(final String prefix) {
		procName = prefix + "_data";
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(id.toString() + "_mdproc");
		proc.setName(prefix + "_metadata");
		proc.setType(DataPanelElementProcType.METADATA);
		procs.put(proc.getId(), proc);
	}
}
