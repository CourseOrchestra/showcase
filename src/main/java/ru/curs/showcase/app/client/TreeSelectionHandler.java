/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.DataPanel;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.navigator.NavigatorElement;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.BasicElementPanelBasis;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Обработчик выделения элемента дерева в аккордеоне.
 * 
 */
public class TreeSelectionHandler implements SelectionHandler<TreeItem> {

	/**
	 * Переменная, защищающая от двойного клика на элементе дерева.
	 */
	private Boolean canBeSelectedAfterPreviousSelection = true;

	@Override
	public void onSelection(final SelectionEvent<TreeItem> arg0) {

		RootPanel.getBodyElement().removeClassName("tabselected");

		if (!canBeSelectedAfterPreviousSelection) {
			Accordeon.selectLastSelectedItem(arg0.getSelectedItem());
			((ScrollPanel) arg0.getSelectedItem().getTree().getParent())
					.setHorizontalScrollPosition(0);
			return;
		} else {
			canBeSelectedAfterPreviousSelection = false;
		}

		final Timer timer = new Timer() {
			@Override
			public void run() {
				canBeSelectedAfterPreviousSelection = true;
			}
		};
		final int n1000 = 1000;
		timer.schedule(n1000);

		if (((NavigatorElement) arg0.getSelectedItem().getUserObject()).getAction() != null) {

			Action ac = ((NavigatorElement) arg0.getSelectedItem().getUserObject()).getAction();

			if (Accordeon.getTempMainContext() != null) {
				Action cloneActionWithTempMainContext = ac.gwtClone();
				cloneActionWithTempMainContext.setMainContext(Accordeon.getTempMainContext());
				Accordeon.setTempMainContext(null);
				ac = cloneActionWithTempMainContext;
			}

			AppCurrContext.getInstance().setNavigatorAction(ac);
			generateDatePanel(AppCurrContext.getInstance().getNavigatorAction(),
					arg0.getSelectedItem());

			String str =
				((NavigatorElement) arg0.getSelectedItem().getUserObject()).getId().getString();
			AppCurrContext.getInstance().setNavigatorItemId(str);
			int ind = AppCurrContext.getInstance().getDatapanelTabIndex();
			if (!((str).equals(getState()))) {
				pushState(str + ";" + ind);
			}

		} else {
			Accordeon.selectLastSelectedItem(arg0.getSelectedItem());
		}
		((ScrollPanel) arg0.getSelectedItem().getTree().getParent())
				.setHorizontalScrollPosition(0);

		final Timer delayTimer = new Timer() {
			@Override
			public void run() {
				RootPanel.getBodyElement().addClassName("navigator-item");
			}
		};
		delayTimer.schedule(1000);

	}

	private void generateDatePanel(final Action action, final TreeItem selectedTreeItem) {

		final DataServiceAsync dataService = GWT.create(DataService.class);
		dataService.getDataPanel(
				action,
				new GWTServiceCallback<DataPanel>(
				// AppCurrContext.getInstance().getBundleMap().get("error")) {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Error")) {
					// @Override
					// public void onFailure(final Throwable caught) {

					// MessageBox.showMessageWithDetails(Constants.ERROR,
					// caught.getMessage(),
					// GeneralServerException
					// .checkExeptionTypeAndCreateDetailedTextOfException(caught));
					// }

					@Override
					public void onSuccess(final DataPanel dp) {
						BasicElementPanelBasis.switchOffAllTimers();
						GeneralDataPanel.redrowGeneralDataPanelAtnavigatorClick(dp);
						Accordeon.unselectAllTreesItemsExcludingLastSelecter(selectedTreeItem);
					}

				});

		return;

	}

	public native void pushState(String obj) /*-{

		$wnd.history.pushState(obj, "Item");
		//$doc.cookie = "hist=1";

	}-*/;

	public native String getState() /*-{

		var s = "" + $wnd.history.state;
		var ar = s.split(";");
		var ret = ar[0];
		return ret;

	}-*/;

}
