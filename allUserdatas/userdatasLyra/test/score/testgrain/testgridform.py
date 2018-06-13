# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import testCursor

@form()
class TestGridForm(GridForm):  
    def __init__(self, context):
        super(TestGridForm, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        
#        print 'dddddddd23'
#        print self
#        print context
        
        return testCursor(context)

    def getGridHeight(self):
        return 20

