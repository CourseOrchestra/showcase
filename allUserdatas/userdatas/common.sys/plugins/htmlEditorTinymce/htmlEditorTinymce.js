function createHtmlEditorTinymce(parentId, pluginParams, data) {

	var HtmlEditorTinymce = function(elId, pluginParams, data) {
		var self = this;
		self.pluginParams = pluginParams || {};
		self.data = data || '';
		self.elId = elId;
		
		self._init();
	};
	HtmlEditorTinymce.prototype = {
		_init: function() {
			var self = this;
			var textareaId = self.textareaId = (self.elId || tinymce.DOM.uniqueId()) +'_textarea';
			var textarea = document.createElement('textarea');
			textarea.id = textareaId;
			textarea.value = self.data;
			var container = self.elId ? document.getElementById(self.elId):document.body;
			container.appendChild(textarea);
			var tinymceConf = self.pluginParams.tinymce || {};
			tinymceConf.selector = '#'+textareaId;
			tinymce.init(tinymceConf);
		},
		getTinymceEditor: function() {
			return tinymce.get(this.textareaId);
		}
	};
	
	var plugin = new HtmlEditorTinymce(parentId, pluginParams, data);
	if (pluginParams.onDrawPluginComplete) {
		pluginParams.onDrawPluginComplete(plugin);
	};	
};