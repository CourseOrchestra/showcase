/**
 * 
 */

function showcaseShowMessage(mainContext, addContext, filterContext)
{
	window.alert('Выделена строка: ' + addContext);
}

function showcaseShowAddContext(mainContext, addContext, filterContext)
{
	window.alert('add_context = ' + addContext);
}

function showcaseShowFilterContext(mainContext, addContext, filterContext)
{
	window.alert('filter_context = ' + filterContext);
}

function showcaseShowMainContext(mainContext, addContext, filterContext)
{
	window.alert('main_context = ' + mainContext);
}

function showcaseShowAllContexts(mainContext, addContext, filterContext)
{
	window.alert('main_context = ' + mainContext + '\n' + 'add_context = ' + 
			addContext + '\n' + 'filter_context = ' + filterContext);
}


function toggleLevel2()
{
	alert("message");
}

function addAdditionalAboutInfo()
{
	//Instead of empty string you can place text that would be printed in window "О программе" 
	
	return "";
}