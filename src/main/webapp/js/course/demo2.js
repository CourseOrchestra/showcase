define([
	"djeo/Map",
	"djeo/control/Navigation",
	"djeo/control/Highlight",
	"djeo/control/Tooltip",
	"djeo/widget/Legend",
	
	"djeo/util/numeric",
	"djeo-colorbrewer/main",
	"djeo-colorbrewer/data",
	"djeo-jenks/main",
	
	"courseApp/data/geo/russia_geometries",
	
	"djeo-proj4js/main",
	"djeo-proj4js/aea"
	
], function(Map, Navigation, Highlight, Tooltip, Legend, numeric, colorbrewer, colorbrewerData, jenks,
			russiaGeometries, proj4js){
	
course.geo.exposeFunctions(numeric, jenks, colorbrewer, Legend);

proj4js.addDef("RUSSIA-ALBERS", "+proj=aea +lat_1=52 +lat_2=64 +lat_0=0 +lon_0=105 +x_0=18500000 +y_0=0 +ellps=krass +units=m +towgs84=28,-130,-95,0,0,0,0 +no_defs");

var mapStyle = {
	//styleClass: "populationDensity",
	fid: "l2",
	stroke: "black",
	strokeWidth: 1,
	composer: numeric.composeStyle,
	composerOptions: {
		numClasses: 7,
		colorSchemeName: "Reds",
		attr: "mainInd",
		breaks: jenks.getBreaks,
		composeStyle: colorbrewer.composeStyle
	},
	legend: Legend._getBreaksAreaLegend
};

return function(mapNode, legendNode, data) {

	var map,
		legend
	;

	data.projection = "RUSSIA-ALBERS";
	
	if (!data.geometries) data.geometries = russiaGeometries;
	if (!data.style) data.style = mapStyle;
	
	map = new Map(mapNode, data);

	map.ready(function(){
		new Navigation(map);
		new Highlight(map);
		new Tooltip(map);		
		if (legendNode) legend = new Legend({map: map}, legendNode);
	});
	
	return {map: map, legend: legend};
};
	
});

//dojo.require("kurs.data.russia_geometriesEpsg4326");
//dojo.require("courseApp.data.geo.russia_geometries");