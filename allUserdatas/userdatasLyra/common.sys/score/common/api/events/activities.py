# coding: UTF-8
'''@package common.api.events.activities Модуль содержит классы конткретных
действий

Created on 16 июля 2015 г.

@author tugushev.rr
'''

from common.api.core import IJSONSerializable
from common.api.events.core import _BaseActivityElement
from common.api.utils.tools import procname


class ActionActivityType(object):
    """Описывает группы конкретных действий"""
    
    ## Обновление элементов информационной панели
    DATAPANEL='datapanel'
    ## Серверное действие
    SERVER='server'
    ## Клиентское действие
    CLIENT='client'
    ## Обновление навигатора
    NAVIGATOR='navigator'
    

class ActivityElementType(object):
    """Описывает элементы, внутри конкретных действий"""
    
    ## Элемент информационной панели; используется с типом @c ActionActivityType.DATAPANEL
    DATAPANEL='element'
    ## Клиентская или серверная функция; используется с типами @c ActionActivityType.SERVER и @c ActionActivityType.CLIENT
    CLIENT_SERVER='activity'


class DatapanelElement(_BaseActivityElement):
    """Описывает обновляемый элемент информационной панели"""
    
    def __init__(self, inId, inAddContext=None, inKeepUserSettings=None, inIsPartialUpdate=None):
        """
        @param inId, inAddContext, inKeepUserSettings, inIsPartialUpdate см. 
        common.api.events.common._BaseActivityElement
        """
        super(DatapanelElement, self).__init__(inId, inAddContext, inKeepUserSettings, inIsPartialUpdate)
    
    
    def hide(self):
        """Скрывает элемент
        @warning Для скрытия элемента в add_context записывается *hide*
        @return ссылка на себя
        """
        self._addContext = 'hide'
        return self


class ActivityElement(_BaseActivityElement):
    """Непосредственное действие, выполняемое в server/client action"""
    
    def __init__(self, inId, inProcName, inAddContext=None):
        """
        @param inId, inAddContext см. 
        common.api.events.common._BaseActivityElement
        @param inProcName (<em>string or function object</em>) функция-обработчик
        загрузки тулбара
        @return ссылка на себя) 
        """
        super(ActivityElement, self).__init__(inId, inAddContext)
        self.setProc(inProcName)
    
    
    def proc(self):
        """Возвращает функцию-обработчик
        @return (@c string) полное имя функции (qualified name)
        """
        return self.__procName
    
    
    @procname
    def setProc(self, value):
        """Устанавливает функцию-обработчик
        @param value (<em>string or function object</em>) фунция-обработчик
        @return ссылка на себя
        """
        self.__procName = value
        return self
        
        
    def toJSONDict(self):
        d = super(ActivityElement, self).toJSONDict()
        d['@name'] = self.proc()

        return d

    
class ServerElement(ActivityElement):
    """Класс непосредственного серверного действия.
    
    Полностью повторяет @c common.api.events.activiies.ActivityElement. 
    Служит для конкретной идентификации типа при использовании в 
    common.api.events.action.Action.add
    """
    def __init__(self, inId, inProcName, inAddContext=None):
        super(ServerElement, self).__init__(inId, inProcName, inAddContext)


class ClientElement(ActivityElement):
    """Класс непосредственного клиентского действия.
    
    Полностью повторяет @c common.api.events.activiies.ActivityElement. 
    Служит для конкретной идентификации типа при использовании в 
    common.api.events.action.Action.add
    """
    def __init__(self, inId, inProcName, inAddContext=None):
        super(ClientElement, self).__init__(inId, inProcName, inAddContext)


class ActionActivity(IJSONSerializable):
    """Базовый класс для определённых типов действий - обновления навигатора,
    обновления информационной панели, server/client activity
    
    Каждый такой тип представляет собой набор конкретных действий.
    """
    
    def __init__(self, inActionActivityType):
        """
        @param inActionActivityType 
        (@c common.api.events.activities.ActionActivityType)
        """
        self.__elements = []
        self._type = inActionActivityType
    
    
    def type(self):
        """Возвращает тип действия
        @return @c common.api.events.activities.ActionActivityType
        """
        return self._type
    
    
    def addElement(self, inActivityElement):
        """Добавляет конкретное действие в группу действий
        @param inActivityElement (@c common.api.events.activities.ActivityElement)
        @return ссылка на себя
        """
        self.__elements.append(inActivityElement)
        
        return self
        
        
    def toJSONDict(self):
        elList = [el.toJSONDict() for el in self.__elements]
            
        return elList


class NavigatorActivity(ActionActivity):
    """Группа действий обновления навигатора.
    
    На самом деле эта группа не является группой в полном смысле, т.к. не
    имеет множества конкретных действий.
    """
    
    def __init__(self, inRefresh, inNodeId):
        """
        @param inRefresh (@c bool) флаг, обновлять навигатор или нет
        @param inNodeId (@c string) узел навигатора, который нужно обновить
        """
        super(NavigatorActivity, self).__init__(ActionActivityType.NAVIGATOR)
        
        self.__refresh = inRefresh
        self.__nodeId = inNodeId
        
        
    def refresh(self):
        """Возвращает флаг обновления навигатора
        @return @c bool
        """
        return self.__refresh
    
    
    def setRefresh(self, value):
        """Устанавливает флаг обновления навигатора
        @param value (@c bool)
        @return ссылка на себя 
        """
        self.__refresh = value
        return self
    
    
    def nodeId(self):
        """Возвращает ИД узла навигатора
        @return @c string
        """
        return self.__nodeId
    
    
    def setNodeId(self, value):
        """Устанавливает ИД узла навигатора для обновления
        @param value (@c string)
        @return ссылка на себя 
        """
        self.__nodeId = value
        return self
    
    
    def addElement(self, inActivityElement):
        """**Не использовать**. NavigatorActivity не может содержать действий."""
        raise NotImplementedError('NavigatorActivity activity cannot have subelements!')
    
    
    def toJSONDict(self):
        return {
            '@refresh': unicode(self.__refresh).lower(),
            '@element': self.__nodeId 
        }
    
    
class DatapanelActivity(ActionActivity):
    """Группа действий обновления элементов информационной панели.
    
    При использовании DatapanelActivity в дейсвтии (*action*) навигатора,
    в качестве параметра @a inPanel конструктора #__init__ должен передаваться
    шаблон информационной панели или функция, возвращающая этот шаблон.
    
    При иcпользовании DatapanelActivity для обновления элемента информационной
    панели в действии (*action*) на какое-либо событие (*event*), в качестве 
    параметров @a inPanel и @a inTab конструктора #__init__ должны передаваться
    идентификаторы информационной панели и вкладки с обновляемым элементом 
    (или *'current'*)
    """
    
    def __init__(self, inPanel="current", inTab="current"):
        """
        @param inPanel (<tt>string or function</tt>) ИД информационной панели, 
        или шаблон, или функция, возвращающая шаблон
        @param inTab (@c string) ИД вкладки на информационной панели @a inPanel
        """
        super(DatapanelActivity, self).__init__(ActionActivityType.DATAPANEL)
        
        self.setPanel(inPanel)
        self.setTab(inTab)
    
    
    def panel(self):
        """Возвращает ИД или шаблон информационной панели, в зависимости
        от варианта использования DatapanelActivity.
        """
        return self.__panel 
    
    
    @procname
    def setPanel(self, value):
        """Устанавливает ИД или шаблон информационной панели, в зависимости
        от варианта использования DatapanelActivity.
        @param value (<tt>string or function</tt>) ИД информационной панели, 
        или шаблон, или функция, возвращающая шаблон
        @return ссылка на себя
        """
        self.__panel = value
        return self
    
    
    def tab(self):
        """Возвращает ИД вкладки в текущем действии
        @return @c string
        """
        return self.__tab
    
    
    def setTab(self, value):
        """Устанавливает ИД вкладки в текущем действии
        @return value (@c string)
        @return ссылка на себя
        """
        self.__tab = value
        return self
    
        
    def add(self, inId, inAddContext="current", inKeepUserSettings=None, inIsPartialUpdate=None):
        """Добавляет элемент, который нужно обновить
        @param inId (@c string) ИД обновляемого элемента
        @param inAddContext (<tt>любой тип</tt>) add_context элемента
        @param inKeepUserSettings, inIsPartialUpdate см. 
        @c common.api.events.common._BaseActivityElement
        @return ссылка на себя
        
        @see #addElement
        """
        return self.addElement(DatapanelElement(inId, inAddContext, inKeepUserSettings, inIsPartialUpdate))
    
    
    def addElement(self, inActivityElement):
        """Добавляет элемент, который нужно обновить
        @param inActivityElement (@c common.api.events.activities.DatapanelElement)
        @return ссылка на себя
        @throw TypeError если @a inActivityElement некорректного типа
        """
        
        if not isinstance(inActivityElement, DatapanelElement):
            raise TypeError(inActivityElement.__class__.__name__ + 'given when ' + DatapanelElement.__class__.__name__ + ' expected!')
        
        return super(DatapanelActivity, self).addElement(inActivityElement)
        
        
    def toJSONDict(self):
        d = {"@type": self.panel(), "@tab": self.tab()}
        d[ActivityElementType.DATAPANEL] = super(DatapanelActivity, self).toJSONDict()
        
        return d


class ServerActivity(ActionActivity):
    """Описывает серверное действие"""
    
    def __init__(self):
        super(ServerActivity, self).__init__(ActionActivityType.SERVER)
    
    def add(self, inId, inProcName, inAddContext="current"):
        """
        @param inId (@c string) ИД обновляемого элемента
        @param inProcName (<tt>string or function object</tt>) функция-обработчик
        действия
        @return ссылка на себя
        """
        return self.addElement(ActivityElement(inId, inProcName, inAddContext))
    
    
    def addElement(self, inActivityElement):
        """Добавляет действие, которое нужно выполнить
        @param inActivityElement (@c common.api.events.activities.ActivityElement)
        @return ссылка на себя
        @throw TypeError если @a inActivityElement некорректного типа
        """
        
        if not isinstance(inActivityElement, ActivityElement):
            raise TypeError(inActivityElement.__class__.__name__ + 'given when ' + ActivityElement.__class__.__name__ + ' expected!')
        
        return super(ServerActivity, self).addElement(inActivityElement)
        
        
    def toJSONDict(self):
        d = { ActivityElementType.CLIENT_SERVER: super(ServerActivity, self).toJSONDict()}
        return d


class ClientActivity(ServerActivity):
    """Описывает клиентское действие.
    
    Поведение этого класса полностью совпадает с поведением
    @c common.api.events.activities.ServerActivity. При добавлении 
    пользовательского действия важно помнить, что функция-обработчик действия 
    должна быть **js-функцией** 
    """
    
    def __init__(self):
        super(ClientActivity, self).__init__()
        self._type = ActionActivityType.CLIENT
        
    
if __name__ == '__main__':
    
    de = DatapanelElement('3').hide()
    
    d = DatapanelActivity() \
            .add('1', {'org': 5}) \
            .add('2', {'org': 5}, True) \
            .addElement(de)
    
    s = ServerActivity() \
            .add('p1', 'calc') \
            .add('p2', 'calc2', {'org': 5})
    
    c = ClientActivity() \
            .add('p1', 'calc') \
            .add('p2', 'calc2', {'org': 5})
            
    n = NavigatorActivity(True, '2')
    
    print d.toJSONDict()
    print s.toJSONDict()
    print c.toJSONDict()
    print n.toJSONDict()

