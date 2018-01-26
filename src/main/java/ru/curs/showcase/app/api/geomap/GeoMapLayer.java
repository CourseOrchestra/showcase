package ru.curs.showcase.app.api.geomap;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Класс слоя на карте. Слой может содержать объекты только одного типа. Кроме
 * того, для слоя задается набор показателей.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoMapLayer extends NamedElement implements SerializableElement {
	/**
	 * Имя, используемое для показателей по умолчанию.
	 */
	public static final String MAIN_IND_NAME = "mainInd";

	/**
	 * Проекция по умолчанию для точек.
	 */
	public static final String DEF_POINT_PROJECTION = "EPSG:4326";

	private static final long serialVersionUID = -3953726500515923129L;

	/**
	 * Тип объектов на слое.
	 */
	private GeoMapFeatureType type;

	/**
	 * Формат подсказки для объектов на слое карты. На основе этого формата
	 * фабрикой формируются подсказки для объектов слоя, которые будут
	 * отправлены компоненте карты.
	 */
	private transient String hintFormat = null;

	/**
	 * Код географической проекции для объектов на слое. Полигоны приходят в
	 * конической проекции, а точечные объекты - в географической. Для работы
	 * компоненты 2карта" точечные объекты должны быть сконвертированы из
	 * географической проекции в коническую. Используемая нами проекция для
	 * точечных объектов: "EPSG:4326". Для полигонов и мультиполигонов проекция
	 * не задается.
	 */
	private String projection;

	/**
	 * Набор ГИС объектов на слое.
	 */
	private List<GeoMapFeature> features = new ArrayList<GeoMapFeature>();

	/**
	 * Набор показателей для слоя.
	 */
	private List<GeoMapIndicator> indicators = new ArrayList<GeoMapIndicator>();

	public GeoMapLayer() {
		super();
	}

	public GeoMapLayer(final GeoMapFeatureType aObjectType) {
		super();
		type = aObjectType;
		determineLayerObjectProjection();
	}

	private void determineLayerObjectProjection() {
		if (type == GeoMapFeatureType.POINT) {
			projection = DEF_POINT_PROJECTION;
		}
	}

	public final GeoMapFeatureType getType() {
		return type;
	}

	/**
	 * Расширенный модификатор свойства type. Кроме модификации также вызывает
	 * функцию определения проекции объектов для слоя.
	 * 
	 * @param aType
	 *            - новый тип объектов для слоя.
	 */
	public final void setType(final GeoMapFeatureType aType) {
		type = aType;
		determineLayerObjectProjection();
	}

	public final List<GeoMapFeature> getFeatures() {
		return features;
	}

	public final void setFeatures(final List<GeoMapFeature> aFeatures) {
		features = aFeatures;
	}

	public final List<GeoMapIndicator> getIndicators() {
		return indicators;
	}

	public final void setIndicators(final List<GeoMapIndicator> aIndicators) {
		indicators = aIndicators;
	}

	/**
	 * Добавляет новую точку на слой. Возвращает null если происходит попытка
	 * тип слоя - не POINT. Нельзя возвращать user defined exception из-за gwt.
	 * 
	 * @param id
	 *            - идентификатор точки.
	 * @param name
	 *            - наименование точки.
	 * @return - добавленная на слой точка.
	 */
	public GeoMapFeature addPoint(final String id, final String name) {
		if (type != GeoMapFeatureType.POINT) {
			return null;
		}
		GeoMapFeature res = new GeoMapFeature(id, name);
		features.add(res);
		return res;
	}

	/**
	 * Добавляет новую область на слой. Возвращает null если происходит попытка
	 * тип слоя - не POLYGON. Нельзя возвращать user defined exception из-за
	 * gwt.
	 * 
	 * @param id
	 *            - идентификатор области.
	 * @param name
	 *            - наименование области.
	 * @return - добавленная область.
	 */
	public GeoMapFeature addPolygon(final String id, final String name) {
		if (type != GeoMapFeatureType.POLYGON) {
			return null;
		}
		GeoMapFeature res = new GeoMapFeature(id, name);
		features.add(res);
		return res;
	}

	/**
	 * Добавляет новый показатель на слой.
	 * 
	 * @param id
	 *            - идентификатор показателя.
	 * @param name
	 *            - имя показателя.
	 * @return - добавленный показатель.
	 */
	public GeoMapIndicator addIndicator(final String id, final String name) {
		GeoMapIndicator res = new GeoMapIndicator(id, name);
		res.setDbId(id);
		indicators.add(res);
		return res;
	}

	/**
	 * Возвращает показатель по его идентификатору.
	 * 
	 * @param aId
	 *            - идентификатор.
	 * @return - показатель.
	 */
	public GeoMapIndicator getIndicatorById(final ID aId) {
		if (aId == null) {
			return null;
		}
		for (GeoMapIndicator cur : indicators) {
			if (aId.equals(cur.getId())) {
				return cur;
			}
		}
		return null;
	}

	public GeoMapIndicator getIndicatorById(final String aId) {
		return getIndicatorById(new ID(aId));
	}

	public final String getHintFormat() {
		return hintFormat;
	}

	public final void setHintFormat(final String aHintFormat) {
		hintFormat = aHintFormat;
	}

	/**
	 * Получение ГИС объекта по его идентификатору.
	 * 
	 * @param aObjectId
	 *            - идентификатор объекта.
	 * @return - объект или null.
	 */
	public GeoMapFeature getObjectById(final ID aObjectId) {
		if (aObjectId == null) {
			return null;
		}
		for (GeoMapFeature cur : features) {
			if (aObjectId.equals(cur.getId())) {
				return cur;
			}
		}
		return null;
	}

	public GeoMapFeature getObjectById(final String aObjectId) {
		return getObjectById(new ID(aObjectId));
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(final String aProjection) {
		projection = aProjection;
	}

	/**
	 * Возвращает показатель для раскраски слоя.
	 * 
	 * @return - показатель для раскраски слоя или null при его отсутствии.
	 */
	public GeoMapIndicator getMainIndicator() {
		for (GeoMapIndicator ind : indicators) {
			if (ind.getIsMain()) {
				return ind;
			}
		}
		return null;
	}

	/**
	 * Возвращает основной идентификатор показателя по его DB идентификатору.
	 * 
	 * @param aDbId
	 *            - DB идентификатор.
	 * @return - основной идентификатор.
	 */
	public ID getAttrIdByDBId(final String aDbId) {
		for (GeoMapIndicator ind : indicators) {
			if (ind.getDbId().equals(aDbId)) {
				return ind.getId();
			}
		}
		return null;
	}

	/**
	 * Генерирует корректные id для показателей, которые можно использовать в
	 * компоненте карты.
	 */
	public void generateIndicatorsIds() {
		int index = 0;
		for (GeoMapIndicator ind : indicators) {
			if (ind.getIsMain()) {
				ind.setId(MAIN_IND_NAME);
			} else {
				String newId = "ind" + index;
				ind.setId(newId);
			}
			index++;
		}
	}

}
