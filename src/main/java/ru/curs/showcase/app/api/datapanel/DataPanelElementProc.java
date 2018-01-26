package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.NamedElement;

/**
 * Хранимая SQL процедура элемента информационной панели.
 * 
 * @author den
 * 
 */
public final class DataPanelElementProc extends NamedElement {

	private static final long serialVersionUID = 8875550160233655449L;

	/**
	 * Тип процедуры.
	 */
	private DataPanelElementProcType type;

	/**
	 * Идентификатор XSLT трансформации, используемой для преобразования данных
	 * перед отправкой их к месту назначения. Может выполняться для процедур
	 * типа SAVE, DOWNLOAD, UPLOAD.
	 * 
	 */
	private String transformName;

	/**
	 * Идентификатор XSD схемы, используемой для проверки данных перед их
	 * преобразованием и отправкой к месту назначения. Может выполняться для
	 * процедур типа SAVE, DOWNLOAD, UPLOAD.
	 * 
	 */
	private String schemaName;

	public DataPanelElementProcType getType() {
		return type;
	}

	public void setType(final DataPanelElementProcType aType) {
		type = aType;
	}

	public String getTransformName() {
		return transformName;
	}

	public void setTransformName(final String aTransformName) {
		transformName = aTransformName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(final String aSchemaName) {
		schemaName = aSchemaName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
		result = prime * result + ((transformName == null) ? 0 : transformName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof DataPanelElementProc)) {
			return false;
		}
		DataPanelElementProc other = (DataPanelElementProc) obj;
		if (schemaName == null) {
			if (other.schemaName != null) {
				return false;
			}
		} else if (!schemaName.equals(other.schemaName)) {
			return false;
		}
		if (transformName == null) {
			if (other.transformName != null) {
				return false;
			}
		} else if (!transformName.equals(other.transformName)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
}
