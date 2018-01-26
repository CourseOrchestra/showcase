#coding: utf-8


from java.lang import Class
from java.sql import Connection, SQLException, DriverManager
from ru.curs.showcase.runtime import ConnectionFactory,AppInfoSingleton 
from com.ziclix.python.sql import zxJDBC,PyConnection
from ru.curs.showcase.core.jython import JythonProc;
try:
    from ru.curs.showcase.activiti import EngineFactory
except:
    from workflow import testConfig as EngineFactory
    
from workflow.processUtils import ActivitiObject
    
     
class processCompleteHandler(JythonProc):     
    def getRawData(self, event):
        print 'KROKUS'
        #global main, add, session, filterContext, elementId
        return mainproc(event)
                             
def mainproc(event):
    act = ActivitiObject()
#     processEngine = EngineFactory.getActivitiProcessEngine()
#     runtimeService = processEngine.getRuntimeService()
    pyConn = ConnectionFactory.getPyConnection();
    pyConn.autocommit = True
    processInstanceId = event.getProcessInstanceId()
    docVersion = act.runtimeService.getVariable(processInstanceId,'documentVersion')
    docId = act.runtimeService.getVariable(processInstanceId,'docId')
    #createProcessInstanceQuery().processInstanceId(processInstanceId).includeProcessVariables().singleResult()    
    try:
        cur = pyConn.cursor()
        try:
#             cur.executemany("""update orders
#                                     set orderVersion=?
#                                         where ordersId=?""", [(docVersion,docId)])
            cur.executemany("""update orders
                                    set orderVersion=?
                                        where ordersId=?""", [(docVersion,docId)])
            print 'qwe'
        except:     
            print 'asd'
    finally:
        cur.close()
        
        
        
        