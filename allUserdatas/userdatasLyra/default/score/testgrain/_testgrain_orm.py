# coding=UTF-8
# Source grain parameters: version=1.0, len=3452, crc32=905B2AFC; compiler=9.
"""
THIS MODULE IS BEING CREATED AUTOMATICALLY EVERY TIME CELESTA STARTS.
DO NOT MODIFY IT AS YOUR CHANGES WILL BE LOST.
"""
import ru.curs.celesta.dbutils.Cursor as Cursor
import ru.curs.celesta.dbutils.ViewCursor as ViewCursor
import ru.curs.celesta.dbutils.ReadOnlyTableCursor as ReadOnlyTableCursor
from java.lang import Object
from jarray import array
from java.util import Calendar, GregorianCalendar
from java.sql import Timestamp
import datetime

def _to_timestamp(d):
    if isinstance(d, datetime.datetime):
        calendar = GregorianCalendar()
        calendar.set(d.year, d.month - 1, d.day, d.hour, d.minute, d.second)
        ts = Timestamp(calendar.getTimeInMillis())
        ts.setNanos(d.microsecond * 1000)
        return ts
    else:
        return d

class testCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.code = None
        self.attrVarchar = None
        self.attrInt = None
        self.f1 = None
        self.f2 = None
        self.f4 = None
        self.f5 = None
        self.f6 = None
        self.f7 = None
        self.f8 = None
        self.f9 = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'test'
    def _parseResult(self, rs):
        self.code = rs.getInt('code')
        if rs.wasNull():
            self.code = None
        self.attrVarchar = rs.getString('attrVarchar')
        if rs.wasNull():
            self.attrVarchar = None
        self.attrInt = rs.getInt('attrInt')
        if rs.wasNull():
            self.attrInt = None
        self.f1 = rs.getBoolean('f1')
        if rs.wasNull():
            self.f1 = None
        self.f2 = rs.getBoolean('f2')
        if rs.wasNull():
            self.f2 = None
        self.f4 = rs.getDouble('f4')
        if rs.wasNull():
            self.f4 = None
        self.f5 = rs.getDouble('f5')
        if rs.wasNull():
            self.f5 = None
        self.f6 = rs.getString('f6')
        if rs.wasNull():
            self.f6 = None
        self.f7 = rs.getString('f7')
        if rs.wasNull():
            self.f7 = None
        self.f8 = rs.getTimestamp('f8')
        if rs.wasNull():
            self.f8 = None
        self.f9 = rs.getTimestamp('f9')
        if rs.wasNull():
            self.f9 = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.attrVarchar = None
        self.attrInt = None
        self.f1 = None
        self.f2 = None
        self.f4 = None
        self.f5 = None
        self.f6 = None
        self.f7 = None
        self.f8 = None
        self.f9 = None
    def _currentKeyValues(self):
        return array([None if self.code == None else int(self.code)], Object)
    def _currentValues(self):
        return array([None if self.code == None else int(self.code), None if self.attrVarchar == None else unicode(self.attrVarchar), None if self.attrInt == None else int(self.attrInt), None if self.f1 == None else bool(self.f1), None if self.f2 == None else bool(self.f2), None if self.f4 == None else float(self.f4), None if self.f5 == None else float(self.f5), None if self.f6 == None else unicode(self.f6), None if self.f7 == None else unicode(self.f7), _to_timestamp(self.f8), _to_timestamp(self.f9)], Object)
    def _setAutoIncrement(self, val):
        self.code = val
    def _preDelete(self):
        for f in testCursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in testCursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in testCursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in testCursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in testCursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in testCursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = testCursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.code = c.code
        self.attrVarchar = c.attrVarchar
        self.attrInt = c.attrInt
        self.f1 = c.f1
        self.f2 = c.f2
        self.f4 = c.f4
        self.f5 = c.f5
        self.f6 = c.f6
        self.f7 = c.f7
        self.f8 = c.f8
        self.f9 = c.f9
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

class streetCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.name = None
        self.rnum = None
        self.code = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'street'
    def _parseResult(self, rs):
        self.name = rs.getString('name')
        if rs.wasNull():
            self.name = None
        self.rnum = rs.getInt('rnum')
        if rs.wasNull():
            self.rnum = None
        self.code = rs.getString('code')
        if rs.wasNull():
            self.code = None
        self.socr = rs.getString('socr')
        if rs.wasNull():
            self.socr = None
        self.gninmb = rs.getString('gninmb')
        if rs.wasNull():
            self.gninmb = None
        self.uno = rs.getString('uno')
        if rs.wasNull():
            self.uno = None
        self.ocatd = rs.getString('ocatd')
        if rs.wasNull():
            self.ocatd = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.name = None
        self.rnum = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
    def _currentKeyValues(self):
        return array([None if self.code == None else unicode(self.code)], Object)
    def _currentValues(self):
        return array([None if self.name == None else unicode(self.name), None if self.rnum == None else int(self.rnum), None if self.code == None else unicode(self.code), None if self.socr == None else unicode(self.socr), None if self.gninmb == None else unicode(self.gninmb), None if self.uno == None else unicode(self.uno), None if self.ocatd == None else unicode(self.ocatd)], Object)
    def _setAutoIncrement(self, val):
        pass
    def _preDelete(self):
        for f in streetCursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in streetCursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in streetCursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in streetCursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in streetCursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in streetCursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = streetCursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.name = c.name
        self.rnum = c.rnum
        self.code = c.code
        self.socr = c.socr
        self.gninmb = c.gninmb
        self.uno = c.uno
        self.ocatd = c.ocatd
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

class street4Cursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.name = None
        self.rnum = None
        self.code = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'street4'
    def _parseResult(self, rs):
        self.name = rs.getString('name')
        if rs.wasNull():
            self.name = None
        self.rnum = rs.getInt('rnum')
        if rs.wasNull():
            self.rnum = None
        self.code = rs.getString('code')
        if rs.wasNull():
            self.code = None
        self.socr = rs.getString('socr')
        if rs.wasNull():
            self.socr = None
        self.gninmb = rs.getString('gninmb')
        if rs.wasNull():
            self.gninmb = None
        self.uno = rs.getString('uno')
        if rs.wasNull():
            self.uno = None
        self.ocatd = rs.getString('ocatd')
        if rs.wasNull():
            self.ocatd = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.name = None
        self.rnum = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
    def _currentKeyValues(self):
        return array([None if self.code == None else unicode(self.code)], Object)
    def _currentValues(self):
        return array([None if self.name == None else unicode(self.name), None if self.rnum == None else int(self.rnum), None if self.code == None else unicode(self.code), None if self.socr == None else unicode(self.socr), None if self.gninmb == None else unicode(self.gninmb), None if self.uno == None else unicode(self.uno), None if self.ocatd == None else unicode(self.ocatd)], Object)
    def _setAutoIncrement(self, val):
        pass
    def _preDelete(self):
        for f in street4Cursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in street4Cursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in street4Cursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in street4Cursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in street4Cursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in street4Cursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = street4Cursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.name = c.name
        self.rnum = c.rnum
        self.code = c.code
        self.socr = c.socr
        self.gninmb = c.gninmb
        self.uno = c.uno
        self.ocatd = c.ocatd
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

class street5Cursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.name = None
        self.rnum = None
        self.code = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'street5'
    def _parseResult(self, rs):
        self.name = rs.getString('name')
        if rs.wasNull():
            self.name = None
        self.rnum = rs.getInt('rnum')
        if rs.wasNull():
            self.rnum = None
        self.code = rs.getString('code')
        if rs.wasNull():
            self.code = None
        self.socr = rs.getString('socr')
        if rs.wasNull():
            self.socr = None
        self.gninmb = rs.getString('gninmb')
        if rs.wasNull():
            self.gninmb = None
        self.uno = rs.getString('uno')
        if rs.wasNull():
            self.uno = None
        self.ocatd = rs.getString('ocatd')
        if rs.wasNull():
            self.ocatd = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.name = None
        self.rnum = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
    def _currentKeyValues(self):
        return array([None if self.code == None else unicode(self.code)], Object)
    def _currentValues(self):
        return array([None if self.name == None else unicode(self.name), None if self.rnum == None else int(self.rnum), None if self.code == None else unicode(self.code), None if self.socr == None else unicode(self.socr), None if self.gninmb == None else unicode(self.gninmb), None if self.uno == None else unicode(self.uno), None if self.ocatd == None else unicode(self.ocatd)], Object)
    def _setAutoIncrement(self, val):
        pass
    def _preDelete(self):
        for f in street5Cursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in street5Cursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in street5Cursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in street5Cursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in street5Cursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in street5Cursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = street5Cursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.name = c.name
        self.rnum = c.rnum
        self.code = c.code
        self.socr = c.socr
        self.gninmb = c.gninmb
        self.uno = c.uno
        self.ocatd = c.ocatd
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

class street6Cursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.name = None
        self.rnum = None
        self.code = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'street6'
    def _parseResult(self, rs):
        self.name = rs.getString('name')
        if rs.wasNull():
            self.name = None
        self.rnum = rs.getInt('rnum')
        if rs.wasNull():
            self.rnum = None
        self.code = rs.getString('code')
        if rs.wasNull():
            self.code = None
        self.socr = rs.getString('socr')
        if rs.wasNull():
            self.socr = None
        self.gninmb = rs.getString('gninmb')
        if rs.wasNull():
            self.gninmb = None
        self.uno = rs.getString('uno')
        if rs.wasNull():
            self.uno = None
        self.ocatd = rs.getString('ocatd')
        if rs.wasNull():
            self.ocatd = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.name = None
        self.rnum = None
        self.socr = None
        self.gninmb = None
        self.uno = None
        self.ocatd = None
    def _currentKeyValues(self):
        return array([None if self.code == None else unicode(self.code)], Object)
    def _currentValues(self):
        return array([None if self.name == None else unicode(self.name), None if self.rnum == None else int(self.rnum), None if self.code == None else unicode(self.code), None if self.socr == None else unicode(self.socr), None if self.gninmb == None else unicode(self.gninmb), None if self.uno == None else unicode(self.uno), None if self.ocatd == None else unicode(self.ocatd)], Object)
    def _setAutoIncrement(self, val):
        pass
    def _preDelete(self):
        for f in street6Cursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in street6Cursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in street6Cursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in street6Cursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in street6Cursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in street6Cursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = street6Cursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.name = c.name
        self.rnum = c.rnum
        self.code = c.code
        self.socr = c.socr
        self.gninmb = c.gninmb
        self.uno = c.uno
        self.ocatd = c.ocatd
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

class test2Cursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.code = None
        self.name = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'test2'
    def _parseResult(self, rs):
        self.code = rs.getInt('code')
        if rs.wasNull():
            self.code = None
        self.name = rs.getString('name')
        if rs.wasNull():
            self.name = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.name = None
    def _currentKeyValues(self):
        return array([None if self.code == None else int(self.code)], Object)
    def _currentValues(self):
        return array([None if self.code == None else int(self.code), None if self.name == None else unicode(self.name)], Object)
    def _setAutoIncrement(self, val):
        self.code = val
    def _preDelete(self):
        for f in test2Cursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in test2Cursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in test2Cursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in test2Cursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in test2Cursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in test2Cursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = test2Cursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.code = c.code
        self.name = c.name
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

class websitesCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.code = None
        self.Name = None
        self.Picture = None
        self.File1 = None
        self.Logo = None
        self.File2 = None
        self.Url = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'websites'
    def _parseResult(self, rs):
        self.code = rs.getInt('code')
        if rs.wasNull():
            self.code = None
        self.Name = rs.getString('Name')
        if rs.wasNull():
            self.Name = None
        self.Picture = rs.getString('Picture')
        if rs.wasNull():
            self.Picture = None
        self.File1 = rs.getString('File1')
        if rs.wasNull():
            self.File1 = None
        self.Logo = rs.getString('Logo')
        if rs.wasNull():
            self.Logo = None
        self.File2 = rs.getString('File2')
        if rs.wasNull():
            self.File2 = None
        self.Url = rs.getString('Url')
        if rs.wasNull():
            self.Url = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.code = None
        self.Name = None
        self.Picture = None
        self.File1 = None
        self.Logo = None
        self.File2 = None
        self.Url = None
    def _currentKeyValues(self):
        return array([None if self.code == None else int(self.code)], Object)
    def _currentValues(self):
        return array([None if self.code == None else int(self.code), None if self.Name == None else unicode(self.Name), None if self.Picture == None else unicode(self.Picture), None if self.File1 == None else unicode(self.File1), None if self.Logo == None else unicode(self.Logo), None if self.File2 == None else unicode(self.File2), None if self.Url == None else unicode(self.Url)], Object)
    def _setAutoIncrement(self, val):
        self.code = val
    def _preDelete(self):
        for f in websitesCursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in websitesCursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in websitesCursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in websitesCursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in websitesCursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in websitesCursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = websitesCursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.code = c.code
        self.Name = c.Name
        self.Picture = c.Picture
        self.File1 = c.File1
        self.Logo = c.Logo
        self.File2 = c.File2
        self.Url = c.Url
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

