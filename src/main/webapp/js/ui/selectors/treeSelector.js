function showTreeSelector(selectorParam) {
	
	require({async:true},
			[
             "dijit/registry",
             "dijit/Dialog",
             "dojo/_base/lang",
             "dgrid/OnDemandGrid",
             "dgrid/Selection",
             "dgrid/Keyboard",
             "dgrid/Tree",
             "dgrid/Selector",             
			 "dgrid/extensions/ColumnResizer",             
             "dojo/_base/declare",
             "dstore/Rest",
             "dstore/Cache",
			 "dstore/Tree",             
			 "dojox/timing",          
			 "dojo/dom-construct",
	         "dojo/domReady!"
	     ], function(registry, Dialog, lang, Grid, Selection, Keyboard, Tree, Selector, ColumnResizer, declare, Rest, Cache, TreeStore, timing, domConstruct){
		
		
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
	        '    			<input style="width: 100%; margin: 0; padding: 0;" data-dojo-type="dijit/form/TextBox" id="selectorSearchString" data-dojo-props="intermediateChanges:true" value="'+getCurrentValue()+'" class="server-treeselector-searchstringtextbox-element">'+
            '       	</td>';
	    if(getManualSearch()){
		    content1 = content1 +
	        '       	<td>'+		    
	        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectorSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorGrid.refresh();}, iconClass: '+"'server-treeselector-manualsearchbutton-image'"+', showLabel: false" class="server-treeselector-manualsearchbutton-element"></button>'+
            '       	</td>';		    
	    }
			content1 = content1 +
	        '       	<td>'+			
	        '    	 		<button style="margin: 0; padding: 0 0 0 5;" for="selectorSearchString" data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorSearchString.clear();}, iconClass: '+"'server-treeselector-clearbutton-image'"+', showLabel: false" class="server-treeselector-clearbutton-element"></button>'+
            '       	</td>'+
	        '		</tr>'+	        
		    '    	</table>'+	        
	        '    </td>'+        
	        '</tr>';
		
	    if(!getHideStartWith()){
			content1 = content1 + 
		    '<tr>'+
		    '    <td>'+
		    '    	 <input data-dojo-type="dijit/form/CheckBox" id="selectorStartsWith" data-dojo-props="checked: '+getStartWith()+'" class="server-treeselector-checkbox-element">'+
		    '    	 <label for="selectorStartsWith" class="server-treeselector-checkbox-element">'+localizedParams["startWithTitle"]+'</label>'+
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
	        '	<button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onFocus:function(){selectorDialog.executeOK();selectorDialog.hide();}" class="server-treeselector-okbutton-element">OK</button>'+	        
	        ' 	<button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){selectorDialog.hide();}" class="server-treeselector-cancelbutton-element">'+localizedParams["cancelTitle"]+'</button>'+
	        '</div>';
		
		var content = content1 + content4;		
		

		selectorSearchString = null;
		var selectorStartsWith = null;

		selectorDialog = new Dialog({			 

		    title: getWindowCaption(),
		    content: content,
		    class : "server-treeselector-popup",
		    	 
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
		    
		    executeOK: function(){
		    	
		    	if(single){
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
		    	}else{
			    	var selected = [];
			        for(var id in selectorGrid.selection){
			            if(selectorGrid.selection[id] && selectorGrid.row(id) && selectorGrid.row(id).data){
					        selected.push(selectorGrid.row(id).data);
			            }
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
		    	}
		    	
		    }
		    

		 });

	     
			     
		 var selectorStore = new declare([ Rest, Cache, TreeStore ])(lang.mixin({
			target:"secured/JSTreeSelectorService",
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

				var storeParentName = null;
				if(this.storeParentId){
					storeParentName = selectorGrid.row(this.storeParentId).data["name"];
				}
				
	 	    	var httpParams = gwtTreeSelectorGetHttpParams(selectorSearchStringValue, selectorStartsWithValue, getXFormId(), getGeneralFilters(), getProcName(), this.storeParentId, storeParentName);
	 	    	
			    var scparams = {};
			    scparams["TreeDataRequest"] = httpParams;	
			    kwArgs["scparams"] = scparams;			
			    
			    kwArgs.start = kwArgs[0].start;
			    kwArgs.end = kwArgs[0].end;					    

				results = Rest.prototype.fetchRange.call(this, kwArgs);
				results.then(function(results){
					if(results && results[0] && results[0]["message_D13k82F9g7"]){
						gwtSelectorShowMessage(results[0]["message_D13k82F9g7"]);
					}
					
					if(results && (!results[0]) && results["message_D13k82F9g7"]){
						gwtSelectorShowMessage(results["message_D13k82F9g7"]);
					}
					
					if(results){
						for (var i = 0; i < results.length; i++) {
							if(results[i].checked && (results[i].checked.toLowerCase() == "true")){
								if(selectorGrid){
									
									if(selectorGrid.single){
								        for(var id in selectorGrid.selection){
								            if(selectorGrid.selection[id]){
								            	selectorGrid.deselect(id);
								            }
								        }
									}
									
									selectorGrid.select(results[i].id);
									
									if(selectorGrid.single){
										break;
									}
									
								}
							}
						}
					}
					
			    }, function(err){
					gwtSelectorShowErrorMessage(err.response.text);
				});
				
				return results;
			},

	     }, {} ));
		 selectorStore.storeParentId = null;		 
		 
		 selectorStore.getRootCollection = function () {
			 selectorStore.storeParentId = null;
		   	 return TreeStore.prototype.getRootCollection.call(this);
		 };
			
		 selectorStore.getChildren = function(object){
			 selectorStore.storeParentId = selectorStore.getIdentity(object);
		     return TreeStore.prototype.getChildren.call(this, object);
		 };
		 
		 function getTitle(title){
			var res = title;
			if(res){
				res = res.replace(/&lt;/g, "<");
				res = res.replace(/&gt;/g, ">");
				res = res.replace(/&amp;/g, "&");
			}
			return res;
		}
		 
		 var single = null;
		 for(var i = 0; i < selectorParam.columns.length; i++) {
				var column = selectorParam.columns[i];
				
				if(!column.type){
					column.type = "string";
				}
				column.type = column.type.toUpperCase();
				
				column.sortable = false;
				
				if(column.selector && (column.selector.toLowerCase() == "radio")){
					single = true;
				}
				 
				column["renderCell"] = function actionRenderCell(object, value, node, options) {
					
					if(!value){
						value = "";
						
					}
					
					var div = document.createElement("div");
					
			        if(this.horAlign){
						div.style["text-align"] = this.horAlign;
			        }
					
					switch (this.type) {
					    case "IMAGE":
					    	var start = "<a><img border=\"0\" src=\"";
					    	var end = "\"></a>";
					    	var sep = ":";
					    	
					    	var title = value;
					    	
							if(value.indexOf(sep)>-1){
								title = value.substring(value.indexOf(sep)+1, value.length);
								value = value.substring(0, value.indexOf(sep));
							}
					    	
							div.innerHTML =  start + value + end;
							div.title = title;	
							
					        break; 
					    default:
							div.innerHTML = value;
					    	div.title = value;
					    	break;
					}					
					
			    	div.title = getTitle(div.title);

					return div;
		        };
		        
		        column["renderHeaderCell"] = function actionRenderCell(node) {
					var div = document.createElement("div");
			        if(this.horAlign){
						div.style["text-align"] = this.horAlign;
			        }
					div.innerHTML = this.label;		        
			    	div.title = this.label;
			    	
			    	div.title = getTitle(div.title);
			    	
					return div;
		        };
				
		        if(column["renderExpando"]){
					column["renderExpando"] = function columnRenderExpando(level, hasChildren, expanded, object) {
				        var dir = this.grid.isRTL ? "right" : "left",
							cls = "dgrid-expando-icon";
						if((hasChildren == "1") || (hasChildren == "true")){
							cls += " ui-icon ui-icon-triangle-1-" + (expanded ? "se" : "e");							
						}
						node = domConstruct.create('div', {
							className: cls,
							innerHTML: '&nbsp;',
							style: 'margin-' + dir + ': ' + (level * this.grid.treeIndentWidth) + 'px; float: ' + dir + ';'
						});

				    	var start = "<a><img border=\"0\" src=\"";
				    	var end = "\"></a>";
						if((hasChildren == "1") || (hasChildren == "true")){
							if(expanded){
								if(object.treeGridNodeOpenIcon && object.treeGridNodeOpenIcon.trim().length > 0){
									node.innerHTML = start+object.treeGridNodeOpenIcon+end;
								}
							}
							else{
								if(object.treeGridNodeCloseIcon && object.treeGridNodeCloseIcon.trim().length > 0){
									node.innerHTML = start+object.treeGridNodeCloseIcon+end;
								}
							}
						}else{
							if(object.treeGridNodeLeafIcon && object.treeGridNodeLeafIcon.trim().length > 0){
								node.innerHTML = start+object.treeGridNodeLeafIcon+end;									
							}
						}
						return node;
					};
				}

		        
			 
		 }
		 
		 
		   
		 selectorGrid = new declare([Grid, Keyboard, Selection, Selector, ColumnResizer, Tree])({
			 
		    columns: selectorParam.columns,		
		    
			collection: selectorStore.getRootCollection(),		    
			minRowsPerPage: 100000,
			loadingMessage: localizedParams["loadingMessage"],
			noDataMessage: localizedParams["noDataMessage"],
		    selectionMode: "none",
			pagingDelay: 50,		    
			deselectOnRefresh: getSelectedFirst(),
			keepScrollPosition: false,
			collapseOnRefresh: true,
			class: "server-treeselector-listwrapper-element",
			showHeader: !getHideHeaders(),
			
		    single: single,
		    
		    allowSelectAll: !single && getAllowSelectAll()
			
		 }, 'selectorGrid');
		 
		 selectorGrid.on("dgrid-select", function(event){
				if(!event.grid.single && getCheckParent()){
					var parentId = event.rows[0].data["parentId_D13k82F9g7"];
					event.grid.select(parentId);
				}
		 });
		 
		 selectorGrid.on("dgrid-deselect", function(event){
				if(!event.grid.single && getCheckParent()){
					var parentId = event.rows[0].data["parentId_D13k82F9g7"];
					event.grid.deselect(parentId);
				}
		 });
		 
		 
		 selectorDialog.show();
		 

			 
	     function getXFormId(){
	    	 return selectorParam.id;
	     }
	     function getGeneralFilters(){
	    	 return getXMLByXPathArray(selectorParam.generalFilters ? selectorParam.generalFilters : "");
	     }
	     function getProcName(){
	    	return selectorParam.getDataProcName ? selectorParam.getDataProcName : "";
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
	     function getHideHeaders(){
	    	 return selectorParam.hideHeaders ? selectorParam.hideHeaders : false;
	     }
	     function getNeedClear(){
	    	 return selectorParam.needClear ? selectorParam.needClear : false;
	     }
	     function getCheckParent(){
	    	 return selectorParam.checkParent ? selectorParam.checkParent : false;
	     }
	     function getAllowSelectAll(){
	    	 return selectorParam.allowSelectAll ? selectorParam.allowSelectAll : false;
	     }

	     
	     
    });	
}
