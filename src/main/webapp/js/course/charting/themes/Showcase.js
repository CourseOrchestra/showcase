dojo.provide("course.charting.themes.Showcase");


dojo.require("dojox.charting.Theme");
/*
dojo.require("dojox.charting.themes.gradientGenerator");

(function(){
	var dc = dojox.charting, themes = dc.themes,
		colors = ["red", "yellow", "green", "blue", "fuchsia", "silver", "aqua", "maroon", "olive", "lime", "navy", "purple", "gray", "teal"],
		defaultFill = {type: "linear", space: "plot", x1: 0, y1: 0, x2: 0, y2: 100};

	course.charting.themes.Showcase = new dc.Theme({
		seriesThemes: themes.gradientGenerator.generateMiniTheme(colors, defaultFill, 90, 40, 25)
	});
})();
*/

course.charting.themes.Showcase = new dojox.charting.Theme({
    colors: [
"red",
"yellow",
"green",
"blue",
"fuchsia",
"silver",
"aqua",
"maroon",
"olive",
"lime",
"navy",
"purple",
"gray",
"teal"
]
});