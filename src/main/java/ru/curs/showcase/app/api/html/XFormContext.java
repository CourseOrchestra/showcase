package ru.curs.showcase.app.api.html;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Контекст XForms, включающий значения, введенные пользователем в mainInstance.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "xformsContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class XFormContext extends CompositeContext {
	private static final long serialVersionUID = -6836184134400790951L;

	private String formData;

	/**
	 * Признак того, что нужно сохранить введенные пользователем данные.
	 */
	@XmlTransient
	private Boolean keepUserSettings = true;

	/**
	 * Признак того, что нужно обновлять форму.
	 */
	@XmlTransient
	private Boolean needReload = true;

	public XFormContext(final CompositeContext baseContext) {
		super();
		assignNullValues(baseContext);
	}

	public XFormContext(final CompositeContext baseContext, final String aFormData) {
		super();
		assignNullValues(baseContext);
		formData = aFormData;
	}

	public XFormContext() {
		super();
	}

	public XFormContext(final Map<String, List<String>> aParams, final String aFormData) {
		super(aParams);
		formData = aFormData;
	}

	public String getFormData() {
		return formData;
	}

	public void setFormData(final String aFormData) {
		formData = aFormData;
	}

	@Override
	public XFormContext gwtClone() {
		XFormContext result = (XFormContext) super.gwtClone();
		result.keepUserSettings = keepUserSettings;
		result.formData = formData;
		return result;
	}

	@Override
	protected XFormContext newInstance() {
		return new XFormContext();
	}

	@Override
	public String toString() {
		return "XFormsContext [formData=" + formData + ", toString()=" + super.toString() + "]";
	}

	public Boolean getKeepUserSettings() {
		return keepUserSettings;
	}

	public void setKeepUserSettings(final Boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
	}

	public Boolean getNeedReload() {
		return needReload;
	}

	public void setNeedReload(final Boolean aNeedReload) {
		needReload = aNeedReload;
	}

}
