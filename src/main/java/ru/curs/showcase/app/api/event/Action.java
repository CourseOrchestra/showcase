package ru.curs.showcase.app.api.event;

import java.util.*;

import javax.xml.bind.annotation.*;

import com.google.gwt.user.client.rpc.GwtTransient;

import ru.curs.showcase.app.api.*;

/**
 * Класс действия, выполняемого при активации визуального элемента UI (например,
 * при щелчке по элементу навигатора, или при щелчке по строке грида).
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Action implements SerializableElement, GWTClonable, ContainingContext,
		SelfCheckObject, SizeEstimate {

	private static final long serialVersionUID = -5014034913652092038L;

	/**
	 * Тэг верхнего уровня для фильтрующего контекста, созданного на основе
	 * выделенных записей грида.
	 */
	public static final String FILTER_TAG = "filter";
	/**
	 * Тэг для записи в фильтрующем контексте, содержащей информацию о контексте
	 * выделенной строки грида.
	 */
	public static final String CONTEXT_TAG = "context";

	/**
	 * Тип действия, которое нужно осуществить с панелью.
	 */
	private DataPanelActionType dataPanelActionType = DataPanelActionType.DO_NOTHING;

	/**
	 * Режим отображения элементов панели.
	 */
	private ShowInMode showInMode = ShowInMode.PANEL;

	/**
	 * Ссылка на информационную панель, которая должна быть открыта при
	 * выполнении действия.
	 */
	private DataPanelLink dataPanelLink;

	/**
	 * Ссылка на элемент навигатора, которая должна быть открыта при выполнении
	 * действия.
	 */
	private NavigatorElementLink navigatorElementLink;

	/**
	 * Признак того, что нужно сохранять пользовательские настройки всех
	 * элементов панели, затрагиваемых данным действием. Если элемент
	 * отображается в первый раз - то признак игнорируется. Пользовательские
	 * настройки имеют не все типы элементов.
	 */
	private Boolean keepUserSettings;

	/**
	 * Признак того, что нужно выполнять частичное обновление элемента.
	 */
	private Boolean partialUpdate = null;

	/**
	 * Признак того, что нужно выполнять обновление текущего уровня элемента.
	 */
	private Boolean currentLevelUpdate = null;

	/**
	 * Признак того, что нужно выполнять обновление нижнего уровня элемента.
	 */
	private Boolean childLevelUpdate = null;

	/**
	 * Признак того, что если во время рефреша элемента он скрыт, то показывать
	 * его не нужно.
	 */
	private Boolean preserveHidden = null;

	/**
	 * Информация об отображении модального окна, связанного с действием.
	 */
	private ModalWindowInfo modalWindowInfo;

	/**
	 * Контекст, общий для всего действия. Играет роль контекста по умолчанию
	 * для элементов, не имеющих переопределенного контекста.
	 */
	private CompositeContext context;

	/**
	 * Список действий на сервере, содержащихся в данном действии.
	 */
	private List<Activity> serverActivities = new ArrayList<Activity>();

	/**
	 * Список действий на сервере, содержащихся в данном действии.
	 */
	private List<Activity> clientActivities = new ArrayList<Activity>();

	/**
	 * Виджет, который вызывает данное действие. Нужен для того, чтобы можно
	 * было с этим виджетом что-нибудь сделать, например, сделать
	 * disabled/enabled.
	 */
	@GwtTransient
	@XmlTransient
	@ExcludeFromSerialization
	private Object actionCaller = null;

	public Object getActionCaller() {
		return actionCaller;
	}

	public void setActionCaller(final Object aActionCaller) {
		actionCaller = aActionCaller;
	}

	public final DataPanelLink getDataPanelLink() {
		return dataPanelLink;
	}

	public final void setDataPanelLink(final DataPanelLink aDataPanelLinkForOpen) {
		this.dataPanelLink = aDataPanelLinkForOpen;
	}

	public final NavigatorElementLink getNavigatorElementLink() {
		return navigatorElementLink;
	}

	public final void
			setNavigatorElementLink(final NavigatorElementLink aNavigatorElementLinkForOpen) {
		this.navigatorElementLink = aNavigatorElementLinkForOpen;
	}

	public final DataPanelActionType getDataPanelActionType() {
		return dataPanelActionType;
	}

	public final void setDataPanelActionType(final DataPanelActionType aDataPanelActionType) {
		this.dataPanelActionType = aDataPanelActionType;
	}

	/**
	 * Возвращает тип действия для навигатора в зависимости от состояния
	 * действия.
	 * 
	 */
	public final NavigatorActionType getNavigatorActionType() {
		if ((navigatorElementLink == null) || (navigatorElementLink.getId() == null)) {
			return NavigatorActionType.DO_NOTHING;
		}
		if (dataPanelLink == null) {
			return NavigatorActionType.CHANGE_NODE_AND_DO_ACTION;
		} else {
			return NavigatorActionType.CHANGE_NODE;
		}
	}

	/**
	 * Функция "самоопределения" состояния действия по его свойствам. Для
	 * каждого действия должны быть вызвана один и только один раз!
	 * 
	 */
	public void determineState() {
		determineDPActionType();
		determineKeepUserSettingsState();
		determinePartialUpdateState();
		determineCurrentLevelUpdateState();
		determineChildLevelUpdateState();
		determinePreserveHiddenState();
	}

	private void determineKeepUserSettingsState() {
		boolean actionParamDefined = true;
		if (keepUserSettings == null) {
			keepUserSettings = true;
			actionParamDefined = false;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
				if (elink.getKeepUserSettings() == null) {
					if (actionParamDefined) {
						elink.setKeepUserSettings(getKeepUserSettings());
					} else {
						elink.setKeepUserSettings(false);
					}
				}

			}
		}
	}

	private void determinePartialUpdateState() {
		boolean actionParamDefined = true;
		if (partialUpdate == null) {
			partialUpdate = false;
			actionParamDefined = false;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
				if (elink.getPartialUpdate() == null) {
					if (actionParamDefined) {
						elink.setPartialUpdate(partialUpdate);
					} else {
						elink.setPartialUpdate(false);
					}
				}

			}
		}
	}

	private void determineCurrentLevelUpdateState() {
		boolean actionParamDefined = true;
		if (currentLevelUpdate == null) {
			currentLevelUpdate = false;
			actionParamDefined = false;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
				if (elink.getCurrentLevelUpdate() == null) {
					if (actionParamDefined) {
						elink.setCurrentLevelUpdate(currentLevelUpdate);
					} else {
						elink.setCurrentLevelUpdate(false);
					}
				}

			}
		}
	}

	private void determineChildLevelUpdateState() {
		boolean actionParamDefined = true;
		if (childLevelUpdate == null) {
			childLevelUpdate = false;
			actionParamDefined = false;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
				if (elink.getChildLevelUpdate() == null) {
					if (actionParamDefined) {
						elink.setChildLevelUpdate(childLevelUpdate);
					} else {
						elink.setChildLevelUpdate(false);
					}
				}

			}
		}
	}

	private void determinePreserveHiddenState() {
		boolean actionParamDefined = true;
		if (preserveHidden == null) {
			preserveHidden = false;
			actionParamDefined = false;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
				if (elink.getPreserveHidden() == null) {
					if (actionParamDefined) {
						elink.setPreserveHidden(preserveHidden);
					} else {
						elink.setPreserveHidden(false);
					}
				}
			}
		}
	}

	/**
	 * TODO: Алгоритм хотя и довольно простой, но не отражен в модели. Подумать
	 * о доработке модели. Повторное определение типа заблокировано!
	 * 
	 * @param aCurrentElementInfo
	 *            - информация о текущем элементе на панели. Используется в
	 *            алгоритме определения типа.
	 */
	private void determineDPActionType() {
		if (dataPanelLink == null) {
			dataPanelActionType = DataPanelActionType.DO_NOTHING;
			return;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			return;
		}
		if (dataPanelLink.isCurrentPanel()) {
			if (dataPanelLink.isCurrentTab()) {
				if (context.mainIsCurrent()) {
					dataPanelActionType = DataPanelActionType.RELOAD_ELEMENTS;
				} else {
					dataPanelActionType = DataPanelActionType.REFRESH_TAB;
				}
			} else {
				dataPanelActionType = DataPanelActionType.REFRESH_TAB;
			}
		} else {
			dataPanelActionType = DataPanelActionType.RELOAD_PANEL;
		}
	}

	private Iterable<ContainingContext> getContainingContextChilds() {
		final Iterator<? extends ContainingContext> caIterator = getClientActivities().iterator();
		final Iterator<? extends ContainingContext> saIterator = getServerActivities().iterator();
		final Iterator<? extends ContainingContext> dpeIterator =
			dataPanelActionType != DataPanelActionType.DO_NOTHING
					? dataPanelLink.getElementLinks().iterator() : null;

		return new Iterable<ContainingContext>() {

			@Override
			public Iterator<ContainingContext> iterator() {
				return new Iterator<ContainingContext>() {

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override
					public ContainingContext next() {
						if ((dpeIterator != null) && (dpeIterator.hasNext())) {
							return dpeIterator.next();
						}
						if (saIterator.hasNext()) {
							return saIterator.next();
						}
						if (caIterator.hasNext()) {
							return caIterator.next();
						}
						return null;
					}

					@Override
					public boolean hasNext() {
						if (dpeIterator != null) {
							return dpeIterator.hasNext() || saIterator.hasNext()
									|| caIterator.hasNext();
						} else {
							return saIterator.hasNext() || caIterator.hasNext();
						}
					}
				};
			}
		};
	}

	/**
	 * Актуализирует состояние действия по переданному контексту.
	 * 
	 * @param callContext
	 *            - контекст.
	 * @return - себя.
	 */
	public Action actualizeBy(final CompositeContext callContext) {
		determineState();

		if (needGeneralContext()) {
			context.actualizeBy(callContext);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().actualizeBy(callContext);
		}

		return this;
	}

	/**
	 * Обновление действия. При этом значения типа current не заменяют реальное
	 * значение.
	 * 
	 * @param prevAction
	 *            - новый Action.
	 * @return - себя.
	 */
	public Action actualizeBy(final Action prevAction) {
		if (prevAction == null) {
			return this;
		}

		if (needGeneralContext()) {
			context.actualizeBy(prevAction.context);
		}

		if (getDataPanelActionType() != DataPanelActionType.DO_NOTHING) {
			if (dataPanelLink.isCurrentPanel()) {
				dataPanelLink.setDataPanelId(prevAction.dataPanelLink.getDataPanelId());
			}
			if (dataPanelLink.isCurrentTab()) {
				dataPanelLink.setTabId(prevAction.dataPanelLink.getTabId());
			}
			if (dataPanelLink.getFirstOrCurrentTab() && dataPanelLink.getDataPanelId()
					.equals(prevAction.dataPanelLink.getDataPanelId())) {
				dataPanelLink.setTabId(prevAction.dataPanelLink.getTabId());
			}
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().actualizeBy(context);
		}

		return this;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public Action gwtClone() {
		Action res = new Action();
		res.dataPanelActionType = dataPanelActionType;
		if (dataPanelLink != null) {
			res.dataPanelLink = dataPanelLink.gwtClone();
		}
		if (navigatorElementLink != null) {
			res.navigatorElementLink = navigatorElementLink.gwtClone();
		}
		res.keepUserSettings = keepUserSettings;
		res.showInMode = showInMode;
		if (modalWindowInfo != null) {
			res.modalWindowInfo = modalWindowInfo.gwtClone();
		}
		if (context != null) {
			res.context = context.gwtClone();
		}
		res.serverActivities.clear();
		for (Activity act : serverActivities) {
			res.serverActivities.add(act.gwtClone());
		}
		res.clientActivities.clear();
		for (Activity act : clientActivities) {
			res.clientActivities.add(act.gwtClone());
		}
		res.actionCaller = actionCaller;
		return res;
	}

	public Action() {
		super();
	}

	public Action(final DataPanelActionType aDataPanelActionType) {
		super();
		dataPanelActionType = aDataPanelActionType;

		if (dataPanelActionType == DataPanelActionType.RELOAD_ELEMENTS) {
			dataPanelLink = DataPanelLink.createCurrent();
			context = CompositeContext.createCurrent();
		} else {
			dataPanelLink = new DataPanelLink();
		}
	}

	/**
	 * Функция обновления add_context у всех дочерних контекстов в действии.
	 * 
	 * @param data
	 *            - данные фильтра (как правило MainInstance XForms).
	 */
	public void setAdditionalContext(final String data) {
		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setAdditional(data);
		}
	}

	/**
	 * Функция добавления фильтра ко всем контекстам в действии.
	 * 
	 * @param data
	 *            - данные фильтра (как правило MainInstance XForms).
	 */
	public void filterBy(final String data) {
		if (needGeneralContext()) {
			context.setFilter(data);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setFilter(data);
		}
	}

	public ShowInMode getShowInMode() {
		return showInMode;
	}

	public void setShowInMode(final ShowInMode aShowInMode) {
		showInMode = aShowInMode;
	}

	/**
	 * Проверка того, что действие включает в себя фильтр. Данный факт
	 * определяется по заполненному фильтрующему контексту у основного
	 * составного контекста действия.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isFiltered() {
		return context.getFilter() != null;
	}

	/**
	 * Устанавливает признак того, что действие содержит фильтр.
	 * 
	 * @param filter
	 *            - строка фильтра.
	 */
	public void markFiltered(final String filter) {
		if (!isFiltered()) {
			context.setFilter(filter);
		}
	}

	public Boolean getPartialUpdate() {
		return partialUpdate;
	}

	public void setPartialUpdate(final Boolean aPartialUpdate) {
		partialUpdate = aPartialUpdate;
	}

	public Boolean getCurrentLevelUpdate() {
		return currentLevelUpdate;
	}

	public void setCurrentLevelUpdate(final Boolean aCurrentLevelUpdate) {
		currentLevelUpdate = aCurrentLevelUpdate;
	}

	public Boolean getChildLevelUpdate() {
		return childLevelUpdate;
	}

	public void setChildLevelUpdate(final Boolean aChildLevelUpdate) {
		childLevelUpdate = aChildLevelUpdate;
	}

	public Boolean getPreserveHidden() {
		return preserveHidden;
	}

	public void setPreserveHidden(final Boolean aPreserveHidden) {
		preserveHidden = aPreserveHidden;
	}

	public Boolean getKeepUserSettings() {
		return keepUserSettings;
	}

	public void setKeepUserSettings(final Boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
	}

	/**
	 * Устанавливает значение aKeepUserSettings как у действия в целом, так и у
	 * его отдельных элементов.
	 * 
	 * @param aKeepUserSettings
	 *            - новое значение признака.
	 */
	public void setKeepUserSettingsForAll(final boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
				elink.setKeepUserSettings(aKeepUserSettings);
			}
		}
	}

	public ModalWindowInfo getModalWindowInfo() {
		return modalWindowInfo;
	}

	public void setModalWindowInfo(final ModalWindowInfo aModalWindowInfo) {
		modalWindowInfo = aModalWindowInfo;
	}

	/**
	 * Устанавливает контекст сессии (в виде карты) для всех составных
	 * контекстов действия.
	 * 
	 * @param data
	 *            - новое значение контекста.
	 */
	public void setSessionContext(final Map<String, List<String>> data) {
		if (needGeneralContext()) {
			context.addSessionParams(data);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().addSessionParams(data);
		}
	}

	/**
	 * Устанавливает контекст сессии (в виде строки) для всех составных
	 * контекстов действия.
	 * 
	 * @param data
	 *            - новое значение контекста.
	 */
	public void setSessionContext(final String data) {
		if (needGeneralContext()) {
			context.setSession(data);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setSession(data);
		}
	}

	/**
	 * Функция, определяющая требует ли действие выполнение каких-либо операций
	 * на сервера (например, вызов процедуры в БД).
	 * 
	 */
	public boolean containsServerActivity() {
		return !serverActivities.isEmpty();
	}

	/**
	 * Функция, определяющая требует ли действие выполнение каких-либо операций
	 * на клиенте (например, вызов процедуры JS).
	 * 
	 */
	public boolean containsClientActivity() {
		return !clientActivities.isEmpty();
	}

	public List<Activity> getServerActivities() {
		return serverActivities;
	}

	@Override
	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	public void setServerActivities(final List<Activity> aServerActivities) {
		serverActivities = aServerActivities;
	}

	/**
	 * Признак того, что действие должно содержать базовый контекст.
	 * 
	 */
	public boolean needGeneralContext() {
		return (dataPanelActionType != DataPanelActionType.DO_NOTHING) || containsServerActivity()
				|| containsClientActivity();
	}

	private boolean canHaveGeneralContext() {
		return needGeneralContext()
				|| ((getNavigatorActionType() != NavigatorActionType.DO_NOTHING));
	}

	public List<Activity> getClientActivities() {
		return clientActivities;
	}

	public void setClientActivities(final List<Activity> aClientActivities) {
		clientActivities = aClientActivities;
	}

	@Override
	public boolean isCorrect() {
		return canHaveGeneralContext() || (context == null);
	}

	public void setMainContext(final String data) {
		getContext().setMain(data);
		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setMain(data);
		}
	}

	public void setRelated(final CompositeContext parentContext) {
		for (Map.Entry<ID, CompositeContext> related : parentContext.getRelated().entrySet()) {
			context.addRelated(related.getKey(), related.getValue());
			for (ContainingContext el : getContainingContextChilds()) {
				el.getContext().addRelated(related.getKey(), related.getValue());
			}
		}

	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		if (context != null) {
			context.sizeEstimate();
		}
		if (dataPanelLink != null) {
			result += dataPanelLink.sizeEstimate();
		}
		if (modalWindowInfo != null) {
			result += modalWindowInfo.sizeEstimate();
		}
		for (Activity activity : getClientActivities()) {
			result += activity.sizeEstimate();
		}
		for (Activity activity : getServerActivities()) {
			result += activity.sizeEstimate();
		}
		return result;
	}
}
