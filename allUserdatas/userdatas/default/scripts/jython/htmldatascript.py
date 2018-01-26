# coding: utf-8
'''
Created on 14.04.2016

'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO


class htmldatascript(JythonProc):
    
    def templateJsForm(self, context, elementId):
        return doTemplateJsForm(context, elementId)

def doTemplateJsForm(context, elementId):
    data = u'''
      <script charset="utf-8">
    
  </script><script type="text/javascript" charset="utf-8">
  
  require(["dijit/registry", "dijit/form/Button", "dojo/dom", "dijit/form/TextBox", "dojo/domReady!"], function(registry, Button, dom){ 
        var destroy = function(widget) {
      if (widget) {      
        widget.destroyRecursive(true);
      }
    }
    destroy(registry.byId("ModalWindow"));
    destroy(registry.byId("JsFormAction"));
    destroy(registry.byId("SynchButton"));
    destroy(registry.byId("ASynchButton"));
    destroy(registry.byId("dojoinput"));
    destroy(registry.byId("simpleinput"));
    
    function jsOnload() {
    document.getElementById("simpleinput").value = "javascript_onload_function";
    }      
    jsOnload();
  
      var callback = function(resulData) {
      if (resulData) {
        if (resulData.isError) {
          dom.byId("dojoinput").value = "Failure. Error message: '"+resulData.message+"'";
        } else {
          dom.byId("dojoinput").value = "Success! Received data: '"+resulData+"'";
        }
      } else {
        dom.byId("dojoinput").value += "Data not received";
      }    
    }
    
    var ASynchButton = new Button({
      label: "Asynch Submit",
      onClick: function(){  
          jsFormSubmitAsynch('13','mysubmit2', 'data', 
            function(data) {
              dom.byId("result").innerHTML += "Success! Received data: '"+data+"'";
            }, function(err) {
              dom.byId("result").innerHTML += "Failure. Error message: '"+err.message+"'";
            }
          );
      }
    }, "ASynchButton").startup();
    
    var SynchButton = new Button({
      label: "Synch Submit",
      onClick: function(){
          var resulData = jsFormSubmitSynch('13','mysubmit1', 'data');
          callback(resulData);
      }
    }, "SynchButton").startup();
    
    var JsFormAction = new Button({
      label: "JsFormAction",
      onClick: function(){
          jsFormAction('13','10');
      }
    }, "JsFormAction").startup();
    
    
    var ModalWindow = new Button({
      label: "Open Modal Window",
      onClick: function(){
          jsFormAction('13','11');
      }
    }, "ModalWindow").startup();
    
    

  });
</script>
<input id="simpleinput" type="text" name="name1"><br><br>
<input id="dojoinput" data-dojo-type="dijit/form/TextBox" type="text" value="dojo textbox default value"></input><br><br>
<button id="ASynchButton" type="button"></button>
<button id="SynchButton" type="button"></button>
<button id="JsFormAction" type="button"></button>
<button id="ModalWindow" type="button"></button>
<div id="result"></div>'''
    settings = u'''<properties>
                <event name="single_click" linkId="10">
                <action>
                <main_context>new_main_context</main_context> 
                <navigator element="0901"/>
                </action>                        
                </event> 
                <event name="single_click" linkId="11">
                <action show_in="MODAL_WINDOW">
                    <main_context>current</main_context>
                    <modalwindow caption="myButton click." height="200" width="600"/>
                    <datapanel type="current" tab="current">
                        <element id="333">
                          <add_context>add_context33</add_context>
                        </element>
                    </datapanel>
                </action>
                </event>
    </properties>'''
    return JythonDTO(data, settings)    
 
if __name__ == "__main__":
    doTemplateJsForm(None, None)