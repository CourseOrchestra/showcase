dojo.provide("djeo.djeo.Engine");

dojo.require("djeo.Engine");
dojo.require("dojox.gfx");
dojo.require("djeo.djeo.Placemark");

(function(){

var gfx = dojox.gfx,
	matrix = gfx.matrix;

dojo.declare("djeo.djeo.Engine", djeo.Engine, {
	
	type: "djeo",
	
	correctionScale: 1000,
	
	pointZoomFactor: 0.01,
	
	resizePoints: true,
	resizeLines: true,
	resizePolygons: true,
	
	correctScale: false,
	
	constructor: function(kwArgs) {
		// initialize basic factories
		this._initBasicFactories(new djeo.djeo.Placemark({
			map: this.map,
			engine: this
		}));
	},
	
	initialize: function(/* Function */readyFunction) {
		var map = this.map;
		this.surface = gfx.createSurface(map.container, map.width, map.height);
		this.group = this.surface.createGroup();
		
		if (map.resizePoints !== undefined) this.resizePoints = map.resizePoints;
		if (map.resizeLines !== undefined) this.resizeLines = map.resizeLines;
		if (map.resizePolygons !== undefined) this.resizePolygons = map.resizePolygons;
		
		if (map.resizePoints !== undefined) this.resizePoints = map.resizePoints;
		if (map.resizeLines !== undefined) this.resizeLines = map.resizeLines;
		if (map.resizePolygons !== undefined) this.resizePolygons = map.resizePolygons;

		this.initialized = true;
		readyFunction();
	},
	
	createContainer: function(parentContainer, featureType) {
		// parentContainer is actually parent group
		return parentContainer.createGroup();
	},

	prepare: function() {
		//transform the map to fit container
		var mapExtent = this.map.extent,
			mapWidth = mapExtent[2] - mapExtent[0],
			mapHeight = mapExtent[3] - mapExtent[1];

		// check if we need to apply a corrective scaling
		if (mapWidth<1000 || mapHeight<1000) this.correctScale = true;

		this.factories.Placemark.init();
		this.zoomTo(mapExtent);
		this.factories.Placemark.prepare();
	},
	
	getTopContainer: function() {
		return this.group;
	},
	
	connect: function(feature, event, context, method) {
		var connections = [];
		// normalize the callback function
		method = this.normalizeCallback(feature, event, context, method);
		dojo.forEach(feature.baseShapes, function(shape){
			connections.push([shape, shape.connect(event, method)]);
		});
		return connections;
	},
	
	disconnect: function(connections) {
		dojo.forEach(connections, function(connection){
			connection[0].disconnect(connection[1]);
		});
	},
	
	destroy: function() {
		this.surface.destroy();
	},
	
	enableLayer: function(layerId, enabled) {
		// for the moment the function does nothing
	},
	
	zoomTo: function(/* Array */extent) {
		// extent is array [x1,y1,x2,y2]
		var map = this.map,
			extentWidth = extent[2] - extent[0],
			extentHeight = extent[3] - extent[1],
			oldScale = (this.group.getTransform()||{xx:1}).xx,
			scale = Math.min(map.width/extentWidth, map.height/extentHeight),
			pf = this.factories.Placemark,
			x1 = pf.getX(extent[0]),
			y2 = pf.getY(extent[3]);
		
		if (extentWidth==0 && extentHeight==0) {
			// we've got a point
			var mapExtent = map.extent,
				mapWidth = mapExtent[2] - mapExtent[0],
				mapHeight = mapExtent[3] - mapExtent[1],
				extentSize = this.pointZoomFactor * Math.min(mapWidth, mapHeight);

			if (map.width > map.height) {
				extentHeight = extentSize;
				extentWidth = map.width/map.height*extentSize;
			}
			else {
				extentWidth = extentSize;
				extentHeight = map.height/map.width*extentSize;
			}

			scale = map.width/extentWidth; // equal to map.height/extentHeight
			x1 = pf.getX(extent[0] - extentWidth/2);
			y2 = pf.getY(extent[3] + extentHeight/2);
		}
		
		if (this.correctScale) scale /= this.correctionScale;

		this.group.setTransform([
			matrix.scale(scale),
			matrix.translate(-x1, -y2)
		]);
		
		if (oldScale != scale) {
			pf.calculateLengthDenominator();
			this.resizeFeatures(this.map.document, oldScale/scale);
		}
	},
	
	resizeFeatures: function(featureContainer, scaleFactor) {
		dojo.forEach(featureContainer.features, function(feature){
			if (feature.isPlacemark) this._resizePlacemark(feature, scaleFactor);
			else if (feature.isContainer) this.resizeFeatures(feature, scaleFactor);
		}, this);
	},
	
	_resizePlacemark: function(feature, scaleFactor) {
		if (feature.invalid) return;
		var type = feature.getCoordsType();

		if (this.resizePoints && type == "Point") {
			dojo.forEach(feature.baseShapes, function(shape){
				shape.applyRightTransform(matrix.scale(scaleFactor));
			});
		}
		else if ( gfx.renderer!="vml" && (
				(this.resizeLines && (type == "LineString" || type == "MultiLineString")) ||
				(this.resizePolygons && (type == "Polygon" || type == "MultiPolygon")) )) {
			dojo.forEach(feature.baseShapes, function(shape){
				var stroke = shape.getStroke();
				if (stroke) {
					stroke.width *= scaleFactor;
					shape.setStroke(stroke);
				}
			});
		}
		
		if (feature.textShapes) {
			var factory = this.map.engine.factories.Placemark,
				center = djeo.util.center(feature),
				x = factory.getX(center[0]),
				y = factory.getY(center[1]);
			dojo.forEach(feature.textShapes, function(t){
				t.applyRightTransform(matrix.scaleAt(scaleFactor, x, y));
			});
		}
	}
});

}());