# coding: utf-8
import importlib
import json

import os
from ru.curs.celesta import Celesta


def toHexForXml(s):
    '''Функция модифицирует спецсимволы в строке в формат пригодный для имен тегов xml'''
    lst = []
    for ch in s:
        numCh = ord(ch)
        if numCh not in xrange(48, 58) and\
                numCh not in xrange(65, 91) and\
                numCh not in xrange(97, 123) and \
                numCh not in xrange(1040, 1104):
            lst.append('_x%s_' % ('000%s' % hex(numCh)[2:])[-4:])
        else:
            lst.append(ch)

    return reduce(lambda x, y: x + y, lst)


def tableCursorImport(grainName, tableName):
    u'''Функция, импортирующая  класс курсора на таблицу'''

    # Импорт гранулы
    if grainName == "celesta":
        from ru.curs.celesta import syscursors as _grain_orm
    else:
        _grain_orm = __import__("%s._%s_orm" % (grainName, grainName),
                                globals(), locals(), "%sCursor" % tableName, -1)

    return getattr(_grain_orm, "%sCursor" % tableName)


def objectImport(path):
    u'''Импорт объекта по пути пакет.модуль.....функция.'''

    (module_name, fun_name) = (".".join(path.split(".")[:-1]), path.split(".")[-1])

    return getattr(importlib.import_module(module_name), fun_name)


def getGridWidth(session, delta=51):
    u"""Функция получает ширину грида, в зависимости от ширины датапанели."""
    if not isinstance(session, dict):
        return unicode(int(json.loads(session)["sessioncontext"]["currentDatapanelWidth"]) - delta) + "px"
    else:
        return unicode(int(session["sessioncontext"]["currentDatapanelWidth"]) - delta) + "px"


def getGridHeight(session, numberOfGrids=1, gridHeaderHeight=55, delta=59):
    u"""Функция получает высоту грида, в зависимости от высоты датапанели."""
    # raise Exception(session)
    if not isinstance(session, dict):
        return unicode(int((int(json.loads(session)["sessioncontext"]["currentDatapanelHeight"])
                            - gridHeaderHeight) / numberOfGrids) - delta)
    else:
        return unicode(int((int(session["sessioncontext"]["currentDatapanelHeight"])
                            - gridHeaderHeight) / numberOfGrids) - delta)


def getSettingsPath():
    u"""Функция возвращает путь к файлу с настройками гранул."""

    try:
        from ru.curs.showcase.runtime import AppInfoSingleton  # @UnresolvedImport
        settingsPath = os.path.join(AppInfoSingleton.getAppInfo().getUserdataRoot(), 'grainsSettings.xml')
    except ImportError:
        settingsPath = Celesta.getInstance().setupProperties.getProperty('grainssettings.path')
    return settingsPath


def getUserSettingsPath():
    settingsPath = Celesta.getInstance().setupProperties.getProperty('usergrainssettings.path')
    if not settingsPath:
        settingsPath = os.path.join(os.path.dirname(getSettingsPath()), "userGrainsSettings.xml")
    return settingsPath


def defaultGroup(navigator_generate_function):
    """
    Обрабатывает данные из навигатора
    превращает пустой root навигатора в отсутствующую группу
    :param navigator_generate_function: группа навигатора
    :return: возвращает группу навигатора, обработанную toJSONDict
    """
    _empty_group = {"group": None}

    def wrapped(*args, **kwargs):
        root_node = navigator_generate_function(*args, **kwargs)
        # rootNode - это root. Он есть всегда.
        # Но в нём может не быть самой группы
        if not root_node.getNumberOfChildren():
            return _empty_group
        # или группа может быть пустой
        if not root_node.getChildren()[0].getNumberOfChildren():
            return _empty_group
        return root_node.toJSONDict()
    return wrapped
