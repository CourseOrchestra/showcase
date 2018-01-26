# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO

def template(context, main, add, filterinfo, session, elementId):
    print 'Get jsForm template.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''

<script type="text/javascript">
  require(["dijit/registry", "dijit/form/Button", "dojo/dom", "dojo/domReady!"], function(registry, Button, dom){   
    //destroy widgets
    //Without this, there will be an error when re-loading this script
    var widget = registry.byId("submitAsynchButton01");
    if (widget) {      
      widget.destroyRecursive(true);
    }
    var widget = registry.byId("submitSynchButton01");
    if (widget) {
      widget.destroyRecursive(true);
    }
    
    var submitAsynchButton01 = new Button({
      label: "Synch Submit",
      onClick: function(){
          var resulData = jsFormSubmitSynch('1','submit01', 'data');
          if (resulData) {
            if (resulData.isError) {
              dom.byId("result1").innerHTML += "Failure. Error message: '"+resulData.message+"'";
            } else {
              dom.byId("result1").innerHTML += "Success! Received data: '"+resulData+"'";
            }
          } else {
            dom.byId("result1").innerHTML += "Data not received";
          }          
      }
    }, "submitAsynchButton01").startup();
    
    var submitSynchButton01 = new Button({
      label: "Asynch Submit",
      onClick: function(){
          jsFormSubmitAsynch('1','submit01', 'data', 
            function(data) {
              dom.byId("result1").innerHTML += "Success! Received data: '"+data+"'";
            }, function(err) {
              dom.byId("result1").innerHTML += "Failure. Error message: '"+err.message+"'";
            }
          );
      }
    }, "submitSynchButton01").startup();
  });
</script>
<button id="submitAsynchButton01" type="button"></button>
<button id="submitSynchButton01" type="button"></button>
<div id="result1"></div>
    
    '''
    settings = u''''''
    return JythonDTO(data, settings) 
    
def formSubmit(context, main, add, filterinfo, session, elementId, data):
    print 'Submit jsForm form.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    data = u'''
Data of celesta source (input data:"'''+data+'''")    
    '''
    return JythonDTO(data)