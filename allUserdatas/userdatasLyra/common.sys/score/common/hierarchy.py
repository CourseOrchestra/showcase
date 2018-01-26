# coding: utf-8
from ru.curs.celesta import CelestaException


def generateSortValue(number, rankList=[3]):
    u'''Функция формирует сортировочное значение из номера иерархии Дьюи.
    @number - номер иерархии Дьюи типа 1.1.1
    @rankList - список отвечающий за количество разрядов на каждом уровне rankList[0] количество по умолчанию
    Данный параметр не обязателен по умолчанию равен [3]
    Пример: generateSortValue('1.2.3.4', [3,1,2,3])='102003004' '''
    if not(isinstance(number, (str, unicode)) and isinstance(rankList, list)):
        raise CelestaException(u'Неверный формат аргументов. аргументы должны иметь вид (строка,список)')
    lenRankList = len(rankList)
#     убираем пустые строки
    numberList = filter(bool, number.split('.'))

    sortVal = []
    for idx, val in enumerate(numberList):
        rank = rankList[idx + 1] if idx + 1 < lenRankList else rankList[0]
        sortVal.append(val.zfill(rank))
    return ''.join(sortVal)


def moveNodeInHierarchy(context, cursorInstance, numberField, position, isOneHierarchy=False):
    u'''Функция передвигает куст в иерархии на указанную позицию на том же уровне
    cursorInstance - курсор, соджержащий корень передвигаемого узла.
    При этом уникальность нумерации не отслеживается.'''
    currentNumber = getattr(cursorInstance, numberField)
    numberList = currentNumber.split('.')
    level = len(numberList)
    # Создаем клон основного курсора и копируем в него фильтры
    cursorInstanceClone = cursorInstance.__class__(context)
    if not isOneHierarchy:
        cursorInstanceClone.copyFiltersFrom(cursorInstance)
    cursorInstanceClone.setFilter(numberField, '''('%s.'%%)''' % currentNumber)
    cursorInstanceClone.limit(0, 0)
    # Меняем номер всех узлов куста, кроме корня
    for cursorInstanceClone in cursorInstanceClone.iterate():
        numberListChild = getattr(cursorInstanceClone, numberField).split('.')
        numberListChild[level - 1] = unicode(position)
        cursorInstanceClone.__setattr__(numberField, '.'.join(numberListChild))
        cursorInstanceClone.update()
    # Меняем номер корня
    numberList[-1] = unicode(position)
    cursorInstance.__setattr__(numberField, '.'.join(numberList))
    cursorInstance.update()
    cursorInstanceClone.close()


def deleteNodeFromHierarchy(context, cursorInstance, numberField, sortField, isOneHierarchy=False):
    u'''Функция удаляет куст из иерархии, сдвигая все нижестоящие кусты на одну позицию вверх'''
    currentNumber = getattr(cursorInstance, numberField)
    numberList = currentNumber.split('.')
    # Создаем клон основного курсора и копируем в него фильтры
    cursorInstanceClone = cursorInstance.__class__(context)
    if not isOneHierarchy:
        cursorInstanceClone.copyFiltersFrom(cursorInstance)
    cursorInstanceClone.limit(0, 0)
    cursorInstanceClone.orderBy(sortField)
    # Удаляем все узлы куста кроме корня
    cursorInstanceClone.setFilter(numberField, '''('%s.'%%)''' % currentNumber)
    for cursorInstanceClone in cursorInstanceClone.iterate():
        cursorInstanceClone.delete()
    # Удаляем корень куста
    currentSort = getattr(cursorInstance, sortField)
    cursorInstance.delete()
    # Сдвигаем все нижестоящие кусты на одну позицию вверх
    if '.' in currentNumber:
        sqlPattern = '''('%s.'%%)&!('%s.'%%'.'%%)''' % ('.'.join(numberList[:-1]), '.'.join(numberList[:-1]))
    else:
        sqlPattern = '''(!%'.'%)'''

    cursorInstanceClone.setFilter(numberField, sqlPattern)
    cursorInstanceClone.setFilter(sortField, '''>'%s' ''' % currentSort)
    cursorInstanceClone.orderBy(sortField)
    for cursorInstanceClone in cursorInstanceClone.iterate():
        numberListChild = getattr(cursorInstanceClone, numberField).split('.')
        moveNodeInHierarchy(context, cursorInstanceClone, numberField, int(numberListChild[-1]) - 1)
    cursorInstanceClone.close()


def changeNodePositionInLevelOfHierarchy(context, cursorInstance, numberField,
                                         sortField, shift, isOneHierarchy=False):
    u'''Функция перемещает куст на @shift позиций, сдвигая при этом необходимое количество соседних кустов'''
    currentNumber = getattr(cursorInstance, numberField)
    numberList = currentNumber.split('.')
    # Создаем клон основного курсора и копируем в него фильтры
    cursorInstanceClone = cursorInstance.__class__(context)
    if not isOneHierarchy:
        cursorInstanceClone.copyFiltersFrom(cursorInstance)
    cursorInstanceClone.limit(0, 0)
    currentSort = getattr(cursorInstance, sortField)
    # Перемещаем указанный куст на нулевую позицию
    moveNodeInHierarchy(context, cursorInstance, numberField, 0)
    # Перемещаем необходимое количество соседних кустов
    if '.' in currentNumber:
        sqlPattern = '''('%s.'%%)&(!'%s.'%%'.'%%)''' % ('.'.join(numberList[:-1]), '.'.join(numberList[:-1]))
    else:
        sqlPattern = '''(!%'.'%)'''

    cursorInstanceClone.setFilter(numberField, sqlPattern)
    if shift >= 0:
        sign = 1
        cursorInstanceClone.setFilter(sortField, '''>'%s' ''' % currentSort)
        cursorInstanceClone.orderBy(sortField)
    else:
        sign = -1
        cursorInstanceClone.setFilter(sortField, '''<'%s' ''' % currentSort)
        cursorInstanceClone.orderBy('%s desc' % sortField)

    cursorInstanceClone.limit(0, abs(shift))
    countOfDownNodes = min(cursorInstanceClone.count(), abs(shift))
    for cursorInstanceClone in cursorInstanceClone.iterate():
        numberListChild = getattr(cursorInstanceClone, numberField).split('.')
        moveNodeInHierarchy(context, cursorInstanceClone, numberField, int(numberListChild[-1]) - sign)
    # Перемещаем указанный куст на свободную после перемещений позицию
    moveNodeInHierarchy(context, cursorInstance, numberField, int(numberList[-1]) + countOfDownNodes * sign)
    cursorInstanceClone.close()


def leftShiftNodeInHierarchy(context, cursorInstance, numberField, sortField):
    u'''Функция перемещает куст на один уровень выше'''
    currentNumber = getattr(cursorInstance, numberField)
    numberList = currentNumber.split('.')
    shiftNodeToOtherLevelInHierarchy(context, cursorInstance, numberField, sortField, '.'.join(numberList[:-2]))


def shiftNodeToOtherLevelInHierarchy(context, cursorInstance, numberField,
                                     sortField, parentNumber=None, isOneHierarchy=False):
    u'''Функция перемещает куст на уровень @parentNumber'''
    currentNumber = getattr(cursorInstance, numberField)
    numberList = currentNumber.split('.')
    parentNumberList = []
    # Создаем клон основного курсора и копируем в него фильтры
    cursorInstanceClone = cursorInstance.__class__(context)
    if not isOneHierarchy:
        cursorInstanceClone.copyFiltersFrom(cursorInstance)
    cursorInstanceClone.limit(0, 0)
    # Находим правильное место вставки
    if parentNumber:
        parentNumberList = parentNumber.split('.')
        cursorInstanceClone.setFilter(numberField, '''('%s.'%%)&(!'%s.'%%'.'%%)''' % (parentNumber, parentNumber))
    else:
        cursorInstanceClone.setFilter(numberField, '''(!%'.'%)''')
    level = len(numberList)
    if numberList == parentNumberList[:level]:
        raise CelestaException(u'''Невозможно сделать узел потомком себя и своих потомков''')
    elif numberList[:-1] == parentNumberList:
        return
    parentNumberList.append(unicode(cursorInstanceClone.count() + 1))
    # Перемещаем все узлы текущего куста кроме корня
    cursorInstanceClone.setFilter(numberField, '''('%s.'%%)''' % currentNumber)
    for cursorInstanceClone in cursorInstanceClone.iterate():
        numberListChild = getattr(cursorInstanceClone, numberField).split('.')
        cursorInstanceClone.__setattr__(numberField, '.'.join(parentNumberList + numberListChild[level:]))
        cursorInstanceClone.update()
    # Перемещаем корень текущего куста на правильную позицию
    currentSort = getattr(cursorInstance, sortField)
    cursorInstance.__setattr__(numberField, '.'.join(parentNumberList))
    cursorInstance.update()
    # Перемещаем все нижестоящие кусты относительно текущего на одну позицию вверх
    if '.' in currentNumber:
        sqlPattern = '''('%s.'%%)&(!'%s.'%%'.'%%)''' % ('.'.join(numberList[:-1]), '.'.join(numberList[:-1]))
    else:
        sqlPattern = '''(!%'.'%)'''
    cursorInstanceClone.setFilter(numberField, sqlPattern)
    cursorInstanceClone.setFilter(sortField, '''>'%s' ''' % currentSort)
    cursorInstanceClone.orderBy(sortField)
    for cursorInstanceClone in cursorInstanceClone.iterate():
        numberListChild = getattr(cursorInstanceClone, numberField).split('.')
        moveNodeInHierarchy(context, cursorInstanceClone, numberField, int(numberListChild[-1]) - 1)
    cursorInstanceClone.close()


def hasChildren(context, cursorInstance, numberField, isOneHierarchy=False):
    u'''Функция определяет наличие у элемента детей.'''
    currentNumber = getattr(cursorInstance, numberField)
    # Создаем клон основного курсора и копируем в него фильтры
    cursorInstanceClone = cursorInstance.__class__(context)
    if not isOneHierarchy:
        cursorInstanceClone.copyFiltersFrom(cursorInstance)
    cursorInstanceClone.limit(0, 0)
    cursorInstanceClone.setFilter(numberField, "'%s.'%%" % currentNumber)
    result = True if cursorInstanceClone.count() > 0 else False
    cursorInstanceClone.close()
    return result


def getNewItemInLevelInHierarchy(context, cursorInstance, numberField, isOneHierarchy=False):
    u'''Функция возвращает номер для нового элемента в указанном уровне иерархии'''
    currentNumber = getattr(cursorInstance, numberField)
    # Создаем клон основного курсора и копируем в него фильтры
    cursorInstanceClone = cursorInstance.__class__(context)
    if not isOneHierarchy:
        cursorInstanceClone.copyFiltersFrom(cursorInstance)
    cursorInstanceClone.limit(0, 0)
    # Отдельное условие, если вставляем на нулевой уровень иерархии
    if currentNumber is not None:
        cursorInstanceClone.setFilter(numberField, "('%s.'%%)&(!'%s.'%%'.'%%)" % (currentNumber, currentNumber))
        childCount = cursorInstanceClone.count()
        cursorInstanceClone.close()
        return '%s.%d' % (currentNumber, childCount + 1)
    else:
        cursorInstanceClone.setFilter(numberField, "!(%'.'%)")
        childCount = cursorInstanceClone.count()
        cursorInstanceClone.close()
        return unicode(childCount + 1)
