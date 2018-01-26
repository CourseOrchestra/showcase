dojo.provide("course.chartinggui.exporting.CourseShowcase");

dojo.require("course.chartinggui.exporting.Base");

dojo.requireLocalization("course.chartinggui.exporting", "CourseShowcase");

dojo.declare("course.chartinggui.exporting.CourseShowcase", course.chartinggui.exporting.Base, {
    
    // options to export
    options: ["plot", "theme", "action", "axisX", "axisY"],

    getExportContent: function(chartOptions) {
        var exportContent = {};
        dojo.forEach(this.options, function(o){
            if (chartOptions[o]) exportContent[o] = chartOptions[o];
        });
        return "<pre>"+dojo.toJson(exportContent, true)+"</pre>";
    },

    getGui: function() {
        return null;
    }
});