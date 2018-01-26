dojo.provide("course.chartinggui.Axis");

dojo.require("course.chartinggui.Option");

dojo.require("dojox.charting.axis2d.Default");

dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Select");


dojo.requireLocalization("course.chartinggui", "Axis");


dojo.declare("course.chartinggui.Axis", course.chartinggui.Option, {
    
    templateString: dojo.cache("course.chartinggui", "templates/Axis.html"),
    
    showAxis: false,
    
    minorLabelsDef: null,
    
    constructor: function(params, srcNodeRef) {
        this.minorLabelsDef = {id: "minorLabels"};
        this.widgetDefs = [
			{id: "title", noDefault: true, attachPoint: "axisTitle"},
            {id: "fixLower"},
            {id: "fixUpper"},
            {id: "rotation"},
            {id: "minorTicks"},
            {id: "microTicks"},
            {id: "majorLabels"},
            this.minorLabelsDef,
            {id: "includeZero"},
            {id: "leftBottom"}
        ];
        if (params.chartOption) this.showAxis = true;
    },
    
    postMixInProperties: function() {
        this.inherited("postMixInProperties", arguments);
        if (this.optionId == "axisY") {
            this.chartOption.vertical = true;
            this._title = this.titleY;
            this._leftBottom1 = this._leftBottom1Y;
            this._leftBottom2 = this._leftBottom2Y;
        }
        else {
            this._title = this.titleX;
            this._leftBottom1 = this._leftBottom1X;
            this._leftBottom2 = this._leftBottom2X;
        }
    },

    buildRendering: function() {
        this.inherited("buildRendering", arguments);
        if (this.showAxis) dojo.style(this.optionsDiv, "display", "");
    },

    postCreate: function() {
        this.inherited("postCreate", arguments);
        if (this.showAxis) {
            this.ignoreFirstChange = true;
            this.showAxisCheckBox.set("value", true);
            dojo.style(this.optionsDiv, "display", "");
        }
        dojo.connect(this.showAxisCheckBox, "onChange", dojo.hitch(this, function(value){
            if (this.ignoreFirstChange) {delete this.ignoreFirstChange; return;};
            this.showAxis = value;
            var display = this.showAxis ? "" : "none";
            dojo.style(this.optionsDiv, "display", display);
            this.optionChanged(this.optionId, this.showAxis ? this.chartOption : undefined);
        }));
    },

    getDefaultValue: function(optionId) {
        return dojox.charting.axis2d.Default.prototype.defaultParams[optionId];
    },
    
    doExport: function() {
        if (this.showAxis) this.inherited("doExport", arguments);
    },
    
    onChange_minorTicks: function(value) {
        var displayValue = value ? "" : "none";
        dojo.style(this.minorLabelsDef.trNode, "display", displayValue);
    },
    
    setValue_leftBottom: function(value) {
        this.chartOption.leftBottom = (value == "lb1") ? true: false;
    },
    
    getValue_leftBottom: function(value) {
        return value ? "lb1" : "lb2";
    }
});