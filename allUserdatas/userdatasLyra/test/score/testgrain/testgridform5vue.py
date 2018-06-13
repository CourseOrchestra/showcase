# coding=UTF-8

import json

from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import websitesVueCursor

@form(
      
      header=u'''<h2 class="testStyle">lyra-vue грид. Хедер</h2>''',
      footer=u'''<h2 class="testStyle">lyra-vue грид. Футер</h2>''',
       
       )
class TestGridForm5vue(GridForm):  
    def __init__(self, context):
        super(TestGridForm5vue, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        
        c = websitesVueCursor(context)
        
        if context.getShowcaseContext().getAdditional() == None:
                print 'addcontext=None';
                c.orderBy('code')
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
                            c.orderBy('code')
                    #else:
                    except:       
                        c.orderBy('code')                                                 
                    
                    
                except (TypeError, ValueError):
                    print 'addcontext is NOT json'
                    c.orderBy('code')


        
         

        return c
    

    def getGridHeight(self):
        return 20
    
    
        
    def get_properties_(self):
        
        ret = u'''

<properties>

        <styleClass name="jslivegrid-record-bold"/>
        <styleClass name="jslivegrid-record-italic"/>

</properties>         
'''                  
        
        return ret

        
        

