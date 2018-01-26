function createFlashD(parentId, data) {	
    var obj = document.createElement('object'); 
    obj.data = "solutions/general/plugins/flashD/Components/ComponentD.swf"; 
    obj.align="top";
    obj.id="ComponentD";
    obj.title="";
    obj.width="100%";
    obj.height="100%";
    var param = document.createElement('param');
    param.name = "FlashVars";
    param.value = "data=" + (JSON.stringify(data));
    var parent = document.getElementById(parentId);    	
    obj.appendChild(param); 
    var param1 = document.createElement('param');
    param1.name = "wmode";
    param1.value = "opaque";
    obj.appendChild(param1);       
    parent.appendChild(obj);
}