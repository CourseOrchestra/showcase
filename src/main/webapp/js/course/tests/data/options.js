define([], function(){

return {
	//managerModule: "solution/test",
	style: [{
			fid: "9CADC219-2A2A-4E9F-946E-7F6CAB0036EF",
			//styleClass: "region",
			stroke: "black",
			strokeWidth: 1,
			composer: "course.geo.composeStyle",
			composerOptions: {
				numClasses:6,
				colorSchemeName: "BuPu",
				attr: "mainInd",
				breaks: "course.geo.getBreaks",
				composeStyle: "course.geo.colorbrewer.composeStyle"
			},
			name: "321",
			legend: "course.geo.getBreaksAreaLegend"
		},
		{
			theme: "highlight",
			fid: "9CADC219-2A2A-4E9F-946E-7F6CAB0036EF",
			//styleClass: "region",
			stroke: "black",
			strokeWidth: 1,
			name: "321"
		}
	]
};

});
