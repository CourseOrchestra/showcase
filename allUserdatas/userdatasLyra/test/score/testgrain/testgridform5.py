# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import websitesCursor

@form( )
class TestGridForm5(GridForm):  
    def __init__(self, context):
        super(TestGridForm5, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        
        c = websitesCursor(context) 

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

        
        

