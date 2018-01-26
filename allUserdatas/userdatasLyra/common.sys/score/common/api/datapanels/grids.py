# coding: UTF-8

'''@package common.api.datapanels.grids Модуль сродержит класс и функции для
работы с элементами типа GRID
 
Created on 01 авг. 2015 г.

@author tugushev.rr
'''

from inspect import isfunction, isclass

from common.api.core import IJSONSerializable, IXMLSerializable
from common.api.datapanels.datapanel import DatapanelElement, DatapanelElementTypes, ProcTypes
from common.api.events.action import Action
from common.api.utils.tools import objectQualifiedName, classQualifiedName


class GridTypes(object):
    """Описывает типы гридов"""

    # Страничный грид.
    PAGE = 1
    # Живой грид.
    LIVE = 2
    # Грид для отображения древовидных (иерархических) данных
    TREE = 3
    # Lyra-грид
    LYRA = 4

    @staticmethod
    def validate(gridType):
        """Проверяет корректность типа грида
        @param gridType (@c int) ИД типа грида (#PAGE, #LIVE, #TREE, #LYRA)
        @return (@c bool) @c True, если @a gridType является гридом, иначе - 
        @c False
        """
        return gridType in [GridTypes.PAGE, GridTypes.LIVE, GridTypes.TREE, GridTypes.LYRA]


class GridProcTypes(ProcTypes):
    """Описывает типы процедур грида"""
    # Функция загрузки настроек грида
    METADATA = u'METADATA'
    # Функция загрузки тулбара грида
    TOOLBAR = u'TOOLBAR'


class GridElement(DatapanelElement):
    """Описывает элемент грида.

    Потребности явно использовать этот класс нет, т.к. для удобства 
    реализованы функции:
    * @c common.api.datapanels.grids.PageGrid
    * @c common.api.datapanels.grids.LiveGrid
    * @c common.api.datapanels.grids.TreeGrid
    * @c common.api.datapanels.grids.LyraGrid
    """

    # syptype и plugin для разных видов гридов
    PAGE = (u'JS_PAGE_GRID', u'pageDGrid')
    LIVE = (u'JS_LIVE_GRID', u'liveDGrid')
    TREE = (u'JS_TREE_GRID', u'treeDGrid')
    LYRA = (u'JS_LYRA_GRID', u'lyraDGrid')

    SUBTYPES_MAP = {
        GridTypes.PAGE: PAGE,
        GridTypes.LIVE: LIVE,
        GridTypes.TREE: TREE,
        GridTypes.LYRA: LYRA
    }

    def __init__(self, elementId, gridType, procName=None):
        """
        @param elementId (@c string) ИД элемента
        @param gridType (@c common.api.datapanels.grids.GridProcTypes) тип грида
        @param procName (<tt>string or function object</tt>) функция-обработчик
        загрузки данных 
        """
        super(GridElement, self).__init__(elementId, DatapanelElementTypes.GRID, procName)

        self.__gridType = gridType

    def gridType(self):
        return self.__gridType

    def toolbarProc(self):
        """Возвращает функцию-обработчик загрузки тулбара грида
        @return (@c string) полное имя функции (qualified name)
        """
        return self._getProc(GridProcTypes.TOOLBAR)

    def setToolbarProc(self, value):
        """Устанавливает функцию-обработчик загрузки тулбара грида
        @param value (<tt>string or function object</tt>) функция-обработчик
        загрузки тулбара
        @return ссылка на себя
        """
        self._addProc(value, GridProcTypes.TOOLBAR)
        return self

    def metadataProc(self):
        """Возвращает функцию-обработчик загрузки настроек грида
        @return (@c string) полное имя функции (qualified name)
        """
        return self._getProc(GridProcTypes.METADATA)

    def setMetadataProc(self, value):
        """Устанавливает функцию-обработчик загрузки настроек грида
        @param value (<tt>string or function object</tt>) фунция-обработчик
        загрузки настроек грида
        @return ссылка на себя
        """
        pp = self.metadataProc()

        if pp:
            if isfunction(value):
                value = classQualifiedName(value) + '.celesta'
            pp['@name'] = value
            return self

        self._addProc(value, GridProcTypes.METADATA)
        return self

    def setPartialUpdateProc(self, value):
        """Устанавливает функцию-обработчик частичного обновления
        @param value (<tt>string or function object</tt>) функция-обработчик
        частичного обновления
        @return ссылка на себя
        @todo Ещё не реализовано
        """
        #         self._addProc(GridProcTypes., procName)
        raise NotImplementedError()

    def toJSONDict(self):
        d = super(GridElement, self).toJSONDict()

        d['@subtype'] = self.SUBTYPES_MAP[self.gridType()][0]
        d['@plugin'] = self.SUBTYPES_MAP[self.gridType()][1]

        return d


class LyraGrid(GridElement):
    """Описывает элемент Lyra-грида.
    """

    def __init__(self, elementId, viewCls=None):
        """
        @param elementId (@c string) ИД элемента
        @param viewCls (<tt>string or lyra.gridForm.GridForm</tt>) Конкретное представление грида
        """
        super(LyraGrid, self).__init__(elementId, GridTypes.LYRA, viewCls)

    def setView(self, viewCls):
        """Устанавливает класс представления Lyra-грида.

        @param viewCls (<tt>string or lyra.gridForm.GridForm</tt>) Конкретное представление грида
        @return ссылка на себя

        """

        self.setProc(viewCls)
        return self

    def setProc(self, value):
        """Переопределённый метод.

        @return ссылка на себя

        @note Хотя этот метод и работает как следует, но использование #setView
        предпочтительнее, т.к. лучше отражает семантику действия.
        @see common.api.datapanels.datapanel.DatapanelElement

        """
        if isfunction(value):
            raise TypeError('Cannot set proc function {} to the LyraGrid instance. Set class instead.'.format(
                classQualifiedName(value)))
        name = value
        if isclass(value):
            name = classQualifiedName(value)
        return super(LyraGrid, self).setProc(name)

    def setMetadataProc(self, value):
        raise NotImplementedError('LyraGrid cannot have metadata proc!')


def PageGrid(elementId, procName=None):
    """Создаёт объект страничного грида с ИД @a elementId и функцией загрузки
    данных @a procName
    @param elementId (@c string) ИД элемента
    @param procName (<tt>string or function object</tt>) функция-обработчик
    загрузки данных 
    @return @c common.api.datapanels.grids.GridElement
    """
    return GridElement(elementId, GridTypes.PAGE, procName)


def LiveGrid(elementId, procName=None):
    """Создаёт объект живого грида с ИД @a elementId и функцией загрузки
    данных @a procName
    @param elementId (@c string) ИД элемента
    @param procName (<tt>string or function object</tt>) функция-обработчик
    загрузки данных 
    @return @c common.api.datapanels.grids.GridElement
    """
    return GridElement(elementId, GridTypes.LIVE, procName)


def TreeGrid(elementId, procName=None):
    """Создаёт объект древовидного грида с ИД @a elementId и функцией загрузки
    данных @a procName
    @param elementId (@c string) ИД элемента
    @param procName (<tt>string or function object</tt>) функция-обработчик
    загрузки данных 
    @return @c common.api.datapanels.grids.GridElement
    """
    return GridElement(elementId, GridTypes.TREE, procName)


class ToolbarItemTypes(object):
    """Описывает типы элементов тулбара"""
    # Простая кнопка тулбара
    ITEM = 'item'
    # Группа тулбара
    GROUP = 'group'
    # Разделитель
    SEPARATOR = 'separator'


class BaseToolbarAction(IJSONSerializable):
    """Базовый класс для кнопок тулбара и групп элементов"""

    def __init__(self, itemType, caption, image=None, enabled=True, hint=None, iconClassName=None, id=None,
                 popupText=None):
        """
        @param itemType (@c common.api.datapanels.grids.ToolbarItemTypes) тип
        элемента
        @param caption (@c string) текст элемента
        @param id (@c string) id элемента
        @param image (@c string) иконка элемента
        @param enabled (@c bool) свойство, отвечающее за активность элементов
        тулбара
        @param hint (@c string) всплывающая подсказка элемента    
        """
        self.__caption = caption
        self.__id = id
        self.__image = image
        self.__iconClassName = iconClassName
        self.__enabled = enabled
        self.__hint = hint
        self.__type = itemType
        self.__popupText = popupText

    def caption(self):
        """Возвращает текст элемента
        @return @c string
        """
        return self.__caption

    def setCaption(self, value):
        """Устанавливает текст элемента
        @param value (@c string)
        @return ссылка на себя
        """
        self.__caption = value
        return self

    def id(self):
        """Возвращает id элемента
        @return @c string
        """
        return self.__id

    def setId(self, value):
        """Устанавливает id элемента
        @param value (@c string)
        @return ссылка на себя
        """
        self.__id = value
        return self

    def image(self):
        """Возвращает иконку элемента
        @return @c string
        """
        return self.__image

    def setImage(self, value):
        """Устанавливает иконку элемента
        @param value (@c string)
        @return ссылка на себя
        """
        self.__image = value
        return self

    def iconClassName(self):
        return self.__iconClassName

    def setIconClassName(self, value):
        self.__iconClassName = value
        return self

    def popupText(self):
        return self.__popupText

    def setPopupText(self, value):
        self.__popupText = value
        return self

    def enabled(self):
        """Возвращает состояение элемента
        @return @c bool 
        """
        return self.__enabled

    def setEnabled(self, value):
        """Устанавливает состояение элемента
        @param value (@c bool)
        @return ссылка на себя
        """
        self.__enabled = value
        return self

    def hint(self):
        """Возвращает текст всплывающей подсказки элемента
        @return @c string
        """
        return self.__hint

    def setHint(self, value):
        """Устанавливает текст всплывающей подсказки элемента
        @param value (@c string)
        @return ссылка на себя
        """
        self.__hint = value
        return self

    def type(self):
        """Возвращает тип элемента
        @return @c common.api.datapanels.grids.ToolbarItemTypes
        """
        return self.__type

    def toJSONDict(self):
        d = {'@text': self.caption()}

        if self.id():
            d['@id'] = self.id()

        if self.image():
            d['@img'] = self.image()

        if self.iconClassName():
            d['@iconClassName'] = self.iconClassName()

        d['@disable'] = str(not self.enabled()).lower()

        if self.hint():
            d['@hint'] = self.hint()

        return d


class Separator(IJSONSerializable):
    """Класс элемента тулбара 'Разделитель'. Выделен в отдельный класс
    для реализации интерфейса IJSONSerializable.
    """

    def __init__(self):
        self.__type = ToolbarItemTypes.SEPARATOR

    def type(self):
        return self.__type

    def toJSONDict(self):
        return None


class ToolbarItem(BaseToolbarAction):
    """Класс элемента тулбара (простая кнопка)."""

    def __init__(self, caption, image=None, enabled=True, hint=None, action=None, iconClassName=None, id=None,
                 downloadLinkId=None):
        """
        @param caption, id, image, enabled, hint см. 
        @c common.api.datapanels.grids.BaseToolbarAction
        @param action (@c common.api.events.action.Action) дейсвтие при клике
        по кнопке
        @param downloadLinkId ссылка на процедуру скачивания
        """
        super(ToolbarItem, self).__init__(ToolbarItemTypes.ITEM, caption, image, enabled, hint, id)

        self.__downloadLinkId = downloadLinkId
        self.setAction(action)

    def action(self):
        """Возвращает действие, вызываемое при клике на узел
        @return (@c common.api.events.action.Action) действие при клике
        на кнопку
        """
        return self.__action

    def setAction(self, value):
        """Устанавливает действие, вызываемое при клике на узел
        @param value (@c common.api.events.action.Action) действие при клике
        по кнопке
        @return ссылка на себя
        """
        if value and not isinstance(value, Action):
            raise TypeError("Instance of type 'Action' expected but '%s' given" % value.__class__.__name__)

        self.__action = value
        return self

    def downloadLinkId(self):
        return self.__downloadLinkId

    def setDownloadLinkId(self, value):
        self.__downloadLinkId = value
        return self

    def toJSONDict(self):
        d = super(ToolbarItem, self).toJSONDict()

        if self.action():
            d.update(self.action().toJSONDict())
        if self.downloadLinkId():
            d['@downloadLinkId'] = self.downloadLinkId()

        return d


class ToolbarContainerMixIn(object):
    """Класс, обеспечивающий функциональность контейнера элементов тулбара:
    кнопок, групп, разделителей.

    При работе с контейнером учитывается порядок добавления элементов.

    @see common.api.datapanels.grids.ToolbarItem
    @see common.api.datapanels.grids.ToolbarGroup
    """

    def __init__(self):
        self.initToolbarContainerMixIn()

    def initToolbarContainerMixIn(self):
        """Инициализирует контейнер. Елси при наследовании ToolbarContainerMixIn
        сотит не на первом месте, то в конструкторе дочернего класса необходимо
        вызвать этот метод. 
        """

        self.__items = []

    def addItem(self, item):
        """
        @param item (@c common.api.datapanels.grids.BaseToolbarAction,
        @c common.api.datapanels.grids.Separator) элемент тулбара
        @return ссылка на себя
        @throw TypeError если некорректный тип @a item
        """

        if not isinstance(item, (BaseToolbarAction, Separator)):
            raise TypeError(
                "Instance of class 'BaseToolbarAction' or 'Separator' expected but '%s' is given" % objectQualifiedName(
                    item))

        self.__items.append(item)
        return self

    def items(self):
        """Возвращает список элементов
        @return <tt>list of common.api.datapanels.grids.BaseToolbarAction or
        common.api.datapanels.grids.Separator</tt>
        """
        return self.__items

    def addSeparator(self):
        """Добавляет разделитель"""
        self.addItem(Separator())

    def _toJSONDict(self):
        """Формирует JSON по списку элементов контейнера с учётом порядка их 
        следования
        @return (@c dict) JSON-словарь или @c None, если в контейнере нет
        элементов
        """

        if not self.items():
            return None

        # итоговый масcив
        itemsList = []

        currentType = None

        # масиы элементов текущей группы
        tmpItems = None

        # В цикле элементы распределяются по группам (item, group), чтобы прописывать
        # корректные тэги, т.к. порядок имеет значение
        for item in self.items():
            if item.type() != currentType:
                # если предыдущий тип задан, то
                # добавляем элементы в итоговый список
                if currentType:
                    itemsList.append({currentType: any(tmpItems) and tmpItems or None})

                # инициализируем массив элементов нового типа
                tmpItems = []
                currentType = item.type()

            tmpItems.append(item.toJSONDict())
        else:
            if tmpItems:
                itemsList.append({currentType: any(tmpItems) and tmpItems or None})

        return itemsList


class ToolbarGroup(BaseToolbarAction, ToolbarContainerMixIn):
    """Описывает группу элементов тулбара."""

    def __init__(self, caption, image=None, enabled=True, hint=None):
        """
        @param caption, image, enabled, hint см. 
        @c common.api.datapanels.grids.BaseToolbarAction
        """
        super(ToolbarGroup, self).__init__(ToolbarItemTypes.GROUP, caption, image, enabled, hint)
        self.initToolbarContainerMixIn()

    def toJSONDict(self):
        d = super(ToolbarGroup, self).toJSONDict()

        subItems = self._toJSONDict()

        if subItems:
            d['#sorted'] = subItems
        else:
            d[ToolbarItemTypes.ITEM] = ToolbarItem(caption=u'Пусто', enabled=False).toJSONDict()

        return d


class Toolbar(ToolbarContainerMixIn, IXMLSerializable):
    """Класс тулбара грида"""

    def __init__(self, className=None, style=None):
        super(Toolbar, self).__init__()
        self.__className = className
        self.__style = style

    def clear(self):
        """Удлаяет все элементы из тулбара"""
        self.initToolbarContainerMixIn()

    def setClassName(self, className):
        self.__className = className

    def className(self):
        return self.__className

    def setStyle(self, style):
        self.__style = style

    def style(self):
        return self.__style

    def toJSONDict(self):
        gridtoolbar = {
            'gridtoolbar': {
                "@className": self.__className,
                "@style": self.__style,
            }
        }
        if self.items():
            gridtoolbar['gridtoolbar'].update({'#sorted': self._toJSONDict()})
        else:
            gridtoolbar['gridtoolbar'].update({ToolbarItemTypes.ITEM: ''})

        return gridtoolbar


if __name__ == '__main__':
    from common.api.events.activities import ClientActivity

    t = Toolbar()

    t.addItem(
        ToolbarItem('item1').setAction(
            Action().addActivity(
                ClientActivity().add('1', 'proc1')
            )
        )
    )

    t.addItem(
        ToolbarItem('item2').setAction(
            Action().addActivity(
                ClientActivity().add('2', 'proc2')
            )
        )
    )

    t.addSeparator()

    grp1 = ToolbarGroup('grp1')
    grp1.addItem(ToolbarItem('gi1').setAction(
        Action().addActivity(
            ClientActivity().add('gi1', 'procGi1')
        )
    )
    )

    grp1.addItem(ToolbarItem('gi2').setAction(
        Action().addActivity(
            ClientActivity().add('gi2', 'procGi2')
        )
    )
    )

    grp1.addSeparator()

    grp2 = ToolbarGroup('grp11')
    grp2.addItem(ToolbarItem('gi111').setAction(
        Action().addActivity(
            ClientActivity().add('gi111', 'procGi111')
        )
    )
    )

    grp2.addItem(ToolbarItem(caption='gi112', enabled=False).setAction(
        Action().addActivity(
            ClientActivity().add('gi112', 'procGi112')
        )
    )
    )

    grp1.addItem(grp2)

    grp1.addSeparator()

    grp1.addItem(ToolbarItem('gi3').setAction(
        Action().addActivity(
            ClientActivity().add('gi3', 'procGi3')
        )
    )
    )

    t.addItem(grp1)
    t.addSeparator()
    t.addItem(ToolbarItem('item3').setAction(
        Action().addActivity(
            ClientActivity().add('3', 'proc3')
        )
    )
    )

    t.addItem(ToolbarGroup(caption='grp3', hint='Disabled group', enabled=False))

    print t.toJSONDict()
