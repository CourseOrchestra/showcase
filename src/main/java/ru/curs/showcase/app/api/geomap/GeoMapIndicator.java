package ru.curs.showcase.app.api.geomap;

import javax.xml.bind.annotation.*;

/**
 * Показатель для карты. Показатель должен относится к какому-то объекту карты.
 * Он отображается рядом с соответствующим объектом.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoMapIndicator extends GeoMapObject {

	private static final long serialVersionUID = 1236534208108835700L;

	/**
	 * Идентификатор показателя в БД. Не используется в клиентском коде, т.к. в
	 * компоненте карты показатели являются атрибутами объекта на карте и их
	 * идентификаторы должны удовлетворять дополнительным требованиям. В
	 * клиентский код не передается.
	 */
	private transient String dbId;

	/**
	 * Признак того, что на основе этого показателя определяется цвет области на
	 * слое. Имеет смысл только для показателей, относящихся к слою типа
	 * POLYGON. Установка данного признака в true имеет приоритет перед явно
	 * заданным цветом области.
	 */
	private Boolean isMain = false;

	public String getDbId() {
		return dbId;
	}

	public void setDbId(final String aDbId) {
		dbId = aDbId;
	}

	public GeoMapIndicator() {
		super();
	}

	public GeoMapIndicator(final String aId, final String aName) {
		super(aId, aName);
	}

	public final Boolean getIsMain() {
		return isMain;
	}

	public final void setIsMain(final Boolean aIsMain) {
		isMain = aIsMain;
	}
}
