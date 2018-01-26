package ru.curs.showcase.app.client;

import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.client.panels.DialogBoxWithCaptionButton;

/**
 * 
 * Класс, генерирующий модальное окно с переданным в него в виде HTML
 * содержанием. Окно может содержать кнопку закрыть (в верхнем правом углу).
 * 
 * @author anlug
 * 
 */
public final class ModalWindowWithHTMLContent {

	private static DialogBox currentOpenModalWindowsl = null;

	private ModalWindowWithHTMLContent() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Процедура показывающая окно.
	 * 
	 * @param caption
	 *            - заголовок окна (название).
	 * @param textHTML
	 *            - HTML, который будет отображаться внутри окна.
	 * @param showCloseButton
	 *            - параметр определяющий будет ли показываться кнопка "Закрыть"
	 * @param windowWidth
	 *            - параметр определяющий ширину html области окна
	 * @param windowHeight
	 *            - параметр определяющий высоту html области окна
	 * 
	 * 
	 */
	public static void showWindow(final String caption, final String textHTML,
			final boolean showCloseButton, final int windowWidth, final int windowHeight) {

		if (getCurrentOpenModalWindowsl() == null) {

			if (showCloseButton) {
				showWindowWithCloseButton(caption, textHTML, windowWidth, windowHeight);
			} else {

				showWindowWithoutCloseButton(caption, textHTML, windowWidth, windowHeight);
			}
		}

	}

	public static void showWindowWithCloseButton(final String caption, final String textHTML,
			final Integer windowWidth, final Integer windowHeight) {
		DialogBoxWithCaptionButton db = new DialogBoxWithCaptionButton(caption) {

			@Override
			public void closeWindow() {
				ModalWindowWithHTMLContent.closeWindow();
			}

		};
		HTML html = new HTML();

		html.setHTML(textHTML);
		final int n500 = 500;
		final int n400 = 400;
		if ((windowWidth == null) || (windowHeight == null)) {
			html.setPixelSize(n500, n400);
		} else {
			html.setPixelSize(windowWidth, windowHeight);
		}
		db.add(html);
		setCurrentOpenModalWindowsl(db);
		db.center();
		db.show();
	}

	public static void showWindowWithoutCloseButton(final String caption, final String textHTML,
			final Integer windowWidth, final Integer windowHeight) {

		DialogBox db = new DialogBox();

		db.setText(caption);

		HTML html = new HTML();

		html.setHTML(textHTML);
		final int n500 = 500;
		final int n400 = 400;
		if ((windowWidth == null) || (windowHeight == null)) {
			html.setPixelSize(n500, n400);
		} else {
			html.setPixelSize(windowWidth, windowHeight);
		}
		db.add(html);
		setCurrentOpenModalWindowsl(db);
		db.center();
		db.show();

	}

	public static DialogBox getCurrentOpenModalWindowsl() {
		return currentOpenModalWindowsl;
	}

	public static void setCurrentOpenModalWindowsl(final DialogBox acurrentOpenModalWindowsl) {
		ModalWindowWithHTMLContent.currentOpenModalWindowsl = acurrentOpenModalWindowsl;
	}

	public static void closeWindow() {
		if (getCurrentOpenModalWindowsl() != null) {
			getCurrentOpenModalWindowsl().hide();
			setCurrentOpenModalWindowsl(null);
		}

	}
}
