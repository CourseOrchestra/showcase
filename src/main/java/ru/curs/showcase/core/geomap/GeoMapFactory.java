package ru.curs.showcase.core.geomap;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

import javax.sql.RowSet;
import javax.xml.stream.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания карт для информационной панели.
 * 
 * @author den
 * 
 */
public final class GeoMapFactory extends AbstractGeoMapFactory {
	private static final String CODE_TAG = "Code";
	private static final String IS_MAIN_TAG = "IsMain";
	private static final String TOOLTIP_COL = "Tooltip";
	private static final String WRONG_LAYER_ERROR =
		"В переданных данных найден объект, ссылающийся на несуществующий слой";
	private static final String NO_IND_VALUES_TABLE_ERROR =
		"Не передана таблица со значениями показателей для объектов на карте";
	private static final String NO_POINTS_TABLE_ERROR = "Не передана таблица с точками для карты";
	private static final String WRONG_OBJ_ERROR =
		"В переданных данных найдено значение показателя, ссылающееся на несуществующий объект";
	private static final String INDICATOR_ID = "IndicatorID";
	private static final String POLYGON_TO_POINT_LAYER_ERROR =
		"В слой типа 'точки' нельзя добавлять области";

	private static final String POINT_TO_POLYGON_LAYER_ERROR =
		"В слой типа 'области' нельзя добавлять точки";

	/**
	 * Таблица с данными о слоях.
	 */
	private RowSet layersSql;

	/**
	 * Таблица с данными о выделенных областях.
	 */
	private RowSet polygonsSql;

	/**
	 * Таблица с данными о точках.
	 */
	private RowSet pointsSql;

	/**
	 * Таблица с данными о показателях для всех слоев.
	 */
	private RowSet indicatorsSql;

	/**
	 * Таблица с данными о значениях показателей.
	 */
	private RowSet indicatorValuesSql;

	public GeoMapFactory(final RecordSetElementRawData aSource) {
		super(aSource);
	}

	@Override
	protected void fillLayers() throws SQLException {

		if (layersSql == null) {
			return;
		}

		while (layersSql.next()) {
			String value = layersSql.getString(OBJECT_TYPE_TAG).toUpperCase().trim();
			GeoMapLayer layer = getData().addLayer(GeoMapFeatureType.valueOf(value));
			layer.setId(layersSql.getString(ID_TAG.toUpperCase()));
			layer.setName(layersSql.getString(NAME_TAG.toUpperCase()));
			if (SQLUtils.existsColumn(layersSql.getMetaData(),
					TextUtils.capitalizeWord(HINT_FORMAT_TAG))) {
				layer.setHintFormat(
						layersSql.getString(TextUtils.capitalizeWord(HINT_FORMAT_TAG)));
			}

		}
	}

	private GeoMapData getData() {
		return getResult().getJavaDynamicData();
	}

	@Override
	protected void fillPolygons() throws SQLException {
		if (polygonsSql == null) {
			return;
		}
		while (polygonsSql.next()) {
			GeoMapLayer layer = getLayerForObject(polygonsSql);
			GeoMapFeature area = layer.addPolygon(polygonsSql.getString(ID_TAG.toUpperCase()),
					polygonsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (area == null) {
				throw new InconsistentSettingsFromDBException(POLYGON_TO_POINT_LAYER_ERROR);
			}
			if (SQLUtils.existsColumn(polygonsSql.getMetaData(), CODE_TAG)) {
				area.setGeometryId(polygonsSql.getString(CODE_TAG));
			}
			if (SQLUtils.existsColumn(polygonsSql.getMetaData(),
					TextUtils.capitalizeWord(COLOR_TAG))) {
				area.setStyle(polygonsSql.getString(TextUtils.capitalizeWord(COLOR_TAG)));
			}
			if (SQLUtils.existsColumn(polygonsSql.getMetaData(),
					TextUtils.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG))) {
				area.setStyleClass(polygonsSql
						.getString(TextUtils.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG)));
			}
			if (SQLUtils.existsColumn(polygonsSql.getMetaData(), TOOLTIP_COL)) {
				String value = polygonsSql.getString(TOOLTIP_COL);
				if (value != null) {
					area.setTooltip(value);
				}
			}
			if (SQLUtils.existsColumn(polygonsSql.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(area.getId(), polygonsSql.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	@Override
	protected void fillPoints() throws SQLException {

		if (pointsSql == null) {
			return;
		}

		while (pointsSql.next()) {
			GeoMapLayer layer = getLayerForObject(pointsSql);
			GeoMapFeature point = layer.addPoint(pointsSql.getString(ID_TAG.toUpperCase()),
					pointsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (point == null) {
				throw new InconsistentSettingsFromDBException(POINT_TO_POLYGON_LAYER_ERROR);
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), LAT_TAG)
					&& SQLUtils.existsColumn(pointsSql.getMetaData(), LON_TAG)) {
				Double[] coords = { pointsSql.getDouble(LON_TAG), pointsSql.getDouble(LAT_TAG) };
				point.setPointCoords(coords);

			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), TOOLTIP_COL)) {
				String value = pointsSql.getString(TOOLTIP_COL);
				if (value != null) {
					point.setTooltip(value);
				}
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(),
					TextUtils.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG))) {
				point.setStyleClass(pointsSql
						.getString(TextUtils.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG)));
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(point.getId(), pointsSql.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	@Override
	protected void fillIndicators() throws SQLException {
		if (indicatorsSql == null) {
			return;
		}
		while (indicatorsSql.next()) {
			GeoMapLayer layer = getLayerForObject(indicatorsSql);
			GeoMapIndicator indicator =
				layer.addIndicator(indicatorsSql.getString(ID_TAG.toUpperCase()),
						indicatorsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (SQLUtils.existsColumn(indicatorsSql.getMetaData(), IS_MAIN_TAG)) {
				indicator.setIsMain(indicatorsSql.getBoolean(IS_MAIN_TAG));
			}
			if (SQLUtils.existsColumn(indicatorsSql.getMetaData(),
					TextUtils.capitalizeWord(COLOR_TAG))) {
				indicator.setStyle(indicatorsSql.getString(TextUtils.capitalizeWord(COLOR_TAG)));
			}
		}
	}

	@Override
	protected void fillIndicatorValues() throws SQLException {
		if (indicatorValuesSql == null) {
			return;
		}
		while (indicatorValuesSql.next()) {
			ID objectId = new ID(indicatorValuesSql.getString(OBJECT_ID_TAG));
			GeoMapLayer layer = getData().getLayerByObjectId(objectId);
			if (layer == null) {
				throw new ResultSetHandleException(WRONG_OBJ_ERROR);
			}
			GeoMapFeature feature = layer.getObjectById(objectId);
			Double value = indicatorValuesSql.getDouble(TextUtils.capitalizeWord(VALUE_TAG));
			String dbId = indicatorValuesSql.getString(INDICATOR_ID);
			feature.setValue(layer.getAttrIdByDBId(dbId).getString(), value);
		}

	}

	private GeoMapLayer getLayerForObject(final RowSet rowset) throws SQLException {
		return getLayerForId(rowset.getString(LAYER_ID_TAG));
	}

	private GeoMapLayer getLayerForId(final String id) {
		ID layerId = new ID(id);
		GeoMapLayer layer = getData().getLayerById(layerId);
		if (layer == null) {
			throw new ResultSetHandleException(WRONG_LAYER_ERROR);
		}
		return layer;
	}

	private void readEvents(final ID objectId, final String value) {
		EventFactory<GeoMapEvent> factory =
			new EventFactory<GeoMapEvent>(GeoMapEvent.class, getCallContext());
		factory.initForGetSimpleSubSetOfEvents(getElementInfo().getType().getPropsSchemaName());
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(objectId, value));
	}

	@Override
	protected void prepareSettings() {
		super.prepareSettings();
		setXmlDS(getSource().getXmlDS());
	}

	@Override
	protected void prepareData() {
		if (getXmlDS() == null) {
			setXmlDS(getSource().getXmlDS());
		}

		prepareDataByNull();

		if (getXmlDS() == null) {
			try {
				switch (ConnectionFactory.getSQLServerType()) {
				case MSSQL:
					prepareDataForMSSQL();
					break;
				case ORACLE:
					prepareDataForOracle();
					break;
				default:
					break;
				}
			} catch (SQLException e) {
				throw new ResultSetHandleException(e);
			}
		}
	}

	private void prepareDataByNull() {
		layersSql = null;
		pointsSql = null;
		polygonsSql = null;
		indicatorsSql = null;
		indicatorValuesSql = null;
	}

	private void prepareDataForMSSQL() throws SQLException {
		ResultSet rs = getSource().nextResultSet();
		if (rs == null) {
			return;
		}

		layersSql = SQLUtils.cacheResultSet(rs);

		rs = getSource().nextResultSet();
		if (rs == null) {
			throw new ResultSetHandleException(NO_POINTS_TABLE_ERROR);
		}
		pointsSql = SQLUtils.cacheResultSet(rs);

		rs = getSource().nextResultSet();
		if (rs == null) {
			return; // разрешаем создавать карту, содержащую только
					// точки -
					// например карту региона.
		}
		polygonsSql = SQLUtils.cacheResultSet(rs);

		rs = getSource().nextResultSet();
		if (rs == null) {
			return; // разрешаем создавать карту без показателей.
		}
		indicatorsSql = SQLUtils.cacheResultSet(rs);

		rs = getSource().nextResultSet();
		if (rs == null) {
			throw new ResultSetHandleException(NO_IND_VALUES_TABLE_ERROR);
		}
		indicatorValuesSql = SQLUtils.cacheResultSet(rs);
	}

	private void prepareDataForOracle() throws SQLException {
		CallableStatement cs = (CallableStatement) getSource().getStatement();
		ResultSet rs =
			(ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_1);
		if (rs == null) {
			return;
		}

		layersSql = SQLUtils.cacheResultSet(rs);

		if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_2)) {
			throw new ResultSetHandleException(NO_POINTS_TABLE_ERROR);
		}
		rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_2);
		pointsSql = SQLUtils.cacheResultSet(rs);

		if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_3)) {
			return; // разрешаем создавать карту, содержащую только
					// точки -
					// например карту региона.
		}
		rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_3);
		polygonsSql = SQLUtils.cacheResultSet(rs);

		if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_4)) {
			return; // разрешаем создавать карту без показателей
		}
		rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_4);
		indicatorsSql = SQLUtils.cacheResultSet(rs);

		if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_5)) {
			throw new ResultSetHandleException(NO_IND_VALUES_TABLE_ERROR);
		}
		rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_5);
		indicatorValuesSql = SQLUtils.cacheResultSet(rs);
	}

	private boolean isCursorOpen(final int index) {
		try {
			((CallableStatement) getSource().getStatement()).getObject(index);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	protected void fillResultByData() {
		if (getXmlDS() == null) {
			try {
				super.fillResultByData();
			} catch (SQLException e) {
				throw new ResultSetHandleException(e);
			}

		} else {
			fillResultByXmlDS();
		}
	}

	private static final String SAX_ERROR_MES = "XML-датасет карты";

	private static final String LAYERS_TAG = "layers";
	private static final String POINTS_TAG = "points";
	private static final String POLYGONS_TAG = "polygons";
	private static final String INDICATORS_TAG = "indicators";
	private static final String INDICATORVALUES_TAG = "indicatorValues";

	/**
	 * Функция заполнения данных на основе XML-датасета.
	 * 
	 */
	private void fillResultByXmlDS() {

		HashMap<String, BaseDSHandler> handlers = new HashMap<String, BaseDSHandler>();
		handlers.put(LAYERS_TAG, new LayersHandler());
		handlers.put(POINTS_TAG, new PointsHandler());
		handlers.put(POLYGONS_TAG, new PolygonsHandler());
		handlers.put(INDICATORS_TAG, new IndicatorsHandler());
		handlers.put(INDICATORVALUES_TAG, new IndicatorValuesHandler());

		XmlDSHandler handler = new XmlDSHandler(handlers);
		SimpleSAX sax = new SimpleSAX(getXmlDS(), handler, SAX_ERROR_MES);
		sax.parse();

		try {
			getXmlDS().close();
			setXmlDS(null);
			getSource().setXmlDS(null);
		} catch (IOException e) {
			throw new SAXError(e);
		}

		boolean layers = handlers.get(LAYERS_TAG).isAvailable();
		boolean indicators = handlers.get(INDICATORS_TAG).isAvailable();
		boolean indicatorValues = handlers.get(INDICATORVALUES_TAG).isAvailable();
		if (layers) {
			if (indicators && !indicatorValues) {
				throw new ResultSetHandleException(NO_IND_VALUES_TABLE_ERROR);
			}
		}

	}

	/**
	 * Формирует карту на основе XML-датасета.
	 */
	private class XmlDSHandler extends DefaultHandler {

		private String currentKey = null;
		private BaseDSHandler currentHandler = null;

		private final HashMap<String, BaseDSHandler> handlers;

		public XmlDSHandler(final HashMap<String, BaseDSHandler> aHandlers) {
			super();

			handlers = aHandlers;
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (currentHandler != null) {
				currentHandler.startElement(uri, localName, name, atts);
				return;
			}

			currentHandler = handlers.get(localName);
			if (currentHandler != null) {
				currentKey = localName;
				return;
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (currentHandler != null) {
				currentHandler.characters(ch, start, length);
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (localName.equals(currentKey)) {
				if ((currentHandler != null) && (currentHandler instanceof IndicatorsHandler)) {
					correctIndicators();
				}

				currentKey = null;
				currentHandler = null;
				return;
			}

			if (currentHandler != null) {
				currentHandler.endElement(uri, localName, name);
				return;
			}
		}
	}

	/**
	 * Базовый класс для обработчиков XML-датасетов.
	 */
	private abstract class BaseDSHandler {

		private boolean available = false;

		public boolean isAvailable() {
			return available;
		}

		protected void setAvailable(final boolean aAvailable) {
			available = aAvailable;
		}

		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
		};

		public void characters(final char[] ch, final int start, final int length) {
		};

		public void endElement(final String uri, final String localName, final String name) {

		};
	}

	/**
	 * Формирует слои.
	 */
	private class LayersHandler extends BaseDSHandler {

		private String layerId;
		private String layerName;
		private String layerObjectType;
		private String layerHintFormat;
		private String value;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				setAvailable(true);

				layerId = null;
				layerName = null;
				layerObjectType = null;
				layerHintFormat = null;
				value = "";
				return;
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			value = value + new String(ch, start, length);
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (ID_TAG.equalsIgnoreCase(localName)) {
				layerId = value.trim();
				value = "";
				return;
			}
			if (NAME_TAG.equalsIgnoreCase(localName)) {
				layerName = value.trim();
				value = "";
				return;
			}
			if (OBJECT_TYPE_TAG.equalsIgnoreCase(localName)) {
				layerObjectType = value.trim();
				value = "";
				return;
			}
			if (HINT_FORMAT_TAG.equalsIgnoreCase(localName)) {
				layerHintFormat = value.trim();
				value = "";
				return;
			}

			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				GeoMapLayer layer = getData()
						.addLayer(GeoMapFeatureType.valueOf(layerObjectType.toUpperCase().trim()));
				layer.setId(layerId);
				layer.setName(layerName);
				if (layerHintFormat != null) {
					layer.setHintFormat(layerHintFormat);
				}
				return;
			}
		}
	}

	/**
	 * Формирует точечные объекты.
	 */
	private class PointsHandler extends BaseDSHandler {

		private String pointId;
		private String pointName;
		private String pointLayerId;
		private String pointLat;
		private String pointLon;
		private String pointTooltip;
		private String pointStyleClass;
		private String pointProperties;
		private ByteArrayOutputStream osProps;
		private XMLStreamWriter writerProps;
		private String value;

		private boolean processProps = false;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				setAvailable(true);

				pointId = null;
				pointName = null;
				pointLayerId = null;
				pointLat = null;
				pointLon = null;
				pointTooltip = null;
				pointStyleClass = null;
				pointProperties = null;
				osProps = null;
				writerProps = null;
				value = "";
				return;
			}

			if (PROPS_TAG.equals(localName)) {
				processProps = true;
				osProps = new ByteArrayOutputStream();
				try {
					writerProps = XMLOutputFactory.newInstance().createXMLStreamWriter(osProps,
							TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (processProps) {
				try {
					writerProps.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerProps.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (processProps) {
				try {
					writerProps.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

			value = value + new String(ch, start, length);
		}

		// CHECKSTYLE:OFF
		@Override
		public void endElement(final String uri, final String localName, final String name) {

			if (processProps) {
				try {
					writerProps.writeEndElement();
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (PROPS_TAG.equals(localName)) {
				try {
					pointProperties = osProps.toString(TextUtils.DEF_ENCODING).trim();
					writerProps.close();
				} catch (UnsupportedEncodingException | XMLStreamException e) {
					throw new SAXError(e);
				}
				processProps = false;
				return;
			}

			if (ID_TAG.equalsIgnoreCase(localName)) {
				pointId = value.trim();
				value = "";
				return;
			}
			if (NAME_TAG.equalsIgnoreCase(localName)) {
				pointName = value.trim();
				value = "";
				return;
			}
			if (LAYER_ID_TAG.equalsIgnoreCase(localName)) {
				pointLayerId = value.trim();
				value = "";
				return;
			}
			if (LAT_TAG.equalsIgnoreCase(localName)) {
				pointLat = value.trim();
				value = "";
				return;
			}
			if (LON_TAG.equalsIgnoreCase(localName)) {
				pointLon = value.trim();
				value = "";
				return;
			}
			if (TOOLTIP_COL.equalsIgnoreCase(localName)) {
				pointTooltip = value.trim();
				value = "";
				return;
			}
			if (GeneralConstants.STYLE_CLASS_TAG.equalsIgnoreCase(localName)) {
				pointStyleClass = value.trim();
				value = "";
				return;
			}

			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				GeoMapLayer layer = getLayerForId(pointLayerId);
				GeoMapFeature point = layer.addPoint(pointId, pointName);
				if (point == null) {
					throw new InconsistentSettingsFromDBException(POINT_TO_POLYGON_LAYER_ERROR);
				}
				if ((pointLat != null) && (pointLon != null)) {
					Double[] coords =
						{ Double.parseDouble(pointLon), Double.parseDouble(pointLat) };
					point.setPointCoords(coords);
				}
				if (pointTooltip != null) {
					point.setTooltip(pointTooltip);
				}
				if (pointStyleClass != null) {
					point.setStyleClass(pointStyleClass);
				}
				if (pointProperties != null) {
					readEvents(point.getId(), pointProperties);
				}

				return;
			}

		}
		// CHECKSTYLE:ON
	}

	/**
	 * Формирует полигоны.
	 */
	private class PolygonsHandler extends BaseDSHandler {

		private String polygonId;
		private String polygonName;
		private String polygonLayerId;
		private String polygonCode;
		private String polygonColor;
		private String polygonTooltip;
		private String polygonStyleClass;
		private String polygonProperties;
		private ByteArrayOutputStream osProps;
		private XMLStreamWriter writerProps;
		private String value;

		private boolean processProps = false;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				setAvailable(true);

				polygonId = null;
				polygonName = null;
				polygonLayerId = null;
				polygonCode = null;
				polygonColor = null;
				polygonTooltip = null;
				polygonStyleClass = null;
				polygonProperties = null;
				osProps = null;
				writerProps = null;
				value = "";
				return;
			}

			if (PROPS_TAG.equals(localName)) {
				processProps = true;
				osProps = new ByteArrayOutputStream();
				try {
					writerProps = XMLOutputFactory.newInstance().createXMLStreamWriter(osProps,
							TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (processProps) {
				try {
					writerProps.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerProps.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (processProps) {
				try {
					writerProps.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

			value = value + new String(ch, start, length);

		}

		// CHECKSTYLE:OFF
		@Override
		public void endElement(final String uri, final String localName, final String name) {

			if (processProps) {
				try {
					writerProps.writeEndElement();
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (PROPS_TAG.equals(localName)) {
				try {
					polygonProperties = osProps.toString(TextUtils.DEF_ENCODING).trim();
					writerProps.close();
				} catch (UnsupportedEncodingException | XMLStreamException e) {
					throw new SAXError(e);
				}
				processProps = false;
				return;
			}

			if (ID_TAG.equalsIgnoreCase(localName)) {
				polygonId = value.trim();
				value = "";
				return;
			}
			if (NAME_TAG.equalsIgnoreCase(localName)) {
				polygonName = value.trim();
				value = "";
				return;
			}
			if (LAYER_ID_TAG.equalsIgnoreCase(localName)) {
				polygonLayerId = value.trim();
				value = "";
				return;
			}
			if (CODE_TAG.equalsIgnoreCase(localName)) {
				polygonCode = value.trim();
				value = "";
				return;
			}
			if (COLOR_TAG.equalsIgnoreCase(localName)) {
				polygonColor = value.trim();
				value = "";
				return;
			}
			if (TOOLTIP_COL.equalsIgnoreCase(localName)) {
				polygonTooltip = value.trim();
				value = "";
				return;
			}
			if (GeneralConstants.STYLE_CLASS_TAG.equalsIgnoreCase(localName)) {
				polygonStyleClass = value.trim();
				value = "";
				return;
			}

			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				GeoMapLayer layer = getLayerForId(polygonLayerId);
				GeoMapFeature polygon = layer.addPolygon(polygonId, polygonName);
				if (polygon == null) {
					throw new InconsistentSettingsFromDBException(POLYGON_TO_POINT_LAYER_ERROR);
				}
				if (polygonCode != null) {
					polygon.setGeometryId(polygonCode);
				}
				if (polygonColor != null) {
					polygon.setStyle(polygonColor);
				}
				if (polygonStyleClass != null) {
					polygon.setStyleClass(polygonStyleClass);
				}
				if (polygonTooltip != null) {
					polygon.setTooltip(polygonTooltip);
				}
				if (polygonProperties != null) {
					readEvents(polygon.getId(), polygonProperties);
				}

				return;
			}

		}
		// CHECKSTYLE:ON
	}

	/**
	 * Формирует показатели.
	 */
	private class IndicatorsHandler extends BaseDSHandler {

		private String indicatorId;
		private String indicatorName;
		private String indicatorLayerId;
		private String indicatorIsMain;
		private String indicatorColor;
		private String value;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				setAvailable(true);

				indicatorId = null;
				indicatorName = null;
				indicatorLayerId = null;
				indicatorIsMain = null;
				indicatorColor = null;
				value = "";
				return;
			}

		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			value = value + new String(ch, start, length);
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (ID_TAG.equalsIgnoreCase(localName)) {
				indicatorId = value.trim();
				value = "";
				return;
			}
			if (NAME_TAG.equalsIgnoreCase(localName)) {
				indicatorName = value.trim();
				value = "";
				return;
			}
			if (LAYER_ID_TAG.equalsIgnoreCase(localName)) {
				indicatorLayerId = value.trim();
				value = "";
				return;
			}
			if (IS_MAIN_TAG.equalsIgnoreCase(localName)) {
				indicatorIsMain = value.trim();
				value = "";
				return;
			}
			if (COLOR_TAG.equalsIgnoreCase(localName)) {
				indicatorColor = value.trim();
				value = "";
				return;
			}

			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				GeoMapLayer layer = getLayerForId(indicatorLayerId);
				GeoMapIndicator indicator = layer.addIndicator(indicatorId, indicatorName);
				if (indicatorIsMain != null) {
					indicator.setIsMain(TextUtils.stringToBoolean(indicatorIsMain));
				}
				if (indicatorColor != null) {
					indicator.setStyle(indicatorColor);
				}

				return;
			}

		}
	}

	/**
	 * Формирует значения показателей.
	 */
	private class IndicatorValuesHandler extends BaseDSHandler {

		private String indicatorId;
		private String objectId;
		private String volume;

		private String value;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {

			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				setAvailable(true);

				indicatorId = null;
				objectId = null;
				volume = null;
				value = "";
				return;
			}

		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			value = value + new String(ch, start, length);
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (INDICATOR_ID.equalsIgnoreCase(localName)) {
				indicatorId = value.trim();
				value = "";
				return;
			}
			if (OBJECT_ID_TAG.equalsIgnoreCase(localName)) {
				objectId = value.trim();
				value = "";
				return;
			}
			if (VALUE_TAG.equalsIgnoreCase(localName)) {
				volume = value.trim();
				value = "";
				return;
			}

			if (RECORD_TAG.equalsIgnoreCase(localName)) {
				ID objId = new ID(objectId);
				GeoMapLayer layer = getData().getLayerByObjectId(objId);
				if (layer == null) {
					throw new ResultSetHandleException(WRONG_OBJ_ERROR);
				}
				GeoMapFeature feature = layer.getObjectById(objId);
				feature.setValue(layer.getAttrIdByDBId(indicatorId).getString(),
						Double.parseDouble(volume));

				return;
			}

		}
	}

}
