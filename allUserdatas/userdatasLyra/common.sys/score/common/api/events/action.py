# coding: UTF-8
'''@package common.api.events.action Модуль содержит классы для работы с
действиями (action) Showcase

Created on 16 июля 2015 г.

@author: tugushev.rr
'''

from common.api.core import IJSONSerializable
from common.api.events.activities import ActionActivity, ActionActivityType,\
    NavigatorActivity, ClientActivity, DatapanelElement, ServerElement,\
    ClientElement, ActivityElement, DatapanelActivity, ServerActivity
from common.api.events.core import ActionBaseElement


class ModalWindow(IJSONSerializable):
    """Описывает модальное окно"""

    def __init__(self, inCaption, inWidth, inHeight):
        """
        @param inCaption (@c string) текст заголовка модального окна
        @param inWidth (@c int) ширина окна
        @param inHeight (@c int) высота окна
        """
        super(ModalWindow, self).__init__()
        self.__caption = inCaption
        self.__width = inWidth
        self.__height = inHeight
        self.__showBottomCloseButton = False
        self.__closeOnEsc = True

    def caption(self):
        """Возвращает текст заголовка модального окна
        @return @c string
        """
        return self.__caption

    def setCaption(self, value):
        """Устанавливает текст заголовка модального окна
        @param value (@c string)
        @return ссылка на себя
        """
        self.__caption = value
        return self

    def width(self):
        """Возвращает ширину окна
        @return @c int
        """
        return self.__width

    def setWidth(self, value):
        """Устанавливает ширину окна
        @param value (@c int)
        @return ссылка на себя
        """
        self.__width = value
        return self

    def height(self):
        """Возвращает высоту окна
        @return @c int
        """
        return self.__height

    def setHeight(self, value):
        """Устанавливает высоту окна
        @param value (@c int)
        @return ссылка на себя
        """
        self.__height = value
        return self

    def showBottomCloseButton(self):
        """Возвращает значение флага, отвечающего за отображение кнопки
        "Закрыть"
        @return @c bool
        """
        return self.__showBottomCloseButton

    def setShowBottomCloseButton(self, value):
        """Устанавливает значение флага, отвечающего за отображение кнопки
        "Закрыть"
        @param value (@c bool)
        @return ссылка на себя
        """
        self.__showBottomCloseButton = value
        return self

    def showCloseOnExc(self):
        """Возвращает значение флага, отвечающего за разрешения закрытия карточки по
        нажатию "Esc"
        @return @c bool
        """
        return self.__closeOnEsc

    def setCloseOnEsc(self, value):
        """Разрешает закрывать карточку по нажатию на "Esc"
        @param value (@c bool)
        @return ссылка на себя
        """
        self.__closeOnEsc = value
        return self

    def toJSONDict(self):
        d = {
            "@caption": self.caption(),
            "@width": self.width(),
            "@height": self.height(),
            "@show_close_bottom_button": unicode(self.showBottomCloseButton()).lower(),
            "@close_on_esc": unicode(self.showCloseOnExc()).lower()
        }

        return d


class Action(ActionBaseElement):
    """Класс действия"""

    class _SHOW_IN_TYPES(object):
        PANEL = u'PANEL'
        MODALWINDOW = 'MODAL_WINDOW'

    def __init__(self, inMainContext='current', inKeepUserSettings=False, inIsPartialUpdate=False):
        """
        @param inMainContext (<tt>любой тип</tt>)
        @param inKeepUserSettings, inIsPartialUpdate см.
        @c common.api.events.common.ActionBaseElement
        """
        super(Action, self).__init__(inKeepUserSettings, inIsPartialUpdate)
        self.__mainContext = inMainContext
        self.__modalWindow = None
        self.__activities = []

    def mainContext(self):
        """Возвращает main context"""
        return self.__mainContext

    def setMainContext(self, value):
        """Устанавливает main context
        @param value (<tt>любой тип</tt>)
        @return ссылка на себя
        """
        self.__mainContext = value
        return self

    def showIn(self, inModalWindow):
        """Устанавливает модальное окно
        @param inModalWindow (@c common.api.events.action.ModalWindow)
        @return ссылка на себя
        """

        self.__modalWindow = inModalWindow
        return self

    def modalWindow(self):
        """Возвращает модальное окно. Если не модальное окно не задано, то @c None.
        @return @c common.api.events.action.ModalWindow или @c None
        """

        return self.__modalWindow

    def addActivity(self, inActivity):
        """Добавляет конкретное действие

        Может быть добавлено только по одному действию каждого типа -
        *datapanel*, *server*, *client*

        @param inActivity (@c common.api.events.activities.ActionActivity)
        @return ссылка на себя

        @throw TypeError если @a inActivity некорректного типа
        @throw ValueError если действие с переданным типом уже добавлено в
        *action*
        """
        if not isinstance(inActivity, ActionActivity):
            raise TypeError()

        if self._checkActivityType(inActivity):
            raise ValueError("Activity of type '%s' already exists in this action!" % inActivity.__class__.__name__)

        self.__activities.append(inActivity)

        return self

    def setDatapanelActivity(self, panel, tab='current'):
        """Устанавливает параметры действия с информационной панелью.
        @param inPanel, inTab см. @c common.api.events.activities.DatapanelActivity

        Это метод для удобства. Следующие фрагменты кода эквивалентны:
        @code
        a = Action().addActivity(DatapanelActivity('panel.xml'))
        @endcode
        и
        @code
        a = Action().setDatapanelActivity('panel.xml')
        @endcode
        """

        a = self._getActivity(ActionActivityType.DATAPANEL, panel, tab)
        a.setPanel(panel)
        a.setTab(tab)

        return self

    def setNavigatorActivity(self, inRefresh, inNodeId):
        """Устанавливает параметры действия обновления навигатора.
        @param inRefresh, inNodeId см. @c common.api.events.activities.NavigatorActivity

        Это метод для удобства. Следующие фрагменты кода эквивалентны:
        @code
        a = Action().addActivity(NavigatorActivity(True, 2))
        @endcode
        и
        @code
        a = Action().setNavigatorActivity(True, 2)
        @endcode
        """

        a = self._getActivity(ActionActivityType.NAVIGATOR, inRefresh, inNodeId)
        a.setRefresh(inRefresh)
        a.setNodeId(inNodeId)

        return self

    def add(self, activityElement):
        """Добавляет элемент действия.

        Метод для удобства. Следующие фрагменты кода эквивалентны:
        @code
        a = Action().addActivity(
                        DatapanelActivity().addElement(
                            DatapanelElement('1', 'add')
                        )
                    )
        @endcode
        и
        @code
        a = Action().add(DatapanelElement('1', 'add'))
        @endcode

        @param activityElement (@c _BaseActivityElement) элемент
        @return ссылка на себя
        """
        if type(activityElement) is ActivityElement:
            raise TypeError('Invalid activity element type! Use concrete ServerElement/ClientElement instead of ActivityElement.')

        actType = None
        if type(activityElement) is DatapanelElement:
            actType = ActionActivityType.DATAPANEL
        elif type(activityElement) is ServerElement:
            actType = ActionActivityType.SERVER
        elif type(activityElement) is ClientElement:
            actType = ActionActivityType.CLIENT
        else:
            raise TypeError('Invalid activity element type!')

        a = self._getActivity(actType, 'current', 'current')
        a.addElement(activityElement)

        return self

    def _getActivity(self, activityType, *args):
        """Возвращает группу действий.

        Если группа отсутствует, то она будет создана.

        @param activityType (@c common.api.events.activities.ActionActivityType)
        @param *args параметры конструктора соответствующего activity
        @return @c common.api.events.activities.ActionActivity
        @throw TypeError если некорректный @a activityType
        """
        r = filter(lambda x: x.type() == activityType, self.__activities)
        if r:
            return r[0]

        a = None
        if activityType == ActionActivityType.DATAPANEL:
            a = DatapanelActivity(*args)
        elif activityType == ActionActivityType.NAVIGATOR:
            a = NavigatorActivity(*args)
        elif activityType == ActionActivityType.SERVER:
            a = ServerActivity()
        elif activityType == ActionActivityType.CLIENT:
            a = ClientActivity()
        else:
            raise TypeError('Invalid activity type!')

        self.addActivity(a)

        return a

    def toJSONDict(self):
        d = super(Action, self).toJSONDict()
        sorted_ = [{'main_context': self.mainContext()}]

        if self.__modalWindow:
            d["@show_in"] = Action._SHOW_IN_TYPES.MODALWINDOW
            sorted_.append({'modalwindow': self.__modalWindow.toJSONDict()})

        for act in self.__activities:
            sorted_.append({act.type(): act.toJSONDict()})

        d['#sorted'] = sorted_

        return {'action': d}

    def _checkActivityType(self, inActivity):
        """Проверяет, есть ли действие такого типа в списке действий.
        @param inActivity (@c common.api.events.activities.ActionActivity)
        @return (@c bool) возвращает True, иначе False
        """
        res = filter(lambda x: x.__class__ == inActivity.__class__, self.__activities)
        return len(res) > 0


if __name__ == '__main__':
    from common.api.events.activities import DatapanelActivity, ServerActivity

    a = Action(inKeepUserSettings=True) \
        .addActivity(DatapanelActivity()
                     .add(inId='1', inAddContext='add')
                     )

    datapanelAcivity = DatapanelActivity()
    datapanelAcivity.add(inId='1', inAddContext='edit')

    b = Action(inKeepUserSettings=True).addActivity(datapanelAcivity)
    b.showIn(ModalWindow('Modal window caption', 200, 200))

    c = Action() \
        .addActivity(
            ServerActivity()
        .add('ac1', 'proc1')) \
        .addActivity(
            DatapanelActivity()
        .add(inId='1', inAddContext='edit')
        .add(inId='2', inAddContext='edit', inKeepUserSettings=True)) \
        #         .addActivity( \
#             DatapanelActivity() \
#                 .addElement(DatapanelElement(inId='1', inAddContext='edit')) \
#                 .addElement(DatapanelElement(inId='2', inAddContext='edit', inKeepUserSettings=True)))

#     print a.toJSONDict()
#     print b.toJSONDict()
#     print c.toJSONDict()

    a = Action('mainContext').add(DatapanelElement('1', 'add'))
    a.add(DatapanelElement('2', 'add2')).add(ServerElement('1', 'procName'))
    a.add(ClientElement('2', 'clientProc'))
    a.setDatapanelActivity('current', 'firstOrCurrent')
    print a.toJSONDict()
