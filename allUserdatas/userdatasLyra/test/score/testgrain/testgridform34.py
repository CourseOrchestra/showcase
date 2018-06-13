# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import street4Cursor

@form(
      profile='test.properties',
      
      #gridwidth='50%',
      
      gridheight='410px',
      #gridheight='300px',
      
      header=u'''<h1 class="testStyle">Лира грид. Хедер</h1>''',
      footer=u'''<h1 class="testStyle">Лира грид4. Футер</h1>''',
      
     )


class TestGridForm34(GridForm):  
    def __init__(self, context):
        super(TestGridForm34, self).__init__(context)
        self.createAllBoundFields()
        
        
    def _getCursor(self, context):
        c = street4Cursor(context)
        c.orderBy('name')
        return c
        

    def getGridHeight(self):
        return 50
    
    
