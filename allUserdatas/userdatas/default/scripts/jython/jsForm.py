# coding: utf-8
'''
Created on 06.02.2016

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO


class jsForm(JythonProc):
    def submiJsForm(self, context, elementId, data):
        return doSubmiJsForm(context, elementId, data)
    def templateJsForm(self, context, elementId):
        return doTemplateJsForm(context, elementId)

def doSubmiJsForm(context, elementId, data):
    data = u'''
Data of jython source (input data:"'''+data+'''")
    '''
    return data

def doTemplateJsForm(context, elementId):
    data = u'''<script type="text/javascript">
  require(["dijit/registry", "dijit/form/Button", "dojo/dom", "dojo/domReady!"], function(registry, Button, dom){    
    var widget = registry.byId("submitSynchButton");
    if (widget) {
      //Without this, there will be an error when re-loading this script
      widget.destroyRecursive(true);
    }
    var submitSynchButton = new Button({
      label: "Asynch Submit",
      onClick: function(){
          //dom.byId("result1").innerHTML += "Thank you! ";
          jsFormSubmitAsynch('1','submit01', 'data', 
            function(data) {
              dom.byId("result1").innerHTML += "Success! Received data: '"+data+"'";
            }, function(err) {
              dom.byId("result1").innerHTML += "Failure. Error message: '"+err.message+"'";
            }
          );
      }
    }, "submitSynchButton").startup();
  });
</script>
<button id="submitSynchButton" type="button"></button>
<div id="result1"></div>'''
    settings = u''''''
    return JythonDTO(data, settings)    
 
if __name__ == "__main__":
    doTemplateJsForm(None, None)