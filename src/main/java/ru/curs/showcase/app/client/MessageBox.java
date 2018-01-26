/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

/**
 * Интерфейс, ссылающийся на иконки, которые могут понадобится в окне сообщений
 * на клиенте.
 * 
 */
interface ImagesForDialogBox extends ClientBundle {
	/**
	 * Возвращает ImageResource для картинки "ошибка".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/message_error.png")
	ImageResource getErrorIcon();

	/**
	 * Возвращает ImageResource для картинки "Вниманеи".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/message_alert.png")
	ImageResource getAlertIcon();

	/**
	 * Возвращает ImageResource для картинки "Инфо".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/message_info.png")
	ImageResource getInfoIcon();

	/**
	 * Возвращает ImageResource для картинки
	 * "arrow_for_disclosure_panel_close.png".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/arrow_for_disclosure_panel_close.png")
	ImageResource getIconArrowForDisclosurePanelClose();

	/**
	 * Возвращает ImageResource для картинки
	 * "arrow_for_disclosure_panel_open.png".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/arrow_for_disclosure_panel_open.png")
	ImageResource getIconArrowForDisclosurePanelOpen();

}

/**
 * @author anlug
 * 
 *         Класс сообщений приложения Showcase
 * 
 */
public final class MessageBox {

	/**
	 * GWT сервис для доступа к иконкам, хранящимся на сервере.
	 */
	private static ImagesForDialogBox images =
		(ImagesForDialogBox) GWT.create(ImagesForDialogBox.class);

	// private static DataServiceAsync dataService;

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	public static final String NBSP = "&nbsp;";

	// private static final int Z_INDEX = 103;
	private static final int Z_INDEX = 951;

	private MessageBox() {
		super();
	}

	/**
	 * 
	 * Процедура вывода стандартного сообщения с одной кнопкой ОК в стиле gwt.
	 * 
	 * @param caption
	 *            - заглавие окна сообщения
	 * @param message
	 *            - текст сообщения
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showSimpleMessage(final String caption, final String message) {
		final DialogBox dlg = new DialogBox();

		dlg.getElement().getStyle().setZIndex(Z_INDEX);

		VerticalPanel dialogContents = new VerticalPanel();
		final int n = 10;
		dialogContents.setSpacing(n);
		dlg.setWidget(dialogContents);
		dlg.setAnimationEnabled(true);
		dlg.setGlassEnabled(true);
		dlg.setText(caption);
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dlg.hide();
			}
		});
		Label l = new Label(message);
		dialogContents.add(l);
		dialogContents.add(ok);
		dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
		dlg.getElement().setAttribute("message_type", "simple");
		dlg.center();
		ok.setFocus(true);
		return dlg;
	}

	/**
	 * 
	 * Функция создания окна DialogBox, которое закрывается горячей клавишей
	 * ESC.
	 * 
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox createDialogBoxWithClosingOnEsc() {

		return new DialogBox() {
			@Override
			protected void onPreviewNativeEvent(final NativePreviewEvent event) {

				if ((event.getTypeInt() == Event.ONKEYUP)
						&& (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)) {
					hide();
				}

				super.onPreviewNativeEvent(event);
			}
		};
	}

	/**
	 * 
	 * Процедура вывода сообщения с одной кнопкой ОК в стиле gwt c текстом для
	 * скрытия.
	 * 
	 * @param caption
	 *            - заглавие окна сообщения
	 * @param message
	 *            - текст сообщения
	 * @param hideMessage
	 *            - текст сообщения, который скрывается
	 * @param messageType
	 *            - тип сообщения: информация, ошибка, предупреждение
	 * @param showDetailedMessage
	 *            - переменная, определяющая показывать ли в сообщении
	 *            "подробности"
	 * @param messageSubtype
	 *            - подтип сообщения
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showMessageWithDetails(final String caption, final String message,
			final String hideMessage, final MessageType messageType,
			final Boolean showDetailedMessage, final String messageSubtype) {
		final DialogBox dlg = createDialogBoxWithClosingOnEsc();

		dlg.getElement().getStyle().setZIndex(Z_INDEX);

		dlg.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		final int n = 10;
		dialogContents.setSpacing(n);
		dlg.setWidget(dialogContents);
		dlg.setAnimationEnabled(true);
		dlg.setGlassEnabled(true);
		dlg.setText(caption);
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dlg.hide();
			}
		});

		HorizontalPanel horPan = new HorizontalPanel();
		final int n5 = 5;
		horPan.setSpacing(n5);
		dialogContents.add(horPan);

		horPan.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horPan.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		Image im1 = new Image();

		String attributeMessage = "";

		if (messageSubtype == null) {
			switch (messageType) {
			case INFO:
				im1.setResource(images.getInfoIcon());
				attributeMessage = "info";
				break;

			case WARNING:
				im1.setResource(images.getAlertIcon());
				attributeMessage = "warning";
				break;

			case ERROR:
				im1.setResource(images.getErrorIcon());
				attributeMessage = "error";
				break;

			default:
				break;
			}
		} else {
			String url = Window.Location.getProtocol() + "//" + Window.Location.getHost()
					+ Window.Location.getPath() + messageSubtype;
			im1.setUrl(url);
		}

		horPan.add(im1);

		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm:ss z");
		String formattedDate = dtf.format(date);

		// Label l = new Label(message);
		Label l = new HTML(message);
		horPan.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_LEFT);
		horPan.add(l);

		if (messageType == MessageType.ERROR) {
			HorizontalPanel consoleLinkPanel = new HorizontalPanel();
			dialogContents.add(consoleLinkPanel);
			consoleLinkPanel.setWidth(SIZE_ONE_HUNDRED_PERCENTS);
			consoleLinkPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			Anchor anchor = new Anchor("Веб-консоль", "log/lastLogEvents.jsp", "_blank");
			consoleLinkPanel.add(anchor);
		}

		if (showDetailedMessage) {
			final DisclosurePanel dp = new DisclosurePanel();

			HorizontalPanel hp = new HorizontalPanel();

			hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			Image im = new Image();
			// im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);
			im.setResource(images.getIconArrowForDisclosurePanelClose());

			hp.add(im);
			hp.add(new HTML(NBSP + "Показать подробную информацию" + NBSP + NBSP));
			dp.setHeader(hp);

			dialogContents.add(dp);
			final TextArea textArea = new TextArea();
			dp.addOpenHandler(new OpenHandler<DisclosurePanel>() {

				@Override
				public void onOpen(final OpenEvent<DisclosurePanel> arg0) {

					HorizontalPanel hp = new HorizontalPanel();

					hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					Image im = new Image();
					// im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_OPEN_IMAGE);
					im.setResource(images.getIconArrowForDisclosurePanelOpen());

					textArea.setSize("600px", "250px");
					hp.add(im);
					hp.add(new HTML(NBSP + "Скрыть подробную информацию" + NBSP + NBSP));
					dp.setHeader(hp);

				}

			});

			dp.addCloseHandler(new CloseHandler<DisclosurePanel>() {

				@Override
				public void onClose(final CloseEvent<DisclosurePanel> arg0) {
					HorizontalPanel hp = new HorizontalPanel();

					hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					Image im = new Image();
					// im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);
					im.setResource(images.getIconArrowForDisclosurePanelClose());

					hp.add(im);
					hp.add(new HTML(NBSP + "Показать подробную информацию"));
					dp.setHeader(hp);

				}

			});

			dp.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
			textArea.setSize("90%", SIZE_ONE_HUNDRED_PERCENTS);
			final int n1 = 5;
			textArea.setVisibleLines(n1);
			textArea.setText("Time: " + formattedDate + "\r\n\r\n" + hideMessage);

			textArea.setReadOnly(true);
			dp.setContent(textArea);
			// }

			final String messageToCopy = "Time: "
					+ formattedDate + "\r\n\r\nUser: " + AppCurrContext.getInstance()
							.getServerCurrentState().getUserInfo().getCaption()
					+ "\r\n\r\n" + hideMessage;

			Button copy = new Button(CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "Copy"));
			copy.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					// if (dataService == null) {
					// dataService = GWT.create(DataService.class);
					// }
					//
					// dataService.copyToClipboard(messageToCopy, new
					// AsyncCallback<Void>() {
					//
					// @Override
					// public void onFailure(final Throwable arg0) {
					// Window.alert("Clipboard Error: " + arg0);
					// }
					//
					// @Override
					// public void onSuccess(Void arg0) {
					// // Ничего не выводим
					// }
					// });
					// }

					copyToClipboard(messageToCopy);
				}
			});
			HorizontalPanel leftPanel = new HorizontalPanel();
			leftPanel.setSize("100%", "100%");
			leftPanel.add(copy);
			leftPanel.setCellHorizontalAlignment(copy, HasHorizontalAlignment.ALIGN_LEFT);
			HorizontalPanel rightPanel = new HorizontalPanel();
			rightPanel.setSize("100%", "100%");
			rightPanel.add(ok);
			rightPanel.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
			DockPanel dockPanel = new DockPanel();
			dockPanel.add(leftPanel, DockPanel.WEST);
			dockPanel.add(rightPanel, DockPanel.EAST);
			dialogContents.add(dockPanel);
			dockPanel.setSize("100%", "100%");
		} else {
			dialogContents.add(ok);
			dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
		}
		dlg.getElement().setAttribute("message_type", attributeMessage);
		dlg.center();
		ok.setFocus(true);

		return dlg;
	}

	public static void showErrorMessageWindow(final String caption, final String message) {
		DialogBox db = showMessageWithDetails(
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						caption),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						message),
				"", MessageType.ERROR, Boolean.FALSE, (String) null);
		db.center();
		db.show();
	}

	public static void showWarningMessageWindow(final String caption, final String message) {
		DialogBox db = showMessageWithDetails(
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						caption),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						message),
				"", MessageType.WARNING, Boolean.FALSE, (String) null);
		db.center();
		db.show();
	}

	public static void showInfoMessageWindow(final String caption, final String message) {
		DialogBox db = showMessageWithDetails(
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						caption),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						message),
				"", MessageType.INFO, Boolean.FALSE, (String) null);
		db.center();
		db.show();
	}

	public static native void copyToClipboard(String text) /*-{
		var textArea = $doc.createElement("textarea");
		textArea.style.position = 'fixed';
		textArea.style.top = 0;
		textArea.style.left = 0;
		textArea.style.width = '2em';
		textArea.style.height = '2em';
		textArea.style.padding = 0;
		textArea.style.border = 'none';
		textArea.style.outline = 'none';
		textArea.style.boxShadow = 'none';
		textArea.style.background = 'transparent';
		textArea.value = text;
		textArea.id = 'ta';
		$doc.body.appendChild(textArea);
		var range = $doc.createRange();
		range.selectNode(textArea);
		textArea.select();
		$wnd.getSelection().addRange(range);
		try {
			var successful = $doc.execCommand('copy');
		} catch (err) {
			alert('Oops, unable to copy');
		}
		$doc.body.removeChild(textArea);
	}-*/;
}
