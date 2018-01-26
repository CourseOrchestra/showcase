function showMultiSelector(selectorParam) {
	require({async:true},
			[
             "dijit/registry",
             "dijit/Dialog",
             "dojo/_base/lang",
             "dgrid/OnDemandGrid",
             "dgrid/Selection",
             "dgrid/Keyboard",
             "dojo/_base/declare",
             "dstore/Rest",
             "dstore/Cache",
             "dstore/Memory",
			 "dstore/Trackable",
			 "dojox/timing",
			 "dojo/domReady!"
	     ], function(registry, Dialog, lang, Grid, Selection, Keyboard, declare, Rest, Cache, Memory, Trackable, timing){
		
		dojo.xhrGet({
			sync: true,
			url: window.appContextPath + "/sessionTimeoutCheckForClientJS",
			handleAs: "json",
			preventCache: true,
			load: function(data) {
				if(data.value === "true"){
					window.location.replace(window.appContextPath + "/sestimeout.jsp");
				}
		  	},
			error: function(error) {
				window.location.replace(window.appContextPath + "/sestimeout.jsp");
			}
		});
		
		
		var localizedParams = gwtSelectorGetLocalizedParams(getXFormId());
		localizedParams = eval('('+localizedParams+')');
		
		
		var content1 = 
		    '<table>'+
		    
	        '<tr>'+
	        '    <td style="margin: 0; padding: 0;">'+
		    '    	<table width="100%">'+
	        '		<tr>'+
	        '       	<td style="width: 100%; margin: 0; padding: 0;">'+	        
	        '    			<input style="width: 100%; margin: 0; padding: 0;" data-dojo-type="dijit/form/TextBox" id="selectorSearchString" data-dojo-props="intermediateChanges:true" value="'+getCurrentValue()+'" class="server-multiselector-searchstringtextbox-element">'+
            '       	</td>';
	    if(getManualSearch()){
		    content1 = content1 +
	        '       	<td>'+		    
	        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectorSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorGrid.refresh();}, iconClass: '+"'server-multiselector-manualsearchbutton-image'"+', showLabel: false" class="server-multiselector-manualsearchbutton-element"></button>'+
            '       	</td>';		    
	    }
			content1 = content1 +
	        '       	<td>'+			
	        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectorSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorSearchString.clear();}, iconClass: '+"'server-multiselector-clearbutton-image'"+', showLabel: false" class="server-multiselector-clearbutton-element"></button>'+
            '       	</td>'+
	        '		</tr>'+	        
		    '    	</table>'+	        
	        '    </td>'+        
	        '</tr>';
		
	    if(!getHideStartWith()){
			content1 = content1 + 
		    '<tr>'+
		    '    <td>'+
		    '    	 <input data-dojo-type="dijit/form/CheckBox" id="selectorStartsWith" data-dojo-props="checked: '+getStartWith()+'" class="server-multiselector-checkbox-element">'+
		    '    	 <label for="selectorStartsWith" class="server-multiselector-checkbox-element">'+localizedParams["startWithTitle"]+'</label>'+
		    '    </td>'+        
		    '</tr>';
	    }
	        
	        content1 = content1 + 
	        '<tr>'+
	        '    <td> <div id="selectorGrid" style= "width: '+getDataWidth()+'; height: '+getDataHeight()+';"></div> </td>'+
	        '</tr>'+
	        '</table>';

		var content4 =	        
	        '<div class="dijitDialogPaneActionBar">'+
	        '	<button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onFocus:function(){selectorDialog.executeOK();selectorDialog.hide();selectorDialog.delayAfterHide();}" class="server-multiselector-okbutton-element">OK</button>'+	        
	        ' 	<button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.hide();selectorDialog.delayAfterHide();}" class="server-multiselector-cancelbutton-element">'+localizedParams["cancelTitle"]+'</button>'+
	        '</div>';
		

		var content2 =
		    '<table>'+
	        '<tr>'+		    
		    '    <td>'+	        
	        '    	 <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.selectAction();}, iconClass: '+"'server-multiselector-selectbutton-element-image'"+', showLabel: false" class="server-multiselector-selectbutton-element server-multiselector-selectionbutton-element"></button>'+
		    '    </td>'+	        
	        '</tr>'+
	        '<tr>'+		    
		    '    <td>'+	        
	        '    	 <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.selectAllAction();}, iconClass: '+"'server-multiselector-selectallbutton-element-image'"+', showLabel: false" class="server-multiselector-selectallbutton-element server-multiselector-selectionbutton-element"></button>'+
		    '    </td>'+	        
	        '</tr>'+
	        '<tr>'+		    
		    '    <td>'+	        
	        '    	 <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.unselectAction();}, iconClass: '+"'server-multiselector-unselectbutton-element-image'"+', showLabel: false" class="server-multiselector-unselectbutton-element server-multiselector-selectionbutton-element"></button>'+
		    '    </td>'+	        
	        '</tr>'+
	        '<tr>'+		    
		    '    <td>'+	        
	        '    	 <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.unselectAllAction();}, iconClass: '+"'server-multiselector-unselectallbutton-element-image'"+', showLabel: false" class="server-multiselector-unselectallbutton-element server-multiselector-selectionbutton-element"></button>'+
		    '    </td>'+	        
	        '</tr>'+
            '</table>';
		
		

			var content3 =
			    '<table height="100%">'+
		        '<tr>'+
		        '    <td style="margin: 0; padding: 0;">'+
			    '    	<table width="100%">'+
		        '		<tr>'+
		        '       	<td style="width: 100%; margin: 0; padding: 0;">'+		        
		        '    	 		<input style="width: 100%; margin: 0; padding: 0;" data-dojo-type="dijit/form/TextBox" id="selectedSearchString" data-dojo-props="intermediateChanges:true" class="server-multiselector-searchselectedstringtextbox-element">'+
	            '       	</td>'+		        
		        '       	<td>'+		        
		        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectedSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.findAction();}, iconClass: '+"'server-multiselector-findbutton-element-image'"+', showLabel: false" class="server-multiselector-findbutton-element"></button>'+
	            '       	</td>'+		        
		        '		</tr>'+	        
			    '    	</table>'+	        
		        '    </td>'+        
		        '</tr>'+
		        '<tr style="height: 100%;">'+
		        '    <td> <div id="selectedGrid" style= "width: '+getSelectedDataWidth()+'; height: 100%;"></div> </td>'+
		        '</tr>'+
	            '</table>';
			
		
		var content =
		    '<table class="dijitDialogPaneContentArea" cols="3" height="100%">'+
	        '<tr>'+
	        '<td style="vertical-align: top;">'+content1+'</td>'+
	        '<td style="vertical-align: middle;">'+content2+'</td>'+
	        '<td style="vertical-align: top;">'+content3+'</td>'+
	        '</tr>'+
            '</table>'+
            content4;
		

		selectorSearchString = null;
		var selectorStartsWith = null;

		selectorDialog = new Dialog({			 

		    title: getWindowCaption(),
		    content: content,
		    class : "server-multiselector-popup",
		    	 
		    onHide: function(){
		    	this.destroyRecursive(false);
		    },
		    
		    onFocus: function(){
		    	if(this.loaded){
		    		return;
		    	}
		    	this.loaded = true;
		    	
		    	selectorGrid.startup();
		    	selectedGrid.startup();
		    	
				selectorSearchString = registry.byId("selectorSearchString");
				if(!getManualSearch()){
				    selectorSearchString.set("onChange", function(event){
				    	if(this.timer){
				    		this.timer.stop();
				    	}
					   	this.timer = new dojox.timing.Timer(430);
					   	this.timer.onTick = function(){
					   		selectorGrid.refresh();
					   		this.stop();
					   	}
					   	this.timer.start();
				    });
				}
			    selectorSearchString.set("clear", function(event){
			    	this.set("value", "");
			    	if(getManualSearch()){
				    	selectorGrid.refresh();
			    	}
			    });
			    
			    selectorStartsWith = registry.byId("selectorStartsWith");
			    if(selectorStartsWith){
				    selectorStartsWith.set("onChange", function(event){
				    	selectorGrid.refresh();
				    });
			    }
			    
			    
		    },
		    
		    onKeyPress: function(event){
		    	if(selectorStartsWith && event.ctrlKey&& event.code == "KeyB"){
		    		selectorStartsWith.set("checked", !selectorStartsWith.get("checked"));
		    		dojo.stopEvent(event);
		    	}
		    },
		    
		    executeOK: function(){
		    	selectedGrid.collection.fetch().then(function (rows){
		    		
			    	var selected = [];
		    	    for (var i = 0; i < rows.length; i++) {
				         selected.push(rows[i]);
			    	}
		    	    
		       	    if(selectorParam.xpathRoot && selectorParam.xpathMapping){
						insertXFormByXPath(true, selected, selectorParam.xpathRoot, selectorParam.xpathMapping, getNeedClear(), localizedParams["subformId"]);
		       	    }
		       	    
			    	if(selectorParam.onSelectionCompleteAction){
			    		selectorParam.onSelectionCompleteAction(true, selected);
			    	}
			    	
			    	if(selectorParam.onSelectionComplete){
			    		selectorParam.onSelectionComplete(true, selected);
			    	}
		    		
		    	});
		    },
		    
		    delayAfterHide: function(){
		    	setTimeout(function() { document.body.className = document.body.className.replace("multiselector","");}, 1000);
			},
		    
		    selectAction: function(){
			     for(var id in selectorGrid.selection){
			         if(selectorGrid.selection[id]){
			        	 selectedGrid.collection.put(selectorGrid.row(id).data);
			         }
			     }
		    },
		    selectAllAction: function(){
		    	 selectorGrid.collection.fetch({start: 0, end: 100000}).then(function (rows){
		    	     for (var i = 0; i < rows.length; i++) {
			        	 selectedGrid.collection.put(rows[i]);
		    	     }
		    	 });
		    },
		    unselectAction: function(){
			     for(var id in selectedGrid.selection){
			         if(selectedGrid.selection[id]){
			        	 selectedGrid.collection.remove(id);
			         }
			     }
		    },
		    unselectAllAction: function(){
		    	 selectedGrid.collection.fetch().then(function (rows){
		    	     for (var i = rows.length - 1; i >= 0; i--) {
		    	    	 selectedGrid.collection.remove(rows[i].id);
		    	     }           
		    	 });
		    },
		    
		    findAction: function(){
		    	
		    	var id = null;
		    	
		    	var count = 0;
			    for(var id2 in selectedGrid.selection){
			        if(selectedGrid.selection[id2]){
			        	count++;
			        }
			    }
			    if(count == 1){
			    	id = id2;
			    } else {
			    	if(selectedGrid._focusedNode){
			    		id = selectedGrid.row(selectedGrid._focusedNode).id;
			    	}
			    }
			    
		    	selectedGrid.collection.fetch().then(function (rows){
		    		
		    		 var index = 0;
		    		 if(id){
			    	     for (var i = 0; i < rows.length; i++) {
			    	    	 if(rows[i].id == id){
			    	    		 index = i;
			    	    		 break;
			    	    	 }
			    	     }
		    		 }
		    		
		    		 var text = registry.byId("selectedSearchString").get("value");
		    		 var found = null;
		    	     for (var i = index+1; i < rows.length; i++) {
		    	    	 if(rows[i].name.toLowerCase().indexOf(text.toLowerCase()) !== -1){
		    	    		 selectedGrid.clearSelection();
		    	    		 selectedGrid.select(rows[i].id);
		    	    		 selectedGrid.row(rows[i].id).element.scrollIntoView();
		    	    		 found = true;
		    	    		 break;
		    	    	 }
		    	     }
		    	     
		    	     if(!found){
		    	    	 gwtSelectorShowTextMessage(localizedParams["selectedNotFound"]);
		    	     }
		    	 });
		    	
		    }
		    
		 });
	     
	     
		   
		 var selectorStore = new declare([ Rest, Cache ])(lang.mixin({
			target:"secured/JSSelectorService",
			idProperty: "id",

			_fetch: function (kwArgs) {
				var results = null;
				
				var selectorSearchStringValue = "";
				var selectorStartsWithValue = "false";
				
				if(selectorSearchString){
					selectorSearchStringValue = selectorSearchString.get("value");
				} else {
					selectorSearchStringValue = getCurrentValue();
				}
				if(selectorStartsWith){
					if(selectorStartsWith.get("checked")){
						selectorStartsWithValue = "true";
					}
				} else {
					selectorStartsWithValue = getStartWith().toString();
				}

	 	    	var httpParams = gwtSelectorGetHttpParams(kwArgs[0].start, kwArgs[0].end-kwArgs[0].start, selectorSearchStringValue, selectorStartsWithValue, getXFormId(), getGeneralFilters(), getProcName());
	 	    	
			    var scparams = {};
			    scparams["DataRequest"] = httpParams;	
			    kwArgs["scparams"] = scparams;			
			    
			    kwArgs.start = kwArgs[0].start;
			    kwArgs.end = kwArgs[0].end;					    

				results = Rest.prototype.fetchRange.call(this, kwArgs);
				results.then(function(results){
					if(getSelectedFirst() && results[0] && results[0]["id"]){
						selectorStore.selectedId = results[0]["id"];
					}
					
					if(results[0] && results[0]["message_D13k82F9g7"]){
						gwtSelectorShowMessage(results[0]["message_D13k82F9g7"]);
					}
					
					if(results && (!results[0]) && results["message_D13k82F9g7"]){
						gwtSelectorShowMessage(results["message_D13k82F9g7"]);
					}
			    }, function(err){
					gwtSelectorShowErrorMessage(err.response.text);
				});
				
				return results;
			},

	     }, {} ));
		   
		        
		 selectorGrid = new declare([Grid, Keyboard, Selection])({
		    columns: [
		    	{
		    		id:  	"name",
		    		field: 	"name",

		    		renderCell: function actionRenderCell(object, value, node, options) {
						function getTitle(title){
							var res = title;
							if(res){
								res = res.replace(/&lt;/g, "<");
								res = res.replace(/&gt;/g, ">");
								res = res.replace(/&amp;/g, "&");
							}
							return res;
						}
		    			
						if(!value){
							value = "";
						}
						
						var div = document.createElement("div");
						
						div.innerHTML = value;
				    	div.title = value;
				    	div.title = getTitle(value);
				    	return div;
		    		}
		    	}
		    ],
		               
		    collection: selectorStore,
		    minRowsPerPage: getVisibleRecordCount(),
			loadingMessage: localizedParams["loadingMessage"],
			noDataMessage: localizedParams["noDataMessage"],
		    selectionMode: "extended",
			pagingDelay: 50,		    
			deselectOnRefresh: getSelectedFirst(),
			keepScrollPosition: false,
			class: "server-multiselector-listwrapper-element",
			showHeader: false
			
		 }, 'selectorGrid');
		 
		 selectorGrid.on("dgrid-refresh-complete", function(event) {
			 if(selectorGrid.collection.selectedId){
				 selectorGrid.select(selectorGrid.collection.selectedId);
				 selectorGrid.collection.selectedId = null;
			 }
			 setTimeout(function() { document.body.className += " multiselector"; }, 500);
		 });
		 
		 selectorGrid.on(".dgrid-row:dblclick", function(event){
			 if(selectorGrid.row(event)){
	        	 selectedGrid.collection.put(selectorGrid.row(selectorGrid.row(event).id).data);
			 }		
		 });

		  
		  
//-------------------------------------------------------------------------------------------
		 
		 
		 var selectedStore = new declare([ Memory, Trackable, Cache ])(lang.mixin({
				idProperty: "id",
		     }, {} ));
			   
			        
		 var selectedGrid = new declare([Grid, Keyboard, Selection])({
			    columns: [
			    	{
			    		id:  	"name",
			    		field: 	"name",

			    		renderCell: function actionRenderCell(object, value, node, options) {
							function getTitle(title){
								var res = title;
								if(res){
									res = res.replace(/&lt;/g, "<");
									res = res.replace(/&gt;/g, ">");
									res = res.replace(/&amp;/g, "&");
								}
								return res;
							}
			    			
							if(!value){
								value = "";
							}
							
							var div = document.createElement("div");
							
							div.innerHTML = value;
					    	div.title = value;
					    	div.title = getTitle(value);
					    	return div;
			    		}
			    	}
			    ],
			               
			    collection: selectedStore,
			    minRowsPerPage: getVisibleRecordCount(),
			    selectionMode: "extended",
				pagingDelay: 50,		    
				deselectOnRefresh: getSelectedFirst(),
				keepScrollPosition: false,
				class: "server-multiselector-selectedlistwrapper-element",
				showHeader: false
				
		 }, 'selectedGrid');
		  
		 selectedGrid.on(".dgrid-row:dblclick", function(event){
			 if(selectedGrid.row(event)){
				 selectedGrid.collection.remove(selectedGrid.row(event).id);
			 }		
		 });
		 
    	 
    	 if(selectorParam.needInitSelection && selectorParam.xpathRoot && selectorParam.xpathMapping){
    		 var initSelection = getInitSelection(selectorParam.xpathRoot, selectorParam.xpathMapping);
    		 if(initSelection){
    			 var length = initSelection.values[0].length;
        		 for(var i = 0; i < length; i++){
        			 var obj = {};
            		 for(var j = 0; j < initSelection.values.length; j++){
            			 obj[initSelection.names[j]] = initSelection.values[j][i];
            		 }
            		 selectedGrid.collection.put(obj);
        		 }
    		 }
    	 }
		 

		 
	     selectorDialog.show();
	    
	     
	     function getXFormId(){
	    	 return selectorParam.id;
	     }
	     function getGeneralFilters(){
	    	 return getXMLByXPathArray(selectorParam.generalFilters ? selectorParam.generalFilters : "");
	     }
	     function getProcName(){
	    	return selectorParam.procListAndCount ? selectorParam.procListAndCount : selectorParam.procCount + "FDCF8ABB9B6540A89E350010424C2B80" + selectorParam.procList;
	     }
	     function getCurrentValue(){
	    	 return selectorParam.currentValue ? getValueByXPath(selectorParam.currentValue) : "";
	     }
	     function getManualSearch(){
	    	 return selectorParam.manualSearch ? selectorParam.manualSearch : false;
	     }
	     function getHideStartWith(){
	    	 return selectorParam.hideStartWith ? selectorParam.hideStartWith : false;
	     }
	     function getStartWith(){
	    	 return selectorParam.startWith ? selectorParam.startWith : false;
	     }
	     function getDataWidth(){
	    	 return (selectorParam.dataWidth ? selectorParam.dataWidth : "400px") + " !important";
	     }
	     function getDataHeight(){
	    	 return (selectorParam.dataHeight ? selectorParam.dataHeight : "250px") + " !important";
	     }
	     function getWindowCaption(){
	    	 return selectorParam.windowCaption ? selectorParam.windowCaption : "";
	     }
	     function getSelectedFirst(){
	    	 return selectorParam.selectedFirst ? selectorParam.selectedFirst : false;
	     }
	     function getVisibleRecordCount(){
	    	 return selectorParam.visibleRecordCount ? selectorParam.visibleRecordCount : 15;
	     }
	     function getSelectedDataWidth(){
	    	 return (selectorParam.selectedDataWidth ? selectorParam.selectedDataWidth : "312px") + " !important";
	     }
	     function getNeedClear(){
	    	 return selectorParam.needClear ? selectorParam.needClear : false;
	     }
	     
	     
    });	
}
