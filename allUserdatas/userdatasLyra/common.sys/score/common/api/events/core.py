# coding: UTF-8

'''@package common.api.events.common Модуль содержит базовые классы для
реализации действий (тэг *action* и его дочерние тэги)

Created on 30 сент. 2015 г.

@author tugushev.rr
'''

from common.api.core import ShowcaseBaseElementMixIn, IJSONSerializable


class ActionBaseElement(IJSONSerializable):
    """Базовый класс для action и его составляющих"""
    
    def __init__(self, inKeepUserSettings=None, inIsPartialUpdate=None):
        """
        @param inKeepUserSettings (@c bool) флаг, сохранять ли пользовательские
        настройки
        @param inIsPartialUpdate (@c bool) флаг использования частичного 
        обновления данных
        """
        super(ActionBaseElement, self).__init__()
        self.__keepUserSettings = inKeepUserSettings
        self.__partialUpdate = inIsPartialUpdate
    
    
    def keepUserSettings(self):
        """Возвращает флаг сохранения пользовательских настроек
        @return @c bool 
        """
        return self.__keepUserSettings
    
    
    def setKeepUserSettings(self, value):
        """Устанавливает флаг сохранения пользовательских настроек
        @param value (@c bool) 
        @return ссылка на себя 
        """
        self.__keepUserSettings = value
        return self
        
        
    def partialUpdate(self):
        """Возвращает флаг использования частичного обновления данных
        @return @c bool 
        """
        return self.__partialUpdate
    
    
    def setPartialUpdate(self, value):
        """Устанавливает флаг использования частичного обновления данных
        @param value (@c bool) 
        @return ссылка на себя 
        """
        self.__partialUpdate = value
        return self
     
        
    def toJSONDict(self):
        d = {}
        if self.keepUserSettings():
            d["@keep_user_settings"] = unicode(self.keepUserSettings()).lower()
        
        if self.partialUpdate():
            d["@partial_update"] = unicode(self.partialUpdate()).lower()
            
        return d
    
    
class _BaseActivityElement(ActionBaseElement, ShowcaseBaseElementMixIn):
    """Базовый класс для конкретных действий, выполняемых в тэге action"""
    
    def __init__(self, inId, inAddContext=None, inKeepUserSettings=None, inIsPartialUpdate=None):
        """
        @param inId (@c string) ИД конкретного действия
        @param inAddContext (<tt>любой тип</tt>) 
        @param inKeepUserSettings, inIsPartialUpdate см. 
        @c common.api.events.common.ActionBaseElement
        """
        super(_BaseActivityElement, self).__init__(inKeepUserSettings, inIsPartialUpdate)
        
        self.setId(inId)
        
        self._addContext = inAddContext
    
    
    def toJSONDict(self):
#         d.update(ShowcaseBaseElement.toJSONDict(self))
#         d["add_context"] = self.__addContext
        d = ShowcaseBaseElementMixIn._toJSONDict(self)
        d.update(super(_BaseActivityElement, self).toJSONDict())
        
        d["add_context"] = self._addContext 
    
        return d
    
    