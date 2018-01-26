/*Фукнция для открытия ссылки, переданной через add-context в новой вкладке*/
function gotoLink(mainContext, addContext, filterContext) {
	window.open(document.location.protocol+"//"+document.location.host+ document.location.pathname+addContext, "_blank");
}