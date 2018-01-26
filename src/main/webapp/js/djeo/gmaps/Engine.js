dojo.provide("djeo.gmaps.Engine");

dojo.require("djeo.Engine");
dojo.require("djeo.gmaps.Placemark");

dojo.require("dojo.io.script");

(function(){

var g = djeo,
	u = g.util,
	gm = g.gmaps,
	GM = window.google && google.maps;

var engineEvents = {onmouseover: "mouseover", onmouseout: "mouseout", onclick: "click"};

var supportedLayers = {
	ROADMAP: 1,
	SATELLITE: 1,
	HYBRID: 1,
	TERRAIN: 1
};

dojo.declare("djeo.gmaps.Engine", djeo.Engine, {
	
	type: "gmaps",
	
	gmap: null,

	constructor: function(kwArgs) {
		// initialize basic factories
		this._initBasicFactories(new gm.Placemark({
			map: this.map
		}));
	},
	
	initialize: function(/* Function */readyFunction) {
		if (GM) {
			// the first case: Google Maps API is completely loaded
			this.map.projection = "EPSG:4326";
			var gmap = new GM.Map(dojo.byId(this.map.container), {
				zoom: 0,
				center: new GM.LatLng(0, 0),
				disableDefaultUI: true,
				disableDoubleClickZoom: true,
				draggable: false,
				scrollwheel: false
			});
			this.gmap = gmap;
			
			this.initialized = true;
			readyFunction();
		}
		else if (window._djeoGmapsInitialized) {
			// the second case: Google Maps API is being loaded
			// wait till window._djeoGmapsInitialized is called
			dojo.connect(window._djeoGmapsInitialized, dojo.hitch(this, function(){
				this.initialize(readyFunction);
			}));
		}
		else {
			window._djeoGmapsInitialized = dojo.hitch(this, function(){
				// delete window._djeoGmapsInitialized does't work in IE8
				window._djeoGmapsInitialized = null;
				gm.Placemark.init();
				GM = google.maps;
				this.initialize(readyFunction);
			});
			dojo.io.script.get({
				url: "http://maps.google.com/maps/api/js",
				content: {
					sensor: false,
					callback: "_djeoGmapsInitialized"
				}
			});
		}
	},
	
	_initialize: function() {
		
	},

	createContainer: function(feature) {
		var container = this.ge.createFolder('');
		this.appendChild(container, feature);
		return container;
	},
	
	prepare: function() {
		this.zoomTo( this.map.extent );
	},
	
	appendChild: function(child, feature) {
		child.setMap(feature.map.engine.gmap);
	},
	
	getTopContainer: function() {
		var features = this.ge.getFeatures();
		return this.ge;
	},
	
	connect: function(feature, event, context, method) {
		var connections = [];
		// normalize the callback function
		method = this.normalizeCallback(feature, event, context, method);
		event = engineEvents[event];
		dojo.forEach(feature.baseShapes, function(shape){
			connections.push( GM.event.addListener(shape, event, method) );
		});
		return connections;
	},
	
	disconnect: function(connections) {
		dojo.forEach(connections, function(connection){
			GM.event.removeListener(connection);
		});
	},
	
	zoomTo: function(extent) {
		var gBounds = new GM.LatLngBounds( new GM.LatLng(extent[1],extent[0]), new GM.LatLng(extent[3],extent[2]) );
		this.gmap.fitBounds(gBounds);
	},
	
	destroy: function() {
		
	},
	
	enableLayer: function(layerId, enabled) {
		if (enabled && supportedLayers[layerId]) this.gmap.setMapTypeId( GM.MapTypeId[layerId] );
	}
});

}());