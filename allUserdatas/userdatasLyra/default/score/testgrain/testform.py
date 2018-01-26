# coding=UTF-8
from lyra.cardForm import CardForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import testCursor

@form(profile='default.properties', 
      gridwidth='100px',
      gridheight='200px',
      defaultaction='<foo/>')
class TestForm(CardForm):  
    def __init__(self, context):
        super(TestForm, self).__init__(context)
        self.f1 = 0
        self.f2 = 1
        #self.createField("ff1")
        #fld = self.createField("id")
        self.createAllUnboundFields()
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        return testCursor(context)

    @formfield(celestatype='INT', 
               caption='unbound field 1',
               scale=5)
    def ff1(self):
        return self.f1

    @ff1.setter
    def ff1(self, value):
        self.f1 = value
        
    @formfield(celestatype='INT', 
               caption='unbound field 2')
    def ff2(self):
        return self.f2

    def _afterReceiving(self, c):
        self.f2 = self.f1 * self.f1
        
        