dojo.provide("course.chartinggui.Option");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.require("dijit.form.CheckBox");

dojo.requireLocalization("course.chartinggui", "Option");

dojo.declare("course.chartinggui.Option", [dijit._Widget, dijit._Templated], {
    
    optionId: '',
    
    chartOption: null,

    widgetDefs: null,

    widgetsInTemplate: true,

    constructor: function(params, srcNodeRef) {
        if (!params.chartOption) this.chartOption = {};
        this.widgetDefs = [];
    },

    postMixInProperties: function() {
        this.inherited("postMixInProperties", arguments);

        // load template
        var moduleNameParts = this.declaredClass.split('.');
        var bundleName = moduleNameParts[moduleNameParts.length-1];
        var packageName = this.declaredClass.substr(0, this.declaredClass.length - bundleName.length - 1);
        dojo.mixin(this, dojo.i18n.getLocalization(packageName, bundleName) );
    },

    buildRendering: function() {
        this.inherited("buildRendering", arguments);
        this.addDefaultValuesGui();
    },

    postCreate: function() {
        this.inherited("postCreate", arguments);
        this.setChartOption();
        this.watchChanges();
    },

    watchChanges: function() {
        for (var i1=0; i1<this.widgetDefs.length; i1++) {
            var widgetDef = this.widgetDefs[i1];
            if (widgetDef.needed == false) continue;
            dojo.connect(this[widgetDef.id], "onChange", this, dojo.partial(function(wd, value) {
                //console.debug(wd.id, value);
                if (wd.ignoreFirstChange) {delete wd.ignoreFirstChange; return;}
                var optionId = wd.id;

                if (this["setValue_"+optionId]) this["setValue_"+optionId](value);
                else this.chartOption[optionId] = value;

                // do specific processing with onChange_<optionId> function
                if (this["onChange_"+optionId]) this["onChange_"+optionId](value);
                // notify observers
                this.optionChanged(this.optionId, this.chartOption);
            }, widgetDef));
        }
    },

    setChartOption: function() {
        for (var i1=0; i1<this.widgetDefs.length; i1++) {
            var widgetDef = this.widgetDefs[i1];
            if (widgetDef.needed == false) continue;
            var optionId = widgetDef.id;
            var value = (optionId in this.chartOption) ? this.chartOption[optionId] : this.getDefaultValue(optionId);
            if (this["getValue_"+optionId]) value = this["getValue_"+optionId](value);

            // make initial call of onChange_<optionId> function
            if (this["onChange_"+optionId]) this["onChange_"+optionId](value);

            if (optionId in this.chartOption) {
                if ( this[optionId].get("value") != value ) {
                    widgetDef.ignoreFirstChange = true;
                    this[optionId].set("value", value);
                }
            }
            else if (!widgetDef.noDefault) {
                if ( this[optionId].get("value") != value /* a hack for dijit.form.CheckBox */|| this[optionId].declaredClass == "dijit.form.CheckBox") widgetDef.ignoreFirstChange = true;
                this.useDefaultValue(widgetDef, true);
            }
        }
    },
    
    optionChanged: function(optionId, chartOption) {
        
    },
    
    addDefaultValuesGui: function() {
        for (var i1=0; i1<this.widgetDefs.length; i1++) {
            var widgetDef = this.widgetDefs[i1];
			if (widgetDef.noDefault) continue;
            var widgetDomNode = this[widgetDef.id].domNode;
            var doCycle = true, trNode = widgetDomNode;
            while (doCycle) {
                trNode = trNode.parentNode;
                if (trNode) {
                    if (trNode.tagName == "TR") {
                        if (widgetDef.needed == false) {
                            // remove "unneeded" widgets (needed == false)
                            dojo.style(trNode, "display", "none");
                        }
                        else {
                            widgetDef.trNode = trNode;
                            // insert default value gui
                            var checkBox = new dijit.form.CheckBox();
                            checkBox.set("value", ( !(widgetDef.id in this.chartOption) ) );
                            var useDefaultStr = dojo.i18n.getLocalization("course.chartinggui", "Option")["_useDefaultValue"];
                            var td = dojo.create("td", {innerHTML: "<span>"+useDefaultStr+"</span>"}, trNode);
                            dojo.place(checkBox.domNode, td, "first");
                            dojo.addClass(td, "useDefault");
                            widgetDef.conDefaultValue = dojo.connect(checkBox, "onChange", this, dojo.partial(function(wd, value){
                                if (wd.conDefaultValue) {delete wd.conDefaultValue;return;}
                                this.useDefaultValue(wd, value);
                            }, widgetDef));
                        }
                        doCycle = false;
                    }
                }
                else doCycle = false;
            }
        }
    },
    
    useDefaultValue: function(widgetDef, useDefault) {
        var optionId = widgetDef.id;
        var defaultValue = this.getDefaultValue(optionId);
        if (useDefault) {
            widgetDef.useDefault = true;
            if (defaultValue != undefined) {
                if (this["getValue_"+optionId]) defaultValue = this["getValue_"+optionId](defaultValue);
                this[widgetDef.id].set("value", defaultValue);
            }
        }
        else {
            widgetDef.useDefault = false;
            if (defaultValue != undefined) this.chartOption[widgetDef.id] = this.getDefaultValue(optionId);
        }
        this[optionId].set("disabled", useDefault);
        var classFunc = useDefault ? dojo.addClass : dojo.removeClass;
        classFunc(widgetDef.trNode, "defaultValue");
    },
    
    getDefaultValue: function(optionId) {
        return undefined;
    },
    
    doExport: function() {
        for (var i1=0; i1<this.widgetDefs.length; i1++) {
            var widgetDef = this.widgetDefs[i1];
            if (widgetDef.needed == false) continue;
            if (widgetDef.useDefault) delete this.chartOption[widgetDef.id];
        }
    }
});