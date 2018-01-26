package ru.curs.showcase.app.api.plugin;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.common.*;
import ru.curs.showcase.app.api.plugin.PluginParam.Options;
import ru.curs.showcase.app.client.panels.DialogBoxWithCaptionButton;

/**
 * Декоратор создает всплывающие окно.
 * 
 * @author bogatov
 * 
 */
public class WindowPluginDecorator extends PluginDecorator {

	private final DialogBoxWithCaptionButton window;

	private JavaScriptObject pluginResult;

	public JavaScriptObject getPluginResult() {
		return pluginResult;
	}

	public void setPluginResult(final JavaScriptObject oPluginResult) {
		this.pluginResult = oPluginResult;
	}

	public WindowPluginDecorator(final PluginComponent oPlugin) {
		super(oPlugin);
		PluginParam pluginParam = getParam();
		Options options = pluginParam.options();

		String renderTo = HTMLPanel.createUniqueId();
		pluginParam.setParentId(renderTo);

		this.window = new DialogBoxWithCaptionButton(options.windowCaption());
		this.window.setWidget(getBodyWidget(renderTo, pluginParam, options));

		oPlugin.addDrawPluginCompleteHandler(new DrawPluginCompleteHandler() {
			@Override
			public void onDrawComplete(final JavaScriptObject o) {
				setPluginResult(o);
			}
		});
	}

	@Override
	public void draw() {
		this.window.center();
		getPlugin().draw();
	}

	private Widget getBodyWidget(final String pluginTargetId, final PluginParam pluginParam,
			final Options options) {

		SimplePanel cellholder = new SimplePanel();
		cellholder.getElement().setId(pluginTargetId);
		cellholder.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		cellholder.setHeight(options.dataHeight());
		cellholder.setWidth(options.dataWidth());

		HorizontalPanel bottonPanel = new HorizontalPanel();
		ActionButtonAdapter okButton =
			new ActionButtonAdapter(getOkAction(options.buttonOkLabel(), options));
		ActionButtonAdapter cancelButton =
			new ActionButtonAdapter(getCancelAction(options.buttonCancelLabel(), options));
		bottonPanel.add(okButton.getButton());
		bottonPanel.add(new HTML("&nbsp;&nbsp;"));
		bottonPanel.add(cancelButton.getButton());

		VerticalPanel bodyPanel = new VerticalPanel();
		bodyPanel.setWidth("100%");
		bodyPanel.add(cellholder);
		bodyPanel.add(bottonPanel);
		bodyPanel.setCellHorizontalAlignment(bottonPanel, HasHorizontalAlignment.ALIGN_RIGHT);

		return bodyPanel;
	}

	private AbstractAction getOkAction(final String label, final Options options) {
		return new AbstractAction(label) {
			@Override
			protected void perform() {
				options.onSelectionComplete(true, getPluginResult());
				window.closeWindow();
			}
		};
	}

	private Action getCancelAction(final String label, final Options options) {
		return new AbstractAction(label) {
			@Override
			protected void perform() {
				options.onSelectionComplete(false, getPluginResult());
				window.closeWindow();
			}
		};
	}
}
