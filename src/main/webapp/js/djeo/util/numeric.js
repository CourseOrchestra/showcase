dojo.provide("djeo.util.numeric");

dojo.require("djeo.common.Placemark");

(function(){

var n = djeo.util.numeric;

n.composeArray = function(featureContainer, attr, performSort, ascendingSort) {
	var values = [];
	dojo.forEach(featureContainer.features, function(feature){
		var value = feature.get(attr);
		if (!isNaN(value)) values.push(value);
	});
	if (performSort) {
		ascendingSort = ascendingSort ? function(a, b) {return (a - b);} : null;
		values.sort(ascendingSort);
	}
	return values;
};

n.getBreakIndex = function(breaks, numClasses, value) {
	for (var i=0; i<numClasses; i++) {
		if (i==0) {
			if (breaks[0]<=value && value<=breaks[1]) break;
		}
		else if (breaks[i]<value && value<=breaks[i+1]) break;
	}
	// the value doesn't belong to the given breaks
	return (i<numClasses) ? i : -1;
};

n.getStyle = function(feature, style, styleFunctionDef) {
	var kwArgs = styleFunctionDef.options,
		attrValue = feature.get(kwArgs.attr);
	
	if (attrValue === undefined) return;
	
	var calculateStyle = dojo.isString(kwArgs.calculateStyle) ? dojo.getObject(kwArgs.calculateStyle) : kwArgs.calculateStyle,
		breaks, numClasses;

	if (dojo.isArray(kwArgs.breaks)) {
		breaks = kwArgs.breaks;
		numClasses = breaks.length - 1;
	}
	else {
		var getBreaks = dojo.isString(kwArgs.breaks) ? dojo.getObject(kwArgs.breaks) : kwArgs.breaks,
			featureContainer = feature.parent;
		breaks = featureContainer._breaks;
		if (!breaks || styleFunctionDef.updated > featureContainer._breaksTimestamp) {
			breaks = getBreaks(featureContainer, styleFunctionDef.options);
			
			// store calculated breaks for the use by other features that are children of the featureContainer
			featureContainer._breaks = breaks;
			featureContainer._breaksTimestamp = (new Date()).getTime();
		}
		numClasses = kwArgs.numClasses;
	}

	var breakIndex = n.getBreakIndex(breaks, numClasses, attrValue);
	if (breakIndex < numClasses) calculateStyle(style, breakIndex, kwArgs);
};

var cp = djeo.common.Placemark;
n.calculateSizeStyle = function(style, breakIndex, kwArgs) {
	var numClasses = dojo.isArray(kwArgs.breaks) ? kwArgs.breaks.length - 1 : kwArgs.numClasses,
		iconSize = kwArgs.medianSize + kwArgs.sizeStep*( breakIndex - parseInt(numClasses/2) + (numClasses%2 ? 0 : 0.5) ),
		src = cp.getImgSrc(style),
		size = src ? cp.getImgSize(style) : cp.getSize(style);

	if (size) style.scale = (size[0]>size[1]) ? iconSize/size[0] : iconSize/size[1]
	else style.size = iconSize;
};

}());