# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import street6Cursor

@form(
      profile='test.properties',
      
      #gridwidth='50%',
      
      gridheight='410px',
      #gridheight='300px',
      
      header=u'''<h1 class="testStyle">Лира грид. Хедер</h1>''',
      footer=u'''<h1 class="testStyle">Лира грид4. Футер</h1>''',
      
      defaultaction=u'''
        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="443">
                                    <add_context>Действие по умолчанию</add_context>                                                                                             
                                </element> 
                                <element id="444">
                                    <add_context>Действие по умолчанию 2</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
        </action>
      '''
     )


class TestGridForm33(GridForm):  
    def __init__(self, context):
        super(TestGridForm33, self).__init__(context)
        self.createAllBoundFields()
        
        
    def _beforeShow(self, context):
        
        self.getFormProperties().setHeader(u'''<h1 class="testStyle">'''+context.getShowcaseContext().getMain()+'''</h1>''')
        
        if context.getShowcaseContext().getAdditional() == None:
                print 'addcontext=None';
                self.rec().tryFirst();
        else: 
                print 'addcontext="'+context.getShowcaseContext().getAdditional()+'"';
                self.rec().code = context.getShowcaseContext().getAdditional()
                
        
            
        
    def _getCursor(self, context):
        
        #raise Exception(u"ddddddd")        

        print 'ffffffffffffffffffffffff44._getCursor'
        print context.getShowcaseContext().getMain();
        print context.getShowcaseContext().getOrderBy();

        c = street6Cursor(context)
        
        
        if context.getShowcaseContext().getOrderBy() == None:
                print '1'
                c.orderBy('name')
#                c.orderBy('uno')
        else: 
                print '2'
                c.orderBy(*context.getShowcaseContext().getOrderBy())
                
                self.getFormProperties().setFooter(u'''<h1 class="testStyle">'''+context.getShowcaseContext().getOrderBy()[0]+'''</h1>''')
                
       
#        c.orderBy('name')
#        c.orderBy('code DESC')
#        c.orderBy('name DESC', 'code DESC')
#        c.orderBy('name aSC')
#        c.orderBy('name desc', 'code', 'gninmb desc')
#        c.orderBy('uno desc', 'code DESC')
#        c.orderBy('name', 'gninmb', 'code')
#        c.orderBy('name desc', 'gninmb desc', 'code desc')
#        c.orderBy('uno desc', 'code asc')


        return c


    def getGridHeight(self):
        return 50
    
    
    def get_properties_(self):
        
        name = self.rec()._currentValues()[0]
        
        ret = u'''

<properties>

<!--
        <styleClass name="jslivegrid-record-bold"/>
        <styleClass name="jslivegrid-record-italic"/>
-->        
        

                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="443">
                                    <add_context>'''+name+'''</add_context>                                                                                             
                                </element> 
                                <element id="444">
                                    <add_context>'''+name+'''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    

  
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="444">
                                            <add_context>'''+name+'''</add_context>   
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>     


            </properties>         
'''                  
        
        return ret

        
        
        
        

