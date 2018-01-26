/**
 * 
 */
package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.client.*;

import com.google.gwt.user.client.Timer;

/**
 * @author anlug
 * 
 */
public abstract class BasicElementPanelBasis implements BasicElementPanel {

	/**
	 * Таймер, для обновление данных панели элемента через заданные интервалы
	 * времени.
	 */
	private Timer timer;

	/**
	 * @return the timer
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * @param atimer
	 *            the timer to set
	 */
	public void setTimer(final Timer atimer) {
		this.timer = atimer;
	}

	/**
	 * CompositeContext элемента, с которым он был отрисован последний раз..
	 */
	private CompositeContext context;

	public void setContext(final CompositeContext acontext) {
		this.context = acontext;
	}

	/**
	 * Расширенная функция получения контекста. Создает related элементы
	 * контекста. При этом учитывается ситуация, когда related панель еще не
	 * отрисована. Кроме того, не передаются данные о себе - они добавляются на
	 * сервере чтобы избежать дублирования данных при передаче.
	 * 
	 * @see ru.curs.showcase.app.client.api.BasicElementPanel#getContext()
	 **/
	@Override
	public CompositeContext getContext() {
		for (ID id : elementInfo.getRelated()) {
			final BasicElementPanel elementPanel = ActionExecuter.getElementPanelById(id);
			if (elementPanel == this) {
				continue;
			}
			if ((elementPanel != null) && (elementPanel.getContext() != null)) {
				context.addRelated(id, elementPanel.getDetailedContext());
			} else {
				context.addRelated(id, new CompositeContext());
			}
		}

		context.setCurrentDatapanelWidth(GeneralDataPanel.getTabPanel().getOffsetWidth());
		context.setCurrentDatapanelHeight(GeneralDataPanel.getTabPanel().getOffsetHeight());

		return context;
	}

	private DataPanelElementInfo elementInfo;
	/**
	 * Опция определяет, нужно ли очищать локальный контекст после загрузки
	 * новых данных грида. Значение зависит от опции keepUserSettings в
	 * действии, обновляющем грид.
	 */
	private boolean needResetLocalContext = true;

	public boolean isNeedResetLocalContext() {
		return needResetLocalContext;
	}

	@Override
	public void setNeedResetLocalContext(final boolean aNeedResetLocalContext) {
		needResetLocalContext = aNeedResetLocalContext;
	}

	private boolean partialUpdate = false;
	private boolean currentLevelUpdate = false;
	private boolean childLevelUpdate = false;

	public boolean isPartialUpdate() {
		return partialUpdate;
	}

	@Override
	public void setPartialUpdate(final boolean aPartialUpdate) {
		partialUpdate = aPartialUpdate;
	}

	public boolean isCurrentLevelUpdate() {
		return currentLevelUpdate;
	}

	@Override
	public void setCurrentLevelUpdate(final boolean aCurrentLevelUpdate) {
		currentLevelUpdate = aCurrentLevelUpdate;
	}

	public boolean isChildLevelUpdate() {
		return childLevelUpdate;
	}

	@Override
	public void setChildLevelUpdate(final boolean aCchildLevelUpdate) {
		childLevelUpdate = aCchildLevelUpdate;
	}

	protected void resetLocalContext() {
		if (needResetLocalContext) {
			internalResetLocalContext();
			needResetLocalContext = false;
		}
	}

	protected void internalResetLocalContext() {
		// по умолчанию ничего не делаем
	}

	@Override
	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aelement) {
		this.elementInfo = aelement;
	}

	@Override
	public CompositeContext getDetailedContext() {
		return getContext();
	}

	/**
	 * Функция, устанавливающая (включающая) в случае необходимости таймер
	 * обновления элемента информационной панели.
	 */
	protected void setupTimer() {

		if (getElementInfo().getRefreshByTimer()) {
			Timer tm = getTimer();
			if (tm != null) {
				tm.cancel();
			}
			tm = new Timer() {

				@Override
				public void run() {
					refreshPanel();
				}

			};
			setTimer(tm);
			final int n1000 = 1000;
			tm.schedule(getElementInfo().getRefreshInterval() * n1000);
		}

	}

	/**
	 * Функция, отключающая таймер обновления элемента информационной панели.
	 */
	protected void cancelTimer() {
		if (getElementInfo().getRefreshByTimer()) {
			Timer tm = getTimer();
			if (tm != null) {
				tm.cancel();
				setTimer(null);
			}
		}
	}

	/**
	 * Функция, отключающая все таймеры обновления всех элементов информационной
	 * панели, в случае если они (таймеры) есть.
	 */
	public static void switchOffAllTimers() {

		for (UIDataPanelTab panelTab : AppCurrContext.getInstance().getUiDataPanel()) {

			for (UIDataPanelElement dataPanelElement : panelTab.getUiElements()) {
				((BasicElementPanelBasis) dataPanelElement.getElementPanel()).cancelTimer();
			}
		}

	}

}
