# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import test2Cursor

@form( 
      
header=u'''<h1 class="testStyle">Лира грид. Хедер</h1>''',
      
)
class TestGridForm4(GridForm):  
    def __init__(self, context):
        super(TestGridForm4, self).__init__(context)
        self.createAllBoundFields()
        
        #self.getFormProperties().setHeader(u'''<h1 class="testStyle">'''+context.getShowcaseContext().getMain()+'''</h1>''')        
        
    def _getCursor(self, context):
        
        print 'ffffffffffffffffffffffff44._getCursor'        
        print self.getFormProperties().getHeader()
        
        c = test2Cursor(context)
        
        
        self.getFormProperties().setHeader(self.getFormProperties().getHeader()+u'''f''')

        return c
    

    def getGridHeight(self):
        return 20
    
    
        
        

