function createCalendar(parentId, data, template) {
	require([
	         "dijit/registry", 
	         "dojox/calendar/Calendar", 
	         "dojo/store/Memory",
	         "dojo/_base/declare",
	         "dojox/calendar/MonthColumnView",
	         "dojox/calendar/Keyboard", 
	         "dojox/calendar/Mouse",
	         "dojox/calendar/VerticalRenderer", 
	         "dijit/form/Button",
	         "dojo/_base/lang",
	         "dojo/on",
	         "dojox/timing"
	         ], function(
	        		 registry, 
	        		 Calendar, 
	        		 Memory,
	        		 declare,
	        		 MonthColumnView,
	        		 Keyboard,
	        		 Mouse,
	        		 VerticalRenderer,
	        		 Button,
	        		 lang,
	        		 on,
	        		 timing
	         ){
		
		            var dateInterval      = data.metadata.dateInterval;
		            var dateIntervalSteps = data.metadata.dateIntervalSteps;
			    	
			    	var w = registry.byId(parentId);
			    	if(w){
			    		if(template){
				    		dateInterval      = w.dateInterval;
				    		dateIntervalSteps = w.dateIntervalSteps;
			    		}
			    		
			    		w.timer.stop();
			    		w.destroy();
			    	}
			    	
			    	var calendar = new Calendar({
			    		  date: data.metadata.date,
			    		  columnViewProps:{minHours:data.metadata.minHours, maxHours:data.metadata.maxHours},
			    		  store: new Memory({data: data.data}),
			    		  dateInterval: dateInterval,
						  dateIntervalSteps: dateIntervalSteps,
			    		  style: data.metadata.style,
			    		  editable: data.metadata.editable,
			    		  
			    			_createDefaultViews: function(){
			    				Calendar.prototype._createDefaultViews.call(this);
			    				
			    				this.monthColumnView = declare([MonthColumnView, Keyboard, Mouse])({
			    					verticalRenderer: VerticalRenderer				
			    				});
			    				
			    				this.monthColumnView.on("columnHeaderClick", lang.hitch(this, function(e){
			    					this.set("dateInterval", "month");
			    					this.set("dateIntervalSteps", 1);
			    					this.set("date", e.date);
			    				}));
			    				
			    				return [this.columnView, this.matrixView, this.monthColumnView];
			    			},
			    			
			    			_computeCurrentView: function(startDate, endDate, duration){
			    				// show the month column view if the duration is greater than 31x2 days
			    				if(duration>62){
			    					return this.monthColumnView;
			    				}else{
			    					return Calendar.prototype._computeCurrentView.call(this, startDate, endDate, duration);
			    				}
			    			},
			    			
			    			_configureView: function(view, index, timeInterval, duration){
			    				// show only from January to June or from July to December
			    				if(view.viewKind == "monthColumns"){
			    					var m = timeInterval[0].getMonth();
			    					var d = this.newDate(timeInterval[0]);
			    					d.setMonth(m<6?0:6);
			    					view.set("startDate", d);
			    					view.set("columnCount", 6);
			    				}else{
			    					return Calendar.prototype._configureView.call(this, view, index, timeInterval, duration);
			    				}
			    			},
			    		  
				    		configureButtons: function(){
					         	var button = new Button({});					         	
								this.toolbar.addChild(button);
								this.sixMonthButton = button;
								
				    			Calendar.prototype.configureButtons.call(this);
				    			
				    			this.own(
				    				on(this.sixMonthButton, "click", lang.hitch(this, function(){
				    					this.set("dateIntervalSteps", 6);
				    					this.set("dateInterval", "month");
				    				}))
				    			);
				    		},
				    		
				    		cssClassFunc: function(item){
				    		    return item.className;
				    		},
			    		  
			    		}, parentId);
			    	
			    	if(data.metadata.timeSlotDuration){
			    		calendar.columnView.timeSlotDuration = data.metadata.timeSlotDuration;
			    	}
			    	
			    	if(data.buttons){
						for(var i = 0; i<data.buttons.length; i++){
							if(data.buttons[i].hide){
						    	calendar[data.buttons[i].id].domNode.parentNode.removeChild(calendar[data.buttons[i].id].domNode);
							}
						}
			    	}
			    	
			    	
			    	calendar.matrixView.itemToRendererKindFunc = function(item){
		    		    return "horizontal";
		    		};
			    	
			    	
			    	calendar.on("itemClick", function(e){
//			    		  console.log("Item clicked", e.item);
			    		gwtPluginFunc(parentId, e.item.id);
			    	});
			    	
			    	calendar.on("gridDoubleClick", function(e){
//			    		  console.log("Item clicked", e.item);
//			    		gwtPluginFunc(parentId, "addItem");
			    		gwtPluginFunc(parentId, "addItem", e.date.toString(), "ADD_CONTEXT");			    		
			    	});
					
			    	calendar.on("onItemEditBegin", function(e){
//			    		  console.log("Item clicked", e.item);
			    		gwtPluginFunc(parentId, e.item.id + "Delete");
			    	});	
					
			    	if(!data.metadata.toolbarVisible){
				        var div = document.getElementById(parentId).parentNode;
				        if(div){
					        var div2 = div.firstChild;
					        if(div2){
					        	for (var index = 0; index < div2.childNodes.length; index++) {
					        		if(div2.childNodes[index].className == "buttonContainer"){
					        			div2.removeChild(div2.childNodes[index]);
					        		}
					        	}
					        }
				        }
			    	}
			    	
			    	
				   	dojo.require('dojox.timing');
				   	var timer = new dojox.timing.Timer(1000);
				   	timer.onTick = function(){
					   	 if(this.calendar.domNode.offsetWidth == 0){
					   		this.stop();
					   		return;
					   	 }
				    	 if(this.offsetWidth != this.calendar.domNode.offsetWidth){
					    	 this.offsetWidth = this.calendar.domNode.offsetWidth;
					    	 this.calendar.currentView.resize();
				    	 }
				   	}
				   	timer.calendar    = calendar;
				   	timer.offsetWidth = calendar.domNode.offsetWidth; 
				   	timer.start();
				   	calendar.timer = timer;

				   	
			  }
	);
}
