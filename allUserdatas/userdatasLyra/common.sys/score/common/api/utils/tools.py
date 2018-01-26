# coding: UTF-8

'''@package common.api.utils.tools Модуль содержит инструментальные функции
общего назначения

Created on 20 июля 2015 г.

@author tugushev.r
'''

import json

from ru.curs.celesta.showcase.utils import XMLJSONConverter
from inspect import isfunction
from importlib import import_module

try:
    from ru.curs.showcase.core.jython import JythonDTO #@UnresolvedImport
except ImportError:
    from ru.curs.celesta.showcase import JythonDTO


serializeJSON = lambda jsonDict: json.dumps(jsonDict)
deserializeJSON = lambda jsonString: json.loads(jsonString)

json2xml = lambda jsonString: XMLJSONConverter.jsonToXml(jsonString)
xml2json = lambda xmlString: XMLJSONConverter.xmlToJson(xmlString)

objectQualifiedName = lambda obj: '%s.%s' %(obj.__module__, obj.__class__.__name__)
classQualifiedName = lambda cls: '%s.%s' %(cls.__module__, cls.__name__)


def importObject(mod, obj):
    obj_ = getattr(import_module(mod), obj)
    return obj_


def getCursor(tableName):
    """Возвращает класс курсора по полному имени таблицы.
    @param tableName (@c string) имя таблицы в формате \<гранула>.\<таблица>
    @return @c Cursor
    """
    
    g, t = tableName.split('.')
    
    return importObject("%s._%s_orm" % (g, g), "%sCursor" % t)


def jsonToXml(arg):
    """Конвертирует значение - JSON-словарь или JSON-строку - в XML.
    
    @param  arg (@c dict or @c string) JSON-словарь или JSON-строка.
    @return @ string XML
    """
    
    data = arg
    if isinstance(arg, dict):
        data = serializeJSON(arg)
        
    data = json2xml(data)
    return data


def createJythonDTO(inData, inSettings=None, convertData=True, convertSettings=True):
    """Конвертирует @a inData и @a inSettings в @c XML и создаёт объект @c JythonDTO. 
    
    По умолчанию производится конвертация обоих параметров. Также, один из или
    оба параметра могут не конвертироваться (@a convertData=False и/или 
    @a convertSettings=False соответственно), т.е. не будет выполняться никаких
    преоразований. Это может потребоваться, если один из параметров уже 
    преобразован в @c XML.
    
    @param inData (@c dict or @c string) JSON-словарь или JSON-строка;
    @param inSettings (@c dict or @cstring) JSON-словарь или JSON-строка;
    @param convertData (@c boolean) выполнять ли конвертацию inData;
    @param convertSettings (@c boolean) выполнять ли конвертацию convertSettings;
    
    @return @c JythonDTO
    """
    
    data = inData
    if data and convertData:
        data = jsonToXml(data)
        
    
    settings = inSettings
    if settings and convertSettings:
        settings = jsonToXml(settings)
        
        
    return JythonDTO(data, settings)



def procname(func):
    """Декоратор, возврщающий функцию получения имени функции."""
    def wrapper(instance, *args):
        """Если первый аргумент - объект функции, возвращает её полное имя
        (*qualified name*) + celesta. Иначе оставляет как есть.
        Второй - т.к. первый - self
        """
        
        value = args[0]
        if isfunction(value):
            # если функция декорирована, то имя формируется для __wrapped__
            if hasattr(value, '__wrapped__'):
                value = value.__wrapped__
            
#             '.'.join([value.__module__, value.__name__, 'celesta'])
            value = classQualifiedName(value) + '.celesta'
            
        newArgs = (value,) + args[1:]
        return func(instance, *newArgs)
    
    return wrapper




