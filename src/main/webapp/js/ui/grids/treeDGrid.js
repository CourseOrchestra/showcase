function createTreeDGrid(elementId, parentId, metadata) {
	

try {
	
	
	var arrScripts = [
	     			 "dojo/_base/lang",
	    	         "dojo/has",			 
	    			 "dgrid/List",
	    			 "dgrid/OnDemandGrid",
	    	         "dgrid/extensions/CompoundColumns", 
	    	         "dgrid/ColumnSet", 
	    			 "dgrid/extensions/ColumnResizer",
	    			 "dgrid/Selection",
	    	         "dgrid/CellSelection", 				
	    			 "dgrid/Editor",
	    			 "dgrid/Keyboard",
	    			 "dgrid/Tree",			 
	    			 "dojo/_base/declare",
	    	         "dstore/QueryResults",
	    			 "dstore/Rest",
	    			 "dstore/Trackable",
	    			 "dstore/Cache",
	    			 "dstore/Tree",			 
	    	     	 "dojo/dom-construct",	     
	    	     	 "dojo/when",
	    			 "dojo/domReady!"
	                 ];
	
	var arrEditors = [
		     	         "dijit/form/Button",
		    	         "dijit/form/DropDownButton",
		    	         "dijit/form/ComboButton",
		    	         "dijit/form/ToggleButton",
		    	         "dijit/form/CurrencyTextBox",
		    	         "dijit/form/DateTextBox",
		    	         "dijit/form/NumberSpinner",
		    	         "dijit/form/NumberTextBox",
		    	         "dijit/form/TextBox",
		    	         "dijit/form/TimeTextBox",
		    	         "dijit/form/ValidationTextBox",
		    	         "dijit/form/SimpleTextarea",
		    	         "dijit/form/Textarea",
		    	         "dijit/form/Select",
		    	         "dijit/form/ComboBox",
		    	         "dijit/form/MultiSelect",
		    	         "dijit/form/FilteringSelect",
		    	         "dijit/form/HorizontalSlider",
		    	         "dijit/form/VerticalSlider",
		    	         "dijit/form/CheckBox",
		    	         "dijit/form/RadioButton",
		    	         "dijit/form/DataList"
		             ];
		
		if(!metadata["common"]["readonly"]){
			for(var i = 0; i < arrEditors.length; i++){
				arrScripts.push(arrEditors[i]);
			}
		}
	

	require({async:true}, arrScripts ,function(
        		 lang, has, List, Grid, CompoundColumns, ColumnSet, ColumnResizer, Selection, CellSelection, Editor, Keyboard, Tree, declare, QueryResults, Rest, Trackable, Cache, TreeStore, domConstruct, when, domReady,	        		 
	        	 Button,DropDownButton,ComboButton,ToggleButton,CurrencyTextBox,DateTextBox,NumberSpinner,NumberTextBox,TextBox,TimeTextBox,ValidationTextBox,SimpleTextarea,Textarea,Select,ComboBox,MultiSelect,FilteringSelect,HorizontalSlider,VerticalSlider,CheckBox,RadioButton,DataList	        		 
		     ){
		
		
		    var firstLoading = true;
		    var needActionExpandAllRecords = metadata["common"]["expandAllRecords"];
		

			var store = new declare([ Rest, Trackable, Cache, TreeStore ])(lang.mixin({
				target:"secured/JSGridService",
				idProperty: "id",			
				
				_fetch: function (kwArgs) {
					var results = null;
					
					var sortColId  = null;
					var sortColDir = null;
					if(firstLoading){
						if(this.initialSort){
							for(var i = 0; i<this.initialSort.length; i++){
								for(var k in metadata["columns"]) {
									if(metadata["columns"][k]["id"] == this.initialSort[i].property){
										sortColId =	metadata["columns"][k]["caption"];
										break;
									}
								}
								if(this.initialSort[i].descending){
									sortColDir = "DESC";
								}
								else{
									sortColDir = "ASC";
								}
								break;
							}
						}
					}
					else{
						if(grid && grid.sort){
							for(var i = 0; i<grid.sort.length; i++){
								var sort = grid.sort[i];
								sortColId = grid.columns[sort.property].label;
								if(sort.descending){
									sortColDir = "DESC";
								}
								else{
									sortColDir = "ASC";
								}
								break;
							}
						}
					}
					
		 	    	var httpParams = gwtGetHttpParamsTree(elementId, kwArgs[0].start, kwArgs[0].end-kwArgs[0].start, sortColId, sortColDir, this.storeParentId);
		 	    	httpParams = eval('('+httpParams+')');	 	 
		 	    	
				    var scparams = {};
				    scparams[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
				    scparams[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];
				    kwArgs["scparams"] = scparams;		
				    
				    kwArgs.start = kwArgs[0].start;
				    kwArgs.end = kwArgs[0].end;					    
				    
					results = Rest.prototype.fetchRange.call(this, kwArgs);
					results.then(function(results){
						var events = null;
						var addData = null;
						
						if(results && (!results[0]) && results["addData_D13k82F9g7_"]){
							addData = results["addData_D13k82F9g7_"];
						}
						
						if(results[0]){
							if(results[0]["events"]){
								events = results[0]["events"];
							}
							if(results[0]["addData_D13k82F9g7_"]){
								addData = results[0]["addData_D13k82F9g7_"];
							}
						}
						var wrongSelection = "";
						if(results && grid && grid.needAdjustSelectionRecords){
						    var i = 0;
					        for(var id in grid.selection){
					            if(grid.selection[id]){
					            	var exist = null;
					            	for (var j = 0; j < results.length; j++) {
					            		if(id == results[j].id){
					            			exist = true;
					            			break;
					            		}
					            	}
					            	if(!exist){
						            	if(i > 0){
						            		wrongSelection = wrongSelection+metadata["common"]["stringSelectedRecordIdsSeparator"];	
						            	}
						            	wrongSelection = wrongSelection+id;
						            	i++;
						            	
						            	grid.deselect(id);
					            	}
					            }
					        }
							grid.needAdjustSelectionRecords = null;
						}
						gwtAfterLoadDataTree(elementId, events, addData, wrongSelection);
						
						if(grid && grid.expandAllRecords && metadata["common"]["selRecId"] && needActionExpandAllRecords){
							for(var i = 0; i<results.length; i++){
								if(results[i].id == metadata["common"]["selRecId"]){
									needActionExpandAllRecords = null;
									gwtAfterClickTree(elementId, metadata["common"]["selRecId"], metadata["common"]["selColId"], metadata["common"]["selRecId"]);
									break;
								}
							}
						}
						
						if(grid){
							grid.dirty = {};
						}
						
				    }, function(err){
						gwtShowErrorMessageTree(elementId, err.response.text);
					});

					
					return results;
				},
				
				
				put: function(object){
					if((object.id).indexOf("addRecord") > -1 ){ // Добавление записи
						
						object["editor"] = "addRecord";
						
						var strObject = JSON.stringify(object);
			 	    	var httpParams = gwtEditorGetHttpParamsTree(elementId, strObject, object["editor"]);
			 	    	httpParams = eval('('+httpParams+')');

					    object[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
					    object[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];
					    
						var result = this.inherited(arguments);
						result.then(function(value){
								if(value.success == '1'){
									arrGrids[parentId].refresh();								
								}
								else{
								}
								gwtShowMessageTree(elementId, value.message, object["editor"]);
						    }, function(err){
						    	alert("Произошла ошибка при добавлении записи:\n"+err+"\nПодробности находятся в консоли броузера.");
						    });
					    return result;
						
					}else{  //Сохранение
						
						object["editor"] = "save";
						
						var strObject = JSON.stringify(object, function(key, value) {
							  if (
									  (key == "dirty")							  
								   || (key == "gridContextName") 
							       || (key == "elementInfoName")
							       || (key == object["gridContextName"])
							       || (key == object["elementInfoName"])
							     )
							  {
								  return undefined;						  
							  }
							  return value;
						});
			 	    	var httpParams = gwtEditorGetHttpParamsTree(elementId, strObject, object["editor"]);
			 	    	httpParams = eval('('+httpParams+')');

			 	    	object["gridContextName"] = httpParams["gridContextName"];
			 	    	object["elementInfoName"] = httpParams["elementInfoName"];
					    object[httpParams["gridContextName"]] = httpParams["gridContextValue"];	
					    object[httpParams["elementInfoName"]] = httpParams["elementInfoValue"];
		 
					    object.dirty = JSON.stringify(grid.dirty);

						var result = this.inherited(arguments);
						
						result.then(function(value){
								if(value.success == '1'){
									
									for(var id in grid.dirty) {
										grid.row(id).element.className = grid.row(id).element.className.replace("jsgrid-record-editing", "");
									}
									
								}
								else{
							        grid.dirty = JSON.parse(object.dirty);
								}
								if(value.refreshAfterSave == 'true'){
							        grid.refresh();								
								}
								gwtShowMessageTree(elementId, value.message, object["editor"]);
						    }, function(err){
						        grid.dirty = JSON.parse(object.dirty);				    	
						    	alert("Произошла ошибка при сохранении данных:\n"+err+"\nПодробности находятся в консоли броузера.");
						    });
						    
					    return result;
						
					}
				}
			}, {} ));
			store.storeParentId = null;
			
			store.getRootCollection = function () {
                store.storeParentId = null;
		    	return TreeStore.prototype.getRootCollection.call(this);
			};
			
			store.getChildren = function(object){
                store.storeParentId = store.getIdentity(object);
		    	return TreeStore.prototype.getChildren.call(this, object);
			};
			
			
			var columns = [];
			for(var k in metadata["columns"]) {
				var column = {};
				
				if(metadata["common"]["readonly"] || metadata["columns"][k]["readonly"]){
				}else{
					var editor = eval("("+metadata["columns"][k]["editor"]+")");
					for(var m in editor) {
						column[m] = editor[m];	
					}
					column["editable"] = true;
				}
				
				column["id"]        = metadata["columns"][k]["id"];
				column["parentId"]  = metadata["columns"][k]["parentId"];			
				column["field"]     = metadata["columns"][k]["id"];			
				column["label"]     = metadata["columns"][k]["caption"];
				column["sortable"]  = "true";
				column["valueType"] = metadata["columns"][k]["valueType"];
				column["firstSortDirectionDescending"] = metadata["columns"][k]["firstSortDirectionDescending"];
				

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
								
								var recId = object.id; 
								var colId = this.id;
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
													"<button onclick=\"gwtProcessFileDownloadTree('"+elementId+"', '"+recId+"', '"+colId+"', '"+downloadFileByGetMethod+"')\">" +
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
			    	
					return div;
		        };
		        
				if(column["id"] == "col1"){
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
							
						if((hasChildren == "1") || (hasChildren == "true")){
							if(expanded){
								if(object.TreeGridNodeOpenIcon && object.TreeGridNodeOpenIcon.trim().length > 0){
									node.innerHTML = object.TreeGridNodeOpenIcon;
								}
							}
							else{
								if(object.TreeGridNodeCloseIcon && object.TreeGridNodeCloseIcon.trim().length > 0){
									node.innerHTML = object.TreeGridNodeCloseIcon;										
								}
							}
						}else{
							if(object.TreeGridNodeLeafIcon && object.TreeGridNodeLeafIcon.trim().length > 0){
								node.innerHTML = object.TreeGridNodeLeafIcon;									
							}
						}
						if(object.col1){
//							node.title = getTitle(object.col1);
						}
						return node;
					};
				}
				
				if(column["editable"]){
					column["canEdit"] = function columnCanEdit(object, value){
						if(object.readonly && (((object.readonly).indexOf("all;") > -1 ) || ((object.readonly).indexOf(this.id+";") > -1 ))){
							return false;
						}
						
						for(var id in this.grid.dirty) {
							if(id != object.id){
								return false;
							}
						}
						
						return true;					
					};
				}		
				
				columns.push(column);
			}
			var columnFieldSave = null;
			if(metadata["common"]["fieldSave"]){
				columnFieldSave = {};
				columnFieldSave["id"]        = "fieldSave";
				columnFieldSave["parentId"]  = column["parentId"];				
				columnFieldSave["sortable"]  = "false";
				columnFieldSave["editable"] = false;				
				columnFieldSave["renderCell"] = function actionRenderCell(object, value, node, options) {
					var div = document.createElement("div");
					div.innerHTML =	"<button  title=\""+metadata["common"]["fieldSave"]+"\"  onclick=\"saveTreeDGrid('"+parentId+"')\">" +
										"<img src=\"resources/internal/Save.png\"  style=\"vertical-align: middle; align: middle; width: 12px; height: 12px;  \"   >" +
									"</button>" ;
					return div;
		        };
		        columnFieldSave["renderHeaderCell"] = function actionRenderCell(node) {
					var div = document.createElement("div");
					div.innerHTML = "<img src=\"resources/internal/Save.png\"  style=\"vertical-align: middle; align: middle; width: 18px; height: 18px;  \"   >";		        
			    	div.title = metadata["common"]["fieldSave"];
					return div;
		        };
				columns.push(columnFieldSave);
			}
			
			
			var virtualColumnType = 0;
			var columnSets = null;
			var columnSetWidths = null;
			var compoundColumns = null;
			var allVirtualColumns = null;
			if(metadata["virtualColumns"]){
				for(var vc in metadata["virtualColumns"]) {
					if(metadata["virtualColumns"][vc]["virtualColumnType"] == "COLUMN_HEADER"){
						virtualColumnType = 1;
						break;	 				
					}
					if(metadata["virtualColumns"][vc]["virtualColumnType"] == "COLUMN_SET"){
						virtualColumnType = 2;
						break;					
					}
				}		
							
				if(virtualColumnType == 1){
					compoundColumns = [];
					allVirtualColumns = [];
					
					var i = 1;
					for(var k2 in metadata["virtualColumns"]) {
						var virtualColumn = {};
						if(metadata["virtualColumns"][k2]["virtualColumnType"] == "COLUMN_HEADER"){
							virtualColumn["id"] = "vcol"+i;
							virtualColumn["parentId"] = metadata["virtualColumns"][k2]["parentId"];
							virtualColumn["label"] = metadata["virtualColumns"][k2]["id"];					
							virtualColumn["style"] = metadata["virtualColumns"][k2]["style"];
							virtualColumn["children"] = [];
							
			
							for(var k3 in allVirtualColumns) {
								if(virtualColumn["parentId"] == allVirtualColumns[k3]["label"]){
									allVirtualColumns[k3]["children"].push(virtualColumn);
									break;
								}
							}
							
							allVirtualColumns.push(virtualColumn);
							if(!virtualColumn["parentId"]){
								compoundColumns.push(virtualColumn);
							}
			
							i++;
						} else {
							for(var k11 in columns) {
								if(columns[k11]["id"] == metadata["virtualColumns"][k2]["id"]){
									compoundColumns.push(columns[k11]);
									break;
								}
							}						
						}
					}
					
					for(var k4 in columns) {
						for(var k5 in allVirtualColumns) {
							if(columns[k4]["parentId"] == allVirtualColumns[k5]["label"]){
								allVirtualColumns[k5]["children"].push(columns[k4]);
								break;
							}
						}	
					}
					
					if(columnFieldSave){
						compoundColumns.push(columnFieldSave);
					}
					
				}
				
				if(virtualColumnType == 2){
					columnSets = [];
					columnSetWidths = [];
					for(var vc in metadata["virtualColumns"]) {
						var columnSet = [];
						for(var kk in columns) {
							if(columns[kk]["parentId"] == metadata["virtualColumns"][vc]["id"]){
								columnSet.push(columns[kk]);
							}
						}
						columnSetWidths.push(metadata["virtualColumns"][vc]["width"]);
						columnSets.push([columnSet]);					
					}
				}
			}

		
		var declareGrid = [Grid, ColumnResizer, Keyboard, Editor, Tree];		
		
		var selectionMode;
		if(metadata["common"]["selectionModel"] == "RECORDS"){
			selectionMode = "extended";			
			declareGrid.push(Selection);
		}else{
			selectionMode = "single";			
			declareGrid.push(CellSelection);
		}
		
		if(virtualColumnType == 1){
			declareGrid.push(CompoundColumns);			
		}
		
		if(virtualColumnType == 2){
			declareGrid.push(ColumnSet);			
		}
		
		var isVisibleColumnsHeader = false;
		if(metadata["common"]["isVisibleColumnsHeader"]){
			isVisibleColumnsHeader = true;	
		}
		
		var isAllowTextSelection = false;
		if(metadata["common"]["isAllowTextSelection"]){
			isAllowTextSelection = true;	
		}

		
		if(virtualColumnType == 1){
		    columns = compoundColumns;
		}
		
		var sort = null;
		if(metadata["common"]["sortColId"] && metadata["common"]["sortColDirection"]){
			var descending = false;
			if(metadata["common"]["sortColDirection"].toUpperCase()=="DESC"){
				descending = true;	
			}
			
			for(var k in metadata["columns"]) {
				if(metadata["columns"][k]["caption"] == metadata["common"]["sortColId"]){
					sort = [{property: metadata["columns"][k]["id"], descending: descending}];
					store.initialSort = sort;
					break;
				}
			}
		}
		
		
		var grid = new declare(declareGrid)({
			columns: columns, 
			columnSets: columnSets,
			sort: sort,
			collection: store.getRootCollection(),
			getBeforePut: false,
			showHeader: isVisibleColumnsHeader,
//			minRowsPerPage: parseInt(metadata["common"]["limit"]),
			minRowsPerPage: 10000,			
			selectionMode: selectionMode,
			allowTextSelection: isAllowTextSelection,
			loadingMessage: metadata["common"]["loadingMessage"],
			noDataMessage: metadata["common"]["noDataMessage"],
			pagingDelay: 50,
			deselectOnRefresh: false,				
			keepScrollPosition: true,
			readonly: metadata["common"]["readonly"],
			expandAllRecords: metadata["common"]["expandAllRecords"],
			
			renderRow: function (object) {
			     var rowElement = this.inherited(arguments);
				 if(object.rowstyle && (object.rowstyle != "")){
						rowElement.className = object.rowstyle;
				 }
			     return rowElement;
			},
			
			shouldExpand: function (row, level, previouslyExpanded) {
				if(this.expandAllRecords){
					if((row.data.hasChildren == "1") || (row.data.hasChildren == "true")){
						return true;
					}else{
						return false;
					}
				}else{
					return this.inherited(arguments);
				}
			}
			
		},  parentId);
	    arrGrids[parentId] = grid;
		

		for(var k in metadata["columns"]) {
			grid.styleColumn(metadata["columns"][k]["id"], metadata["columns"][k]["style"]);
		}
		if(grid.column("fieldSave")){
			grid.styleColumn("fieldSave", "width: 30px;");	
		}
		if(virtualColumnType == 1){
		    for(var k1 in allVirtualColumns) {
				if(allVirtualColumns[k1]["style"]){
					grid.styleColumn(allVirtualColumns[k1]["id"], allVirtualColumns[k1]["style"]);
				}	
			}
		}
		if(virtualColumnType == 2){
			for(var vcc in columnSets) {
				if(columnSetWidths[vcc]){
					grid.styleColumnSet(vcc, "width:"+columnSetWidths[vcc]+";");					
				}
			}
		}
		
		
		grid.on("dgrid-select", function(event){
			if(event.parentType && ((event.parentType.indexOf("mouse") > -1) || (event.parentType.indexOf("pointer") > -1))){
				return;
			}

			if(firstLoading){
				 if(!event.grid.expandAllRecords){
					 gwtAfterClickTree(elementId, metadata["common"]["selRecId"], metadata["common"]["selColId"], getSelection());
				 }
			} else {
				setTimeout(function(){
					if(!grid.readonly){
						if(grid.currentRowId != grid.row(event.grid._focusedNode).id){
							grid.currentRowId = grid.row(event.grid._focusedNode).id;
							grid.save();
						}
					}
					
					gwtAfterClickTree(elementId, grid.row(event.grid._focusedNode).id, grid.column(event.grid._focusedNode).label, getSelection());
				}, 50);
			}
		});
		grid.on(".dgrid-row:click", function(event){
			if(grid.row(event) && grid.column(event)){
				if(!grid.readonly){
					if(grid.currentRowId != grid.row(event).id){
						grid.currentRowId = grid.row(event).id;
						grid.save();
					}
				}
				
				gwtAfterClickTree(elementId, grid.row(event).id, grid.column(event).label, getSelection());
			}
		});
		grid.on(".dgrid-row:dblclick", function(event){
			if(grid.row(event) && grid.column(event)){
				gwtAfterDoubleClickTree(elementId, grid.row(event).id, grid.column(event).label, getSelection());
			}
		});
		function getSelection()
		{
		    var selection = "";
		    var i = 0;
	        for(var id in grid.selection){
	            if(grid.selection[id]){
	            	if(i > 0){
		            	selection = selection+metadata["common"]["stringSelectedRecordIdsSeparator"];	
	            	}
	            	selection = selection+id;
	            	i++;
	            }
	        }
	        return selection; 
		}
		grid.on("dgrid-refresh-complete", function(event) {
			if(firstLoading){
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
				firstLoading = false;
			}
		});
		grid.on("dgrid-datachange", function(event){
			if(typeof event.value === "string"){
				if(event.value.indexOf("<") > -1){
					event.returnValue = false;
					console.log("Заблокирована строка, содержащая символ '<'");
					return;
				}
			}
			
			for(var id in event.grid.dirty) {
				if(id != event.cell.row.id){
					event.returnValue = false;
					return;
				}
			}
			
			if(event.cell.row.element.className.indexOf("jsgrid-record-editing")==-1){
				event.cell.row.element.className = event.cell.row.element.className + " jsgrid-record-editing";
				
				event.grid.editing = JSON.stringify(event.cell.row.data, function(key, value) {
					  if (
							  (key == "dirty")							  
						   || (key == "gridContextName") 
					       || (key == "elementInfoName")
					       || (key == event.cell.row.data["gridContextName"])
					       || (key == event.cell.row.data["elementInfoName"])
					     )
					  {
						  return undefined;						  
					  }
					  return value;
				});				
			}
			
		});
		
		
		grid.resizeColumnWidth("col1", "5px");		
		
	});
	
	
} catch (err) {
	console.log("При построении tree-грида возникла следующая ошибка: "+err);
	throw err;
}	
	
	
}

function refreshTreeDGrid(parentId){
	arrGrids[parentId].needAdjustSelectionRecords = true;	
	arrGrids[parentId].expandAllRecords = null;
	arrGrids[parentId].refresh();
}

function addRecordTreeDGrid(parentId){
	arrGrids[parentId].expandAllRecords = null;
	arrGrids[parentId].collection.add({id: "addRecord_"+GenerateGUID()});
}

function saveTreeDGrid(parentId){
	arrGrids[parentId].expandAllRecords = null;
	arrGrids[parentId].save();
}

function revertTreeDGrid(parentId){
	arrGrids[parentId].expandAllRecords = null;
	
	for(var id in arrGrids[parentId].dirty) {
		if(id){
			arrGrids[parentId].dirty = {};
			arrGrids[parentId].collection.emit('update', {target: JSON.parse(arrGrids[parentId].editing)});
			arrGrids[parentId].dirty = {};
		}
	}
	
//	arrGrids[parentId].revert();
}

function clipboardTreeDGrid(parentId){
	arrGrids[parentId].expandAllRecords = null;
	
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
	
	return str;
}

function partialUpdateTreeDGrid(elementId, parentId, partialdata){
	arrGrids[parentId].expandAllRecords = null;
	
	for(var k in partialdata) {
		if(arrGrids[parentId].row(partialdata[k].id).data){
				arrGrids[parentId].collection.emit('update', {target: partialdata[k]});				
		}
	}
	
	if(partialdata[0]){
		var events = null;
		if(partialdata[0]["events"]){
			events = partialdata[0]["events"];
		}
		gwtAfterPartialUpdateTree(elementId, events);						
	}
}


function currentLevelUpdateTreeDGrid(elementId, parentId){
	arrGrids[parentId].expandAllRecords = null;
	
	var rowId = null;
	try {
		rowId = arrGrids[parentId].row(arrGrids[parentId]._focusedNode).data.parentId;
	}catch(err){}
	
	if(rowId){
		if(!gwtUpdateParents(elementId, rowId)){
			var oldLoadingMessage = arrGrids[parentId].loadingMessage; 
			arrGrids[parentId].set("loadingMessage", null);
			arrGrids[parentId].expand(rowId, true, null, true);
			arrGrids[parentId].set("loadingMessage", oldLoadingMessage);
		}
	}
}

function childLevelUpdateTreeDGrid(elementId, parentId){
	arrGrids[parentId].expandAllRecords = null;	
	
	var rowId = null;
	var hasChildren = null;
	try {
		rowId = arrGrids[parentId].row(arrGrids[parentId]._focusedNode).id;
		hasChildren = arrGrids[parentId].row(arrGrids[parentId]._focusedNode).data.hasChildren;
	}catch(err){}
	
	if(rowId){
		if(!gwtUpdateParents(elementId, rowId)){
			if((hasChildren == "1") || (hasChildren == "true")){
				var oldLoadingMessage = arrGrids[parentId].loadingMessage; 
				arrGrids[parentId].set("loadingMessage", null);
				arrGrids[parentId].expand(rowId, true, null, true);
				arrGrids[parentId].set("loadingMessage", oldLoadingMessage);
			}
		}
	}
}







