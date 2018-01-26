package ru.curs.showcase.core.html.jsForm;

/**
 * Данные элемента JsForm.
 * 
 * @author bogatov
 *
 */
public class JsFormData {
	private String data;
	private String setting;

	public JsFormData() {
	}

	public JsFormData(final String sData, final String sSetting) {
		super();
		this.data = sData;
		this.setting = sSetting;
	}

	public String getData() {
		return data;
	}

	public void setData(final String sData) {
		this.data = sData;
	}

	public String getSetting() {
		return setting;
	}

	public void setSetting(final String sSetting) {
		this.setting = sSetting;
	}

}
