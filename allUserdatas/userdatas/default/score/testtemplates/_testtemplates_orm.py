# coding=UTF-8
# Source grain parameters: version=1.0, len=169, crc32=6B34CF18; compiler=13.
"""
THIS MODULE IS BEING CREATED AND UPDATED AUTOMATICALLY.
DO NOT MODIFY IT AS YOUR CHANGES WILL BE LOST.
"""
import ru.curs.celesta.dbutils.Cursor as Cursor
import ru.curs.celesta.dbutils.ViewCursor as ViewCursor
import ru.curs.celesta.dbutils.ReadOnlyTableCursor as ReadOnlyTableCursor
import ru.curs.celesta.dbutils.MaterializedViewCursor as MaterializedViewCursor
import ru.curs.celesta.dbutils.ParameterizedViewCursor as ParameterizedViewCursor
from java.lang import Object
from jarray import array
from java.util import Calendar, GregorianCalendar, HashSet, HashMap
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

class myTableCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context, fields = []):
        if fields:
            Cursor.__init__(self, context, HashSet(fields))
        else:
            Cursor.__init__(self, context)
        self.id = None
        self.name = None
        self.context = context
    def _grainName(self):
        return 'testtemplates'
    def _tableName(self):
        return 'myTable'
    def _parseResult(self, rs):
        if self.inRec('id'):
            self.id = rs.getString('id')
            if rs.wasNull():
                self.id = None
        if self.inRec('name'):
            self.name = rs.getString('name')
            if rs.wasNull():
                self.name = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.name = None
    def _currentKeyValues(self):
        return array([None if self.id == None else unicode(self.id)], Object)
    def _currentValues(self):
        return array([None if self.id == None else unicode(self.id), None if self.name == None else unicode(self.name)], Object)
    def _setAutoIncrement(self, val):
        pass
    def _preDelete(self):
        for f in myTableCursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in myTableCursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in myTableCursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in myTableCursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in myTableCursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in myTableCursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context, fields=None):
        result = myTableCursor(context, fields)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.id = c.id
        self.name = c.name
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

