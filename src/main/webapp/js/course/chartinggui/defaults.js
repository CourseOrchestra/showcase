dojo.provide("course.chartinggui.defaults");

(function() {
var d = course.chartinggui.defaults;

//
// define default GUI mode
//
var plotLines = "course.chartinggui.PlotLines";
var plotColumns = "course.chartinggui.PlotColumns";
var lines = {plot: plotLines};
var areas = {plot: {
    className: plotLines,
    params: {widgetDefs: [{id: "lines", needed: false}, {id: "areas", needed: false}, {id: "markers"}]}
}};
var columns = {plot: plotColumns};

d.mode = {
    "default": {
        order: ["plot", "theme", "axisX", "axisY", "grid"],
        legend: "course.chartinggui.Legend",
        theme: "course.chartinggui.Theme",
        axisX: "course.chartinggui.Axis",
        axisY: "course.chartinggui.Axis",
		grid: "course.chartinggui.Grid"
    },
    Lines: lines,
    StackedLines: lines,
    Areas: areas,
    StackedAreas: areas,
    MarkersOnly: {order: ["theme", "axisX", "axisY"]},
    Bars: columns,
    ClusteredBars: columns,
    StackedBars: columns,
    Columns: columns,
    ClusteredColumns: columns,
    StackedColumns: columns,
    Pie: {order: ["plot", "theme"], plot: "course.chartinggui.PlotPie"}
};

})();