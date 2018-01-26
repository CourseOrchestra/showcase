dojo.provide("course.charting");

dojo.require("dojox.charting.Chart2D");
dojo.require("dojox.charting.widget.SelectableLegend");

(function(){
var dc = dojox.charting;
var c = course.charting;

var chartRegistry = {};

var chrt = {
    
    setChart: function(id) {
		this.id = id;
		this.chart = chartRegistry[id].chart;
    },

    makePlot: function(plot, plotName) {
		plotName = plotName ? plotName : "default";
		this.chart.addPlot(plotName, plot);
    },
    
    makeAxes: function(axisX, axisY, plotName) {
		if (axisX) {
			var axisName = plotName ? "x_" + plotName : "x";
			this._makeAxis(axisX);
			this.chart.addAxis(axisName, axisX);
		}
		if (axisY) {
			var axisName = plotName ? "y_" + plotName : "y";
			this._makeAxis(axisY);
			this.chart.addAxis(axisName, axisY);
		}
    },
    
    _makeAxis: function(axis) {
		// add titleOrientation: "away"
		axis.titleOrientation = "away";
		var labelFunc = axis.labelFunc;
		if (labelFunc && dojo.isString(labelFunc)) {
			var _labelFunc = dojo.getObject(labelFunc);
			axis.labelFunc = _labelFunc ? _labelFunc : eval("_labelFunc="+labelFunc);
		}
    },
    
    makeSeries: function(series) {
		if (!series) return;
		if (!dojo.isArray(series)) {
			series = [series];
		}
		var numSeries = series.length;
		for (var i1=0; i1<numSeries; i1++) {
			this.chart.addSeries(series[i1].name, series[i1].data, series[i1].options);
		}
    },
    
    makeTheme: function(theme) {
		if (!theme) return;
		if (dojo.isString(theme)) {
			dojo.require(theme);
			theme = dojo.getObject(theme);
		}
		this.chart.setTheme(theme);
    },
    
    makeLegend: function(legend) {
		if (!legend) return;		
		
		var options = legend.options ? legend.options : {};
		options.chart = this.chart;
		var legendNode = dojo.byId(legend.id);
		if (legendNode) {
			try {
				if (legend.selectable) {
					legendWidget = new dojox.charting.widget.SelectableLegend(options, legendNode);
				}
				else {
					legendWidget = new dojox.charting.widget.Legend(options, legendNode);
				}				
			} finally  {
				if (legendWidget) {
					chartRegistry[this.id].legend = legendWidget;
				}
			}
			
		}
    },
    
    makeAction: function(action, plotName) {
		if (!action) return;
		if (!dojo.isArray(action)) {
			action = [action];
		}
		plotName = plotName ? plotName : "default";
		var numActions = action.length;
		for (var i1=0; i1<numActions; i1++) {
			var actionType = action[i1].type;
			if (!actionType) continue;
			if (dojo.isString(actionType)) {
				dojo.require(actionType);
				actionType = dojo.getObject(actionType);
			}
			var options = action[i1].options;
			if (options) {
				var easing = options.easing;
				if (easing && dojo.isString(easing)) {
					dojo.require("dojo.fx.easing");
					options.easing = dojo.getObject(easing);
				}
			}
			new actionType(this.chart, plotName, options);
		}
    },
    
    makeEventHandler: function(eventHandler) {
		if (!eventHandler) return;
		if (!dojo.isArray(eventHandler)) {
			eventHandler = [eventHandler];
		}
		var numEventHandlers = eventHandler.length;
		for (var i1=0; i1<numEventHandlers; i1++) {
			var plotName = "default", handlerFunc;
			if (dojo.isString(eventHandler[i1])) {
			handlerFunc = dojo.getObject(eventHandler[i1]);
			}
			else { // object
			if (eventHandler[i1].plot) plotName = eventHandler[i1].plot;
			handlerFunc = eventHandler[i1].handlerFunc;
			if (dojo.isString(handlerFunc)) handlerFunc = dojo.getObject(handlerFunc);
			}
			this.chart.connectToPlot(plotName, handlerFunc);
		}
    }

};

c.makeCharts = function(charts) {
    var numCharts = charts.length;
    for (var i1=0; i1<numCharts; i1++) {
        c.makeChart(charts[i1]);
    }
};

c.makeChart = function(chartOptions) {
    var o = chartOptions; // chartOptions
    // check if we have a convertor function
    if (arguments.length > 1) {
        // convertor function is the last argument
        var convertorFunc = arguments[arguments.length-1];
        if (dojo.isFunction(convertorFunc)) o = convertorFunc.apply(null, arguments);
    }
    if (dojo.isString(o)) o = dojo.fromJson(o);

    var chartNode = dojo.byId(o.id);
    if (!chartNode) return null;

    c.destroyChart(o.id);
    chart = dc.Chart2D(chartNode);
    chartRegistry[o.id] = {chart: chart};

    chrt.setChart(o.id);
	if (dojo.isArray(o.plot)) {
		dojo.forEach(o.plot, function(plot){
			chrt.makePlot(plot, plot.name);
		});
	}
    else {
		chrt.makePlot(o.plot);
	}
    chrt.makeAxes(o.axisX, o.axisY);
    chrt.makeSeries(o.series);
    chrt.makeTheme(o.theme);
    chrt.makeAction(o.action);
    chrt.makeEventHandler(o.eventHandler);
    if (o.width && o.height) chart.resize(o.width, o.height);
    else chart.render();
    chrt.makeLegend(o.legend);
    
    return chart;
};

c.destroyChart = function(id) {
    if (chartRegistry[id]) {
	chartRegistry[id].chart.destroy();
	if (chartRegistry[id].legend) chartRegistry[id].legend.destroy(true);
	delete chartRegistry[id];
    }
};

}());