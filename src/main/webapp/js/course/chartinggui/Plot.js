dojo.provide("course.chartinggui.Plot");

dojo.require("course.chartinggui.Option");

dojo.declare("course.chartinggui.Plot", course.chartinggui.Option, {

    constructor: function(params, srcNodeRef) {
        params.chartOptions.options;
    },
    
    postCreate: function() {
        this.inherited("postCreate", arguments);
        //this.watchChanges();
    }
});