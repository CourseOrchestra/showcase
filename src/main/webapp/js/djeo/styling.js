dojo.provide("djeo.styling");

dojo.require("dojo.colors");

(function() {
	
var g = djeo,
	s = g.styling;
	
var symbolizers = ["points", "lines"],
	styleAttributes = {theme:1, name: 1, legend: 1},
	noStyleMixin = {id:1, filter:1, styleClass:1, fid:1,  styleFunction:1};

dojo.declare("djeo.Style", null, {
	
	// json style definition
	def: null,
	
	// features that have this style in their style attribute (i.e. inline style)
	_features: null,

	constructor: function(def, map) {
		this.map = map;
		this._features = {};
		this._setDef(def);
		if (def.id) map.styleById[def.id] = this;
	},
	
	set: function(def) {
		var m = this.map;
		var affectedFeatures = this.styleClass || this.fid ? {} : this._features;
		// remove the style from styleByClassAndFid, styleByFid, styleByClass
		if (this.styleClass) {
			dojo.forEach(this.styleClass, function(_class){
				// remove the style from styleByClassAndFid
				if (this.fid) {
					dojo.forEach(this.fid, function(_fid){
						var entry = m.styleByClassAndFid[_class][_fid],
							entryLength = entry.length;
						for(var i=0; i<entryLength;i++) {
							if (entry[i]==this) break;
						}
						if (i<entryLength) {
							entry.splice(i,1);
							var feature = m.getFeatureById(_fid);
							if (feature && feature.styleClass) {
								for (var i=0; i<feature.styleClass.length; i++) {
									if (_class==feature.styleClass[i] && !affectedFeatures[feature.id]) {
										affectedFeatures[feature.id] = feature;
										break;
									}
								}
							}
						}
					}, this);
				}
				else {
					// remove the style from styleByClass
					var entry = m.styleByClass[_class],
						entryLength = entry.length;
					for(var i=0; i<entryLength;i++) {
						if (entry[i]==this) break;
					}
					if (i<entryLength) {
						entry.splice(i,1);
						var features = m.featuresByClass[_class];
						if (features) dojo.forEach(features, function(f){
							if (!affectedFeatures[f.id]) affectedFeatures[f.id] = f;
						});
					}
				}
			}, this);
		}
		else if (this.fid) {
			// remove the style from styleByFid
			dojo.forEach(this.fid, function(_fid){
				var entry = m.styleByFid[_fid],
					entryLength = entry.length;
				for(var i=0; i<entryLength;i++) {
					if (entry[i]==this) break;
				}
				if (i<entryLength) {
					entry.splice(i,1);
					var feature = m.getFeatureById(_fid);
					if (feature && !affectedFeatures[feature.id]) affectedFeatures[feature.id] = feature;
				}
			}, this);
		}
		
		delete this.styleClass, this.fid, this.filter;

		this._setDef(def);
		// apply the style to the the affected features
		this.map.renderFeatures(affectedFeatures, true, "normal");
	},
	
	_setDef: function(def) {
		var m = this.map;
		// mixin style attributes
		for (attr in def) {
			if (attr in styleAttributes) this[attr] = def[attr];
		}

		// prepare filter function
		var filter = def.filter;
		if (filter) this.filter = dojo.isString(filter) ? eval("_=function(){return "+filter+";}" ) : filter;

		// prepare styleClass and fid
		var styleClass = def.styleClass;
		var fid = def.fid;
		if (fid && !dojo.isArray(fid)) fid = [fid];
		if (styleClass) {
			if (!dojo.isArray(styleClass)) styleClass = [styleClass];
			dojo.forEach(styleClass, function(_class){
				// styleClass and fid simultaneously
				if (fid) {
					var byClassAndFid = m.styleByClassAndFid;
					dojo.forEach(fid, function(_fid){
						if (!byClassAndFid[_class]) byClassAndFid[_class] = {};
						if (!byClassAndFid[_class][_fid]) byClassAndFid[_class][_fid] = [];
						byClassAndFid[_class][_fid].push(this);
					}, this);
				}
				// styleClass only
				else {
					if (!m.styleByClass[_class]) m.styleByClass[_class] = [];
					m.styleByClass[_class].push(this);
				}
			}, this);
		}
		else if (fid) {
			// fid only
			dojo.forEach(fid, function(_fid){
				if (!m.styleByFid[_fid]) m.styleByFid[_fid] = [];
				m.styleByFid[_fid].push(this);
			}, this);
		}
		if (styleClass) this.styleClass = styleClass;
		if (fid) this.fid = fid;

		// preare styleFunction
		var styleFunction = def.styleFunction;
		if (styleFunction) {
			this.styleFunction = {
				// features may need this attribute to be aware that the style has changed
				updated: (new Date()).getTime()
			};
			var getStyle = styleFunction.getStyle;
			getStyle = dojo.isString(getStyle) ? dojo.getObject(getStyle) : getStyle;
			for(var attr in styleFunction) {
				this.styleFunction[attr] = (attr == "getStyle") ? getStyle : styleFunction[attr];
			}
		}

		// copy actual style attribute (e.g. color) to a separate javascript object
		this.def = {};
		for (attr in def) {
			if (!(attr in styleAttributes || attr in noStyleMixin)) this.def[attr] = def[attr];
		}
	},
	
	destroy: function() {
		delete this.def, this.filter, this.map, this._features, this.styleFunction;
	}
});

s.calculateStyle = function(feature, theme) {
	var styles = [];
	// find all features participating in the style calculation
	// features = feature itself + all its parents
	var features = [],
		parent = feature;
	do {
		features.push(parent);
		parent = parent.parent;
	}
	while (parent != feature.map)
	
	for (var i=features.length-1; i>=0; i--) {
		appendFeatureStyle(features[i], styles, theme);
	}
	
	// now do actual style calculation
	var resultStyle = {};
	dojo.forEach(styles, function(style) {
		var applyStyle = style.filter ? evaluateFilter(style.filter, feature) : true;
		if (applyStyle) {
			styleMixin(resultStyle, style.def);
			if (style.styleFunction) style.styleFunction.getStyle(feature, resultStyle, style.styleFunction);
		}
	});
	
	// final adjustments
	dojo.forEach(symbolizers, function(styleType){
		// ensure that specific style is defined as array
		var s = resultStyle[styleType];
		if (s && !dojo.isArray(s)) resultStyle[styleType] = [s];
	})
	return resultStyle;
}

var evaluateFilter = function(filter, feature) {
	return filter.call(feature.map.useAttrs ? feature.attrs : feature);
}

var appendFeatureStyle = function(feature, styles, theme) {
	// TODO: duplication of styleClass
	var m = feature.map;
		styleClass = feature.styleClass,
		fid = feature.id;
	if (styleClass) {
		dojo.forEach(styleClass, function(_styleClass){
			var byClassAndFid = m.styleByClassAndFid;
			if (byClassAndFid[_styleClass] && byClassAndFid[_styleClass][fid]) {
				append(byClassAndFid[_styleClass][fid], styles, theme);
			}
			else if (m.styleByClass[_styleClass]) append(m.styleByClass[_styleClass], styles, theme);
		});
	}
	else if (m.styleByFid[fid]) append(m.styleByFid[fid], styles, theme);

	if (feature.style) append(feature.style, styles, theme);
}

var append = function(/*Array*/what, /*Array*/to, theme) {
	dojo.forEach(what, function(element){
		if ( ( (!element.theme||element.theme=="normal") && (!theme||theme=="normal") ) || (element.theme==theme) ) {
			if (element.def.reset) {
				// clear all entries in the destination
				for(var i=to.length-1; i>=0; i--) to.pop();
			}
			to.push(element);
		}
	});
}

var styleMixin = function(styleDef, styleAttrs) {
	for (attr in styleAttrs) {
		if (attr=="shape" && styleDef.img) {
			// we've got a vector icon, override the currently set bitmap icon
			delete styleDef.img;
		}
		else if (attr=="img" && styleDef.shape) {
			// we've got a vector icon, override the currently set bitmap icon
			delete styleDef.shape;
		}
		styleDef[attr] = styleAttrs[attr];
	}
	return styleDef;
}

s.style = [
	{
		stroke: "black",
		strokeWidth: 0.5,
		strokeOpacity: 1,
		fill: "#B7B7B7",
		fillOpacity: 0.8,
		shape: "square", // circle, square, triangle, star, cross, or x
		size: 10
	},
	{
		theme: "highlight",
		stroke: "orange",
		polygon: {strokeWidth: 3},
		rScale: 1.5
	}
]

}())