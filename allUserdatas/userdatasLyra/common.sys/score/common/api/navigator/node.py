# coding: UTF-8

'''@package common.api.navigator.node Модуль содержит классы для работы с
навигатором 

Created on 15 сент. 2015 г.

@author tugushev.rr
'''

from common.api.core import ShowcaseBaseNamedElement
from common.api.events.action import Action
from common.api.tree.treenode import Node


class NavigatorNode(ShowcaseBaseNamedElement, Node):
    """Класс узла дерева навигатора.
    
    Для построения дерева всегда необходим фиктивный корневой узел, у
    которого нет родителей (<em>#parent is None</em>);
    Этот узел не отображается, и может иметь любые ИД и наименование, и 
    задаётся, например, так: 
    @code
    rootNode = NavigatorNode('root', 'root')
    @endcode
    
    NavigatorNode сам расчитывает уровни иерархии пунктов навигатора 
    (тэг *<level>*). Достаточно только использовать метод #addChild.
    
    @see common.api.tree.treenode.Node, common.api.core.ShowcaseBaseNamedElement
    """
    
    def __init__(self, inId, inName, inAction=None, inIcon=None):
        """
        @param inId (@c string) ИД узла навигатора
        @param inName (@c string) отображаемое наименование узла
        @param inAction (@c common.api.events.action.Action) действие при клике
        @param inIcon (@c string) иконка для группы (см. #setIcon) 
        на узел навигатора   
        """
        
        super(NavigatorNode, self).__init__(inId, inName)
        self.__action = inAction
        self.__selectOnLoad = False
        self.__icon = inIcon
        self.initializeNode()

    
    def setAction(self, inAction):
        """Устанавливает действие, вызываемое при клике на узел
        @param inAction (@c common.api.events.action.Action) действие при клике
        на узел навигатора
        @return ссылка на себя
        """
        self.__action = inAction
        return self
    
    
    def getLevel(self):
        """Возвращает уровень узла навигатора:
        
        Возможные значения:
        - 0 - раздел (group)
        - 1-5 - уровни
        - -1 - корневой элемент
        
        @return @c int
        """
        
        # -2 - т.к. корневой элмент имеет rank=1, 
        # разделы имеют индексы вида 0.1, 0.2 и т.п.
        return self.index.rank() - 2
    
    
    def setSelectOnLoad(self, value):
        """Устанавливает флаг выбора узла при загрузке.
        
        @param value (@c bool) выбран/не выбран
        
        @warning Выбран может быть только один узел. На уровне #NavigatorNode
        такая не производится.
        """
        self.__selectOnLoad = value
        return self

        
    def selectOnLoad(self):
        """Возвращает флаг выбора узла при загрузке
        @return @c bool
        """
        return self.__selectOnLoad
    
    
    def setIcon(self, icon):
        """Устанавливает иконку для группы.
        
        @param icon (@c string)
        
        @note Действует только для узлов, являющихся группами, т.е. родителями которых является 
        корневрй узел. Для простых узлов (пунктов и подпунктов) заданное значение игнорируется.  
        """
        self.__icon = icon
        return self
    
    
    def icon(self):
        """Возвращает иконку, заданную для узла.
        @return @c string
        """
        return self.__icon
        
    
    def toJSONDict(self):
        if self.isRoot() and self.getNumberOfChildren() == 0:
            raise Exception('There are no child (group) nodes in root node! Root node has to have at least one child node!')
        
        d = {}
        
        # значит узел - группа
        if self.parent and self.parent.isRoot():
            if self.__icon:
                d['@icon'] = self.__icon
        
        childrenLevel = self.getLevel() + 1
        
        srtLst = []
        # если корневой элемент, то не надо записывать атрибуты
        if childrenLevel > 0:
            sd = super(NavigatorNode, self).toJSONDict()
            d.update(sd)
            
            if self.__selectOnLoad:
                d["@selectOnLoad"] = "true"
                 
            if self.__action:
                srtLst.append(self.__action.toJSONDict())
        
        levelNodes = [n.toJSONDict() for n in self.getChildren()]
                   
        if levelNodes or childrenLevel == 0:
            levelText = None
            if childrenLevel > 0:
                levelText = 'level%i' % childrenLevel 
                srtLst.append({levelText: levelNodes})
            else: 
                levelText = 'group'
                d[levelText] = {}
                d[levelText].update(levelNodes[0])
        
        if srtLst:
            d["#sorted"] = srtLst 
        
        return d 


if __name__ == '__main__':
    from common.api.events.activities import DatapanelActivity
    rootNode = NavigatorNode(None, None) 
    
#     print rootNode.toJSONDict()
    
    n1 = NavigatorNode('g1', u'Group 1', inIcon='icon.png')
    
    n11 = NavigatorNode('n1.1', u'Node 1.1', 
            Action().addActivity(
                DatapanelActivity().add('datapanel1.xml','firstOrCurrent')
            )
        )
    
    n11.setIcon('icon11.png')
     
    n1.addChild(n11)
     
    n1.addChild(
        NavigatorNode('n1.2', u'Node 1.2', 
            Action().addActivity(
                DatapanelActivity().add('datapanel2.xml','firstOrCurrent')
            )
        ).setSelectOnLoad(True)
    )
     
    n11.addChild(NavigatorNode('n1.1.1', u'Node 1.1.1', 
            Action().addActivity(
                DatapanelActivity().add('datapanel3.xml','firstOrCurrent')
            )
        ))
     
    n2 = NavigatorNode('n1.3', u'Node 1.3',
            Action().addActivity(
                    DatapanelActivity().add('datapanel4.xml','firstOrCurrent')
                )
        )
    
    n2.setIcon(n1.icon())
    
    n21 = NavigatorNode('n1.3.1', u'Node 1.3') 
    
    rootNode.addChild(n1)
#     rootNode.addChild(n2)
    
#     print t.getRoot().toJSONDict()
    print rootNode.toJSONDict()
    