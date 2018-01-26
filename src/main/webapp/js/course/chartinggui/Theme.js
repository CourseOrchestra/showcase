dojo.provide("course.chartinggui.Theme");

dojo.require("course.chartinggui.Option");

dojo.require("dijit.form.Select");

dojo.requireLocalization("course.chartinggui", "Theme");

(function() {
themes = [
"Adobebricks",
"Algae",
"Bahamation",
"BlueDusk",
"Charged",
"Chris",
"CubanShirts",
"Desert",
"Distinctive",
"Dollar",
"Electric",
"Grasshopper",
"Grasslands",
"GreySkies",
"Harmony",
"IndigoNation",
"Ireland",
"Julie",
"MiamiNice",
"Midwest",
"Minty",
"PrimaryColors",
"PurpleRain",
"Renkoo",
"RoyalPurples",
"SageToLime",
"Shrooms",
"ThreeD",
"Tom",
"Tufte",
"WatersEdge",
"Wetland",
"PlotKit.base",
"PlotKit.blue",
"PlotKit.cyan",
"PlotKit.green",
"PlotKit.orange",
"PlotKit.purple",
"PlotKit.red"
];

dojo.declare("course.chartinggui.Theme", course.chartinggui.Option, {
    
    noThemeValue: "-",
    
    templateString: dojo.cache("course.chartinggui", "templates/Theme.html"),
    
    constructor: function(params, srcNodeRef) {
        this.widgetDefs = [];
        this.chartOption = this.noThemeValue;
    },

    buildRendering: function() {
        this.inherited("buildRendering", arguments);
        dojo.forEach(themes, function(theme){
            this.addTheme({value: "dojox.charting.themes."+theme, label: theme});
        }, this);
    },
    
    addTheme: function(theme) {
        this.themeSelect.addOption(theme);
    },
    
    watchChanges: function() {
        dojo.connect(this.themeSelect, "onChange", dojo.hitch(this, function(value){
            if (this.ignoreFirstChange) {delete this.ignoreFirstChange; return;}
            if (value == this.noThemeValue) value= "";
            this.optionChanged(this.optionId, value);
        }));
    },
    
    setChartOption: function() {
        var value = this.chartOption;
        if (value != this.themeSelect.get("value")) {
            this.ignoreFirstChange = true;
            this.themeSelect.set("value", value);
        }
    }
});

})();