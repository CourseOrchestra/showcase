# coding: utf-8
'''
Created on 23.10.2014

'''

from security._security_orm import customPermsCursor, customPermsTypesCursor, rolesCustomPermsCursor

import datetime
#from java.text import SimpleDateFormat

def permInit(context):
    #raise Exception(context)
    customPermsTypes = customPermsTypesCursor(context)
    if not customPermsTypes.tryGet('forms'):
        customPermsTypes.name='forms'
        customPermsTypes.description=u'Формы'
        customPermsTypes.insert()
    customPerms = customPermsCursor(context)
    if not customPerms.tryGet('htmlHintsEdit'):
        customPerms.name='htmlHintsEdit'
        customPerms.description=u'Разрешение редактирование подсказок в формате HTML'
        customPerms.type='forms'
        customPerms.insert()
    rolesCustomPerms = rolesCustomPermsCursor(context)
    if not rolesCustomPerms.tryGet('editor', 'htmlHintsEdit'):
        rolesCustomPerms.roleid='editor'
        rolesCustomPerms.permissionId='htmlHintsEdit'
        rolesCustomPerms.insert()