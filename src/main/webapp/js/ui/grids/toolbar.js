function createGridToolBar(elementId, parentId, metadata) {
	require({async:true},
			[
             "dijit/registry",
	         "dijit/Toolbar",
	         "dijit/form/Button",
	         "dojo/_base/array",
             "dijit/ToolbarSeparator",
             "dijit/form/DropDownButton", 
             "dijit/DropDownMenu", 
             "dijit/MenuItem",
             "dijit/popup",
             "dojo/fx",
	         "dojo/domReady!"
	     ], function(registry, Toolbar, Button, array, ToolbarSeparator,
	    		     DropDownButton, DropDownMenu, MenuItem, popup, SimpleTextarea, Textarea){
		
    		 var w = registry.byId(parentId);
    		 if(w){
    		  	popup.close(w.popupWidget);
    		  	w.destroy();
    		 }
		     
    	
		     var paramToolbar = {};
    		 if(metadata["common"] && metadata["common"].style){
    			 paramToolbar.style = metadata["common"].style;
    		 }
    		 if(metadata["common"] && metadata["common"].className){
    			 paramToolbar.class = metadata["common"].className;
    		 }
		
	         var toolbar = new Toolbar(paramToolbar, parentId);
	         
	         function addSeparator(parent) {
		         var sep = new ToolbarSeparator({});
		         parent.addChild(sep);
	         }
	         
	         function blinkItem(item){

	        	    var count = 5;
	        	    var duration = 50;
	        	 
	        	    var effects = new Array();
	        	    
	        	    var hide = dojo.fadeOut({node: item.domNode, duration: duration});
	        	    var show = dojo.fadeIn({node: item.domNode, duration: duration});
	        	    
	        	    for(var i = 0; i < count; i++){
	        	        effects.push(hide);
	        	        effects.push(show);
	        	    }
	        	    
	        	    dojo.fx.chain(effects).play()
	         }
	         
	         function closePopup() {
				  popup.close(toolbar.popupWidget);
	         }
	         
	         function showPopup(item){
	        	var popupWidget = new Button({
	        		label: item.popupText,
		            onClick: function() {
		                popup.close(this);
		            }
	        	});
	        	
	        	popup.moveOffScreen(popupWidget);
			    if(popupWidget.startup && !popupWidget._started){
			    	popupWidget.startup();
			    }

    		    popup.open({
    		        parent: item,
    		        popup: popupWidget,
    		        around: item.domNode,
    		        orient: ["below-centered", "above-centered"]
    		    });
    		    
    		    toolbar.popupWidget = popupWidget;
    		    
				setTimeout(closePopup, 3000);
    		    
	         }
	         
	         function addButton(type, parent, md, menu) {
	        	 
	              var param = {
		                 label: md["text"],
		                 showLabel: md["text"] != "",
		                 
		                 title: md["hint"],
		                 
		                 disabled: md["disable"] === 'true',
		                 
		                 idAction: md["id"],
		                 
		                 downloadLinkId: md["downloadLinkId"],
		                 
		                 popupText: md["popupText"],
		                 
		                 needEnableDisableState: md["needEnableDisableState"],
		                 
		                 
		                 canOnClick: true,
		                 
		                 onMouseDown: function(event) {
		                	 this.canOnClick = event.button == 0;
		                 },
		                 
		                 onClick: function(event) {
		                	 
			            	 if(!this.canOnClick){
			            		 this.canOnClick = true;
			            		 return;
			            	 }
		                	 
			            	 if(gwtToolbarRunAction(elementId, this.idAction, this.downloadLinkId)) {
			            		 blinkItem(this);
			            	 } else {
			            		 if(this.popupText){
			            			 showPopup(this);
			            		 }
			            		 
			            		 if(this.needEnableDisableState){
			            			 this.disabled = true;
			         				 setTimeout(function(item){
			         					item.set("disabled", false);
			        				 }, 4500, this);
			            		 }
			            	 }
			             },
		                 
			             style: md["style"],
			             class: md["className"],
		                 iconClass: md["iconClassName"]		                	 
		                	 
		             };
	              
	        	 
	        	    var button;
					switch (type) {
				    case "Button":
				    	 button = new Button(param);
				         break;
				    case "MenuItem":
				    	 button = new MenuItem(param);
				         break;
				    case "DropDownButton":
				    	 param.dropDown = menu;
				    	 button = new DropDownButton(param);
				         break;
					}
					
	              parent.addChild(button);
	         }
	         
	         for(var k in metadata["staticItems"]) {
	        	 addButton("Button", toolbar, metadata["staticItems"][k]);
	         }
	         
			 for(var k in metadata["dynamicItems"]) {
					switch (metadata["dynamicItems"][k]["type"]) {
				    case "item":
			        	 addButton("Button", toolbar, metadata["dynamicItems"][k]);
 				         break; 
				    case "group":
				         var menu = new DropDownMenu({ style: "display: none;"});				    	
						 for(var k2 in metadata["dynamicItems"][k]) {
								switch (metadata["dynamicItems"][k][k2]["type"]) {
							    case "item":
						        	 addButton("MenuItem", menu, metadata["dynamicItems"][k][k2]);
			 				         break; 
							    case "separator":
							         addSeparator(menu);
			 				         break; 
			 				    }
						 }
						 
			             addButton("DropDownButton", toolbar, metadata["dynamicItems"][k], menu);
			             
				         break;
				         
				    case "separator":
				         addSeparator(toolbar);
 				         break; 
 				    }
			 }
	         
	         toolbar.startup();
             
	     });	
}
