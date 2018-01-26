# coding: UTF-8

"""@package common.api.core Модуль содержит описание базовых интерфейсов."""

from abc import abstractmethod

from common.api.utils.tools import serializeJSON, json2xml, deserializeJSON

class IJSONSerializable(object):
    u"""Базовый класс, определяющий интерфес преобразования в JSON"""
    def __init__(self):
        pass
    
    @abstractmethod
    def toJSONDict(self):
        """Должен возвращать JSON-словарь. **Абстрактыный**, должен быть
        реализован наследниками
        @return @c dict
        """
        pass
    
    def toJSON(self):
        """Возвращает JSON-строку, сформированную по словарю, который вернул
        #toJSONDict
        @return @c string
        """
        jd = self.toJSONDict()
        return serializeJSON(jd)


    
class IXMLSerializable(IJSONSerializable):
    """Базовый класс, определяющий интерфес преобразования в XML"""
    
    def toXML(self):
        """Возвращает @c XML, полученной из JSON-строки (см. #toJSON)
        @return @c string
        """
        return json2xml(self.toJSON())


class IJSONConstructable(object):
    """Интерфейс для классов, объекты которых могут формироватсья из JSON.
    
    Методы этого интерфейса являются зеркальнымы методам интерфейса
    commonapi.common.IJSONSerializable
    
    Поля объекта должен заполнять #fromJSONDict
    """
    def fromJSON(self, jsonString):
        """Должен возвращать JSON-словарь, сформированный по @a jsonString
        @param jsonString (@c string) JSON-строка
        @return (@c dict) JSON-словарь 
        """
        d = deserializeJSON(jsonString)
        self.fromJSONDict(d)
    
    def fromJSONDict(self, jsonDict):
        """Должен заполнять поля объекта из словаря @a jsonDict.
        **Абстрактный**. Должен быть переопределён в наследнике.
        @param jsonDict (@c dict) JSON-словарь  
        """
        raise NotImplementedError()


class ShowcaseBaseElementMixIn(object):
    """Добавочный класс для определения элементов Showcase.
    
    Класс можен использоваться как базовый, так и как MixIn-класс.
    В последнем случае метод #setId должен вызываться явно для установки
    ИД.
    """
    def __init__(self, inId):
        """
        @param inId (@c string) ИД элемента
        """ 
        self.__id = inId
        
    def id(self):
        """Возвращает ИД элемента
        @return @c string
        """
        return self.__id
    
    def setId(self, value):
        """Устанавливает ИД элемента
        @param value (@c string) ИД элемента 
        """
        self.__id = value
        return self
        
    @staticmethod    
    def _toJSONDict(cls):
        """Возвращается ИД элемента, как атрибут.
        @return (@c dict) JSON-словарь
        """
        
        return {'@id': cls.id()}
    
    
class ShowcaseBaseElement(ShowcaseBaseElementMixIn, IJSONSerializable):
    """Базовый класс для всех элементов Showcase. 
    Реализует ShowcaseBaseElementMixIn и IJSONSerializable.
    """
    
    def __init__(self, inId):
        """
        @param inId (@c string) ИД элемента
        """ 
        super(ShowcaseBaseElement, self).__init__(inId)
    
    
    def toJSONDict(self):
        return ShowcaseBaseElementMixIn._toJSONDict(self)
    

class ShowcaseBaseNamedElement(ShowcaseBaseElement):
    """Базовый класс для всех именованных элементов Showcase.
    
    Методы:
        - id(), setId(value) - см.  ShowcaseBaseElement
        - name(), setName(value) - возвращает/устанавливает имя элемента.
        Тип value - string.
    
    """
    
    def __init__(self, inId, inName):
        """
        @param inId см. commonapi.common.ShowcaseBaseElement
        @param inName (@c string) наименование элемента Showcase
        """
        super(ShowcaseBaseNamedElement, self).__init__(inId)
        self.__name = inName
    
    
    def name(self):
        """Врзвращает наименование элемента Showcase
        @return @c string
        """
        return self.__name
    
    
    def setName(self, value):
        """Устанавливает наименование элемента Showcase.
        @param value (@c string)
        @return сслыка на себя
        """
        self.__name = value
        return self
    
        
    def toJSONDict(self):
        d = super(ShowcaseBaseNamedElement, self).toJSONDict()
        d['@name'] = self.name()
        
        return d


        