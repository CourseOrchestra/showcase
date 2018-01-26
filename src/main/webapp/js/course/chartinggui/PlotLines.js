dojo.provide("course.chartinggui.PlotLines");

dojo.require("course.chartinggui.Option");

dojo.require("dijit.form.CheckBox");

dojo.require("dojox.charting.plot2d.Default");

dojo.requireLocalization("course.chartinggui", "PlotLines");


dojo.declare("course.chartinggui.PlotLines", course.chartinggui.Option, {
    
    templateString: dojo.cache("course.chartinggui", "templates/PlotLines.html"),

    constructor: function(params, srcNodeRef) {
        if (this.widgetDefs.length==0) this.widgetDefs = [
            {id: "lines", needed: false},
            {id: "areas"},
            {id: "markers"}
        ];
    },
    
    getDefaultValue: function(optionId) {
        return dojox.charting.plot2d.Default.prototype.defaultParams[optionId];
    }
});