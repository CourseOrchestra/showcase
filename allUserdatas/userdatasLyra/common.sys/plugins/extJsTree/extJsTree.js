function createExtJsTree(parentId, pluginParams, data) {
	//////////////////////// ExtJsTree ////////////////////////
	var ExtJsTree = function(el, pluginParams, data) {
		var self = this;
		this.el = el;
		if (data) {
			this.data = eval('(' + data + ')');
		}
		this.filter = {};//данные относящиеся к фильтру поиска
		this.pluginParams = pluginParams || {};
		var treePanel = this.pluginParams.treePanel;
		if (treePanel == undefined) {
			this.pluginParams.treePanel = treePanel = {};
		}
		if (treePanel.listeners == undefined) {
			treePanel.listeners = {};
		}
		//перехватываем обработчик события checkchange, для хранения выбранных значений
		var checkchangeFn = undefined;
		if (treePanel.listeners.checkchange) {
			checkchangeFn = treePanel.listeners.checkchange.fn;
		}
		treePanel.listeners.checkchange = {
			fn: function(node, checked, eOpts) {
				self._doCheckchange(checkchangeFn, node, checked, eOpts);
			}
		};
		
		this.selectedItems = new Array();//выбранные значения
		this.idPref = '_';//префикс идентификатора выбранного значения
		
		this._init();
	};
	ExtJsTree.prototype = {
		_init: function() {
			//DataModel
			var modelOptions = Ext.apply((this.pluginParams.dataModel || {}), {
				extend: 'Ext.data.Model'
			}, {});
			modelOptions.fields = this._applyFieldsToModel([
				{name:'id',type:'string'},
				{name:'name',type:'string'}
			], modelOptions.fields||[]);
			Ext.define('ExtJsTree.DataModel', modelOptions);
			
			this.store=this._createStore(this.data);
			this.dataLoader = this._createDataLoader();
			this.dataLoader.callback = {
				'target':this,
				'loadData':this._doLoadData
			};
			
			this._addDomElement(this.el);
			this.treePanel = this._createExtTreePanel(this.el);
						
		},
		_addDomElement: function(parentEl) {
			//добавление фильтра поиска
			var self = this;
			var dh = Ext.DomHelper;
			var inputId = Ext.id();
			var checkboxId = Ext.id();
			var findButton = {};
			if (pluginParams.core.filter.autofilter==false) {
				findButton = {
					tag:'input',
					id:'findButton'+inputId,
					type:'button',
					value:'Найти',
					style:'float:right;'
				};
			}
			var filterEl = new Ext.dom.Element(dh.createDom({
				tag:'div',
				style:'float:left;text-align:center;',
				children: [{
					tag:'input',
					id:inputId,
					type:'text',
					autocomplete:'off',
					style:'width:'+(parentEl.getWidth()-(pluginParams.core.filter.autofilter==false?60:0)-5)+'px;'
				}, findButton,
				{
					tag:'div',
					style:'text-align:left;',
					children: [{
						tag:'input',
						id:checkboxId,
						type:'checkbox',
						value:true
					},{
						tag:'label',
						'for':checkboxId,
						html:'Начинается с'
					}]
				}]
			}));		
			parentEl.appendChild(filterEl);				
						
			var inputEl = Ext.get(inputId);		
			var checkBoxEl = Ext.get(checkboxId);						
			if (pluginParams.core.filter.startsWith) {
				checkBoxEl.set({checked:'checked'});
			}			
			if (pluginParams.core.filter.autofilter==false) {
				var findButtonEl = Ext.get('findButton'+inputId);
				findButtonEl.on('click', function(event, htmlEl, o) {
					self._doFilterRefresh();
				});
			} else {
				checkBoxEl.on('click', function(event, htmlEl, o) {
					self._doClickFilterCheckBox(htmlEl);
				});
				inputEl.on('keyup', function(event, htmlEl, o) {
					self._doKeyupFilterInput(htmlEl);
				});
			}
			
			this.filter = {
				'filterEl':filterEl,
				'inputEl':inputEl,
				'checkBoxEl':checkBoxEl
			};
		},
		_createExtTreePanel: function(parentEl) {
			//Построение элемента Ext.tree.Panel
			
			var columns =
				[
				 {xtype: 'treecolumn', header: 'Название', dataIndex: 'name', flex: 1}
				];
			if(pluginParams.view){
				columns.push(pluginParams.view);				
			}
			
			var option = {
				store: this.store,
				columns: columns,
				rootVisible:false,
				hideHeaders:true,
				useArrows: true,
				frame: true,
				title: 'Check Tree',
				renderTo: parentEl,
				width: parentEl.getWidth(),
				height: parentEl.getHeight()-this.filter.filterEl.getHeight()-2
			};
			
			option = Ext.apply(option, pluginParams.treePanel || {});
			var tree = Ext.create('Ext.tree.Panel', option);
			return tree;
		},
		_doClickFilterCheckBox: function(el) {
			this.dataLoader.doFilter(this.filter.inputEl.getValue(), el.checked, false);
		},
		_doKeyupFilterInput: function(el) {
			this.dataLoader.doFilter(el.value, this.filter.checkBoxEl.dom.checked);
		},
		_doFilterRefresh: function() {
			this.dataLoader.doFilter(this.filter.inputEl.getValue(), this.filter.checkBoxEl.dom.checked, true, true);
		},
		_selectParentNodes: function (node, checked) {
			var parentNode = node.parentNode;
			if (parentNode && parentNode.get('id')!='root')  {
				parentNode.set('checked', checked);
				this._selectParentNodes(parentNode, checked);
				if (checked) {
					this.addItem(parentNode.get('id'), parentNode.getData());
				} else {
					this.removeItem(parentNode.get('id'));
				}
			}
		},
		_doCheckchange: function(callbackFn, node, checked, eOpts) {			
			if (checked) {
				if (this.pluginParams.core.checkParent) {
					this._selectParentNodes(node, true);
				}
				this.addItem(node.get('id'), node.getData());
			} else {
				if (this.pluginParams.core.checkParent) {
					this._selectParentNodes(node, false);
				}
				this.removeItem(node.get('id'));
			}
			if (callbackFn!=undefined && Ext.isFunction(callbackFn)) {
				callbackFn.call(this.treePanel, node, checked, eOpts);
			}
		},
		_doLoadData: function(data) {
			for (i in data) {
				if (this.getValue(data[i].id)) {
					data[i].checked=true;
				}
				if (data[i].children) {
					this._doLoadData(data[i].children);
				}
			}
		},
		_applyFieldsToModel: function(fields, addFields) {
			var result = addFields || [];
			for (var i = 0; i < fields.length; i++) {
				var isContains = false;
				for (var j = 0; j < addFields.length; j++) {
					if (fields[i].name==addFields[j].name) {
						isContains = true;
						break;
					}
				}
				if (!isContains) {
					result.push(fields[i]);
				}
			}
			return result;
		},
		_createStore: function(data) {
			var root = !data ? [] : {
				id:'root',
				name:'/',
				expanded:true,
				leaf:false,
				icon:Ext.BLANK_IMAGE_URL,
				children:data
			};	
			var store = Ext.create('Ext.data.TreeStore', {
				model: 'ExtJsTree.DataModel',
				root: root,
				proxy: {
				type: 'memory',
					reader: {
						type: 'json'
					}
				}
			});
			return store;
		},
		_createDataLoader: function() {
			var dataLoader = this.dataLoader = new DataLoader(this.store, this.pluginParams.core.filter.delay, this.pluginParams.generalFilters);		
			return dataLoader;
		},
		addItem: function(id, data) {
			this.selectedItems[this.idPref+id] = data;
		},
		removeItem: function(id) {
			delete this.selectedItems[this.idPref+id];
		},
		getValue: function(id) {
			return this.selectedItems[this.idPref+id];
		},
		getValues: function() {
			return this.selectedItems;
		}
	};
	//////////////////////// END ExtJsTree ////////////////////
	
	//////////////////////// DataLoader ////////////////////////
	var DataLoader = function(store, delay, generalFilters) {
		this.timeoutId = false;
		this.setFilterValue('',true);
		this.store = store;
		this.store.proxy.ExtJsTree = {
			self: this
		};
		this.store.proxy.read=this.loadNode;
		this.delay = delay || 900;
		this.generalFilters = generalFilters || '';
	};
	DataLoader.prototype = {			
			load:function(operation, callback, scope) {
				var self = this;
				gwtGetDataPlugin({
					pluginParam:pluginParams.all,
					id:pluginParams.elementPanelId,
					parentId:parentId,
					params: Ext.apply(operation.params, {generalFilters:this.generalFilters}),
					callbackFn: function(data) {
						if (data) {
							if (!Ext.isArray(data)) {
								data = [data];
							}
							if (self.callback!=undefined && Ext.isFunction(self.callback.loadData)) {
								self.callback.loadData.call(self.callback.target, data);
							}
							operation.resultSet = self.store.proxy.getReader().read(data);	
						}							
						operation.setCompleted();
						operation.setSuccessful();
						Ext.callback(callback, scope, [operation]);
					}
				});
			},
			loadNode:function(operation, callback, scope) {
				var dataLoader = this.ExtJsTree.self;
				if (operation.node != dataLoader.store.getRootNode()) {
					var dataNode = operation.node.data;
					operation.params={
						id:dataNode.id,
						name:dataNode.name,
						curValue:dataLoader.curVal.val,
						startsWith:dataLoader.curVal.curIsChecked
					};		
				}
				dataLoader.load(operation, callback, scope);
			},			
			_doEvent:function() {
				var rootNode = this.store.getRootNode();
				rootNode.removeAll();
				this.store.load({node: rootNode, params:{
						curValue:this.curVal.val,
						startsWith:this.curVal.curIsChecked
					}
				});
			},
			doFilter: function(val, isChecked, isNoDelay, isRefresh) {
				if (this.timeoutId || isNoDelay) {
					clearTimeout(this.timeoutId);
					this.timeoutId = false;
				}
				if (isRefresh || this.curVal.val != val || this.curVal.curIsChecked !=isChecked) {
					var self = this;
					if (!isNoDelay) {						
						this.timeoutId = setTimeout(function() {
							self.timeoutId = false;
							self.setFilterValue(val,isChecked);
							self._doEvent();
						},this.delay);
					} else {
						self.setFilterValue(val,isChecked);
						this._doEvent();
					}
				}
			},
			setFilterValue: function(curVal, curIsChecked) {
				this.curVal = {
					val:curVal || '',
					curIsChecked:curIsChecked || false
				};
			}
	};
	//////////////////////// END DataLoader ////////////////////////
	
	if (!pluginParams.core.filter) {
		pluginParams.core.filter = {};
	}	
	Ext.require([
        'Ext.tree.*',
        'Ext.data.*'
        ]);    		
    Ext.onReady(function() {
		var parentEl = Ext.get(parentId);
		var extJsTree = new ExtJsTree(parentEl, pluginParams, data);
		Ext.ExtJsTree = extJsTree;
		
		
		if (pluginParams.all.options.needInitSelection) {
			var tmpUtils = {};
			tmpUtils.utils = {
					singleXpathMapping: function(xpathMapping) {
						var selected = getInitSelectionForSingleSelector(xpathMapping);
						if(selected.id.trim().length > 0){
							extJsTree.addItem(selected.id, selected);
						}
					},
					multiXpathMapping: function(xpath, needClear) {
						var selected = getInitSelection(xpath.xpathRoot, xpath.xpathMapping);
						
						for (var i = 0; i < selected.recordCount; i++) {
							var record = {};
							for (var j = 0; j < selected.columnCount; j++) {
								record[selected.names[j]] = selected.values[j][i]; 
							}
							extJsTree.addItem(record.id, record);
						}
					}
			};
	    	
			if (Ext.isFunction(pluginParams.all.options.onSelectionComplete)) {
				pluginParams.all.options.onSelectionComplete(true, tmpUtils);
			};

			extJsTree.treePanel.getRootNode().cascadeBy(function(node) {
				if (extJsTree.getValue(node.data.id)) {
					node.set("checked", true);
				}
			});		
		}
		
		
		extJsTree.utils = {
			singleXpathMapping: function(xpathMapping) {
				var records = extJsTree.getValues();//extJsTree.treePanel.getView().getChecked();
				if (records!=undefined) {
					for (i in records) {
						var selected = records[i];
						setXFormByXPath(true, selected, xpathMapping, pluginParams.elementPanelId+"_");
						break;
					}
				}				
			},
			multiXpathMapping: function(xpath, needClear) {
				var records = extJsTree.getValues();//extJsTree.treePanel.getView().getChecked();
				if (records!=undefined) {
					var selected = [];
					for (i in records) {
						var selectedItem = records[i];
						if(selectedItem.id != ""){
							selected.push(selectedItem);							
						}
					}
					insertXFormByXPath(true, selected, xpath.xpathRoot, xpath.xpathMapping, needClear, pluginParams.elementPanelId+"_");
				}				
			}
		};
		
		if (Ext.isFunction(pluginParams.onDrawPluginComplete)) {
			pluginParams.onDrawPluginComplete(extJsTree);
		};		
    });
};