# coding: UTF-8

'''@package common.api.utils.cursortools Модуль содержит функции-утилиты для 
автоматизации некоторых операций с курсорами Celesta.

Created on 22 июля 2015 г.

@author tugushev.r
'''

from datetime import datetime
from itertools import imap

CELESTA_DATE_FORMAT_JAVA = 'YYYYMMdd'
CELESTA_DATE_FORMAT_PYTHON = '%Y%m%d'


def isFieldsUnique(rec, idField, fieldNames):
    """Проверяет, уникальны ли поля из @a fieldNames в таблице, представленной
    записью в @a rec.
    
    @param rec (@c Cursor) курсор с конкретной записью, значения в которой 
    нужно проверить
    @param idField (@c string) имя поля, в котором лежит id. id должен быть @c int
    или @c string
    @param fieldNames (<em>list of string</em>) список полей, которые проверяются на 
    уникальность по условию "И"
    
    @return @c True если таких записей не найдено. Иначе - @c False
    """
    
    curClone = rec.__class__(rec.context)
    
    # если проверка происходит при попытке обновления заиси, то критерий
    # поиска дублмикатов не должен включать тот же id
    
    recId = getattr(rec, idField)
    if recId:
        strId = ''
        if isinstance(recId, (int, float)):
            strId = str(int(recId))
        else:
            strId = "'%s'" % recId
            
        curClone.setFilter(idField, "!" + strId)
     
    for field in fieldNames:
        curClone.setRange(field, getattr(rec, field))
        
    result = (curClone.count() == 0)    
    
    curClone.close()
    
    return result


def makeFilterSequence(inValuesList):
    """Возвращает строку для Celesta filter c условием ИЛИ.
    Тип элемента определяется по первому элементу списка.
    
    @param inValuesList (<em>list of int|float|string|datetime</em>) список значений 
    фильтра. Должен содержать хотя бы один элемент.
    @return @c string 
    """
    
    paramString = "'%s'"
    lst = inValuesList
    
    if isinstance(inValuesList[0], basestring):
#         Это условие добавлено, чтобы в случае строки не проверялись остальные
#         условия
        pass
    elif isinstance(inValuesList[0], int):
        paramString = '%i'
    elif isinstance(inValuesList[0], float):
        paramString = '%f'
    elif isinstance(inValuesList[0], datetime):
        lst = imap(lambda x: x.strftime(CELESTA_DATE_FORMAT_PYTHON), inValuesList)
    else:
        raise ValueError('Undefined datatype!')
    
    return '|'.join(imap(lambda x: paramString % x, lst))
    
    