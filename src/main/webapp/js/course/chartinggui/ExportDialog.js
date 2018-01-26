dojo.provide("course.chartinggui.ExportDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.require("dijit.form.Select");

dojo.require("dijit.Dialog");
dojo.requireLocalization("course.chartinggui", "ExportDialog");


dojo.declare("course.chartinggui.ExportDialog", [dijit._Widget, dijit._Templated], {
    
    dialog: null,

    widgetsInTemplate: true,
    
    _exporterSelectCon: null,
    
    templateString: dojo.cache("course.chartinggui", "templates/ExportDialog.html"),

    constructor: function(params, srcNodeRef) {
        this.widgetDefs = [
            {id: "lines", needed: false},
            {id: "areas"},
            {id: "markers"}
        ];
    },

    postMixInProperties: function() {
        this.inherited("postMixInProperties", arguments);

        dojo.mixin(this, dojo.i18n.getLocalization("course.chartinggui", "ExportDialog") );
    },

    buildRendering: function() {
        this.inherited("buildRendering", arguments);
        this.dialog = new dijit.Dialog({ title: this._dialogTitle}, this.domNode);
    },
    
    postCreate: function() {
        this.inherited("postCreate", arguments);
        this._exporterSelectCon = dojo.connect(this.exporterSelect, "onChange", this.onExporterChanged);
    },
    
    show: function() {
        this.dialog.show();
    },
    
    hide: function() {
        this.dialog.hide();
    },
    
    setExportContent: function(exportContent) {
        dojo.attr(this.exportContent, "innerHTML", exportContent);
    },
    
    addExporter: function(exporterId, exporterLabel, makeCurrent) {
        var option = {value:exporterId, label:exporterLabel};
        this.exporterSelect.addOption(option);
        if (makeCurrent) this.exporterSelect.set("value", exporterId);
    }
});