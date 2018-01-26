dojo.provide("course.chartinggui.exporting.Base");


dojo.declare("course.chartinggui.exporting.Base", null, {
    
    exportContentTemplate: '',
    
    constructor: function() {
        // load template
        var moduleNameParts = this.declaredClass.split('.');
        var bundleName = moduleNameParts[moduleNameParts.length-1];
        var packageName = this.declaredClass.substr(0, this.declaredClass.length - bundleName.length - 1);
        dojo.mixin(this, dojo.i18n.getLocalization(packageName, bundleName) );  
    },
    
    getExportContent: function(chartOptions) {
        
    },
    
    getGui: function() {
        return null;
    }
});