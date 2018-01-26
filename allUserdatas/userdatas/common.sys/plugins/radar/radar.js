function createRadar(parentId, data, template) {
	
	Ext.require('Ext.data.*');
	Ext.require('Ext.chart.*');
	
	Ext.EventManager.fireDocReady();
	
	Ext.onReady(function() { 		
		var store = Ext.create('Ext.data.JsonStore', {
		fields: ['name', 'data1', 'data2', 'data3'],
	    data : data,
		});	
		
		var parent = Ext.get(parentId);		
		
		Ext.create('Ext.chart.Chart', {
		    width: parent.getWidth(),
		    height: parent.getHeight(),
		    store: store,
		    renderTo: parent,
		    insetPadding: 20,  
		    animate: template != null? template.animate: true,		    
		    theme: template != null? template.theme: 'Category1',		    
		    axes: [{
		        type: 'Radial',
		        position: 'radial',
		        label: {
		            display: true,
		        },
		    }],			    
		    series: [{
		        type: 'radar',
		        xField: 'name',
		        yField: 'data1',
		        showMarkers: template != null? template.showMarkers: true,
		        markerConfig: {
		            radius: 5,
		            size: 5,
		        },
		        style: {
		            'stroke-width': 2,
		            fill: 'none',
		        },	
		        listeners:{
		            itemmousedown : function(obj) {
		            window.gwtExternalComponentEventHandler(parentId, obj.point[0] + "_" +obj.point[1]);
		            }
		        },			        
		    },
		    {
		        type: 'radar',
		        xField: 'name',
		        yField: 'data2',
		        showMarkers: template != null? template.showMarkers: true,
		        markerConfig: {
		            radius: 5,
		            size: 5
		        },
		        style: {
		            'stroke-width': 2,
		            fill: 'none'
		        }
		    },
		    {
		        type: 'radar',
		        xField: 'name',
		        yField: 'data3',
		        showMarkers: template != null? template.showMarkers: true,
		        markerConfig: {
		            radius: 5,
		            size: 5
		        },
		        style: {
		            'stroke-width': 2,
		            fill: 'none'
		        }
		    }]
		});
				
	});
	
}
