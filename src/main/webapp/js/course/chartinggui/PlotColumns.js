dojo.provide("course.chartinggui.PlotColumns");

dojo.require("course.chartinggui.Option");

dojo.require("dijit.form.NumberSpinner");

dojo.require("dojox.charting.plot2d.Columns");

dojo.requireLocalization("course.chartinggui", "PlotColumns");


dojo.declare("course.chartinggui.PlotColumns", course.chartinggui.Option, {
    
    templateString: dojo.cache("course.chartinggui", "templates/PlotColumns.html"),
    
    constructor: function(params, srcNodeRef) {
        this.widgetDefs = [
            {id: "gap"}
        ];
    },
    
    getDefaultValue: function(optionId) {
        return dojox.charting.plot2d.Columns.prototype.defaultParams[optionId];
    }
});