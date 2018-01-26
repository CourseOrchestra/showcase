# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import street4Cursor

@form()
class TestGridForm2(GridForm):  
    def __init__(self, context):
        super(TestGridForm2, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        
#        print 'dddddddd23'
#        print self
#        print context

        c = street4Cursor(context) 

        c.orderBy('name')
        
        return c
    

    def getGridHeight(self):
        return 20

