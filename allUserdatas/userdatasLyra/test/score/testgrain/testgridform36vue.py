# coding=UTF-8

import json

from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import street4Cursor

@form(
      profile='special.properties',
      
      #gridwidth='50%',
      
      gridheight='610px',
      #gridheight='300px',
      
     )


class TestGridForm36vue(GridForm):  
    def __init__(self, context):
        super(TestGridForm36vue, self).__init__(context)
        self.createAllBoundFields()
        
        
        
    def _getCursor(self, context):
        
        c = street4Cursor(context)
        
        if context.getShowcaseContext().getAdditional() == None:
                c.orderBy('ocatd')
        else: 
                addcontext = None 
                try:
                    addcontext = json.loads(context.getShowcaseContext().getAdditional())
                    try:
                        selectKey = addcontext["refreshParams"]["selectKey"]                    
                        sort = addcontext["refreshParams"]["sort"]
                        filter = addcontext["refreshParams"]["filter"]
                        if sort != "":
                            arrSort = sort.split(',')
                            c.orderBy(*arrSort)
                        else:                        
                            c.orderBy('ocatd')
                    except:       
                        c.orderBy('ocatd')                                                 
                except (TypeError, ValueError):
                    c.orderBy('ocatd')

        self.getFormProperties().setHeader(u'''<h2 class="testStyle">Профайлы</h2>''')
                
        return c


    def getGridHeight(self):
        return 50
    
    
    def get_properties_(self):
        ret = u'''

<properties>

<!--

        <styleClass name="jslivegrid-record-bold"/>
        <styleClass name="jslivegrid-record-italic"/>
        
-->        
        
            </properties>         
'''                  
        
        return ret

        
        
        
        

