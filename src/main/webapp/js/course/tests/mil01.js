define([
	"djeo/Map",
	"djeo/control/Navigation",
	"djeo/control/Highlight",
	"djeo/control/Tooltip",
	"mil/demos/style",
	"djeo/Circle",
	"mil/Sector",
	"dojo/domReady!"
	
], function(Map, Navigation, Highlight, Tooltip, style){

return function(mapNode, legendNode, data) {

	data.extent = [33,43,45,45];
	data.layers = "webtiles:js/mil/tiles";
	data.style = style;
	data.iconBasePath = "js/mil/demos/"
	
	var map = new Map(mapNode, data);

	map.ready(function(){
		new Navigation(map);
		//new Highlight(map);
		var tooltipControl = new Tooltip(map);
		map.document.on("click", function(event){
			tooltipControl.hideTooltip();
			gwtMapFunc(mapNode, event.feature.id)
		});
	});
	
	return {map: map};
};
	
});