# coding: utf-8
"""
Created on 02.03.2016
HTML подсказки
@author: a.rudenko
"""
import StringIO
import json
from xml.sax import make_parser, ContentHandler, SAXParseException

from java.io import ByteArrayInputStream, ByteArrayOutputStream
from java.lang import String

from common._common_orm import htmlHintsCursor, htmlHintsUsersCursor
from common.api.context.sessioncontext import SessionContext
from common.api.datapanels.datapanel import XForm
from common.api.utils.tools import createJythonDTO
from security.functions import userHasPermission

try:
    from org.apache.commons.lang3.StringEscapeUtils import unescapeHtml4
except ImportError:
    pass

try:
    from ru.curs.showcase.core.jython import JythonDTO
except ImportError:
    from ru.curs.celesta.showcase import JythonDTO


def htmlHintElement(elementId, is_object=False):
    """возвращает элемент датапанели"""

    element = (XForm(elementId, "common/htmlHints/htmlHint.xml", cardData)
               .setSaveProc(cardSave)
               )

    if not is_object:
        element = element.toJSONDict()

    return element


def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    """Карточка HTML подсказки"""
    sid = SessionContext(session).sid
    htmlHints = htmlHintsCursor(context)
    htmlHintsUsers = htmlHintsUsersCursor(context)
    height = "300px"
    hint_exist = False
    if htmlHints.tryGet(elementId):
        hint_exist = True
        htmlText = htmlHints.htmlText or ""

        showOnLoad = htmlHints.showOnLoad
        fullScreen = htmlHints.fullScreen
        if showOnLoad == 1:
            showOnLoad = 'true'
        else:
            showOnLoad = 'false'
        if fullScreen == 1:
            fullScreen = 'true'
            height = "100%"
        else:
            fullScreen = 'false'
    else:
        htmlText = u""
        fullScreen = 'false'
        showOnLoad = 'true'

    if htmlHintsUsers.tryGet(elementId, sid):
        showHideHint = htmlHintsUsers.showOnLoad
        if showHideHint == 1:
            showHideHint = 'true'
        else:
            showHideHint = 'false'
    else:
        showHideHint = showOnLoad

    xformsdata = {
        "schema": {
            "@xmlns": "",
            "hideHint": int(not (userHasPermission(context, sid, 'htmlHintsEdit') or hint_exist)),
            "elementId": elementId,
            "htmlText": unescapeHtml4(htmlText),
            "showHideHint": showHideHint,
            "showOnLoad": showOnLoad,
            "showHideEdit": 0,
            "userPerm": int(userHasPermission(context, sid, 'htmlHintsEdit')),
            "fullScreen": fullScreen,
            "height": height
        }
    }

    xformssettings = {
        "properties": {
        }
    }

    return createJythonDTO(xformsdata, xformssettings)


def cardSave(context, main=None, add=None, filterinfo=None, session=None,
             elementId=None, xformsdata=None):
    """Сохранение HTML подсказки"""
    htmlHints = htmlHintsCursor(context)

    htmlText = json.loads(xformsdata)["schema"]["htmlText"]
    showOnLoad = json.loads(xformsdata)["schema"]["showOnLoad"]
    fullScreen = json.loads(xformsdata)["schema"]["fullScreen"]

    if htmlHints.tryGet(elementId):
        htmlHints.htmlText = htmlText
        htmlHints.showOnLoad = int(showOnLoad == 'true')
        htmlHints.fullScreen = int(fullScreen == 'true')
        htmlHints.update()
    else:
        htmlHints.elementId = elementId
        htmlHints.htmlText = htmlText
        htmlHints.showOnLoad = int(showOnLoad == 'true')
        htmlHints.fullScreen = int(fullScreen == 'true')
        htmlHints.insert()


class ParserSAXHandlerHTMLEdit(ContentHandler):
    def __init__(self):
        self.firstDiv = False


def htmlEdit(context, main, add, filterinfo, session, elementId):
    """Вызывается при передаче данных из xform-ы в tinymce"""
    jsonData = json.loads(filterinfo)
    data = jsonData['schema']['filter']['htmlText']
    try:
        # tinymce принимает только валидный xml
        # В textarea в xform-е можно задать plain text
        # Если вываливается ошибка парсинга, мы оборачиваем полученный текст в div-ы
        # Хотя на самом деле здесь возможно больше ошибок в парсинге
        parser = make_parser()
        handler = ParserSAXHandlerHTMLEdit()
        parser.setContentHandler(handler)
        parser.parse(StringIO.StringIO(cleanData(data).encode('utf8')))
    except SAXParseException:
        data = "<div>%s</div>" % data

    settings = '''
    <properties>
    </properties>
    '''

    return JythonDTO(cleanData(data), settings)


def showOnLoadSave(context, main=None, add=None, filterinfo=None, session=None, xformsdata=None):
    """функция сабмишна для проверки СНИЛС."""
    showHideHint = json.loads(xformsdata)["schema"]["showHideHint"]
    elementId = json.loads(xformsdata)["schema"]["elementId"]
    sid = SessionContext(session).sid
    htmlHintsUsers = htmlHintsUsersCursor(context)

    if showHideHint == 'true':
        showHideHint = 1
    else:
        showHideHint = 0
    if htmlHintsUsers.tryGet(elementId, sid):
        htmlHintsUsers.showOnLoad = showHideHint
        htmlHintsUsers.update()
    else:
        htmlHintsUsers.elementId = elementId
        htmlHintsUsers.sid = sid
        htmlHintsUsers.showOnLoad = showHideHint
        htmlHintsUsers.insert()
    return xformsdata


def cleanData(data):
    from org.w3c.tidy import Tidy
    tidy = Tidy()
    tidy.setXHTML(True)
    tidy.setInputEncoding("UTF-8")
    tidy.setOutputEncoding("UTF-8")

    tidy.setMakeClean(False)
    tidy.setDropEmptyParas(False)
    tidy.setPrintBodyOnly(True)
    tidy.setQuoteAmpersand(True)
    tidy.setTrimEmptyElements(False)

    inputStream = ByteArrayInputStream(String(data).getBytes("UTF-8"))
    outputStream = ByteArrayOutputStream()

    tidy.parseDOM(inputStream, outputStream)

    return outputStream.toString("UTF-8")
