IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[jsFormSubmit]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[jsFormSubmit]
GO
CREATE PROCEDURE [dbo].[jsFormSubmit]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
	@inData varchar(max)='',
    @outData varchar(max) output
AS
BEGIN
	set @outData=N'Data of DB '+OBJECT_NAME(@@PROCID)+' procedure';	
END
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[jsFormTemplate]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[jsFormTemplate]
GO
CREATE PROCEDURE [dbo].[jsFormTemplate]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',
    @outData varchar(max) output,
    @settings xml ='' output
AS
BEGIN
	set @outData=N'
<script type="text/javascript">
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
          jsFormSubmitAsynch(''1'',''submit01'', ''data'', 
            function(data) {
              dom.byId("result1").innerHTML += "Success! Received data: ''"+data+"''";
            }, function(err) {
              dom.byId("result1").innerHTML += "Failure. Error message: ''"+err.message+"''";
            }
          );
      }
    }, "submitSynchButton").startup();
    });
</script>
<button id="submitSynchButton" type="button"></button>
<div id="result1"></div>';	

set @settings=N'';

END
GO
