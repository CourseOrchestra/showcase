function createNavigator(parentId, data, template) {
	require(["dijit/registry", 
	         "dijit/layout/AccordionContainer", 
	         "dijit/layout/ContentPane",
	         
	         "dojo/_base/lang", 
	         "dstore/Memory",
	         "dgrid/Selection",
	         "dgrid/OnDemandGrid",
	         "dgrid/Keyboard",
	    	 "dgrid/Tree",		
	    	 "dstore/Tree",
	         "dojo/_base/declare",
	     	 "dojo/dom-construct",	     	         
	         "dojo/domReady!"
	        ], function(
			    registry, AccordionContainer, ContentPane,
   			    lang, Memory, Selection, Grid, Keyboard, Tree, TreeStore, declare, domConstruct 
			){
		
		
    	var w = registry.byId(parentId);
    	if(w){
    		w.destroy();			    		
    	}
    	
    	
    	function getTreeIcon(str){
    		return "<a><img border=\"0\" src=\""+str+"\"></a>";
    	}
    	
    	function fillData(arr, obj, num){
    		if(obj["level"+num] && !obj["level"+num].length){
    			obj["level"+num] = [obj["level"+num]];
    		}
    		
    		if(obj["level"+num]){
        	    for (var k = 0; k < obj["level"+num].length; k++) {    			
        			var parent = null;
        			if(num > 1){
        				parent = obj["@id"];
        			}
        			
        			var hasChildren = false;
        			if(obj["level"+num][k]["level"+(num+1)]){
        				hasChildren = true;	
        			}
        				
        			arr.push({id: obj["level"+num][k]["@id"], 
        				      name: obj["level"+num][k]["@name"],
        				      parent: parent,
        				      hasChildren: hasChildren,
        				      TreeGridNodeCloseIcon: obj["level"+num][k]["@closeIcon"],
        				      TreeGridNodeOpenIcon: obj["level"+num][k]["@openIcon"],
        				      TreeGridNodeLeafIcon: obj["level"+num][k]["@leafIcon"],
        				      open: obj["level"+num][k]["@open"],
        				      selectOnLoad: obj["level"+num][k]["@selectOnLoad"],
        				      classNameRow: obj["level"+num][k]["@classNameRow"] 
        			
        				      });
        			
        			if(hasChildren == 1){
            			fillData(arr, obj["level"+num][k], num+1);    				
        			}
        		}
    		}
    	}
    	
    	if(!data.navigator.group){
    		return;
    	}
    	
	    var aContainer = new AccordionContainer({}, parentId);
	    
		if(!data.navigator.group.length){
			data.navigator.group = [data.navigator.group];
		}	    
	    
	    for (var k = 0; k < data.navigator.group.length; k++) {
			if(data.navigator.group[k]["@hide"]  && data.navigator.group[k]["@hide"].toLowerCase()=="true"){
				continue;
			}
			
			
			var groupData = [];
			fillData(groupData, data.navigator.group[k], 1);
			
			
    		var store = new declare([ Memory, TreeStore ])(lang.mixin({	    		
	    		data: groupData,
	    	}, {} ));

	    	
	    	var classNameGrid="";
	        if(data.navigator.group[k]["@classNameGrid"] && (data.navigator.group[k]["@classNameGrid"].trim()!="")){
	        	classNameGrid=data.navigator.group[k]["@classNameGrid"];
	        }
	        classNameGrid=classNameGrid+" plugin-navigator-grid";
	        
	    	var classNameColumn="";
	        if(data.navigator.group[k]["@classNameColumn"] && (data.navigator.group[k]["@classNameColumn"].trim()!="")){
	        	classNameColumn=data.navigator.group[k]["@classNameColumn"];
	        }
	        classNameColumn=classNameColumn+" plugin-navigator-grid-column";
     
	        
	        var  grid = new declare([Grid, Selection, Keyboard, Tree])({
	        	
	        	firstLoading:  true,
	        	
	        	className: classNameGrid,
	        	
	        	collection: store.getRootCollection(),
				
				showHeader: false,
				selectionMode: "single",
				
				columns: [{
					
					className: classNameColumn,
					
					id: "name",
					field:"name",					
					
					formatter: function columnFormatter(item){
						return item;
					},
					
					renderExpando: function columnRenderExpando(level, hasChildren, expanded, object) {
				        var dir = this.grid.isRTL ? "right" : "left",
							cls = "dgrid-expando-icon";
						if(hasChildren){
							cls += " ui-icon ui-icon-triangle-1-" + (expanded ? "se" : "e");							
						}
						node = domConstruct.create('div', {
							className: cls,
							innerHTML: '&nbsp;',
							style: 'margin-' + dir + ': ' + (level * this.grid.treeIndentWidth) + 'px; float: ' + dir + ';'
						});
							
						if(hasChildren){
							if(expanded){
								if(object.TreeGridNodeOpenIcon && object.TreeGridNodeOpenIcon.trim().length > 0){
									node.innerHTML = getTreeIcon(object.TreeGridNodeOpenIcon);
								}
							}
							else{
								if(object.TreeGridNodeCloseIcon && object.TreeGridNodeCloseIcon.trim().length > 0){
									node.innerHTML = getTreeIcon(object.TreeGridNodeCloseIcon);										
								}
							}
						}else{
							if(object.TreeGridNodeLeafIcon && object.TreeGridNodeLeafIcon.trim().length > 0){
								node.innerHTML = getTreeIcon(object.TreeGridNodeLeafIcon);									
							}
						}
						node.title = object.col1;
						return node;
					}
					
				}],
				
				shouldExpand: function columnShouldExpand(row, level, previouslyExpanded){
                    if(this.firstLoading){
    					if(row.data.selectOnLoad){
    						this.select(row);	
    						if(data.navigator["@afterReloadAction"] && data.navigator["@afterReloadAction"].toLowerCase()=="true"){
    				    		gwtPluginFunc(parentId, row.data.id);  
    						}
    					}
    					return row.data.open;
                    }else{
    					return previouslyExpanded;
                    }
				},

				renderRow: function (object) {
				     var rowElement = this.inherited(arguments);
					 if(object.classNameRow && (object.classNameRow.trim() != "")){
							rowElement.className = object.classNameRow;
					 }
				     return rowElement;
				}
				
			});
	        
			grid.on("dgrid-refresh-complete", function(event) {
				this.firstLoading = false;
			});
	        
	        grid.on("dgrid-select", function(event){
	    		gwtPluginFunc(parentId, event.rows[0].id);
	        });
			
	        
	        grid.startup();	        

	        
	        if(data.navigator.group[k]["@styleColumn"] && (data.navigator.group[k]["@styleColumn"].trim()!="")){
				grid.styleColumn("name", data.navigator.group[k]["@styleColumn"]);
	        }
	        
	        
	        var classNameTitle="";
	        if(data.navigator.group[k]["@classNameTitle"] && (data.navigator.group[k]["@classNameTitle"].trim()!="")){
	        	classNameTitle="class=\""+data.navigator.group[k]["@classNameTitle"]+"\"";
	        }
	        var styleTitle="";
	        if(data.navigator.group[k]["@styleTitle"] && (data.navigator.group[k]["@styleTitle"].trim()!="")){
	        	styleTitle="style=\""+data.navigator.group[k]["@styleTitle"]+"\"";
	        }
	        var iconTitle="";
	        if(data.navigator.group[k]["@icon"] && (data.navigator.group[k]["@icon"].trim()!="")){
	        	iconTitle="<img border=\"0\" src=\""+data.navigator.group[k]["@icon"]+"\">";
	        }
	        var title = "<p "+classNameTitle+" "+styleTitle+">"+iconTitle+""+data.navigator.group[k]["@name"]+"</p>";
	        
	        
		    var pane = new ContentPane({
		        title: title,
		        content: grid
		    });
		    aContainer.addChild(pane);		    
			if(data.navigator.group[k]["@open"] && data.navigator.group[k]["@open"].toLowerCase()=="true"){
				aContainer.selectedPane = pane;  
			}
		}
		
	    aContainer.startup();		
	    
		if(aContainer.selectedPane){
		    aContainer.selectChild(aContainer.selectedPane, false);			
		}
		
			      
			  }
	);
}
