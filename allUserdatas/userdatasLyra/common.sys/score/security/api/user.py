# coding: utf-8
'''
Created on 28 февр. 2017 г.

@author: m.prudyvus
'''

from __future__ import unicode_literals

import hashlib
import string

from security._security_orm import subjectsCursor, loginsCursor
from security.functions import Settings, id_generator


def loginCheck(context, login):
    """Проверяет уникальность логина.

    Параметры:
        - context (CallContext) - контекст Celesta
        - login (string) - логин

    Возвращает:
        (bool) True, если login уникален, иначе - False
    """

    logins = loginsCursor(context)
    exist = not logins.tryGet(login)
    logins.close()
    return exist


def getLoginsBySubject(context, subject_id):
    """Получает список логинов по subject id"""
    logins = loginsCursor(context)
    logins.setRange("subjectId", subject_id)
    return logins


def getSubjectByLogin(context, sid):

    subject = subjectsCursor(context)
    logins = loginsCursor(context)
    logins.get(sid)
    subject.get(logins.subjectId)
    logins.close()
    return subject


def getSubjectIdByEmployeeId(context, employee_id):
    """возвращает сид пользователя по идентификатору сотрудника"""
    subjects = subjectsCursor(context)
    subjects.setRange("employeeId", employee_id)
    if subjects.count() > 1:
        subjects.close()
        raise Exception(u"Employee got more than one subjects")
    else:
        subjects.tryFirst()
        sid = subjects.sid
        subjects.close()
        return sid


def generateSalt(length=32):
    """Генерируем соль указанной длины"""
    return id_generator(length, string.ascii_lowercase + string.digits)


def genHash(password, hashalgorithm):
    """Генерируем хеш по указанному алгоритму"""
    if hashalgorithm == "MD2":
        raise NotImplementedError("MD2 algorithm is not implemented in the security grain")
    hash_dict = {
        "MD2": "",
        "MD5": hashlib.md5,
        "SHA-1": hashlib.sha1,
        "SHA-224": hashlib.sha224,
        "SHA-256": hashlib.sha256,
        "SHA-384": hashlib.sha384,
        "SHA-512": hashlib.sha512
    }

    pass_hash = hash_dict[hashalgorithm]()
    # Записываем в него текущий пароль из карточки
    pass_hash.update(password)
    # Выполняем hash функцию, записываем результат в курсор.
    return pass_hash.hexdigest()


def blockUser(context, sid, block=True):
    subject = subjectsCursor(context)
    subject.get(sid)
    subject.isBlocked = block
    subject.update()
    subject.close()


def changePassword(context, sid, password=None, hashalgorithm=None, salt=None):
    """Функция для смены пароля пользователя"""
    settings = Settings()
    logins = loginsCursor(context)
    logins.get(sid)
    if not hashalgorithm:
        hashalgorithm = settings.getParam("hashalgorithm") or "SHA-256"
    if not password:
        password = id_generator(8)

    if not salt:
        salt = generateSalt()
    localsecuritysalt = settings.getParam("localsecuritysalt") or ""

    hash_ = genHash("".join([password, salt, localsecuritysalt]), hashalgorithm)
    logins.password = "#".join([hashalgorithm.lower().replace("-", ""), salt, hash_])
    logins.update()
    logins.close()
