function setCurrentUserDetailsForViewInHTMLControl(preffix)
{
	
	if (document.getElementById('CURRENT_USER_CONTROL_' + preffix)) {
	 document.getElementById('CURRENT_USER_CONTROL_' + preffix).innerHTML=getCurrentUserNameFeedbackJSNIFunction();
	}
	
	if (document.getElementById('CURRENT_USER_CONTROL_FULLNAME_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_FULLNAME_' + preffix).innerHTML=getCurrentUserFullNameFeedbackJSNIFunction();
	}
	
	if (document.getElementById('CURRENT_USER_CONTROL_EMAIL_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_EMAIL_' + preffix).innerHTML=getCurrentUserEMailFeedbackJSNIFunction();
	}

	if (document.getElementById('CURRENT_USER_CONTROL_SID_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_SID_' + preffix).innerHTML=getCurrentUserSIDFeedbackJSNIFunction();
	}

	if (document.getElementById('CURRENT_USER_CONTROL_PHONE_' + preffix)) {
		 document.getElementById('CURRENT_USER_CONTROL_PHONE_' + preffix).innerHTML=getCurrentUserPhoneFeedbackJSNIFunction();
	}	

}

function safeIncludeJS(jsFile) { 
	        dojo.xhrGet({ 
	                url: jsFile, 
	                sync: true,
	                load: function(responce, ioArgs) { 
	                        if (responce != null) { 
                                var newscript = document.createElement('script'); 
	                                newscript.text = responce; 
	                                newscript.type = "text/javascript"; 
	                                var body = document.body; 
	                                body.appendChild(newscript); 
                        } 
	                else { 
                        console.log("failed load script!");    
	                     } 
	              }                
	        }); 
	
}

function getErrorByIFrame(iframeName)
{
	var err = null; 
  
	var iframe = document.getElementsByName(iframeName)[0];
	
	if(iframe.contentDocument != null){
		var body = iframe.contentDocument.getElementsByTagName("body")[0];
		
		if((body != null) && (typeof body != "undefined")) {
			var message = body.innerHTML;
			if((message.trim() != "") && (message.trim() != "<pre></pre>") && (message.trim() != "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\"></pre>")) {
				err = message;
				
				err = err.replace("<root>", "").replace("</root>", "");
			}
		}
	}
	
	return err; 
}

function addUpload(formId)
{
	var baseForm = document.getElementById(formId); 
	
	var form = baseForm.cloneNode(true);
	
	var lastAddingId = baseForm.getAttribute("lastAddingId");
	if(lastAddingId)
	{
		lastAddingId++; 
	}
	else
	{
		lastAddingId = 1;
	}
	baseForm.setAttribute("lastAddingId",lastAddingId);
	form.setAttribute("id", baseForm.getAttribute("id")+"_add_"+lastAddingId);
	
	var baseTarget = baseForm.getAttribute("target");	
	var target = baseTarget+"_add_"+lastAddingId;
	form.setAttribute("target", target);	
	
	
	var inputs = form.getElementsByTagName("input");
	for (var i=0; i<inputs.length; i++)
	{
		var name = inputs[i].getAttribute("name");
		if(name.indexOf("@@filedata@@") > -1)
		{
			var onchange = inputs[i].getAttribute("onchange");
			if(onchange)
			{
				onchange = onchange.replace("add_upload_index_0", "add_upload_index_"+lastAddingId);
				inputs[i].setAttribute("onchange", onchange);
			}
			break;
		}	
	}
	
	
	baseForm.parentNode.appendChild(form);
	
//--------------	
	
	var baseFrame = document.getElementsByName(baseTarget)[0];
	
	var frame = baseFrame.cloneNode(true);

	frame.setAttribute("name", target);
	
	var onload = frame.getAttribute("onload");
	onload = onload.replace(baseTarget, target);
	frame.setAttribute("onload", onload);
	
	baseFrame.parentNode.appendChild(frame);
	
}

function _showModalDialog(url, width, height, closeCallback) {
    var modalDiv,
        dialogPrefix = window.showModalDialog ? 'dialog' : '',
        unit = 'px',
        maximized = width === true || height === true,
        w = width || 600,
        h = height || 500,
        border = 5,
        taskbar = 40, // windows taskbar
        header = 20,
        x,
        y;

    if (maximized) {
        x = 0;
        y = 0;
        w = screen.width;
        h = screen.height;
    } else {
        x = window.screenX + (screen.width / 2) - (w / 2) - (border * 2);
        y = window.screenY + (screen.height / 2) - (h / 2) - taskbar - border;
    }

    var features = [
            'toolbar=no',
            'location=no',
            'directories=no',
            'status=no',
            'menubar=no',
            'scrollbars=no',
            'resizable=no',
            'copyhistory=no',
            'center=yes',
            dialogPrefix + 'width=' + w + unit,
            dialogPrefix + 'height=' + h + unit,
            dialogPrefix + 'top=' + y + unit,
            dialogPrefix + 'left=' + x + unit
        ],
        showModal = function (context) {
            if (context) {
                modalDiv = context.document.createElement('div');
                modalDiv.style.cssText = 'top:0;right:0;bottom:0;left:0;position:absolute;z-index:50000;';
                modalDiv.onclick = function () {
                    if (context.focus) {
                        context.focus();
                    }
                    return false;
                }
                window.top.document.body.appendChild(modalDiv);
            }
        },
        removeModal = function () {
            if (modalDiv) {
                modalDiv.onclick = null;
                modalDiv.parentNode.removeChild(modalDiv);
                modalDiv = null;
            }
        };

    // IE
    if (window.showModalDialog) {
        window.showModalDialog(url, null, features.join(';') + ';');

        if (closeCallback) {
            closeCallback();
        }
    // Other browsers
    } else {
        var win = window.open(url, '', features.join(','));
        if (maximized) {
            win.moveTo(0, 0);
        }

        // When charging the window.
        var onLoadFn = function () {
                showModal(this);
            },
            // When you close the window.
            unLoadFn = function () {
                window.clearInterval(interval);
                if (closeCallback) {
                    closeCallback();
                }
                removeModal();
            },
            // When you refresh the context that caught the window.
            beforeUnloadAndCloseFn = function () {
                try {
                    unLoadFn();
                }
                finally {
                    win.close();
                }
            };

        if (win) {
            // Create a task to check if the window was closed.
            var interval = window.setInterval(function () {
                try {
                    if (win == null || win.closed) {
                        unLoadFn();
                    }
                } catch (e) { }
            }, 500);

            if (win.addEventListener) {
                win.addEventListener('load', onLoadFn, false);
            } else {
                win.attachEvent('load', onLoadFn);
            }

            window.addEventListener('beforeunload', beforeUnloadAndCloseFn, false);
        }
    }
}

function showAboutFeedbackJSNIFunction()  
{	
	_showModalDialog("about.jsp", 600, 450, false);
}

var convertorFunc = function(chartId, chartLegendId, optionSet1, optionSet2) {

   if (dojo.isString(optionSet1)) optionSet1 = dojo.fromJson(optionSet1);
   if (dojo.isString(optionSet2)) optionSet2 = dojo.fromJson(optionSet2);
   var chartOptions = optionSet2;

   // copy id, width and height
   chartOptions.id = chartId;   
   if (optionSet1.width) chartOptions.width = optionSet1.width;
   if (optionSet1.height) chartOptions.height = optionSet1.height;
   
   // copy series
   chartOptions.series = optionSet1.series;
   
   // copy labels to the chartOptions
   if (chartOptions.axisX && optionSet1.labelsX) {
      chartOptions.axisX.labels = optionSet1.labelsX;
   }
   if (chartOptions.axisY && optionSet1.labelsY) {
      chartOptions.axisY.labels = optionSet1.labelsY;
   }   

   // copy legend settings
   if (chartLegendId) {
	  chartOptions.legend = chartOptions.legend ? chartOptions.legend : {};
      chartOptions.legend.id = chartLegendId;
      chartOptions.legend.options =  {horizontal: false};
   }

   return chartOptions;
};

var eventCallbackChartHandler = function(chartEvent) {
   if (chartEvent.type=="onclick") {
      //console.debug(chartEvent.chart.node.id, chartEvent.run.name, chartEvent.index);
      gwtChartFunc(chartEvent.chart.node.id, chartEvent.run.name, chartEvent.index);
   }
};

var eventHandler2 = function(chartEvent) {
   if (chartEvent.type=="onclick") console.debug("2", chartEvent);
};


// need for dgrids !!
var arrGrids = {};



function measureDownloadSpeed(contentSize)  
{	
	var startTime = (new Date()).getTime(); 
	
    dojo.xhrGet({ 
        url: "secured/MeasureDownloadSpeed?contentSize="+contentSize, 
        sync: true,
        load: function(responce, ioArgs) { 
                if (responce != null) { 
//                	alert("2");
                	
                	var endTime = (new Date()).getTime();

/*                	
                	var duration = Math.round((endTime - startTime) / 1000) ;
                	var bitsLoaded = (contentSize*1024*1024) * 8;
                	var speedBps = Math.round(bitsLoaded / duration);
                	var speedKbps = (speedBps / 1024).toFixed(2);
                	var speedMbps = (speedKbps / 1024).toFixed(2);
                	
*/
                	var duration = Math.round((endTime - startTime)) ;
                	var bitsLoaded = (contentSize*1024*1024) * 8 * 1000;
                	var speedBps = Math.round(bitsLoaded / duration);
                	var speedKbps = (speedBps / 1024).toFixed(2);
                	var speedMbps = (speedKbps / 1024).toFixed(2);
                	
                	
                	
                	alert('Скорость загрузки контента: \n' +
                			speedBps + ' bps\n' +
                			speedKbps + ' kbps\n' +
                			speedMbps + ' Mbps\n');
 
 
                }  else {
                	alert("Ошибка определения скорости загрузки контента!");
                } 
        }                
    }); 
	
}



function preloadGrids() 
{
	require({async:true}, 
			[
             "dijit/registry",
	         "dijit/Toolbar",
	         "dojo/_base/array",
             "dijit/ToolbarSeparator",
             "dijit/DropDownMenu", 
             "dijit/MenuItem",
             "dijit/popup",
             "dojo/fx",
	         
	         "dijit/form/Button",
	         "dijit/form/DropDownButton",
	         "dijit/form/ComboButton",
	         "dijit/form/ToggleButton",
	         "dijit/form/CurrencyTextBox",
	         "dijit/form/DateTextBox",
	         "dijit/form/NumberSpinner",
	         "dijit/form/NumberTextBox",
	         "dijit/form/TextBox",
	         "dijit/form/TimeTextBox",
	         "dijit/form/ValidationTextBox",
	         "dijit/form/SimpleTextarea",
	         "dijit/form/Textarea",
	         "dijit/form/Select",
	         "dijit/form/ComboBox",
	         "dijit/form/MultiSelect",
	         "dijit/form/FilteringSelect",
	         "dijit/form/HorizontalSlider",
	         "dijit/form/VerticalSlider",
	         "dijit/form/CheckBox",
	         "dijit/form/RadioButton",
	         "dijit/form/DataList",

			 "dojo/_base/lang",
	         "dojo/has",			 
			 "dgrid/List",
			 "dgrid/OnDemandGrid",
	         "dgrid/extensions/Pagination",			 
	         "dgrid/extensions/CompoundColumns", 
	         "dgrid/ColumnSet", 
			 "dgrid/extensions/ColumnResizer",
			 "dgrid/Selection",
	         "dgrid/CellSelection", 				
			 "dgrid/Editor",
			 "dgrid/Keyboard",
			 "dgrid/Tree",			 
			 "dojo/_base/declare",
	         "dstore/QueryResults",
			 "dstore/Rest",
			 "dstore/Trackable",
			 "dstore/Cache",
			 "dstore/Tree",			 
	     	 "dojo/dom-construct",	     
	     	 "dojo/when",	         
			 "dojo/domReady!"
	     	 
	         ],	function(){
		
		safeIncludeJS("js/ui/grids/toolbar.js");
		safeIncludeJS("js/ui/grids/liveDGrid.js");
		safeIncludeJS("js/ui/grids/pageDGrid.js");
		safeIncludeJS("js/ui/grids/treeDGrid.js");
		safeIncludeJS("js/ui/grids/lyraDGrid.js");
		
	});
	
	
	
	
	
}



