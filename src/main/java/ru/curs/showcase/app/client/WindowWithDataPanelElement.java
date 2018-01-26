/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.api.BasicElementPanel;
import ru.curs.showcase.app.client.panels.DialogBoxWithCaptionButton;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * 
 * Класс окна для отображения элементов информационной панели различных типов.
 * 
 * @author anlug
 * 
 */
public class WindowWithDataPanelElement extends DialogBoxWithCaptionButton {

	private final int n100 = 100;

	/**
	 * ScrollPanel, которая содержит содержимое модального окна.
	 */
	private final ScrollPanel sp = new ScrollPanel();
	/**
	 * BasicElementPanel bep.
	 */
	private BasicElementPanel bep = null;

	/**
	 * Переменная определяющая показывать ли внизу модального окна кнопку
	 * Закрыть.
	 */
	private Boolean showCloseBottomButton;

	/**
	 * @param ashowCloseBottomButton
	 *            the showCloseBottomButton to set
	 */
	public final void setShowCloseBottomButton(final Boolean ashowCloseBottomButton) {
		this.showCloseBottomButton = ashowCloseBottomButton;
	}

	/**
	 * @return the showCloseBottomButton
	 */
	public Boolean getShowCloseBottomButton() {
		return showCloseBottomButton;
	}

	public WindowWithDataPanelElement(final Boolean ashowCloseBottomButton,
			final Boolean aCloseOnEsc, final String aCssClass) {
		super();
		if (aCssClass != null) {
			this.addStyleName(aCssClass);
		}
		this.addStyleName("modalwindow-element");

		setShowCloseBottomButton(ashowCloseBottomButton);
		setCloseOnEsc(aCloseOnEsc);
		sp.setSize(String.valueOf(Window.getClientWidth() - n100) + "px",
				String.valueOf(Window.getClientHeight() - n100) + "px");
	}

	public WindowWithDataPanelElement(final String caption, final Boolean ashowCloseBottomButton,
			final Boolean aCloseOnEsc, final String aCssClass) {
		super(caption);
		if (aCssClass != null) {
			this.addStyleName(aCssClass);
		}
		this.addStyleName("modalwindow-element");

		setShowCloseBottomButton(ashowCloseBottomButton);
		setCloseOnEsc(aCloseOnEsc);
		sp.setSize(String.valueOf(Window.getClientWidth() - n100) + "px",
				String.valueOf(Window.getClientHeight() - n100) + "px");
	}

	public WindowWithDataPanelElement(final String caption, final Integer width1,
			final Integer heigth1, final Boolean ashowCloseBottomButton,
			final Boolean aCloseOnEsc, final String aCssClass) {

		super(caption);
		if (aCssClass != null) {
			this.addStyleName(aCssClass);
		}
		this.addStyleName("modalwindow-element");

		Integer width = width1;
		Integer heigth = heigth1;
		if (width != null) {
			width =
				(width > Window.getClientWidth() - n100) ? Window.getClientWidth() - n100 : width;
		}
		if (heigth != null) {

			heigth =

			(heigth > Window.getClientHeight() - n100) ? Window.getClientHeight() - n100 : heigth;
		}

		if ((width != null) && (heigth != null)) {
			sp.setSize(String.valueOf(width) + "px", String.valueOf(heigth) + "px");
		} else {

			if (width != null) {
				sp.setSize(String.valueOf(width) + "px",
						String.valueOf(Window.getClientHeight() - n100) + "px");
			}

			if (heigth != null) {
				sp.setSize(String.valueOf(Window.getClientWidth() - n100) + "px",
						String.valueOf(heigth) + "px");
			}

		}
		setShowCloseBottomButton(ashowCloseBottomButton);
		setCloseOnEsc(aCloseOnEsc);
	}

	// public WindowWithDataPanelElement(final boolean autoHide, final boolean
	// modal) {
	// super(autoHide, modal, "");
	//
	// }

	// public WindowWithDataPanelElement(final boolean autoHide) {
	// super(autoHide);
	// }

	/**
	 * Показывает модальное окно с переданной ему BasicElementPanel и заголовком
	 * окна.
	 * 
	 * @param bep1
	 *            - BasicElementPanel, который будет отображаться в окне.
	 */
	public void showModalWindow(final BasicElementPanel bep1) {

		bep = bep1;

		VerticalPanel dialogContents = new VerticalPanel();
		DOM.setElementAttribute(dialogContents.getElement(), "id", "showcaseModalWindow");

		sp.add(dialogContents);
		final int n = 10;
		dialogContents.setSpacing(n);
		dialogContents.setSize("100%", "100%");
		setWidget(sp);
		setAnimationEnabled(true);
		setGlassEnabled(true);

		dialogContents.add(bep.getPanel());

		if (getShowCloseBottomButton()) {
			Button ok = new Button("Закрыть");
			ok.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					closeWindow();
				}
			});

			dialogContents.add(ok);
			dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);

			ok.setFocus(true);

		}

		AppCurrContext.getInstance().setCurrentOpenWindowWithDataPanelElement(this);
		center();
		show();

		final Timer timer = new Timer() {
			@Override
			public void run() {
				RootPanel.getBodyElement().addClassName("modalwindow");
			}
		};
		final int n1000 = 1000;
		timer.schedule(n1000);

		// альтернатива - xforms в iframe

		// NamedFrame iframe = new NamedFrame("modalXForm1");
		// DOM.setElementAttribute(iframe.getElement(), "id", "modalXForm1");
		// dialogContents.add(iframe);
		//
		// DOM.setElementAttribute(dialogContents.getElement(), "id",
		// "xformParent");
		// DOM.setElementAttribute(bep.getPanel().getElement(), "id", "xform1");
		// show(); // must do it!

		// IFrameElement frame =
		// IFrameElement.as(RootPanel.get("modalXForm1").getElement());
		// Document doc = frame.getContentDocument();
		// BodyElement be = doc.getBody();
		//
		// DivElement div = doc.createDivElement();
		// div.setId("target");
		// be.appendChild(div);
		//
		// be.appendChild(bep.getPanel().getElement());
		//
		// addScriptLink("xsltforms/xsltforms.js");
		// addCSSLink("xsltforms/xsltforms.css");
		// show();
		// плюс к вышеперечисленному во всех JSNI функциях XFormPanel нужно
		// заменить $wnd на $wnd.frames['modalXForm1'] и $doc на
		// $wnd.frames['modalXForm1'].document
		// и добавить 2 функции:
		//
		// public static native void addScriptLink(final String link) /*-{
		// var newscript =
		// $wnd.frames['modalXForm1'].document.createElement('script');
		// newscript.type = "text/javascript";
		// newscript.src = link;
		// var body =
		// $wnd.frames['modalXForm1'].document.getElementsByTagName('body')[0];
		// body.insertBefore(newscript, body.firstChild);
		// }-*/;
		//
		// public static native void addCSSLink(final String link) /*-{
		// var objCSS =
		// $wnd.frames['modalXForm1'].document.createElement('link');
		// objCSS.rel = 'stylesheet';
		// objCSS.href = link;
		// objCSS.type = 'text/css';
		// var hh1 =
		// $wnd.frames['modalXForm1'].document.getElementsByTagName('head')[0];
		// hh1.appendChild(objCSS);
		// }-*/;
	}

	/**
	 * Скрывает модальное окно.
	 */
	@Override
	public void closeWindow() {
		super.closeWindow();
		AppCurrContext.getInstance().setCurrentOpenWindowWithDataPanelElement(null);
		if ((bep != null) && (bep instanceof XFormPanel)) {
			((XFormPanel) bep).unloadSubform();
		}
		bep = null;

		final Timer timer = new Timer() {
			@Override
			public void run() {
				RootPanel.getBodyElement().removeClassName("modalwindow");
				RootPanel.getBodyElement().addClassName("ready");
			}
		};
		final int n1000 = 1000;
		timer.schedule(n1000);

	}
}
