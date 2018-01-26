package ru.curs.showcase.app.api.plugin;

/**
 * Декоратор компоненты плагина.
 * 
 * @author bogatov
 * 
 */
public class PluginDecorator implements PluginComponent {

	private final PluginComponent plugin;

	public PluginDecorator(final PluginComponent oPlugin) {
		super();
		this.plugin = oPlugin;
	}

	@Override
	public void draw() {
		this.plugin.draw();
	}

	public PluginComponent getPlugin() {
		return this.plugin;
	}

	@Override
	public PluginParam getParam() {
		return this.plugin.getParam();
	}

	@Override
	public void addDrawPluginCompleteHandler(final DrawPluginCompleteHandler handler) {
		this.plugin.addDrawPluginCompleteHandler(handler);
	}
}
