package ru.curs.showcase.app.api.datapanel;

import javax.xml.bind.annotation.*;

/**
 * Информация о элементе типа UI плагин.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "pluginElement")
@XmlAccessorType(XmlAccessType.FIELD)
public final class PluginInfo extends DataPanelElementInfo {

	private static final long serialVersionUID = -1192137836340386361L;

	private String plugin;
	private String getDataProcName;

	public PluginInfo(final String id, final String aPlugin, final String aProcName) {
		super(id, DataPanelElementType.PLUGIN);
		plugin = aPlugin;
		setProcName(aProcName);
	}

	public String getPlugin() {
		return plugin;
	}

	public void setPlugin(final String aPlugin) {
		plugin = aPlugin;
	}

	public void addPostProcessProc(final String id, final String name) {
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(id);
		proc.setName(name);
		proc.setType(DataPanelElementProcType.POSTPROCESS);
		getProcs().put(proc.getId(), proc);
	}

	public String getPostProcessProcName() {
		if (getProcByType(DataPanelElementProcType.POSTPROCESS) != null) {
			return getProcByType(DataPanelElementProcType.POSTPROCESS).getName();
		}
		return null;
	}

	public PluginInfo() {
		super();
	}

	public PluginInfo(final int aIndex, final DataPanelTab aTab) {
		super(aIndex, aTab);
		setType(DataPanelElementType.PLUGIN);
	}

	@Override
	public boolean isCorrect() {
		return (getSubtype() != null) || (super.isCorrect() && (plugin != null));
	}

	public String getGetDataProcName() {
		return getDataProcName;
	}

	public void setGetDataProcName(final String sGetDataProcName) {
		this.getDataProcName = sGetDataProcName;
	}

}
