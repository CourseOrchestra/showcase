/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.SizeParser;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса нижней части приложения
 *         Showcase.
 * 
 */
public class Footer {

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	/**
	 * Генерация заголовка (шапки) приложения Showcase.
	 * 
	 * @return возвращает виджет заголовка (шапки)
	 */
	public Widget generateBottom() {

		final SimplePanel tabVerticalPanel = new SimplePanel();
		tabVerticalPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		HTML ht = new HTML();
		// ht = new HTML();

		ht.setHTML(AppCurrContext.getInstance().getMainPage().getFooter());
		// ht.setHTML("<iframe style='border:0px; width: 100%; height: 100%'
		// src='"
		// + AccessToDomModel.getAppContextPath() + "/secured/footer"
		// + Window.Location.getQueryString() + "'/>");

		// MultiUserData.getPathWithUserData("html/footer.jsp")

		// ===
		int sizeNumber = 0;
		int absolutePixelSize = 0;
		try {
			sizeNumber =
				SizeParser.getSize(AppCurrContext.getInstance().getMainPage().getFooterHeight());
		} catch (NumberFormatException e) {

			MessageBox.showMessageWithDetails(
			// AppCurrContext.getInstance().getBundleMap().get("transformation_header_or_footer_width_error"),
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Error when converting height of the header or footer"), e.getClass()
							.getName() + ": " + e.getMessage(), GeneralException
							.generateDetailedInfo(e), MessageType.ERROR, GeneralException
							.needDetailedInfo(e), null);
		}

		switch (SizeParser.getSizeType(AppCurrContext.getInstance().getMainPage()
				.getFooterHeight())) {

		case PIXELS:
			absolutePixelSize = sizeNumber;
			break;

		case PERCENTS:
			final int percentsTotal = 100;
			absolutePixelSize = sizeNumber * Window.getClientWidth() / percentsTotal;
			break;

		default:
			absolutePixelSize = Constants.HEIGHTOFFOOTERANDBOTTOM;
			break;

		}
		// ===

		ht.setSize(SIZE_ONE_HUNDRED_PERCENTS, String.valueOf(absolutePixelSize) + "px");

		if (AppCurrContext.getInstance().getMainPage().getFooter() == null) {
			ht.setSize(SIZE_ONE_HUNDRED_PERCENTS, "0px");
		}
		tabVerticalPanel.add(ht);

		// final VerticalPanel bottomVerticalPanel = new VerticalPanel();
		// bottomVerticalPanel.setSize("100%", "100%");
		// bottomVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		// HorizontalPanel bottomHorizontalPanel = new HorizontalPanel();
		// bottomVerticalPanel.add(bottomHorizontalPanel);
		// // final int n = 10;
		// bottomHorizontalPanel.setSpacing(0);
		// // bottomHorizontalPanel.setSize("100%", "100%");
		//
		// // VerticalPanel vp1 = new VerticalPanel();
		// // bottomHorizontalPanel.add(vp1);
		// // vp1.add(new
		// Label("111020, г. Москва, Боровая ул., д. 7, стр. 1"));
		// // vp1.add(new Label("тел.: +7-495-7805090"));
		// // vp1.add(new Label("info@curs.ru"));
		//
		// bottomHorizontalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		// // VerticalPanel vp2 = new VerticalPanel();
		// // bottomHorizontalPanel.add(vp2);
		// // vp2.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		// bottomHorizontalPanel.add(new Label("© Copyright,"));
		// HTML html = new HTML();
		// html.setHTML("&nbsp;");
		// bottomHorizontalPanel.add(html);
		// Anchor exitLink = new Anchor("<b>ООО 'КУРС-ИТ'</b>", true,
		// "http://www.curs.ru", "_blank");
		// exitLink.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		//
		// bottomHorizontalPanel.add(exitLink);
		// bottomHorizontalPanel.add(new Label(", 2010-2011"));
		//
		// // vp2.add(new Label("www.curs.ru"));
		// final int n = 17;
		// bottomVerticalPanel.setWidth(Window.getClientWidth() - n + "px");
		//
		// Window.addResizeHandler(new ResizeHandler() {
		// @Override
		// public void onResize(final ResizeEvent event) {
		// int width = event.getWidth() - n;
		// bottomVerticalPanel.setWidth(width + "px");
		// }
		// });

		// return bottomVerticalPanel;
		return tabVerticalPanel;

	}
}
