define([
	"dojo/_base/lang", // getObject, isString, mixin
	"dojo/_base/array", // forEach
	"dojo/dom", // byId
	"dojo/json"
], function(lang, array, dom, json) {
	
var defaultManagerModule = "course/demo2",
	defaultManagerFunction = "make",
	defaultMapEngine = "djeo"
;

var g =  lang.getObject("course.geo", true);

var mapRegistry = {};

function convertData(data) {

	if (lang.isString(data))
		features = json.parse(data);

	if (data.layers) {
		var layers = data.layers;
		data.features = layers;
		delete data.layers;

		array.forEach(layers, function(layer) {
			delete layer.type;
			if (layer.features)
				array.forEach(layer.features, function(feature) {
					var coords = undefined;
					var type = undefined;
					if (feature.pointCoords) {
						coords = feature.pointCoords;
						type = "Point";
					} else if (feature.polygonCoords) {
						coords = feature.polygonCoords;
						type = "Polygon";
					} else if (feature.multiPolygonCoordinates) {
						coords = feature.multiPolygonCoords;
						type = "MultiPolygon";
					}
					// clean feature object
					delete feature.pointCoords, feature.polygonCoords,
							feature.multiPolygonCoords;
					if (coords) {
						feature.coords = coords;
						feature.type = type;
					}
					// process style
					var style = feature.style;
					if (style && lang.isString(style)) {
						if (style.charAt(0) == "#")
							feature.style = {
								fill : style
							};
					}
				});
		});
	}
}

g.makeMap = function(mapDivId, mapLegendId, data, options) {
	if (lang.isString(data)) {
		data = json.parse(data);
	}
	if (lang.isString(options)) {
		options = dojo.fromJson(options);
	}
	if (!options) options = {};

	convertData(data);
	// append features from options to data.features
	if (options.features) {
		array.forEach(options.features, function(f){
			data.features.push(f);
		});
		delete options.features;
	}

	var mapNode = dom.byId(mapDivId);
	if (!mapNode) return;

	g.destroyMap(mapDivId);

	var managerModule = options.managerModule ? options.managerModule : defaultManagerModule;
	
	require([managerModule], function(managerFunction) {
		// clean up options
		delete options.managerModule, options.managerFunction, options.registerModules;

		lang.mixin(data, options);

		// patch data
		if (!data.engine) {
			data.engine = defaultMapEngine;
		}
		data.useAttrs = true;
		mapRegistry[mapDivId] = managerFunction(mapDivId, mapLegendId, data);
	});
};

g.destroyMap = function(id) {
	if (mapRegistry[id]) {
		mapRegistry[id].map.destroy();
		if (mapRegistry[id].legend)
			mapRegistry[id].legend.destroy(true);
		delete mapRegistry[id];
	}
};

g.toSvg = function(mapId, exportType, gwtSuccessCallback, gwtFailureCallback) {
	if (lang.isString(mapId))
		map = mapRegistry[mapId].map;
	if (!map)
		return;
	if (map.engine.type != "djeo") {
		window.alert('Данная карта имеет подложку типа "' + map.engine.type + '". Для любых внешних подложек (Яндекс, Google) экспорт невозможен.');
		return;
	}

	dojo.require("dojox.gfx.utils");

	if (!gwtSuccessCallback) {
		successCallback = function(svg) {
			var svgWindow = window.open("", "svg");
			svgWindow.document.write(svg);
		};
	} else {
		successCallback = function(svg) {
			gwtSuccessCallback(mapId, exportType, svg);
		};
	}
	if (!gwtFailureCallback)
		failureCallback = function(error) {
			console.log(error);
		};
	else {
		failureCallback = function(error) {
			gwtFailureCallback(mapId, error);
		};
	}

	var svgDeferred = dojox.gfx.utils.toSvg(map.engine.surface);
	svgDeferred.addCallback(successCallback);
	svgDeferred.addErrback(failureCallback);
};

g.exposeFunctions = function(numeric, jenks, colorbrewer, Legend) {
	g.colorbrewer = {};
	course.geo.composeStyle = numeric.composeStyle;
	course.geo.composeSizeStyle = numeric.composeSizeStyle;
	course.geo.getBreaks = jenks.getBreaks;
	course.geo.colorbrewer.composeStyle = colorbrewer.composeStyle;
	course.geo.getBreaksAreaLegend = Legend._getBreaksAreaLegend;
	course.geo.getBreaksIconLegend = Legend._getBreaksIconLegend;
}

});