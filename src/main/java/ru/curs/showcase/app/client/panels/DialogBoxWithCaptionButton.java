/**
 * 
 */
package ru.curs.showcase.app.client.panels;

/**
 * @author anlug
 *
 */

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.*;

/**
 * Класс, ссылающийся на иконки для {@link DialogBoxWithCaptionButton}.
 * 
 */
interface ImagesForDialogBox extends ClientBundle {
	/**
	 * Возвращает ImageResource для иконки на кнопку Закрыть.
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/close_button_for_dialogbox.png")
	ImageResource getCloseButton();
}

/**
 * Класс диалогового окна с кнопкой закрыть в заглавной панели, также горячей
 * кнопкой Esc для закрытия окна.
 * 
 * @author anlug
 */
public class DialogBoxWithCaptionButton extends DialogBox {

	/**
	 * GWT сервис для доступа к иконкам, хранящимся на сервере.
	 */
	private static ImagesForDialogBox images =
		(ImagesForDialogBox) GWT.create(ImagesForDialogBox.class);

	/**
	 * Объект Image иконки на кнопку закрыть.
	 */
	private final Image close;

	/**
	 * HTML виджет, в который будет помещен заголовок окна.
	 */
	private final HTML title = new HTML("");

	/**
	 * Горизонтальная панель заголовка модального окна.
	 */
	private final HorizontalPanel captionPanel = new HorizontalPanel();

	/**
	 * Переменная определяющая,будет ли закрыто окно при нажатии на кнопку Esc
	 * клавиатуры.
	 */
	private Boolean closeOnEsc = true;

	/**
	 * @param aCloseOnEsc
	 *            the closeOnEsc to set
	 */
	public final void setCloseOnEsc(final Boolean aCloseOnEsc) {
		closeOnEsc = aCloseOnEsc;
	}

	/**
	 * @return the closeOnEsc
	 */
	public Boolean getCloseOnEsc() {
		return closeOnEsc;
	}

	/**
	 * Конструктор.
	 * 
	 * @param autoHide
	 * @param modal
	 */
	public DialogBoxWithCaptionButton(final boolean autoHide, final boolean modal,
			final String caption) {

		// super(autoHide, modal);
		super(autoHide, false);

		Element td = getCellElement(0, 1);
		DOM.removeChild(td, (Element) td.getFirstChildElement());
		DOM.appendChild(td, captionPanel.getElement());
		captionPanel.setStyleName("Caption");
		captionPanel.setSize("100%", "100%");
		title.setText(caption);
		captionPanel.add(title);

		close = new Image(images.getCloseButton());
		// close.addStyleName("CloseButton");// float:right
		captionPanel.add(close);

		captionPanel.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_RIGHT);
		super.setGlassEnabled(true);
		// super.setAnimationEnabled(true);

		final int zIndex = 102;
		getElement().getStyle().setZIndex(zIndex);

	}

	public DialogBoxWithCaptionButton(final boolean autoHide, final String caption) {
		this(autoHide, true, caption);
	}

	public DialogBoxWithCaptionButton() {
		this(false, "");
	}

	public DialogBoxWithCaptionButton(final String caption) {

		this(false, caption);
	}

	@Override
	public String getHTML() {
		return this.title.getHTML();
	}

	@Override
	public String getText() {
		return this.title.getText();
	}

	@Override
	public void setHTML(final String html) {
		this.title.setHTML(html);
	}

	@Override
	public void setText(final String text) {
		this.title.setText(text);
	}

	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();

		// if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
		// event.cancel();
		// }

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK)
				&& isCloseEvent(nativeEvent) && !isActiveXFormDialog()) {
			closeWindow();
		}

		if (getCloseOnEsc() != null) {
			if (getCloseOnEsc()) {
				if ((event.getTypeInt() == Event.ONKEYUP)
						&& (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
						&& !isActiveXFormDialog()) {
					closeWindow();
				}
			}
		}

		super.onPreviewNativeEvent(event);
	}

	private boolean isActiveXFormDialog() {
		boolean res = false;
		Element surround = DOM.getElementById("xforms-dialog-surround");
		if ((surround != null) && (surround.getStyle().getDisplay().equalsIgnoreCase("block"))) {
			res = true;
		}
		return res;
	}

	private boolean isCloseEvent(final NativeEvent event) {
		return event.getEventTarget().equals(close.getElement()); // compares
																	// equality
																	// of the
																	// underlying
																	// DOM
																	// elements
	}

	/**
	 * Скрывает модальное окно.
	 */
	public void closeWindow() {
		this.hide();
	}
}
