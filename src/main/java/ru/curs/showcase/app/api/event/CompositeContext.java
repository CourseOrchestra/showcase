package ru.curs.showcase.app.api.event;

import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Класс составного контекста. Контекст определяет условия фильтрации данных,
 * которые будут получены из БД для отображения элементов информационной панели.
 * Контекст может быть задан как для панели в целом, так и для отдельных ее
 * элементов. Массивы related и sessionParamsMap являются временными и не
 * участвуют в проверке на идентичность.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = Action.CONTEXT_TAG)
@XmlAccessorType(XmlAccessType.FIELD)
public class CompositeContext extends TransferableElement
		implements CanBeCurrent, SerializableElement, Assignable<CompositeContext>, GWTClonable,
		AbstractCompositeContext, SizeEstimate {

	@Override
	public String toString() {
		return "CompositeContext [main=" + main + ", additional=" + additional + ", session="
				+ session + ", filter=" + filter + "]";
	}

	private static final long serialVersionUID = 6956997088646193138L;

	/**
	 * Признак того, что идентификатор указывает на hide context - т.е. контекст
	 * для сокрытия элемента с информационной панели. Может быть задан только у
	 * параметра additional context.
	 */
	private static final String HIDE_ID = "hide";

	/**
	 * Основной контекст.
	 */
	private String main;

	/**
	 * Дополнительный контекст. Задается для отдельных элементов информационной
	 * панели.
	 */
	private String additional;

	/**
	 * Контекст пользовательской сессии. Имеет формат XML. В случае, если явно
	 * задан "пользовательский контекст", он включается в контекст сессии.
	 * Содержит серверный данные и поэтому в целях экономии трафика не
	 * передается на клиента.
	 */
	private String session;

	/**
	 * Фильтрующий контекст. Задается с помощью компонента XForms.
	 */
	private String filter;

	/**
	 * Параметры URL, полученные из клиентской части. На основе их создается
	 * session context для БД.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private Map<String, ArrayList<String>> sessionParamsMap =
		new TreeMap<String, ArrayList<String>>();

	/**
	 * Контексты связанных элементов. Перед передачей в БД их содержимое
	 * включается в session.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private Map<ID, CompositeContext> related = new HashMap<ID, CompositeContext>();

	/**
	 * "ok"-сообщение.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private UserMessage okMessage = null;

	private Boolean partialUpdate = false;

	/**
	 * Предлагаемая ширина элемента, соответствующая ширине доступного
	 * пространства.
	 */
	private Integer currentDatapanelWidth = 0;

	/**
	 * Предлагаемая высота элемента, соответствующая высоте доступного
	 * пространства.
	 */
	private Integer currentDatapanelHeight = 0;

	public CompositeContext(final Map<String, List<String>> aParams) {
		super();
		addSessionParams(aParams);
	}

	public CompositeContext() {
		super();
	}

	@Override
	public String getSession() {
		return session;
	}

	@Override
	public void setSession(final String aSession) {
		session = aSession;
	}

	@Override
	public final String getMain() {
		return main;
	}

	@Override
	public final void setMain(final String aMain) {
		this.main = aMain;
	}

	@Override
	public final String getAdditional() {
		return additional;
	}

	@Override
	public final void setAdditional(final String aAdditional) {
		this.additional = aAdditional;
	}

	@Override
	public String getFilter() {
		return filter;
	}

	@Override
	public void setFilter(final String aFilter) {
		filter = aFilter;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

	public Boolean getPartialUpdate() {
		return partialUpdate;
	}

	public void setPartialUpdate(final Boolean aPartialUpdate) {
		partialUpdate = aPartialUpdate;
	}

	public Integer getCurrentDatapanelWidth() {
		return currentDatapanelWidth;
	}

	public void setCurrentDatapanelWidth(final Integer acurrentDatapanelWidth) {
		currentDatapanelWidth = acurrentDatapanelWidth;
	}

	public Integer getCurrentDatapanelHeight() {
		return currentDatapanelHeight;
	}

	public void setCurrentDatapanelHeight(final Integer acurrentDatapanelHeight) {
		currentDatapanelHeight = acurrentDatapanelHeight;
	}

	/**
	 * Проверяет, задан ли основной контекст как текущий.
	 * 
	 * @return результат проверки.
	 */
	public boolean mainIsCurrent() {
		return CURRENT_ID.equals(main);
	}

	/**
	 * Проверяет, задан ли дополнительный контекст как текущий.
	 * 
	 * @return результат проверки.
	 */
	public boolean addIsCurrent() {
		return CURRENT_ID.equals(additional);
	}

	@Override
	public void assignNullValues(final CompositeContext sourceContext) {
		if (sourceContext == null) {
			return;
		}
		if (main == null) {
			main = sourceContext.main;
		}
		if (additional == null) {
			additional = sourceContext.additional;
		}
		if (filter == null) {
			filter = sourceContext.filter;
		}
		if (session == null) {
			session = sourceContext.session;
		}
		if (sessionParamsMap.isEmpty()) {
			sessionParamsMap.putAll(sourceContext.sessionParamsMap);
		}
		if (related.isEmpty()) {
			related.putAll(sourceContext.related);
		}
		if ((currentDatapanelWidth == null) || (currentDatapanelWidth == 0)) {
			currentDatapanelWidth = sourceContext.currentDatapanelWidth;
		}
		if ((currentDatapanelHeight == null) || (currentDatapanelHeight == 0)) {
			currentDatapanelHeight = sourceContext.currentDatapanelHeight;
		}
	}

	/**
	 * Актуализирует контекст на основе переданного.
	 * 
	 * @param aCallContext
	 *            - актуальный контекст.
	 * @return - измененный контекст.
	 */
	public CompositeContext actualizeBy(final CompositeContext aCallContext) {
		if (mainIsCurrent()) {
			main = aCallContext.main;
		}
		if (addIsCurrent()) {
			additional = aCallContext.additional;
		}
		return this;
	}

	/**
	 * Определяет, являются ли контекст скрывающим (элемент).
	 * 
	 * @return результат проверки.
	 */
	public boolean doHiding() {
		return HIDE_ID.equalsIgnoreCase(additional);
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 */
	@Override
	public CompositeContext gwtClone() {
		CompositeContext res = newInstance();
		res.setMain(getMain());
		res.setAdditional(getAdditional());
		res.setSession(getSession());
		res.setFilter(getFilter());
		for (Entry<String, ArrayList<String>> entry : sessionParamsMap.entrySet()) {
			ArrayList<String> values = new ArrayList<String>();
			for (String value : entry.getValue()) {
				values.add(value);
			}
			res.sessionParamsMap.put(entry.getKey(), values);
		}
		for (Entry<ID, CompositeContext> entry : related.entrySet()) {
			res.related.put(entry.getKey(), entry.getValue().gwtClone());
		}
		res.currentDatapanelWidth = currentDatapanelWidth;
		res.currentDatapanelHeight = currentDatapanelHeight;
		return res;
	}

	protected CompositeContext newInstance() {
		return new CompositeContext();
	}

	/**
	 * Возвращает строку фильтра на основе переданного дополнительного
	 * контекста.
	 * 
	 * @param aAdditional
	 *            - значение дополнительного контекста.
	 * @return - строка фильтра.
	 */
	public static String generateFilterContextLine(final String aAdditional) {
		return "<" + Action.CONTEXT_TAG + ">" + aAdditional + "</" + Action.CONTEXT_TAG + ">";
	}

	/**
	 * Генерирует общую часть фильтра использую переменную часть, зависящую от
	 * выделенных строк.
	 * 
	 * @param aMutableFilterPart
	 *            - переменная часть фильтра.
	 * @return - строка с фильтром, готовая к использованию в хранимой
	 *         процедуре.
	 */
	public static String generateFilterContextGeneralPart(final String aMutableFilterPart) {
		return "<" + Action.FILTER_TAG + ">" + aMutableFilterPart + "</" + Action.FILTER_TAG + ">";
	}

	/**
	 * Функция, создающая "текущий" контекст.
	 * 
	 * @return - контекст.
	 */
	public static CompositeContext createCurrent() {
		CompositeContext res = new CompositeContext();
		res.main = CURRENT_ID;
		res.additional = CURRENT_ID;
		return res;
	}

	/**
	 * Функция добавления строки фильтра в контекст.
	 * 
	 * @param source
	 *            - контекст источник для фильтра.
	 */
	public void addFilterLine(final CompositeContext source) {
		filter = filter + generateFilterContextLine(source.additional);
	}

	/**
	 * Заканчивает построение фильтра контекста добавляя общую часть.
	 */
	public void finishFilter() {
		filter = generateFilterContextGeneralPart(filter);
	}

	public Map<String, ArrayList<String>> getSessionParamsMap() {
		return sessionParamsMap;
	}

	/**
	 * Функция для установки параметров URL в формате Map<String, List<String>>.
	 * 
	 * @param aData
	 *            - параметры.
	 */
	public final void addSessionParams(final Map<String, List<String>> aData) {
		sessionParamsMap.clear();
		if (aData == null) {
			return;
		}
		for (Map.Entry<String, List<String>> entry : aData.entrySet()) {
			ArrayList<String> values = new ArrayList<String>();
			values.addAll(entry.getValue());
			sessionParamsMap.put(entry.getKey(), values);
		}
	}

	public void setSessionParamsMap(final Map<String, ArrayList<String>> aSessionParamsMap) {
		sessionParamsMap = aSessionParamsMap;
	}

	public void addRelated(final ID aId, final CompositeContext aContext,
			final boolean clearRelated) {
		CompositeContext context = aContext.gwtClone();
		// в БД не должно идти ничего лишнего
		context.setMain(null);
		context.setSession(null);
		if (clearRelated) {
			context.getRelated().clear();
		}
		context.getSessionParamsMap().clear();
		related.put(aId, context);
	}

	public void addRelated(final ID aId, final CompositeContext aContext) {
		addRelated(aId, aContext, true);
	}

	public void addRelated(final String id, final CompositeContext context) {
		addRelated(new ID(id), context);
	}

	public Map<ID, CompositeContext> getRelated() {
		return related;
	}

	public void setRelated(final Map<ID, CompositeContext> aRelated) {
		related = aRelated;
	}

	@Override
	public long sizeEstimate() {
		long result = 0;
		if (main != null) {
			result += main.getBytes().length;
		}
		if (additional != null) {
			result += additional.getBytes().length;
		}
		if (session != null) {
			result += session.getBytes().length;
		}
		if (filter != null) {
			result += filter.getBytes().length;
		}
		return result;
	}
}
