function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.search);
	if (results == null)
		return "";
	else
		return decodeURIComponent(results[1].replace(/\+/g, " "));
}

function currentPatient(mainContext, addContext, folterContext) {
	if (mainContext == 'test') {
		window.open(document.location.protocol+"//"+document.location.host+ document.location.pathname+"?test", 'Тестирование разработок', 'menubar=yes, toolbar=yes, status=yes, location=yes');
	}
	else {
		document.location.href = document.location.protocol+"//"+document.location.host+ document.location.pathname + "?patient"
	}
	
}

function gotoLink(mainContext, addContext, filterContext) {
	window.open(document.location.protocol+"//"+document.location.host+ document.location.pathname+addContext, "_blank");
}

function InsertCodeInTextArea(textValue, fieldId, type, queryIndex) {

	queryIndex = queryIndex || 1;

	//work with textValue:
	if (type == 'text') {
		textValue = "'" + textValue + "'";
	} else if (type == 'date') {
		textValue = "D" + textValue.split('-').join('');
	} else if (type == 'brackets') {
		textValue = "[" + textValue + "]";
	} else if (type == 'currentChoice') {
		textValue = "[current].[" + textValue + "]";
	}

	//Get textArea HTML control
	var txtArea = document.getElementsByClassName(fieldId)[queryIndex-1].getElementsByTagName("textarea")[0];

	//IE
	if (document.selection) {
		txtArea.focus();
		var sel = document.selection.createRange();
		sel.text = textValue;
		return "";
	}
	//Firefox, chrome, mozilla
	else if (txtArea.selectionStart || txtArea.selectionStart == '0') {
		var startPos = txtArea.selectionStart;
		var endPos = txtArea.selectionEnd;
		var scrollTop = txtArea.scrollTop;
		txtArea.value = txtArea.value.substring(0, startPos)  + textValue + txtArea.value.substring(endPos, txtArea.value.length);
		txtArea.focus();
		txtArea.selectionStart = startPos + textValue.length;
		txtArea.selectionEnd = startPos + textValue.length;
	} else {
		txtArea.value += textArea.value;
		txtArea.focus();
	}
}

function headerRefresh(mainContext, addContext, filterContext){
	//var add = JSON.parse(addContext);
	modeHtml = 'Текущая разработка: <b>' + addContext + '</b>';
	if (document.getElementById("modeHtml")) {
			//var modeTr = document.getElementById("modeHtml").getElementsByTagName("b")[0];
			//document.getElementById("modeHtml").removeChild(modeTr);
			document.getElementById("modeHtml").innerHTML = modeHtml;
			}
}
