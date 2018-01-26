function createToolbar(parentId, data) {
	
	$("#"+parentId).ribbon({
		data: data.data,
		
	    onClick:function(name, target){
    		gwtPluginFunc(parentId, name);
	    }		
	
	});
	
}



