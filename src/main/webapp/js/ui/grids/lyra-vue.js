
	require({async:true},
	[ "vue/vue"], function(Vue){


		
		lyraGridEvents = new Vue();
		lyraGridEvents.$on('refresh', function(formClass, instanceId, addContext) {
			refreshLyraVueDGrid(getParentId(formClass, instanceId), addContext);
		});
		lyraGridEvents.$on('export-to-clipboard', function(formClass, instanceId) {
			exportToClipboardLyraVueDGrid(getParentId(formClass, instanceId));
		});
		lyraGridEvents.$on('export-to-excel', function(formClass, instanceId, exportType, fileName) {
			exportToExcelLyraVueDGrid(getParentId(formClass, instanceId), exportType, fileName);
		});
		lyraGridEvents.$on('file-download', function(formClass, instanceId, procName) {
			fileDownloadLyraVueDGrid(getParentId(formClass, instanceId), procName);
		});
		lyraGridEvents.$on('set-columns-visibility', function(formClass, instanceId, columns) {
			setColumnsVisibility(getParentId(formClass, instanceId), columns);
		});

		
		
		Vue.component('lyra-grid', {
			
		  props: ['formclass', 'instanceid', 'maincontext', 'addcontext'],
		  
		  data: function () {
			  return {
				  gridDivId: ('lyra_grid_vue_'+Math.random()*10).replace(/\./g, ""),
				  header: "",
				  footer: ""
			  }
		  },
		  
		  template: '<div><div v-html="header"></div><div :id="gridDivId"></div><div v-html="footer"></div></div>',
		  
	      mounted: function () {
	    	  
	    	    var gridDivId = this.gridDivId;
	    	    var parentId = getParentId(this.formclass, this.instanceid);
	    	    
	    	    var vueComponent = this;
	    	    
	    	    var userdata = "default";
	    	    var urlparams = location.search.substr(1).split('&').reduce(function (res, a) {
	    	      var t = a.split('=');
	    	      res[decodeURIComponent(t[0])] = t.length == 1 ? null : decodeURIComponent(t[1]);
	    	      return res;
	    	    }, {});
	    	    if(urlparams.userdata){
	    	    	userdata = urlparams.userdata;
	    	    }
	    	    if(urlparams.perspective){
	    	    	userdata = urlparams.perspective;
	    	    }
	    	  
	    		dojo.xhrPost({
	    			sync: true,
	    			url: window.appContextPath + "/secured/JSLyraVueGridMeta",
	    			postData: {
	    				userdata: userdata,

	    				formClass:  this.formclass, 
	    				instanceId: this.instanceid,
	    				
	    				mainContext: this.maincontext,
	    				addContext:  this.addcontext,
	    			},
	    			handleAs: "json",
	    			preventCache: true,
	    			load: function(metadata) {
	    				
	    				var div = document.getElementById(gridDivId);
	    				div.style = 'width:'+metadata["common"]["gridWidth"]+'; height:'+metadata["common"]["gridHeight"]+'px;';
	    				
	                    createLyraVueDGrid(this.postData.userdata, vueComponent, parentId, div.id, metadata, this.postData.formClass, this.postData.instanceId, this.postData.mainContext, this.postData.addContext);
	                    
	    		  	},
	    			error: function(error) {
	    			}
	    		});
	    		
	      },			
		  
	})		
});		



function createLyraVueDGrid(userdata, vueComponent, parentId, gridDivId, metadata, formClass, instanceId, mainContext, addContext) {
	
	
    //console.log(metadata);
    
	
	try {
		
		require({async:true},		
				[
				 "dojo/_base/lang",
				 "dgrid/List",
				 "dgrid/OnDemandGrid",
				 "dgrid/extensions/ColumnResizer",
				 "dgrid/extensions/ColumnHider",
				 "dgrid/extensions/ColumnReorder",
				 "dgrid/Selection",
		         "dgrid/CellSelection", 				
				 "dgrid/Keyboard",
				 "dojo/_base/declare",
		         "dstore/QueryResults",
				 "dstore/Rest",
				 "dstore/Cache",
		     	 "dojo/when",	         
				 "dojo/domReady!"
		         ],	function(
		        	 lang, List, Grid, ColumnResizer, ColumnHider, ColumnReorder, Selection, CellSelection, Keyboard, declare, QueryResults, Rest, Cache, when, domReady	        		 
			     ){
	    	
			
			function createWebSocket(){
	    		var protocol;
	    		if(window.location.protocol.indexOf("https") > -1){
	    			protocol = "wss";
	    		} else {
	    			protocol = "ws";
	    		}
	    		
	    		var webSocket = new WebSocket(protocol+"://"+window.location.host+window.location.pathname+"secured/JSLyraVueGridScrollBack");
	            webSocket.onopen = function(){
	            	
	    			var postData = {
    					userdata: userdata,

	    				formClass:formClass, 
	    				instanceId:instanceId,
	    				
	    				mainContext:mainContext,
	    				addContext:addContext,	    				
	    			};
	    			
	                webSocket.send(JSON.stringify(postData));
	                
	            };
	            webSocket.onmessage = function(message){
	    			var grid = arrGrids[parentId];
	    			
	    			var pos = parseInt(message.data);
	    			pos = pos * grid.rowHeight;
	    			pos = pos + grid.getScrollPosition().y-Math.floor(grid.getScrollPosition().y/grid.rowHeight)*grid.rowHeight;
	    			grid.backScroll = true;
	    			grid.scrollTo({x:0, y:pos});
	            };
	            return webSocket; 
			}
			
					
			var webSocket;
		    if((!metadata["common"]["isNeedCreateWebSocket"]) && arrGrids[parentId] && arrGrids[parentId].webSocket && (arrGrids[parentId].webSocket.readyState == arrGrids[parentId].webSocket.OPEN)){
	    		webSocket = arrGrids[parentId].webSocket;
	    	}else{
	    		webSocket = createWebSocket();
	    	}
			    

		    
				var columns = [];
				for(var k in metadata["columns"]) {
					var column = {};
					
					column["id"]        = metadata["columns"][k]["id"];
					column["parentId"]  = metadata["columns"][k]["parentId"];			
					column["field"]     = metadata["columns"][k]["id"];
					column["hidden"]    = !metadata["columns"][k]["visible"];
					//column["unhidable"] = true;
					column["label"]     = metadata["columns"][k]["caption"];
					column["valueType"] = metadata["columns"][k]["valueType"];
					column["linkId"] = metadata["columns"][k]["linkId"];					
					column["sortingAvailable"] = metadata["columns"][k]["sortingAvailable"];

					
					function getTitle(title){
						var res = title;
						if(res){
							res = res.replace(/&lt;/g, "<");
							res = res.replace(/&gt;/g, ">");
							res = res.replace(/&amp;/g, "&");
						}
						return res;
					}
					
					column["renderCell"] = function actionRenderCell(object, value, node, options) {
						
						if(!value){
							value = "";
							
						}
						
						var div = document.createElement("div");
						
						switch (this["valueType"]) {
						    case "DOWNLOAD":
								if(value && (value.trim()!="")){
									
									var recId = object.id_D13k82F9g7_; 
									var colId = this.linkId;
									var downloadFileByGetMethod = "false";
									
									if(value.indexOf(";downloadFileByGetMethod=") > -1){
										downloadFileByGetMethod = value.substring(value.indexOf(";downloadFileByGetMethod=")+25, value.length);
										value = value.substring(0, value.indexOf(";downloadFileByGetMethod="));
									}
									
									div.innerHTML = 
										"<tbody>" +
											"<tr>";
									if(value.trim()!="enableDownload"){
										div.innerHTML = div.innerHTML +							
										"<td>"+value+"" +
										"</td>";
									}
									div.innerHTML = div.innerHTML +										
												"<td  align=\"center\" style=\"vertical-align: middle;\">" +
														"<button onclick=\"gwtProcessFileDownloadLyraVue('"+arrGrids[parentId].formClass
														+"', '"+instanceId
														+"', '"+encodeURIComponent(arrGrids[parentId].mainContext)
														+"', '"+encodeURIComponent(arrGrids[parentId].addContext)														
														+"', '"+recId
														+"', '"+colId
														+"', '"+downloadFileByGetMethod
														+"', '"+arrGrids[parentId].userdata+"')\">" +
																"<img src="+metadata["columns"][k]["urlImageFileDownload"]+" title=\"Загрузить файл с сервера\"  style=\"vertical-align: middle; align: right; width: 8px; height: 8px;  \"   >" +
														"</button>" +
												"</td>" +
											"</tr>" +
										"</tbody>";
								}else{
									div.innerHTML = value;
								}
						        break; 
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
				        if(metadata["common"]["haColumnHeader"]){
							div.style["text-align"] = metadata["common"]["haColumnHeader"];
				        }
						div.innerHTML = this.label;		        
				    	div.title = this.label;
				    	
				    	div.title = getTitle(div.title);
				    	
				    	if(this.sortingPic || this.sortingAvailable){
				    		
				    		var pathPic = "resources/internal/grid/"; 
				    		var extPic = ".png";
				    		var onePic = "one.png";
				    		
							div.innerHTML = 
								"<tbody>" +
									"<tr>";

								div.innerHTML = div.innerHTML +							
								"<td>"+this.label +	"</td>";
								
							if(this.sortingPic){
								div.innerHTML = div.innerHTML +										
								"<td><span style='padding-left:10px;'> </span></td>" +
								
								"<td  align=\"right\" style=\"vertical-align: middle;\">" +
								"<a>" +
									"<img src="+(pathPic+this.sortingPic+extPic)+" title=\"Порядок и направление сортировки\"  style=\"vertical-align: middle; align: right; width: 32px; height: 14px;  \"   >" +
								"</a>" +
			    				"</td>";
							}
							
							if(this.sortingAvailable){
								div.innerHTML = div.innerHTML +	    		    				
			    				"<td><span style='padding-left:10px;'> </span></td>" +    		    				

								"<td  align=\"right\" style=\"vertical-align: middle;\">" +
								"<a>" +
										"<img src="+(pathPic+onePic)+" title=\"По данному полю есть индекс одиночной сортировки\"  style=\"vertical-align: middle; align: right; width: 14px; height: 14px;  \"   >" +
								"</a>" +
								"</td>";
							}
							
							div.innerHTML = div.innerHTML +
									"</tr>" +
								"</tbody>";
							
				    	}
				    	
						return div;
			        };
					
					columns.push(column);
				}
  	    		lyraGridEvents.$emit('columns-info', formClass, instanceId, columns);
  	    		vueComponent.$emit('columns-info', formClass, instanceId, columns);
				

			var sort = getParamFromAddContext(addContext, "sort");
			setExternalSorting(columns, sort);
			
			
			var declareGrid = [Grid, ColumnResizer, ColumnHider, ColumnReorder, Keyboard];		
			
			var selectionMode;
			if(metadata["common"]["selectionModel"] == "RECORDS"){
				selectionMode = "extended";			
				declareGrid.push(Selection);
			}else{
				selectionMode = "single";			
				declareGrid.push(CellSelection);
			}
			
			var isVisibleColumnsHeader = false;
			if(metadata["common"]["isVisibleColumnsHeader"]){
				isVisibleColumnsHeader = true;	
			}
			
			var isAllowTextSelection = false;
			if(metadata["common"]["isAllowTextSelection"]){
				isAllowTextSelection = true;	
			}
			
			
			var localizedParams = gwtLyraVueGridGetLocalizedParams();
			localizedParams = eval('('+localizedParams+')');
			
			
			var grid = new declare(declareGrid)({
				
				columns: columns, 
				//collection: store,

				webSocket: webSocket,
				
				minRowsPerPage: parseInt(metadata["common"]["limit"]),
				maxRowsPerPage: parseInt(metadata["common"]["limit"]),
				bufferRows: 0,
				farOffRemoval: 0,
				pagingDelay: 50,			
				
				selectionMode: selectionMode,
				allowTextSelection: isAllowTextSelection,			
				showHeader: isVisibleColumnsHeader,
				loadingMessage: localizedParams["loadingMessage"],
				noDataMessage: localizedParams["noDataMessage"],
				
				deselectOnRefresh: false,

				keepScrollPosition: false,
				
				renderRow: function (object) {
				     var rowElement = this.inherited(arguments);
					 if(object.rowstyle && (object.rowstyle != "")){
							rowElement.className = rowElement.className +" "+ object.rowstyle +" ";
					 }
				     return rowElement;
				},
				
		    
			    backScroll: false,
			    resScroll: null,
			    
				
				dgridOldPosition: 0,
				limit: parseInt(metadata["common"]["limit"]), 
				
				mainContext: mainContext,
				addContext: addContext,
				
				firstLoading: true,
				
				oldSort: "D13k82F9g7",
				oldFilter: "D13k82F9g7",
				
				userdata: userdata,
				formClass: formClass,
				instanceId: instanceId
				
			},  gridDivId);
		    arrGrids[parentId] = grid;
		    
		    
			var store = new declare([ Rest, Cache ])(lang.mixin({
				target:"secured/JSLyraVueGridData",
				idProperty: "id_D13k82F9g7_",
				
				grid: grid,
				
				
				_fetch: function (kwArgs) {
					
					
					if(this.grid.backScroll){
						
							results =  new QueryResults(when(this.grid.resScroll), {
								totalLength: when(this.grid._total)
							});
							
							setTimeout(function(){
								arrGrids[parentId].backScroll = false;
							}, 150);
					
							
							return results;
							
					} else {
						
						var results = null;

						var refreshId = null;
						if(this.grid.refreshId){
							refreshId = this.grid.refreshId;
							
							if(this.grid.oldStart > 0){
								this.grid.oldStart = 0;
								
								results = new QueryResults(when(this.grid.resScroll), {
									totalLength: when(this.grid._total)
								});
								return results;
							}
						}
						

						this.grid.oldStart = kwArgs[0].start;

						
					    var scparams = {};
					    scparams["userdata"] = this.grid.userdata;
					    scparams["mainContext"] = this.grid.mainContext;
					    scparams["addContext"] = this.grid.addContext;
					    scparams["offset"] = kwArgs[0].start;	
					    scparams["limit"] = kwArgs[0].end-kwArgs[0].start;
					    scparams["dgridOldPosition"] = this.grid.dgridOldPosition;
					    this.grid.dgridOldPosition = scparams["offset"];
					    this.grid.limit = scparams["limit"]; 

						var sort = getParamFromAddContext(this.grid.addContext, "sort");
						var filter = getParamFromAddContext(this.grid.addContext, "filter");
						if ((sort != this.grid.oldSort) || (filter != this.grid.oldFilter)) {
							scparams["sortingOrFilteringChanged"] = true;
						    scparams["dgridOldPosition"] = 0;
						    this.grid.dgridOldPosition = 0;
						    this.grid.oldSort = sort; 
						    this.grid.oldFilter = filter;
						}
					    
					    scparams["firstLoading"] = this.grid.firstLoading;
					    scparams["refreshId"] = refreshId;
					    scparams["formClass"] = formClass;
					    scparams["instanceId"] = instanceId;
					    kwArgs["scparams"] = scparams;			
					    
					    kwArgs.start = kwArgs[0].start;
					    kwArgs.end = kwArgs[0].end;
						
						
						results = Rest.prototype.fetchRange.call(this, kwArgs);
						results.then(function(results){
							var addData = null;
							
							if(results && (!results[0]) && results["addData_D13k82F9g7_"]){
								addData = results["addData_D13k82F9g7_"];
							}
							
							if(results[0]){

								
								arrGrids[parentId].resScroll = results;

								
								if(results[0]["addData_D13k82F9g7_"]){
									addData = results[0]["addData_D13k82F9g7_"];
								}
								
								if(results[0]["dgridNewPosition"]){
									arrGrids[parentId].dgridNewPosition = results[0]["dgridNewPosition"];
									arrGrids[parentId].dgridNewPositionId = results[0]["dgridNewPositionId"];
									
									arrGrids[parentId].dgridOldPosition = arrGrids[parentId].dgridNewPosition;
								}
								
								if(results[0]["needRecreateWebsocket"]){
						    		arrGrids[parentId].webSocket = createWebSocket();
								}
								
							}
							
							if(addData){
								vueComponent.header = addData.header; 
								vueComponent.footer = addData.footer;
							}
							
					    }, function(err){
					    	gwtLyraVueGridShowErrorTextMessage(err.response.text);
					    });
						
						return results;
					}
					
				},
				
				
			}, {} ));
		    grid.set("collection", store);
		    
		    
			for(var k in metadata["columns"]) {
				grid.styleColumn(metadata["columns"][k]["id"], metadata["columns"][k]["style"]);
			}
			
			
			grid.on("dgrid-select", function(event){
				if(event.parentType && ((event.parentType.indexOf("mouse") > -1) || (event.parentType.indexOf("pointer") > -1))){
					return;
				}
		
				emitSelect(grid.row(event.grid._focusedNode));
			});
			grid.on(".dgrid-row:click", function(event){
				if(grid.row(event) && grid.column(event)){
    				emitSelect(grid.row(event));
				}
			});
			function emitSelect(row)
			{
				obj = {};
				obj.currentRowId = row.id;
				obj.currentRowData = row.data;				
				obj.selection    = getSelection(grid);
				
  	    		lyraGridEvents.$emit('select', grid.formClass, grid.instanceId, obj);
  	    		vueComponent.$emit('select', grid.formClass, grid.instanceId, obj);
			}
			grid.on(".dgrid-row:dblclick", function(event){
				if(grid.row(event) && grid.column(event)){
					obj = {};
					obj.currentRowId = grid.row(event).id;
					obj.currentRowData = grid.row(event).data;					
					obj.selection    = getSelection(grid);
					
      	    		lyraGridEvents.$emit('dblclick', grid.formClass, grid.instanceId, obj);
      	    		vueComponent.$emit('dblclick', grid.formClass, grid.instanceId, obj);
				}
			});
			
			
			grid.on("dgrid-sort", function(event) {
				var sort=event.sort[0].property;
				if(event.sort[0].descending){
					sort=sort+" desc";
				}
				
				var primaryKey = metadata["common"]["primaryKey"].split(",");
				for (var n = 0; n < primaryKey.length; n++) {
					if((n == 0) && (event.sort[0].property == primaryKey[n])){
						continue;
					}
					sort=sort+","+primaryKey[n];
					if(event.sort[0].descending){
						sort=sort+" desc";
					}
				}

				
				var addContext = event.grid.addContext;
				
    			var refreshParams = {
		        		selectKey: "",		
		        		sort: sort,
		        		filter: ""
    			};
				
    			var objAddContext; 
		        if(!addContext || addContext.trim() == ''){
		        	objAddContext = {refreshParams: refreshParams};
		        }else{
		    		objAddContext =	JSON.parse(addContext);
		    		if(objAddContext.refreshParams){
		    			objAddContext.refreshParams.selectKey = "";
		    			objAddContext.refreshParams.sort = sort;
		    		}else{
		    			objAddContext.refreshParams = refreshParams;
		    		}
		        } 
		        
	        	event.grid.addContext = JSON.stringify(objAddContext);
	        	
				setExternalSorting(event.grid._columns, sort);
				event.grid.renderHeader();
				
				event.grid.firstLoading = true;

			});
			
			
			grid.on("dgrid-refresh-complete", function(event) {
				if(event.grid.firstLoading){
					
					if(event.grid.dgridNewPosition){
						var pos = parseInt(event.grid.dgridNewPosition);
						pos = pos * event.grid.rowHeight;
						event.grid.backScroll = true;
						event.grid.scrollTo({x:0, y:pos});
						
						event.grid.select(event.grid.row(event.grid.dgridNewPositionId));
						
						event.grid.dgridNewPosition = null;
						event.grid.dgridNewPositionId = null;
					}
					
					
					if(metadata["common"]["selectionModel"] == "RECORDS"){
						if(metadata["common"]["selRecId"]){
							event.grid.select(event.grid.row(metadata["common"]["selRecId"]));
						}
					}else{
						if(metadata["common"]["selRecId"] && metadata["common"]["selColId"]){
							for(var col in event.grid.columns){
								if(event.grid.columns[col].label == metadata["common"]["selColId"]){
									event.grid.select(event.grid.cell(metadata["common"]["selRecId"], col));
									break;
								}
							}
						}
					}
					event.grid.firstLoading = false;
				}
			});
			
			if(columns[0]){
				grid.resizeColumnWidth(columns[0].field, "5px");
			}
		    
		});
		
	} catch (err) {
		gwtLyraVueGridShowErrorTextMessage(err);
		throw err;
	}	


	}



function getParentId(formClass, instanceId){
	return formClass+"."+instanceId;
}

function getSelection(grid)
{
    var selection = [];
    for(var id in grid.selection){
        if(grid.selection[id]){
        	selection.push(id);
        }
    }
    return selection; 
}

function setExternalSorting(columns, sort){
	if(!sort){
		return;
	}
	
	for (var n = 0; n < columns.length; n++) {
		columns[n].sortingPic = null;
	}
	
	var desc = false;
	var arr = sort.split(",");
	for (var m = 0; m < arr.length; m++) {
		
		if((m == 0) && (arr[m].toLowerCase().indexOf(" desc")>-1)){
			desc = true;
		}
		
		var sortName = arr[m].substring(0, arr[m].indexOf(" "));
		if(sortName == ""){
			sortName = arr[m];
		}
		
		for (var n = 0; n < columns.length; n++) {
			if(columns[n].id == sortName){
				if(desc){
					columns[n].sortingPic = "d";
				} else {
					columns[n].sortingPic = "a";
				}
				
				columns[n].sortingPic = columns[n].sortingPic + (m+1);

				break;
			}
		}
		
	}
}


    function getParamFromAddContext(addContext, param){
    	var ret = "";
        if(addContext && addContext.trim() != ''){
    		var objAddContext =	JSON.parse(addContext);
    		if(objAddContext && objAddContext.refreshParams && objAddContext.refreshParams[param]){
    			ret = objAddContext.refreshParams[param];
    		}
        }
    	return ret;
    }

	function refreshLyraVueDGrid(parentId, addContext){
        if(getParamFromAddContext(addContext, "selectKey") == "current"){
    		var row;
    		if(arrGrids[parentId].oldFocusedNode && arrGrids[parentId].row(arrGrids[parentId].oldFocusedNode)){
    			row = arrGrids[parentId].row(arrGrids[parentId].oldFocusedNode);
    		} else {
    			row = arrGrids[parentId].row(arrGrids[parentId]._focusedNode);
    		}
    		
			var sort = getParamFromAddContext(addContext, "sort");
			var filter = getParamFromAddContext(addContext, "filter");
			if ((sort == arrGrids[parentId].oldSort) && (filter == arrGrids[parentId].oldFilter)) {
		        if(addContext && addContext.trim() != ''){
		        	arrGrids[parentId].addContext = addContext;
		        }
				
				arrGrids[parentId].refreshId = arrGrids[parentId].row(row).id;
				
	   	     	arrGrids[parentId].firstLoading = false;    
	    		arrGrids[parentId].refresh({keepScrollPosition: true});
			}else{
				
				var selectKey = getSelection(arrGrids[parentId])[0];
				
			    if(!selectKey){
			    	gwtLyraVueGridShowErrorTextMessage("Отсутствует выделенная запись. Выполнение операции невозможно.");
			    	return;
			    }
				
    			var refreshParams = {
		        		selectKey: selectKey,		
		        		sort: "",
		        		filter: ""
    			};
				
    			var objAddContext; 
		        if(!addContext || addContext.trim() == ''){
		        	objAddContext = {refreshParams: refreshParams};
		        }else{
		    		objAddContext =	JSON.parse(addContext);
		    		if(objAddContext.refreshParams){
		    			objAddContext.refreshParams.selectKey = selectKey;
		    		}else{
		    			objAddContext.refreshParams = refreshParams;
		    		}
		        }
		        
		        arrGrids[parentId].addContext = JSON.stringify(objAddContext);
		        
				setExternalSorting(arrGrids[parentId]._columns, sort);
				arrGrids[parentId].renderHeader();

	   	     	arrGrids[parentId].firstLoading = true;        	
	    		arrGrids[parentId].refresh({keepScrollPosition: false});
			}
        }else{
            if(addContext && addContext.trim() != ''){
            	arrGrids[parentId].addContext = addContext;
            }
        	
			var sort = getParamFromAddContext(arrGrids[parentId].addContext, "sort");
	        if(sort && sort.trim() != ''){
				setExternalSorting(arrGrids[parentId]._columns, sort);
				arrGrids[parentId].renderHeader();
	        }
	        
   	     	arrGrids[parentId].firstLoading = true;        	
    		arrGrids[parentId].refresh({keepScrollPosition: false});
        }
	}

	function exportToClipboardLyraVueDGrid(parentId){
		var str = "";
		
		var grid = arrGrids[parentId];
		
		for(var col in grid.columns){
			str = str + grid.columns[col].label + "\t";
		}
		
		str = str + "\n";
			
	    for(var id in grid.selection){
	        if(grid.selection[id]){
	        	for(var col in grid.columns){
	        		str = str + grid.row(id).data[col] + "\t";
	        	}
	        	str = str + "\n";
	        }
	    }
	
		gwtLyraVueGridExportToClipboard(str);
	}

	function exportToExcelLyraVueDGrid(parentId, exportType, fileName){

		var row;
		if(arrGrids[parentId].oldFocusedNode && arrGrids[parentId].row(arrGrids[parentId].oldFocusedNode)){
			row = arrGrids[parentId].row(arrGrids[parentId].oldFocusedNode);
		} else {
			row = arrGrids[parentId].row(arrGrids[parentId]._focusedNode);
		}
		var refreshId = arrGrids[parentId].row(row).id;
		
		gwtLyraVueGridExportToExcel(
				arrGrids[parentId].formClass, 
				arrGrids[parentId].instanceId, 
				arrGrids[parentId].mainContext,
				arrGrids[parentId].addContext,
				refreshId,
				arrGrids[parentId].dgridOldPosition,
				arrGrids[parentId].limit,
				exportType, 
				fileName,
				arrGrids[parentId].userdata);
		
	}

	function fileDownloadLyraVueDGrid(parentId, procName){
		
		var recId = getSelection(arrGrids[parentId])[0];
		
		gwtProcessFileDownloadLyraVue(
				arrGrids[parentId].formClass,
				arrGrids[parentId].instanceId,
				encodeURIComponent(arrGrids[parentId].mainContext),
				encodeURIComponent(arrGrids[parentId].addContext),
				recId,
				procName,
				"false",
				arrGrids[parentId].userdata);
		
	}

	function setColumnsVisibility(parentId, columns){
		for (var n = 0; n < columns.length; n++) {
			arrGrids[parentId].toggleColumnHiddenState(columns[n].id, !columns[n].visible);
		}
	}
	

	
	
	
	