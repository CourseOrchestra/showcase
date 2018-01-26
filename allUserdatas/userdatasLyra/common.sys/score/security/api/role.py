# coding: utf-8
'''
Created on 28 февр. 2017 г.

@author: m.prudyvus
'''

from __future__ import unicode_literals
from ru.curs.celesta.syscursors import UserRolesCursor


def getUserRoles(context, sid):
    """Возвращает список ролей пользователя"""
    userRoles = UserRolesCursor(context)
    userRoles.setRange("userid", sid)
    if userRoles.tryFindSet():
        while True:
            yield userRoles.roleid
            if not userRoles.nextInSet():
                break

    userRoles.close()


def getRoleUsers(context, role_id):
    userRoles = UserRolesCursor(context)
    userRoles.setRange("roleid", role_id)
    if userRoles.tryFindSet():
        while True:
            yield userRoles.userid
            if not userRoles.nextInSet():
                break

    userRoles.close()


def addUserRole(context, sid, role_list):
    """Добавляет пользователю роли
    @param role - список: """
    userRoles = UserRolesCursor(context)
    for role in role_list:
        userRoles.userid = sid
        userRoles.roleid = role
        userRoles.tryInsert()

    userRoles.close()


def removeUserRole(context, sid, role_list):
    """Удаляет пользователю роли
    @param role - список: """
    userRoles = UserRolesCursor(context)
    userRoles.setRange("userid", sid)
    userRoles.setFilter("roleid", "|".join("'%s'" % role for role in role_list))
    userRoles.deleteAll()  # возможно нужна итерация, чтобы могли дергаться триггеры?
    userRoles.close()
