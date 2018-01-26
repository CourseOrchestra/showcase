# coding: utf-8
'''
Created on 23.10.2014

@author: d.bozhenko
'''

from security._security_orm import customPermsCursor, customPermsTypesCursor, rolesCustomPermsCursor
from common._common_orm import numbersSeriesCursor, linesOfNumbersSeriesCursor

import datetime
#from java.text import SimpleDateFormat

def securityInit(context):
    u"""Функция устанавливает необходимые для работы гранулы значения таблиц:
    numbersSeries
    linesOfNumbersSeries
    customPermsTypes
    customPerms
    rolesCustomPerms
    """
    numbersSeries = numbersSeriesCursor(context)
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
    numbersSeries.id = 'subjects'
    numbersSeries.description = 'subjects'
    if not numbersSeries.tryInsert():
        numbersSeries.update()
    if not linesOfNumbersSeries.tryGet('subjects', 1):
#        sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        now = datetime.datetime.now()#fromtimestamp(time.time()).strftime("%Y-%m-%d %H:%M:%S")
        linesOfNumbersSeries.seriesId = 'subjects'
        linesOfNumbersSeries.numberOfLine = 1                
        linesOfNumbersSeries.startingDate = now#sdf.parse(now)
        linesOfNumbersSeries.startingNumber = 10
        linesOfNumbersSeries.endingNumber = 100000
        linesOfNumbersSeries.incrimentByNumber = 1
        linesOfNumbersSeries.isOpened = True
        linesOfNumbersSeries.prefix = 'subject-'
        linesOfNumbersSeries.postfix = '-'
        linesOfNumbersSeries.isFixedLength = False
        linesOfNumbersSeries.insert()
    # Добавили серии номеров для таблицы subjects
    customPermsTypes = customPermsTypesCursor(context)
    if not customPermsTypes.tryGet('navigator'):
        customPermsTypes.name='navigator'
        customPermsTypes.description=u'Пункты меню навигатора'
        customPermsTypes.insert()
    # Добавили (если еще нет) тип разрешений "Пункты меню навигатора" 
    customPerms = customPermsCursor(context)
    if not customPerms.tryGet('loginsSubjectsPoint'):
        customPerms.name='loginsSubjectsPoint'
        customPerms.description=u'Разрешение на отображение пункта меню Сотрудники и Пользователи'
        customPerms.type='navigator'
        customPerms.insert()
    if not customPerms.tryGet('rolesPoint'):
        customPerms.name='rolesPoint'
        customPerms.description=u'Разрешение на отображение пункта меню Роли'
        customPerms.type='navigator'
        customPerms.insert()
    if not customPerms.tryGet('permissionsPoint'):
        customPerms.name='permissionsPoint'
        customPerms.description=u'Разрешение на отображение пункта меню Разрешения'
        customPerms.type='navigator'
        customPerms.insert()        
    if not customPerms.tryGet('numbersSeriesPoint'):
        customPerms.name='numbersSeriesPoint'
        customPerms.description=u'Разрешение на отображение пункта меню Серии номеров'
        customPerms.type='navigator'
        customPerms.insert()
    # Добавили (если еще нет) разрешения на отображения пунктов меню гранул common и security 
    rolesCustomPerms = rolesCustomPermsCursor(context)
    if not rolesCustomPerms.tryGet('editor', 'loginsSubjectsPoint'):
        rolesCustomPerms.roleid='editor'
        rolesCustomPerms.permissionId='loginsSubjectsPoint'
        rolesCustomPerms.insert()
    if not rolesCustomPerms.tryGet('editor', 'rolesPoint'):
        rolesCustomPerms.roleid='editor'
        rolesCustomPerms.permissionId='rolesPoint'
        rolesCustomPerms.insert()
    if not rolesCustomPerms.tryGet('editor', 'permissionsPoint'):
        rolesCustomPerms.roleid='editor'
        rolesCustomPerms.permissionId='permissionsPoint'
        rolesCustomPerms.insert()
    if not rolesCustomPerms.tryGet('editor', 'numbersSeriesPoint'):
        rolesCustomPerms.roleid='editor'
        rolesCustomPerms.permissionId='numbersSeriesPoint'
        rolesCustomPerms.insert()
    #Привязываем добавленные разрешения к роли editor 
    # Уже неактуально, так как функция userHasPermission возвращает True в любом случае, если роль - editor  