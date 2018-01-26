dojo.provide("course.chartinggui.exporting.Javascript");

dojo.require("course.chartinggui.exporting.Base");

dojo.requireLocalization("course.chartinggui.exporting", "Javascript");

dojo.declare("course.chartinggui.exporting.Javascript", course.chartinggui.exporting.Base, {
    
    getExportContent: function(chartOptions) {
        return "<pre>"+dojo.toJson(chartOptions, true)+"</pre>";
    },
    
    getGui: function() {
        return null;
    }
});