# coding: UTF-8

'''@package common.api.events.events Модуль содержит классы событий

Created on 30 сент. 2015 г.

@author tugushev.rr
'''

from common.api.core import IJSONSerializable
from common.api.events.action import Action

EVENT_TAG = 'event'

class GeneralEventTypes(object):
    """События, доступные для всех элементов Showcase"""
    
    ## Событие клика по кнопке
    BUTTON_CLICK = 'single_click'


class GridEventTypes(object):
    """Типы событий грида."""
    
    ROW_SINGLE_CLICK = 'row_single_click'
    ROW_DOUBLE_CLICK = 'row_double_click'
    
    CELL_SINGLE_CLICK = 'cell_single_click'
    CELL_DOUBLE_CLICK = 'cell_double_click'
    
    ROW_SELECTION = 'row_selection'
    
    ROW_SAVE_DATA = 'row_save_data'
    ROW_ADD_RECORD = 'row_add_record'
    
    CELL_EVENTS = [CELL_SINGLE_CLICK, CELL_DOUBLE_CLICK]


class _Events(IJSONSerializable):
    """Базовый класс для создания событий."""
    
    def __init__(self, inType, inAction=Action()):
        """
        @param inType (@c common.api.events.events.GeneralEventTypes or
        @c common.api.events.events.GridEventTypes) тип события
        @param inAction (@c common.api.events.action.Action) действие по событию
        """
        self.__action = inAction
        self.__type = inType
    
    
    def type(self):
        """Возвращает тип события
        @return @c common.api.events.events.GeneralEventTypes or
        @c common.api.events.events.GridEventTypes
        """
        return self.__type
    
    
    def action(self):
        """Возвращает действие по событию
        @return @c common.api.events.action.Action
        """
        return self.__action
    
    
    def setAction(self, value):
        """Устанавливает действие по событию
        @param inAction (@c common.api.events.action.Action) действие по событию
        @return ссылка на себя
        """
        self.__action = value
        return self
        
        
    def toJSONDict(self):
        d = {
            "@name": self.__type,
        }
        
        if self.action():
            d.update(self.__action.toJSONDict())
        
        return d


class Event(_Events):
    """Описывает событие"""
    
    def __init__(self, inLinkId, inType=GeneralEventTypes.BUTTON_CLICK, inAction=Action()):
        """
        @param inLinkId (@c string) ИД события
        @param inType, inAction см. @c common.api.events.events._Events
        """
        super(Event, self).__init__(inType, inAction)
        self.__linkId = inLinkId
    
    
    def linkId(self):
        """Возвращает ИД события
        @return @c string
        """
        return self.__linkId
    
    
    def toJSONDict(self):
        d = super(Event, self).toJSONDict()
        d['@linkId'] = self.linkId()
        
        return d
    
    
    
class GridEvent(_Events):
    """Класс событий грида."""
    
    def __init__(self, inType, inAction=Action(), inColumn=None):
        """
        @param inType (<tt>константа common.api.events.events.GridEventTypes</tt>) тип события
        @param inAction (@c common.api.events.action.Action) действие по событию
        @param inColumn (@c string) столбец, на котором инициируется событие.
        Заполняется для типов @c GridEventTypes.CELL_SINGLE_CLICK и
        @c GridEventTypes.CELL_DOUBLE_CLICK
        """
        
        super(GridEvent, self).__init__(inType, inAction)
        self.__column = inColumn
        
        
    def column(self):
        """Возвращает столбец, на котором инициируется событие."""
        return self.__column
    
    
    def setColumn(self, value):
        """Устанавливает столбец, на котором инициируется событие"""
        
        self.__column = value
        return self 
    
    
    def toJSONDict(self):
        """Перегруженный метод (см. common.api.core.IJSONSerializable).
        
        @throw ValueError Если тип события @c GridEventTypes.CELL_SINGLE_CLICK 
        или @c GridEventTypes.CELL_DOUBLE_CLICK, но столбец (@a column) не задан 
        """
        if self.column() in GridEventTypes.CELL_EVENTS and not self.column():
            raise ValueError(self.column(), 
                "Column has to be specified when using %s event type" % 
                    ' or '.join(e.__name__ for e in GridEventTypes.CELL_EVENTS))
        
        d = super(GridEvent, self).toJSONDict()
        if self.column():
            d['@column'] = self.column()
        
        return d
    
    
def getEvent(eventType):
    def decorator(func):
        def wrapper(cls, *args):
            eventAction = func(cls, *args)
            
            if not eventAction:
                return None
            
            if not isinstance(eventAction, Action):
                raise ValueError("Object of '%s' class expected, but '%s' is given!" % (Action.__package__ + '.' + Action.__name__, eventAction.__class__.__name__))
            
            event = GridEvent(eventType)
            
            # Если больше одного аргумента, значит событие на ячейке и
            # первый аргумент - header item
            if len(args) > 1:
                event.setColumn(args[0].caption)

            event.setAction(eventAction)
                
            return event
        
        return wrapper
    return decorator

