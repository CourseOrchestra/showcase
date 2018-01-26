Ext.ns('App');
//Ext.Loader.setConfig({enabled: true, disableCaching : false });
//Ext.Loader.setPath('Gnt', '../../js/Gnt');
//Ext.Loader.setPath('Sch', '../../../ExtScheduler2.x/js/Sch');
//Ext.Loader.setPath('MyApp', '.');

Ext.require([
    'MyApp.DemoGanttPanel'
]);

Ext.onReady(function() {
    Ext.QuickTips.init();
    
    App.Gantt.init();
});

App.Gantt = {
    
    // Initialize application
    init : function(serverCfg) {        
        this.gantt = this.createGantt();
        
        var vp = Ext.create("Ext.Viewport", {
            layout  : 'border',
            items   : [
                {
                    region      : 'north',
                    contentEl   : 'north',
                    bodyStyle   : 'padding:15px'
                },
                this.gantt
            ]
        });
    },
    
    createGantt : function() {
        var taskStore = Ext.create("Gnt.data.TaskStore", {
            sorters : 'StartDate',
            proxy : {
                type : 'ajax',
                method: 'GET',
                url: 'tasks.json',
                reader: {
                    type : 'json'
                }
            }
        });
        
        var dependencyStore = Ext.create("Ext.data.Store", {
            autoLoad: true,
            model : 'Gnt.model.Dependency',
            proxy: {
                type : 'ajax',
                url: 'dependencies.json',
                method: 'GET',
                reader: {
                    type : 'json'
                }
            }
        });
        
        var g = Ext.create("MyApp.DemoGanttPanel", {
            region          : 'center',
            selModel        : new Ext.selection.TreeModel({ ignoreRightMouseSelection : false, mode     : 'MULTI'}),
            taskStore       : taskStore,
            dependencyStore : dependencyStore,
            
            //snapToIncrement : true,    // Uncomment this line to get snapping behavior for resizing/dragging.
            
            startDate       : new Date(2010,0,4), 
            endDate         : Sch.util.Date.add(new Date(2010,0,4), Sch.util.Date.WEEK, 20), 
            
            viewPreset      : 'weekAndDayLetter'
        });
        
        g.on({
            dependencydblclick : function(ga, rec) {
                var from    = taskStore.getNodeById(rec.get('From')).get('Name'),
                    to      = taskStore.getNodeById(rec.get('To')).get('Name');
                    
                Ext.Msg.alert('Hey', Ext.String.format('You clicked the link between "{0}" and "{1}"', from, to));
            },
            scope : this
        });
        
        return g;
    }
};

