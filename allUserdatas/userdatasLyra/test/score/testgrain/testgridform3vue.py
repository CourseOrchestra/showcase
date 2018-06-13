# coding=UTF-8

import json

from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import street4Cursor

@form(
      profile='test.properties',
      
      #gridwidth='50%',
      
      gridheight='410px',
      #gridheight='300px',
      
     )


class TestGridForm3vue(GridForm):  
    def __init__(self, context):
        super(TestGridForm3vue, self).__init__(context)
        self.createAllBoundFields()
        
        
    def _beforeShow_________________OLD(self, context):
        print 'LLLLLLLLLLLLLLL44.__beforeShow'
        print 'Main'
        print context.getShowcaseContext().getMain();
        print 'Additional'
        print context.getShowcaseContext().getAdditional();
        print 'Filter'
        print context.getShowcaseContext().getFilter();
        print 'Session'
        print context.getShowcaseContext().getSession();
        print 'ElementId'
        print context.getShowcaseContext().getElementId();
        print 'OrderBy'
        print context.getShowcaseContext().getOrderBy();
        
        
        self.getFormProperties().setHeader(u'''<h1 class="testStyle">'''+context.getShowcaseContext().getMain()+'''</h1>''')
        

        if context.getShowcaseContext().getAdditional() == None:
                print 'addcontext=None';
                self.rec().tryFirst();
        else: 
                addcontext = None 
                try:
                    addcontext = json.loads(context.getShowcaseContext().getAdditional())
                    
                    print 'addcontext_json'
                    print addcontext
                    
                    #if addcontext["refreshParams"] != None:
                    try:
                        print addcontext["refreshParams"]
                                                
                        selectKey = addcontext["refreshParams"]["selectKey"]                    
                        sort = addcontext["refreshParams"]["sort"]
                        filter = addcontext["refreshParams"]["filter"]
                    
                        print 'selectKey="'+selectKey+'"';
                        print 'sort="'+sort+'"';
                        print 'filter="'+filter+'"';
                    
                        if selectKey != "current":
                            if selectKey == "":
                                self.rec().tryFirst();
                            else:
                                self.rec().code = selectKey      
                
                    #else:
                    except:
                        self.rec().tryFirst(); 

                    
                        
                    
                except (TypeError, ValueError):
                    print 'addcontext is NOT json'
                    self.rec().tryFirst();            
    
    
        #self.rec().tryFirst();
        
        
#        if context.getShowcaseContext().getAdditional() == None:
#                print 'addcontext=None';
#                self.rec().tryFirst();
#        else: 
#                print 'addcontext="'+context.getShowcaseContext().getAdditional()+'"';
#                self.rec().code = context.getShowcaseContext().getAdditional()




        #self.rec().code = '03007000023001900'
        #self.rec().rnum = u'''967025'''
        #self.rec().gnimb = '7725'
        #self.rec().code = '63012000044006700'
                
        #self.rec().code = '03007000023001900'
        
            
        
    def _getCursor(self, context):
        
        #raise Exception(u"ddddddd")        

        print 'LLLLLLLLLLLLLLL44._getCursor'
        print 'Main'
        print context.getShowcaseContext().getMain();
        print 'Additional'
        print context.getShowcaseContext().getAdditional();
        print 'Filter'
        print context.getShowcaseContext().getFilter();
        print 'Session'
        print context.getShowcaseContext().getSession();
        print 'ElementId'
        print context.getShowcaseContext().getElementId();
        print 'OrderBy'
        print context.getShowcaseContext().getOrderBy();
        

        c = street4Cursor(context)
        
        
        if context.getShowcaseContext().getAdditional() == None:
                print 'addcontext=None';
                c.orderBy('ocatd')
        else: 
                addcontext = None 
                try:
                    addcontext = json.loads(context.getShowcaseContext().getAdditional())
                    
                    print 'addcontext_json'
                    print addcontext
                    
                    #if addcontext["refreshParams"] != None:
                    try:
                    
                        print addcontext["refreshParams"]
                    
                        selectKey = addcontext["refreshParams"]["selectKey"]                    
                        sort = addcontext["refreshParams"]["sort"]
                        filter = addcontext["refreshParams"]["filter"]
                    
                        print 'selectKey="'+selectKey+'"';
                        print 'sort="'+sort+'"';
                        print 'filter="'+filter+'"';

                        if sort != "":
                        
                            arrSort = sort.split(',')
                    
                            print 'arrSort';
                            print arrSort
                    
                            c.orderBy(*arrSort)
                        else:                        
                            c.orderBy('ocatd')
                    #else:
                    except:       
                        c.orderBy('ocatd')                                                 
                    
                    
                except (TypeError, ValueError):
                    print 'addcontext is NOT json'
                    c.orderBy('ocatd')


        

        self.getFormProperties().setHeader(u'''<h2 class="testStyle">'''+context.getShowcaseContext().getMain()+'''</h2>''')
        self.getFormProperties().setFooter(u'''<h2 class="testStyle">'''+context.getShowcaseContext().getAdditional()+'''</h2>''')        

        
                
        
        
        
#        if context.getShowcaseContext().getOrderBy() == None:
#                print '1'
##                c.orderBy('name')                
#                c.orderBy('ocatd')

#        else: 
#                print '2'
#                c.orderBy(*context.getShowcaseContext().getOrderBy())
                
#                self.getFormProperties().setFooter(u'''<h1 class="testStyle">'''+context.getShowcaseContext().getOrderBy()[0]+'''</h1>''')
                
      
#        c.orderBy('name')
#        c.orderBy('uno')
#        c.orderBy('code DESC')
#        c.orderBy('name DESC', 'code DESC')
#        c.orderBy('name aSC')
#        c.orderBy('name desc', 'code', 'gninmb desc')
#        c.orderBy('uno desc', 'code DESC')
#        c.orderBy('name', 'gninmb', 'code')
#        c.orderBy('name desc', 'gninmb desc', 'code desc')
#        c.orderBy('uno desc', 'code asc')


        return c


    def getGridHeight(self):
        return 50
    
    
    def get_properties_(self):
        
        
        name = self.rec()._currentValues()[0]
        
        ret = u'''

<properties>

<!--
        <styleClass name="jslivegrid-record-bold"/>
        <styleClass name="jslivegrid-record-italic"/>
-->        
        
        
            </properties>         
'''                  
        
        return ret

        
        
        
        

