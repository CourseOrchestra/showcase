# coding: utf-8
'''
Created on 28 февр. 2017 г.

@author: m.prudyvus
'''

from __future__ import unicode_literals

from ru.curs.celesta.syscursors import UserRolesCursor

from security._security_orm import customPermsCursor, rolesCustomPermsCursor


def userHasPermission(context, sid, permission):
    """
        Функция возвращает False, если разрешения не существует или
        у данного пользователя нет такого разрешения.
        В случае, если у данного пользователя разрешение есть,
        возвращает True
        sid - sid пользователя
        permission - разрешение из таблицы customPermissions
    """
    userRoles = UserRolesCursor(context)
    if userRoles.tryGet(sid, "editor"):
        # Для роли editor есть все(!) разрешения
        return True
    userRoles.clear()
    # выбираем разрешения данного пользователя.
    userRoles.setRange("userid", sid)
    permissions = customPermsCursor(context)
    if not permissions.tryGet(permission):
        # Разрешения не нашли, возвращаем False
        return False
    rolePermissions = rolesCustomPermsCursor(context)
    if userRoles.tryFindSet():
        while True:
            rolePermissions.setRange("roleid", userRoles.roleid)
            rolePermissions.setRange("permissionId", permission)
            if rolePermissions.tryFirst():
                return True
            if not userRoles.nextInSet():
                break
    return False
