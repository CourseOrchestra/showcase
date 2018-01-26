dojo.provide("course.chartinggui.PlotPie");

dojo.require("course.chartinggui.Option");

dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.NumberSpinner");

dojo.require("dojox.charting.plot2d.Pie");

dojo.requireLocalization("course.chartinggui", "PlotPie");


dojo.declare("course.chartinggui.PlotPie", course.chartinggui.Option, {
 
    templateString: dojo.cache("course.chartinggui", "templates/PlotPie.html"),
    
    labelOffsetDef: null,

    constructor: function(params, srcNodeRef) {
        this.labelOffsetDef = {id: "labelOffset"};
        this.widgetDefs = [
            {id: "labels"},
            this.labelOffsetDef,
            {id: "precision"},
            {id: "startAngle"}
        ];
    },
    
    onChange_labels: function(value) {
        var displayValue = value ? "" : "none";
        dojo.style(this.labelOffsetDef.trNode, "display", displayValue);
    },

    getDefaultValue: function(optionId) {
        return dojox.charting.plot2d.Pie.prototype.defaultParams[optionId];
    }
});