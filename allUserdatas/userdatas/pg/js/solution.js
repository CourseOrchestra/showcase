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