dojo.provide("course.chartinggui.BaseGui");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.require("dijit.layout.BorderContainer");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.Toolbar");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.Select");

dojo.requireLocalization("course.chartinggui", "common");


dojo.declare("course.chartinggui.BaseGui", [dijit._Widget, dijit._Templated], {
    
    templateString: dojo.cache("course.chartinggui", "templates/BaseGui.html"),
    
    widgetsInTemplate: true,

    constructor: function(params, srcNodeRef) {

    },
    
    postMixInProperties: function() {
        this.inherited("postMixInProperties", arguments);

        dojo.mixin(this, dojo.i18n.getLocalization("course.chartinggui", "common") );
    }
    
    
});