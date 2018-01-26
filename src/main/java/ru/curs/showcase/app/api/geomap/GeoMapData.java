package ru.curs.showcase.app.api.geomap;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.Size;

/**
 * Динамические данные для карты.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoMapData extends Size implements SerializableElement {

	private static final long serialVersionUID = -3552200592580335858L;

	/**
	 * Коллекция слоев на карте.
	 */
	private List<GeoMapLayer> layers = new ArrayList<GeoMapLayer>();

	public GeoMapData(final GeoMap aParent) {
		super();
		parent = aParent;
	}

	public GeoMapData() {
		super();
		parent = null;
	}

	private final transient GeoMap parent;

	@Override
	public final void setWidth(final Integer aWidth) {
		super.setWidth(aWidth);
		if (parent != null) {
			parent.determineAutoSize();
		}
	}

	@Override
	public final void setHeight(final Integer aHeight) {
		super.setHeight(aHeight);
		if (parent != null) {
			parent.determineAutoSize();
		}
	}

	public final List<GeoMapLayer> getLayers() {
		return layers;
	}

	public final void setLayers(final List<GeoMapLayer> aLayers) {
		layers = aLayers;
	}

	/**
	 * Добавляет слой с определенным типом объектов.
	 * 
	 * @param aObjectType
	 *            - тип объекта.
	 * @return - добавленный слой.
	 */
	public GeoMapLayer addLayer(final GeoMapFeatureType aObjectType) {
		GeoMapLayer res = new GeoMapLayer(aObjectType);
		layers.add(res);
		return res;
	}

	/**
	 * Возвращает слой по его ID.
	 * 
	 * @param aLayerId
	 *            - ID слоя.
	 * @return - слой.
	 */
	public GeoMapLayer getLayerById(final ID aLayerId) {
		if (aLayerId == null) {
			return null;
		}
		for (GeoMapLayer cur : layers) {
			if (aLayerId.equals(cur.getId())) {
				return cur;
			}
		}
		return null;
	}

	public GeoMapLayer getLayerById(final String aLayerId) {
		return getLayerById(new ID(aLayerId));
	}

	/**
	 * Возвращает слой по идентификатору добавленного в него объекта.
	 * 
	 * @param aObjectId
	 *            - идентификатор объекта.
	 * @return - слой.
	 */
	public GeoMapLayer getLayerByObjectId(final ID aObjectId) {
		if (aObjectId == null) {
			return null;
		}
		for (GeoMapLayer curLayer : layers) {
			for (GeoMapFeature curObj : curLayer.getFeatures()) {
				if (aObjectId.equals(curObj.getId())) {
					return curLayer;
				}
			}
		}
		return null;
	}

	public GeoMapLayer getLayerByObjectId(final String aObjectId) {
		return getLayerByObjectId(new ID(aObjectId));
	}
}
