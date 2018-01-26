dojo.provide("course.geo");

dojo.require("djeo.Map");

(function() {
	var defaultManagerModule = "course.demo", defaultManagerFunction = "make", defaultMapEngine = "gfx";

	var g = course.geo;

	g.mapRegistry = {};

	function convertData(data) {

		if (dojo.isString(data))
			features = dojo.fromJson(data);

		if (data.layers) {
			var layers = data.layers;
			data.features = layers;
			delete data.layers;

			dojo.forEach(layers, function(layer) {
				delete layer.type;
				if (layer.features)
					dojo.forEach(layer.features, function(feature) {
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
						if (style && dojo.isString(style)) {
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
		if (dojo.isString(data))
			data = dojo.fromJson(data);
		if (dojo.isString(options))
			options = dojo.fromJson(options);
		convertData(data);

		var mapNode = dojo.byId(mapDivId);
		if (!mapNode)
			return;

		g.destroyMap(mapDivId);

		if (!options)
			options = {};

		// register dojo modules
		if (options.registerModules) {
			dojo.forEach(options.registerModules, function(module) {
				dojo.registerModulePath(module[0], module[1]);
			});
		}

		var managerModule = options.managerModule ? options.managerModule
				: defaultManagerModule;
		dojo.require(managerModule);
		managerModule = dojo.getObject(managerModule);
		var managerFunction = options.managerFunction ? options.managerFunction
				: defaultManagerFunction;
		if (managerFunction) {
			// clean up options
			delete options.managerModule, options.managerFunction,
					options.registerModules;
			dojo.mixin(data, options);
			// patch data
			if (!data.mapEngine)
				data.mapEngine = defaultMapEngine;
			data.useAttrs = true;
			g.mapRegistry[mapDivId] = managerModule[managerFunction](mapDivId, mapLegendId, data);
		}
	};

	g.destroyMap = function(id) {
		if (g.mapRegistry[id]) {
			g.mapRegistry[id].map.destroy();
			if (g.mapRegistry[id].legend)
				g.mapRegistry[id].legend.destroy(true);
			delete g.mapRegistry[id];
		}
	};

	g.toSvg = function(mapId, exportType, gwtSuccessCallback, gwtFailureCallback) {
		if (dojo.isString(mapId))
			map = g.mapRegistry[mapId].map;
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

})();