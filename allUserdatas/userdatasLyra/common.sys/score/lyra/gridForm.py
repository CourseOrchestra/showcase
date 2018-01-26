# coding: utf-8
import ru.curs.lyra.BasicGridForm as BasicGridForm

class GridForm(BasicGridForm):
    u'''Basic class for a grid form'''
    def __init__(self, context):
        BasicGridForm.__init__(self, context)
        
        
    def setContext(self, session, main, add, elemetId):
        self.session = session
        self.main = main
        self.add = add
        self.elemetId = elemetId
        
        
    def getGridHeight(self):
        return 50
    
    def _beforeShow(self, context):
        '''Override this method to implement some actions 
        to be performed before the grid is shown or refreshed, 
        e. g. position cursor to a certain record'''
        pass
    
    def _beforeSending(self, c):
        '''Override this method to implement some actions 
        to be performed before data XML serialization and sending to client'''
        pass