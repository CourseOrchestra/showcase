dojo.provide("solution.world_demo");

dojo.require("djeo.Map");

//dojo.registerModulePath("", "");

dojo.require("djeo.control.Navigation");
dojo.require("djeo.control.Highlight");
dojo.require("djeo.control.Tooltip");
dojo.require("djeo.widget.Legend");

dojo.require("solution.world_geometries");

dojo.require("djeo.util.numeric");
dojo.require("djeo.util.colorbrewer");
dojo.require("djeo.util.jenks");


(function() {

solution.world_demo.make = function(mapNode, legendNode, data) {
	var map;
	var legend = null;
	
	data.geometries = solution.world_geometries;

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