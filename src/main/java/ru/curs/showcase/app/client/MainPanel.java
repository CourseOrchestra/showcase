/**
 * 
 */
package ru.curs.showcase.app.client;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.panels.*;
import ru.curs.showcase.app.client.utils.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса средней (главной,
 *         рабочей панели приложения - MainPanel) части приложения Showcase.
 * 
 */
public class MainPanel {

	/**
	 * Базовая вертикальная панель, содержащая все виджеты MainPanel.
	 */
	private final VerticalPanel basicVerticalPanel = new VerticalPanel();

	/**
	 * CursSplitLayoutPanel.
	 */
	private final CursSplitLayoutPanel p = new CursSplitLayoutPanel();

	/**
	 * Виджет, который содержит GeneralDataPanel.
	 */
	private Widget gp;

	/**
	 * Виджет который содержит навигатор.
	 */
	private Widget accordeonWidget;

	/**
	 * Переменная которая содержит класс навигатора Accordeon.
	 */
	private Accordeon accordeon;

	/**
	 * @return the accordeon
	 */
	public Accordeon getAccordeon() {
		return accordeon;
	}

	/**
	 * @param aaccordeon
	 *            the accordeon to set
	 */
	public void setAccordeon(final Accordeon aaccordeon) {
		this.accordeon = aaccordeon;
	}

	/**
	 * Переменная, которая определяет на какую ширину от ширины экрана(от ширины
	 * рабочей части окна браузера) нужно уменьшить MainPanel.
	 */
	private static final int N16 = 16;

	/**
	 * Процедура создания MainPanel, которая включает в себя Accordeon и
	 * GeneralDataPanel.
	 * 
	 * @return возвращает заполненный виджет MainPanel типа VerticalPanel.
	 */
	public Widget startMainPanelCreation() {
		// ProgressWindow.showProgressWindow();
		basicVerticalPanel.add(DownloadHelper.getInstance());

		final int n10 = 20;

		basicVerticalPanel.setStyleName("basicVerticalPanel-CellspacingForMainPanel");
		p.setPixelSize(Window.getClientWidth() - N16,
				Window.getClientHeight() - n10
						- DOM.getElementById("showcaseHeaderContainer").getOffsetHeight()
						- DOM.getElementById("showcaseBottomContainer").getOffsetHeight());

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				int height = event.getHeight() - n10
						- DOM.getElementById("showcaseHeaderContainer").getOffsetHeight()
						- DOM.getElementById("showcaseBottomContainer").getOffsetHeight();
				int width = event.getWidth() - N16;
				p.setHeight(height + "px");
				p.setWidth(width + "px");
			}
		});

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.addStyleName("DockLayoutPanel-MainPanel");
		decoratorPanel.setWidget(p);
		basicVerticalPanel.add(decoratorPanel);

		basicVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		basicVerticalPanel.setSpacing(Constants.SPACINGN);

		accordeon = new Accordeon();
		accordeonWidget = accordeon.generateAccordeon();

		return basicVerticalPanel;
	}

	/**
	 * 
	 * Процедура которая продолжает создание главной панели MainPanel (добавляет
	 * на нее Accordeon и GeneralDataPanel) на основе настроек пришедших из
	 * хранимой процедуры через асинхронный gwt-servlet запрос.
	 * 
	 * @param showNavigator
	 *            - показывать ли навигатор в приложении или скрывать его.
	 * @param navigatorWidth
	 *            - переменная которая содержит значение шириныв навигатора в
	 *            пикселях или процентах (напр. "500px" или "30%").
	 */
	public void generateMainPanel(final boolean showNavigator, final String navigatorWidth,
			final String welcomeTabCaption) {

		if (showNavigator) {

			int widthNumber = 0;
			try {
				widthNumber = SizeParser.getSize(navigatorWidth);
			} catch (NumberFormatException e) {

				MessageBox.showMessageWithDetails(
						// AppCurrContext.getInstance().getBundleMap().get("transformation_navigator_width_error"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Converting navigator width error"),
						e.getClass().getName() + ": " + e.getMessage(),
						GeneralException.generateDetailedInfo(e), MessageType.ERROR,
						GeneralException.needDetailedInfo(e), null);
			}

			switch (SizeParser.getSizeType(navigatorWidth)) {

			case PIXELS:

				p.addWest(accordeonWidget, widthNumber);
				break;

			case PERCENTS:
				final int percentsTotal = 100;
				final int absoluteWidth =
					widthNumber * (Window.getClientWidth() - N16) / percentsTotal;
				p.addWest(accordeonWidget, absoluteWidth);
				break;

			default:

				p.addWest(accordeonWidget, Constants.SPLITLAYOUTPANELSIZEN);
				break;

			}

		}

		gp = (new GeneralDataPanel()).generateDataPanel(welcomeTabCaption);

		p.setSplitterDragHandler(new SplitterDragHandler() {

			@Override
			public void splitterDragEvent() {
				// GeneralDataPanel.getTabPanel().saveTabBarCurrentScrollingPosition();
				GeneralDataPanel.getTabPanel().checkIfScrollButtonsNecessary();

			}

		});

		p.add(gp);

		p.setWidgetMinSize(gp, 1);

	}
}
