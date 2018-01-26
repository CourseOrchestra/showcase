dojo.provide("solution.test");

dojo.require("djeo.Map");

//dojo.registerModulePath("", "");

dojo.require("djeo.control.Navigation");
dojo.require("djeo.control.Highlight");
dojo.require("djeo.control.Tooltip");
dojo.require("djeo.widget.Legend");

//dojo.require("kurs.data.russia_geometriesEpsg4326");
dojo.require("courseApp.data.geo.russia_geometries");

dojo.require("djeo.util.numeric");
dojo.require("djeo.util.colorbrewer");
dojo.require("djeo.util.jenks");

dojo.require("djeo.util.proj4js");
dojo.require("djeo.util.proj4js.aea");
dojo.require("djeo.projection");
djeo.util.proj4js.addDef("RUSSIA-ALBERS", "+proj=aea +lat_1=52 +lat_2=64 +lat_0=0 +lon_0=105 +x_0=18500000 +y_0=0 +ellps=krass +units=m +towgs84=28,-130,-95,0,0,0,0 +no_defs");

(function() {

	solution.test.make = function(mapNode, legendNode, data) {
	var map;
	var legend = null;
	var mapStyle = {
		id: "populationDensity",
		//styleClass: "populationDensity",
		fid: "l2",
		stroke: "black",
		strokeWidth: 1,
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 7,
				colorSchemeName: "Reds",
				attr: "mainInd",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: "djeo.util.colorbrewer.calculateStyle"
			}
		},
		legend: "djeo._getBreaksAreaLegend"
	};

	dojo.mixin(data, {
		projection: "RUSSIA-ALBERS"
	});
	
	if (!data.geometries) data.geometries = courseApp.data.geo.russia_geometries;
	if (!data.style) data.style = mapStyle;
	
//	Supported mapping engines
//	djeo native mapping engine (default): data.engine = "djeo"
//	Google Maps API: data.engine = "gmaps"
//	Google Earth API: - data.engine = "ge"
//	Yandex Maps API: data.engine = "ymaps"	
	data.engine = "djeo";
	
	map = new djeo.Map(mapNode, data);

	map.ready(function(){
		new djeo.control.Navigation(map);
		new djeo.control.Highlight(map);
		new djeo.control.Tooltip(map);
		if (legendNode) legend = new djeo.widget.Legend({map: map}, legendNode);
	});
	
	return {map: map, legend: legend};
};

}());