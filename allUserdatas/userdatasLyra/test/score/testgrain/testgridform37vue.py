# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import testCursor

@form()
class TestGridForm37vue(GridForm):  
    def __init__(self, context):
        super(TestGridForm37vue, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        
        self.getFormProperties().setHeader(u'''<h2 class="testStyle">Различные типы столбцов</h2>''')
        
        return testCursor(context)

    def getGridHeight(self):
        return 50

