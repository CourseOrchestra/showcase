Ext.define("MyApp.DemoGanttPanel", {
    extend : "Gnt.panel.Gantt", 
    requires : [
        'Gnt.plugin.TaskContextMenu',
        'Gnt.column.StartDate',
        'Gnt.column.EndDate',
        'Gnt.column.Duration',
        'Gnt.column.PercentDone',
        'Gnt.column.ResourceAssignment',
        'Sch.plugin.TreeCellEditing',
        'Sch.plugin.Pan'
    ],
    rightLabelField : 'Responsible',
    highlightWeekends : true,
    showTodayLine : true,
    loadMask : true,
    stripeRows : true,
    enableProgressBarResize : true,
    
    enableAnimations        : true,

    initComponent : function() {

        Ext.apply(this, {
            leftLabelField : {
                dataIndex : 'Name',
                editor : { xtype : 'textfield' }
            },
            
            // Add some extra functionality
            plugins : [
                Ext.create("Gnt.plugin.TaskContextMenu"), 
                Ext.create("Sch.plugin.Pan"), 
                Ext.create('Sch.plugin.TreeCellEditing', { clicksToEdit: 1 })
            ],

            // Define an HTML template for the tooltip
            tooltipTpl : new Ext.XTemplate(
                '<h4 class="tipHeader">{Name}</h4>',
                '<table class="taskTip">', 
                    '<tr><td>Start:</td> <td align="right">{[Ext.Date.format(values.StartDate, "y-m-d")]}</td></tr>',
                    '<tr><td>End:</td> <td align="right">{[Ext.Date.format(Ext.Date.add(values.EndDate, Ext.Date.MILLI, -1), "y-m-d")]}</td></tr>',
                    '<tr><td>Progress:</td><td align="right">{PercentDone}%</td></tr>',
                '</table>'
            ).compile(),
            
            // Define the static columns
            columns : [
                {
                    xtype : 'treecolumn',
                    header: 'Tasks',
                    sortable: true,
                    dataIndex: 'Name',
                    width: 200,
                    field: {
                        allowBlank: false
                    }
                },
                {
                    xtype : 'startdatecolumn'
                },
                {
                    xtype : 'durationcolumn'
                },
                {
                    xtype : 'percentdonecolumn',
                    width : 50
                }
            ],
            
             // Define the buttons that are available for user interaction
            tbar : [{
                xtype: 'buttongroup',
                title: 'Navigation',
                columns: 2,
                defaults: {
                    scale: 'large'
                },
                items: [{
                    iconCls : 'icon-prev',
                    scope : this,
                    handler : function() {
                        this.shiftPrevious();
                    }
                },
                {
                    iconCls : 'icon-next',
                    scope : this,
                    handler : function() {
                        this.shiftNext();
                    }
                }]
            },
            {
                xtype: 'buttongroup',
                title: 'View tools',
                columns: 2,
                defaults: {
                    scale: 'small'
                },
                items: [
                    {
                        text : 'Collapse all',
                        iconCls : 'icon-collapseall',
                        scope : this,
                        handler : function() {
                            this.collapseAll();
                        }
                    },
                    {
                        text : 'Zoom to fit',
                        iconCls : 'zoomfit',
                        handler : function() {
                            this.zoomToFit();
                        },
                        scope : this
                    },
                    {
                        text : 'Expand all',
                        iconCls : 'icon-expandall',
                        scope : this,
                        handler : function() {
                            this.expandAll();
                        }
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                title: 'View resolution',
                columns: 2,
                defaults: {
                    scale: 'large'
                },
                items: [{
                        text: '10 weeks',
                        scope : this,
                        handler : function() {
                            this.switchViewPreset('weekAndDayLetter');
                        }
                    },
                    {
                        text: '1 year',
                        scope : this,
                        handler : function() {
                            this.switchViewPreset('monthAndYear');
                        }
                    }
                ]},
                '->',
                {
                    xtype: 'buttongroup',
                    title: 'Try some features...',
                    columns : 2,
                    items: [
                    {
                        text : 'Highlight critical chain',
                        iconCls : 'togglebutton',
                        scope : this,
                        enableToggle : true,
                        handler : function(btn) {
                            var v = this.getSchedulingView();
                            if (btn.pressed) {
                                v.highlightCriticalPaths(true);
                            } else {
                                v.unhighlightCriticalPaths(true);
                            }
                        }
                    },
                    {
                        iconCls : 'action',
                        text : 'Highlight tasks longer than 7 days',
                        scope : this,
                        handler : function(btn) {
                            this.taskStore.getRootNode().cascadeBy(function(task) {
                                if (Sch.util.Date.getDurationInDays(task.get('StartDate'), task.get('EndDate')) > 7) {
                                    var el = this.getSchedulingView().getElementFromEventRecord(task);
                                    el && el.frame('lime');
                                }
                            }, this);
                        }
                    },
                    {
                        iconCls : 'togglebutton',
                        text : 'Filter: Tasks with progress < 30%',
                        scope : this,
                        enableToggle : true,
                        toggleGroup : 'filter',
                        handler : function(btn) { 
                            if (btn.pressed) {
                                this.taskStore.filter(function(task) {
                                    return task.get('PercentDone') < 30;
                                });
                            } else {
                                this.taskStore.clearFilter();
                            }
                        }
                    },
                    {
                        iconCls : 'action',
                        text : 'Scroll to last task',
                        scope : this,
                        
                        handler : function(btn) {
                            var latestEndDate = new Date(0),
                                latest;
                            this.taskStore.getRootNode().cascadeBy(function(task) {
                                if (task.get('EndDate') >= latestEndDate) {
                                    latestEndDate = task.get('EndDate');
                                    latest = task;
                                }
                            });
                            this.getSchedulingView().scrollEventIntoView(latest, true);
                        }
                    },
                    {
                        iconCls : 'togglebutton',
                        text : 'Cascade changes',
                        scope : this,
                        enableToggle : true,
                        handler : function(btn) {
                            this.setCascadeChanges(btn.pressed);
                        }
                    },
                    {
                        xtype : 'textfield',
                        emptyText : 'Search for task...',
                        scope : this,
                        width:150,
                        enableKeyEvents : true,
                        listeners : {
                            keyup : {
                                fn : function(field, e) {
                                    var value   = field.getValue();
                                    
                                    if (value) {
                                        this.taskStore.filter('Name', field.getValue(), true, false);
                                    } else {
                                        this.taskStore.clearFilter();
                                    }
                                },
                                scope : this
                            },
                            specialkey : {
                                fn : function(field, e) {
                                    if (e.getKey() === e.ESC) {
                                        field.reset();
                                    }
                                    this.taskStore.clearFilter();
                                },
                                scope : this
                            }
                        }
                    }]
                }
            ]
        });
        
        this.callParent(arguments);
    }
});