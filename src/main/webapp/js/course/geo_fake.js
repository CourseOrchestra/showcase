dojo.provide("course.geo");

(function() {
	var mes = 'geo module disabled!';
	var g = course.geo;
	
	g.makeMap = function(mapDivId, mapLegendId, data, options) {
		console.log(mes);
		throw mes;
	};

	g.destroyMap = function(id) {
	};

	g.toSvg = function(mapId, exportType, gwtSuccessCallback, gwtFailureCallback) {
		console.log(mes);
		throw mes;
	};

})();