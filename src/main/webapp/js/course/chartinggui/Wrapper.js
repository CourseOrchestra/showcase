dojo.provide("course.chartinggui.Wrapper");

dojo.require("course.chartinggui.defaults");

dojo.require("dijit.layout.ContentPane");

dojo.require("course.charting");

dojo.require("course.chartinggui.exporting.Javascript");
dojo.require("course.chartinggui.ExportDialog");

dojo.requireLocalization("course.chartinggui", "common");


dojo.declare("course.chartinggui.Wrapper", null, {
    
    // supported plot types
    plotTypes: [
        "Lines",
        "StackedLines",
        "Columns",
        "ClusteredColumns",
        "StackedColumns",
        "Bars",
        "ClusteredBars",
        "StackedBars",
        "Areas",
        "StackedAreas",
        "Pie",
        "MarkersOnly"
    ],
    
    // hooks from GUI widget if the widget is used
    guiAttachPoints: ["chartDiv", "plotTypeSelect", "exportButton", "tabContainer", "chartContentPane"],
    
    initPlotTypeSelect: true,

    chartDiv: null,
    chartContentPane: null,
    tabContainer: null,
    plotTypeSelect: null,
    exportButton: null,
    chartLegendId: "courseChartLegend",
    
    // json object
    chartOptions: null,
    
    optionWidgets: null,
    
    chartDefs: course.chartinggui.defaults.mode,
    
    // reference to the chart object
    chart: null,
    
    // a regitry to store exporters by id
    exporters: null,
    
    currentExporter: null,
    
    // GUI export dialog
    exportDialog: null,
    
    lastChartDef: null,
    
    constructor: function(params) {
        this.optionWidgets = [];
        this.exporters = {};
        dojo.mixin(this, params);
        this.init();
    },

    init: function() {
        // check if this.gui is provided
        if (this.gui) {
            dojo.forEach(this.guiAttachPoints, function(guiAttachPoint){
                this[guiAttachPoint] = this.gui[guiAttachPoint];
            }, this);
        }
        
        if (this.initPlotTypeSelect) {
            // fill this.plotTypeSelect
            dojo.forEach(this.plotTypes, function(plotType) {
                this.plotTypeSelect.addOption({ value: plotType, label: dojo.i18n.getLocalization("course.chartinggui", "common")[plotType] });
            }, this);
        }
        
        this.initConnections();

        // force wrapper to render chart
        var plotType = this.getPlotType();
        if (plotType==this.plotTypeSelect.get("value")) this.setPlotType(plotType);
        else this.plotTypeSelect.set("value", plotType);
    },
    
    initConnections: function() {
        dojo.connect(this.plotTypeSelect, "onChange", this, "setPlotType");
        dojo.connect(this.chartContentPane, "resize", dojo.hitch(this, function(changeSize, resultSize) {
            var contentPaneBox = dojo.contentBox(this.chartContentPane.domNode);
            this.resizeChart(contentPaneBox.w, contentPaneBox.h);
        }));
        dojo.connect(this.exportButton, "onClick", this, "doExport");
    },
    
    initExportDialog: function() {
        this.exportDialog = new course.chartinggui.ExportDialog({
            onExporterChanged: dojo.hitch(this, function(exportId) {
                this.currentExporter = this.exporters[exportId];
                this.exportDialog.setExportContent(this.currentExporter.getExportContent(this.chartOptions));
            })
        });
        this.addExporter(new course.chartinggui.exporting.Javascript());
    },

    setPlotType: function(type) {
        // TODO: show loading
        
        // remove option widgets
        for (var i1=this.optionWidgets.length-1; i1>=0; i1--) {
            this.optionWidgets.pop();
        }
        // remove existing tabs
        var tabs = this.tabContainer.getChildren();
        for(var i1=tabs.length-1; i1>=0; i1--) {
            this.tabContainer.removeChild(tabs[i1]);
            tabs[i1].destroyRecursive();
        }
        
        this.chartOptions.plot[0].type = type;
        // load new chart def
        var chartDef = this.chartDefs[type];
        var defaultDef = undefined;
        if (this.lastChartDef != chartDef) { // otherwise no need to load a new GUI
            defaultDef = this.chartDefs['default'];
            var optionsOrder = chartDef['order'] ? chartDef['order'] : defaultDef['order'];
            dojo.forEach(optionsOrder, function(optionId){
                var optionDef = chartDef[optionId] ?  chartDef[optionId] : defaultDef[optionId];
                var className = dojo.isObject(optionDef) ? optionDef.className : optionDef;
                dojo.require(className);
                var params = {optionId: optionId};
                if (dojo.isObject(optionDef))  dojo.mixin(params, optionDef.params);
                if (this.chartOptions[optionId]) params.chartOption = this.chartOptions[optionId];
				else params.chartOptions = chartOptions;
                var optionTabContent = new dojo.getObject(className)(params);
                this.optionWidgets.push(optionTabContent);
                var optionTab = dijit.layout.ContentPane({content: optionTabContent.domNode});
                // set tab title
				// _title is the special case for Axis, title is busy with dojoAttachPoint in Axis
                optionTab.title = optionTabContent._title ? optionTabContent._title : (optionTabContent.title ? optionTabContent.title : dojo.i18n.getLocalization("course.chartinggui", "common")[optionId]);
                dojo.connect(optionTabContent, "optionChanged", this, "optionChanged");
                this.tabContainer.addChild(optionTab);
            }, this);
        }

        this.lastChartDef = defaultDef;
        this.renderChart();
    },
    
    optionChanged: function(optionId, value) {
        if (value != undefined) {
            if ( (dojo.isObject(value) && this.chartOptions[optionId]==undefined) || !dojo.isObject(value) ) {
                this.chartOptions[optionId] = value;
            }
        }
        else {
            delete this.chartOptions[optionId];
        }
        this.renderChart();
    },
    
    renderChart: function() {
        var o = this.chartOptions;

        // save chart and legend id
        var chartId = o.id;
        o.id = this.chartDiv;
        var chartLegendId = undefined;
        if (o.legend) {
            chartLegendId = o.legend.id;
            o.legend.id = this.chartLegendId;
        }
        
        this.chart = course.charting.makeChart(this.chartOptions);

        // restore chart and legend id
        o.id = chartId;
        if (o.legend) o.legend.id = chartLegendId;
    },
    
    getPlotType: function() {
        return this.chartOptions.plot[0].type;
    },

    resizeChart: function(w, h) {
        if (this.chart) this.chart.resize(w, h);
    },

    doExport: function() {
        if (!this.exportDialog) this.initExportDialog();
        for(var i1=0; i1<this.optionWidgets.length; i1++) {
            this.optionWidgets[i1].doExport();
        }
        this.exportDialog.setExportContent(this.currentExporter.getExportContent(this.chartOptions));
        this.exportDialog.show();
    },
    
    addExporter: function(exporter, makeCurrent) {
        if (!this.exportDialog) this.initExportDialog();
        this.exporters[exporter.declaredClass] = exporter;
        if (!this.currentExporter || makeCurrent) {
            this.currentExporter = exporter;
        }
        this.exportDialog.addExporter(exporter.declaredClass, exporter._label, makeCurrent);
    }
});