dojo.provide("course.chartinggui.Grid");

dojo.require("course.chartinggui.Option");

dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.SimpleTextarea");

dojo.requireLocalization("course.chartinggui", "Theme");

(function() {

dojo.declare("course.chartinggui.Grid", course.chartinggui.Option, {
    
    templateString: dojo.cache("course.chartinggui", "templates/Grid.html"),
    
    constructor: function(params, srcNodeRef) {
        this.chartOption = undefined;
		this.plot = params.chartOptions.plot;
    },
    
    watchChanges: function() {
        dojo.connect(this.showGrid, "onChange", dojo.hitch(this, function(value){
			if (value) {
				this.plot[1] = dojo.fromJson(this.gridSettings.get("value"));
			}
			else {
				this.plot.pop()
			}
			this.optionChanged();
        }));
        dojo.connect(this.gridSettings, "onChange", dojo.hitch(this, function(value){
			if (this.showGrid.get("value")) {
				this.plot[1] = dojo.fromJson(this.gridSettings.get("value"));
				this.optionChanged();
			}
        }));
    },
    
    setChartOption: function() {
		this.ignoreFirstChange = true;
		var plot = this.plot;
		if (plot.length>1) {
			this.gridSettings.set("value", dojo.toJson(plot[1], true));
			this.showGrid.set("value", true);
		}
    }
});

})();