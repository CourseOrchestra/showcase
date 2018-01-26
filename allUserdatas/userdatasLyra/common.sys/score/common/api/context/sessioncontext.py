# coding: UTF-8

"""@package common.api.context.sessioncotext Модуль содержит описание классов
для работы с session context в Showcase

Created on 09 марта 2015 г.

@author tugushev.rr
"""

import json
from common.sysfunctions import getGridWidth, getGridHeight


class GridContext:
    """Описывает структуру контекста грида."""

    class PageInfo:
        """Описывает структуру информации о странице
        @todo Уточнить описание
        """

        def __init__(self, inNumber=-1, inSize=0):
            self.number = inNumber
            self.size = inSize

    class LiveInfo:
        """Описывает количественные характеристики грида
        @todo Уточнить описание
        """

        def __init__(self, totalCount, pageNumber, offset, limit):
            self.totalCount = totalCount
            self.pageNumber = pageNumber
            self.offset = offset
            self.limit = limit

    def __init__(self, gridContextJson):
        """
        @param gridContextJson (@c dict) JSON-словарь c содержимым тэга 
        *gridContext*
        """

        # (@c string) ИД выбранного столбца
        self.currentColumnId = gridContextJson.get('currentColumnId', None)

        pInfo = gridContextJson.get('pageInfo', None)
        pNum, pSize = -1, 0

        if pInfo is not None:
            pNum = pInfo['@number']
            pSize = pInfo['@size']

        # (@c common.api.context.sessioncontext.GridContext.PageInfo) информация
        # о странице
        # @todo Уточнить описание
        self.pageInfo = GridContext.PageInfo(pNum, pSize)

        lInfo = gridContextJson.get('liveInfo', None)

        if lInfo is not None:
            totalCount = lInfo['@totalCount']
            pageNumber = lInfo['@pageNumber']
            offset = lInfo['@offset']
            limit = lInfo['@limit']

        # (@c common.api.context.sessioncontext.GridContext.LiveInfo) информация
        # о странице
        # @todo Уточнить описание
        self.liveInfo = GridContext.LiveInfo(totalCount, pageNumber, offset, limit)

        # (@c bool) флаг частичного обновления
        self.partialUpdate = (gridContextJson['partialUpdate'] == 'true')

        # (@c string) ИД элемента грида на информационной панели
        self.id = gridContextJson['@id']

        recId = gridContextJson.get('selectedRecordId', None)
        recId = recId or None

        if recId and not isinstance(recId, list):
            recId = [recId]

        # (<tt>list of string</tt>) список выбранных в гриде записей.
        # Состояние по умолчанию - пустой список.
        # Если выбрана только одна запись, то #selectedRecordIds - список
        # с одним элементом.
        self.selectedRecordIds = recId or []

        recId = gridContextJson.get('currentRecordId', None)

        # (@c string) текущая выбранная запись.
        # Если ничего не выбрано, то @c None
        self.currentRecordId = recId or None


class FormsContext:
    """Описывает контекст формы"""

    def __init__(self, formContextJson):
        """
        @param formContextJson (@c dict) JSON-словарь c содержимым тэга 
        *xformsContext*
        """

        # (@c string) ИД элемента формы на информационной панели
        self.id = formContextJson['@id']
        # (@c dict) JSON-словарь данных формы.
        # Если данные отсутствуют, т.е. нет @e formData, то @c None
        self.data = formContextJson.get("formData", None)


class URLParams(object):
    """Класс доступа к параметрам URL через атрибуты.

    @see common.api.context.sessioncontext.SessionContext.urlparams
    """

    PARAM_KEY = 'urlparam'

    def __init__(self, urlparams):
        """
        @param urlparams (@c dict | @c None) содержимое тэга *urlparams* в 
        session context
        """

        self.__params = urlparams or {}
        if self.__params:
            self.__params = self.__params.get(self.PARAM_KEY, {})

        if self.__params:
            res = {}
            getValue = lambda valuesList: valuesList[0] if len(valuesList) == 1 else valuesList
            if isinstance(self.__params, list):
                for p in self.__params:
                    res[p['@name']] = getValue(p['@value'])
            else:
                res[self.__params['@name']] = getValue(self.__params['@value'])

            self.__params = res

    def __contains__(self, attr):
        return attr in self.__params

    def __getattr__(self, name):
        try:
            return self.__params[name]
        except KeyError:
            raise AttributeError("URL parameter '{}' does not set".format(name))

    def __iter__(self):
        return self.__params.iteritems()

    def __bool__(self):
        return bool(self.__params)

    __nonzero__ = __bool__

    def __repr__(self, ):
        return "{}({})".format(self.__class__.__name__, str(self.__params).replace('{', '').replace('}', '').replace(': ', '='))


class SessionContext:
    """Описывает структуру session context."""

    def __init__(self, jsonString, panelGridsCount=1):
        """
        @param jsonString (@ string) JSON-строка, содержащая session context.
        Обычно такая строка приходит одним из параметров функции-обработчика
        Showcase
        @param panelGridsCount (@c int) количество гридов на датапанели.
        Исходя из количества гридов рассчитываются их размеры (см. #getGridWidth
        и #getGridHeight)
        """

        # (@c int) количество гридов на датапанели
        self.gridsCount = panelGridsCount

        # (@c string) JSON-строка, содержащая session context
        self.sessionString = jsonString

        sesJson = json.loads(jsonString)['sessioncontext']
        
        # (@c string) ID сессии
        self.sessionId = sesJson['sessionid']
        # (@c string) логин
        self.login = sesJson['username']
        # (@c string) SID пользователя
        self.sid = sesJson['sid']
        # (@c string) IP
        self.ip = sesJson['ip']
        # (@c string) e-mail пользователя
        self.email = sesJson['email']
        # (@c string) полное имя пользователя
        self.username = sesJson['fullusername']
        # (@c string) телефон пользователя
        self.phone = sesJson['phone']
        # (@c string) текущая перспектива
        self.userdata = sesJson['userdata']

        
        # Т.к. длины отсутствуют при работе с модальными окнами (или только с
        # модальными формами). Возможно - баг.
        ## (@c int) высота датапанели. Если параметр недоступен, то @c None
        self.currentDatapanelHeight = None
        if 'currentDatapanelHeight' in sesJson:
            self.currentDatapanelHeight = int(sesJson['currentDatapanelHeight'])
            
        ## (@c int) ширина датапанели. Если параметр недоступен, то @c None
        self.currentDatapanelWidth = None
        if 'currentDatapanelWidth' in sesJson:
            self.currentDatapanelWidth = int(sesJson['currentDatapanelWidth'])

        # (<tt>list of common.api.context.sessioncontext.GridContext</tt>) Список
        # контекстов связанных гридов.
        #
        # Состояние по умолчанию - пустой список.
        # Если существует только один связанный грид, то #relatedGrids - список
        # с одним элементом.
        #
        # Как правило прямое использование этого поля не требуется.
        # @see #getGridContext
        self.relatedGrids = []
        # (<tt>list of common.api.context.sessioncontext.FormsContext</tt>) Список
        # контекстов связанных форм.
        #
        # Состояние по умолчанию - пустой список.
        # Если существует только одна связанная форма, то #relatedForms - список
        # с одним элементом.
        #
        # Как правило прямое использование этого поля не требуется
        # @see #getFormContext
        self.relatedForms = []

        related = sesJson.get('related', None)
        if related is not None and related != "":
            gc = related.get('gridContext', None)
            if gc is not None:
                self._createRelatedGrids(gc)

            fc = related.get('xformsContext', None)
            if fc is not None:
                self._createRelatedForms(fc)

        # (@c common.api.context.sessioncontext.URLParams) Параметры URL.
        #
        # Доступ к парметрам осуществляется через атрибуты, соответствующие
        # имени параметра:
        #
        # @code
        # ses = SessionContext(session)
        # try:
        #     # получение значения параметра с именем param1
        #     param1Value = ses.urlparams.param1
        # except AttributeError:
        #    # выбрасывает AttributeError, если параметр не был задан
        #
        # # проверка наличия параметра
        # if 'paramName' in ses.urlparams:
        #    print 'URL has parameter!'
        #
        # # итерация по всем параметрам
        # for paramName, paramValue in ses.urlparams:
        #     print paramName, paramValue
        #
        # @endcode
        #
        # @note Если у запрашиваемого параметра больше одного значения, то
        # они будут возвращены в виде списка.
        self.urlparams = URLParams(sesJson.get('urlparams'))

    def getGridContext(self, gridId=None):
        """Возвращает контекст грида с ИД @a gridId.

        Если @a gridId не задан, возвращает первый элемент из списка контекстов
        гридов #relatedGrids.
        Если нет ни одного контекста или контекст для заданного @a gridId не
        найден, возвращает @c None

        @param gridId (@c string) ИД грида
        @return @c common.api.context.sessioncontext.GridContext
        """
        if not self.relatedGrids:
            return None

        if gridId is None:
            return self.relatedGrids[0]

        foundGrid = filter(lambda x: x.id == gridId, self.relatedGrids)

        if len(foundGrid) == 0:
            return None

        return foundGrid[0]

    def getFormContext(self, formId=None):
        """Возвращает контекст формы с ИД @a formId.

        Если @a formId не задан, возвращает первый элемент из списка контекстов
        форм #relatedForms.
        Если нет ни одного контекста или контекст для заданного @a formId не
        найден, возвращает @c None.

        @param formId (@c string) ИД формы
        @return @c common.api.context.sessioncontext.FormsContext
        """

        if not self.relatedForms:
            return None

        if not formId:
            return self.relatedForms[0]

        foundForm = filter(lambda x: x.id == formId, self.relatedForms)

        if len(foundForm) == 0:
            return None

        return foundForm[0]

    def getGridWidth(self):
        """Возвращает ширину грида исходя из размеров датапанели.
        @return (@c int) размер в пикселях

        @see common.sysfunctions.getGridWidth
        """
        return int(getGridWidth(self.sessionString).replace('px', ''))

    def getGridHeight(self):
        """Возвращает высоту грида исходя из размеров датапанели и количества
        гридов на ней.

        @return (@c int) размер в пикселях

        @see #gridsCount
        @see common.sysfunctions.getGridHeight
        """
        return int(getGridHeight(self.sessionString, self.gridsCount))

    def _createRelatedGrids(self, gridContextContent):
        gridContextList = gridContextContent
        if not isinstance(gridContextContent, list):
            gridContextList = [gridContextContent]

        self.relatedGrids = [GridContext(rg) for rg in gridContextList]

    def _createRelatedForms(self, formContextContent):
        formContextList = formContextContent
        if not isinstance(formContextContent, list):
            formContextList = [formContextList]

        self.relatedForms = [FormsContext(rg) for rg in formContextList]


if __name__ == '__main__':
    s = '{"sessioncontext":{"userdata":"default","phone":"","username":"super","fullusername":"super","email":"","sid":"super","login":"super","related":{"gridContext":[{"currentColumnId":"ID","@id":"optGrid","gridFilterInfo":"","pageInfo":{"@size":"20","@number":"1"},"liveInfo":{"@totalCount":"2","@pageNumber":"1","@offset":"0","@limit":"50"},"partialUpdate":"false","currentDatapanelHeight":"782","selectedRecordId":"1","currentRecordId":"1","currentDatapanelWidth":"1596"},{"currentColumnId":"ID","@id":"optValsGrid","gridFilterInfo":"","pageInfo":{"@size":"20","@number":"1"},"liveInfo":{"@totalCount":"5","@pageNumber":"1","@offset":"0","@limit":"50"},"partialUpdate":"false","currentDatapanelHeight":"782","selectedRecordId":"6","currentRecordId":"6","filter":{"filter":{"context":"null"}},"currentDatapanelWidth":"1596"}]},"ip":"0:0:0:0:0:0:0:1","sessionid":"3D372AA540A12432E88D01B2823C7804"}}'

    ses = SessionContext(s)

    t = 1
