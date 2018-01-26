function showSelector(selectorParam) {
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
			 "dojox/timing",             
	         "dojo/domReady!"
	     ], function(registry, Dialog, lang, Grid, Selection, Keyboard, declare, Rest, Cache, timing){
		
		
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
		    '<table class="dijitDialogPaneContentArea">'+
		    
	        '<tr>'+
	        '    <td style="margin: 0; padding: 0;">'+
		    '    	<table width="100%">'+
	        '		<tr>'+
	        '       	<td style="width: 100%; margin: 0; padding: 0;">'+	        
	        '    			<input style="width: 100%; margin: 0; padding: 0;" data-dojo-type="dijit/form/TextBox" id="selectorSearchString" data-dojo-props="intermediateChanges:true" value="'+getCurrentValue()+'" class="server-selector-searchstringtextbox-element">'+
            '       	</td>';
	    if(getManualSearch()){
		    content1 = content1 +
	        '       	<td>'+		    
	        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectorSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorGrid.refresh();}, iconClass: '+"'server-selector-manualsearchbutton-image'"+', showLabel: false" class="server-selector-manualsearchbutton-element"></button>'+
            '       	</td>';		    
	    }
			content1 = content1 +
	        '       	<td>'+			
	        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectorSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorSearchString.clear();}, iconClass: '+"'server-selector-clearbutton-image'"+', showLabel: false" class="server-selector-clearbutton-element"></button>'+
            '       	</td>'+
	        '		</tr>'+	        
		    '    	</table>'+	        
	        '    </td>'+        
	        '</tr>';
		
	    if(!getHideStartWith()){
			content1 = content1 + 
		    '<tr>'+
		    '    <td>'+
		    '    	 <input data-dojo-type="dijit/form/CheckBox" id="selectorStartsWith" data-dojo-props="checked: '+getStartWith()+'" class="server-selector-checkbox-element">'+
		    '    	 <label for="selectorStartsWith" class="server-selector-checkbox-element">'+localizedParams["startWithTitle"]+'</label>'+
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
	        '	<button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onFocus:function(){selectorDialog.executeOK();selectorDialog.hide();selectorDialog.delayAfterHide();}" class="server-selector-okbutton-element">OK</button>'+	        
	        ' 	<button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.hide();selectorDialog.delayAfterHide();}" class="server-selector-cancelbutton-element">'+localizedParams["cancelTitle"]+'</button>'+
	        '</div>';
		
		var content = content1 + content4;		
		

		selectorSearchString = null;
		var selectorStartsWith = null;

		selectorDialog = new Dialog({			 

		    title: getWindowCaption(),
		    content: content,
		    class : "server-selector-popup",
		    	 
		    onHide: function(){
		    	this.destroyRecursive(false);
		    },
		    
		    onFocus: function(){
		    	if(this.loaded){
		    		return;
		    	}
		    	this.loaded = true;
		    	
		    	selectorGrid.startup();
		    	
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
		    
		    delayAfterHide: function(){
		    	setTimeout(function() { document.body.className = document.body.className.replace("selector","");}, 1000);
			},
		    
		    executeOK: function(){
		    	
		    	var selected = null;
		        for(var id in selectorGrid.selection){
		            if(selectorGrid.selection[id]){
		            	selected = selectorGrid.row(id).data;
		            	break;
		            }
		        }
		        if(!selected){
		        	selected = {};
		        }
		        
		    	if(selectorParam.xpathMapping){
			    	setXFormByXPath(true, selected, selectorParam.xpathMapping, localizedParams["subformId"]);
		    	}
		    	
		    	if(selectorParam.onSelectionCompleteAction){
		    		selectorParam.onSelectionCompleteAction(true, selected);
		    	}
		    	
		    	if(selectorParam.onSelectionComplete){
		    		selectorParam.onSelectionComplete(true, selected);
		    	}
		    	
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
		    selectionMode: "single",
			pagingDelay: 50,		    
			deselectOnRefresh: getSelectedFirst(),
			keepScrollPosition: false,
			class: "server-selector-listwrapper-element",
			showHeader: false
			
		 }, 'selectorGrid');
		 
		 selectorGrid.on("dgrid-refresh-complete", function(event) {
			 if(selectorGrid.collection.selectedId){
				 selectorGrid.select(selectorGrid.collection.selectedId);
				 selectorGrid.collection.selectedId = null;
			 }
			 setTimeout(function() { document.body.className += " selector"; }, 500);
		 });
		 
		 selectorGrid.on(".dgrid-row:dblclick", function(event){
			 selectorDialog.executeOK();
			 selectorDialog.hide();
		 });
		 
		 
		 
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
	     
         
    });	
}
