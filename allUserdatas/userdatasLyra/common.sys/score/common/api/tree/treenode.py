# coding: UTF-8
u'''@package common.api.tree.treenode Модуль содержит класс для работы с узлами
дерева

Created on 11 сент. 2015 г.

@author: tugushev.r
'''

from common.api.tree.treeindex import TreeIndex

class Node(object):
    """Класс узла индексируемого дерева.
    
    Кажлому узлу присваивается индекс (код Дьюи) в соответствие с его позицией
    в иерархии.
    
    Корневой узел имеет индекс 0, узлы первого уровня - 0.<0...>,
    второго - 0.<0...>.<0...> и т.д. Индексы представлены классом 
    common.api.tree.treeindex.TreeIndex.
    Индексы вычисляются автоматически при добавлении узлов-потомков.
    
    @note Изначально этот класс задумывался, как контейнер, аналогичный 
    шаблонам в C++ и Java, т.е. сам класс Node должен обеспечивать только
    управление структурой, а пользовательские данные должны устанавливаться
    в #data. Но т.к. в Python динамическая типизация, то для создания
    собственного дерева, можно просто унаследоваться от Node.
    """
    
    
    def __init__(self, parentNode=None, data=None):
        """
        @param parentNode, data см. #initializeNode
        """
        self.initializeNode(parentNode, data)
        
    
    def initializeNode(self, parentNode=None, data=None):
        """Инициализация узла.
        
        @param parentNode (@c common.api.tree.treenode.Node) родительский узел
        @param data (@c любой тип) пользовательские данные, связанные с узлом
        
        @note Хотя в Python код инициализации обыно располагается в #__init__,
        здесь он вынесен в отдельный метод, чтобы было возможно создавать
        MixIn-классы
        """
        
        self.__data = None
        self.__parent = None
        self.__index = TreeIndex()
        self.__children = []
        
        self.data = data
        
        if parentNode is None:
            self._setParent(None)
            return
        
        if not isinstance(parentNode, Node):
            raise TypeError('Parent node has to be only instance of Node!')
        
        parentNode.addChild(self)
    
    
    if 0:
        ## (@c любой тип) пользовательские данные
        data = property
        ## (@c common.api.tree.treenode.Node, read only) родительский узел.
        # Назначается автоматически sпри добавлении узла родительскому узлу 
        # или при создании узла
        parent = property
        ## (@c common.api.tree.treeindex.TreeIndex) индекс узла. Вычисляется
        # автоматически.
        # @note Хотя это свойство и не является read only, не следует задавать
        # его вручную
        index = property
    
    
    @property
    def data(self):
        return self.__data
        
    
    @data.setter
    def data(self, value):
        self.__data = value
    
    
    @property
    def parent(self):
        return self.__parent
    
    
    @property
    def index(self):
        return self.__index
    
    
    @index.setter
    def index(self, inIndex):
        if inIndex == self.__index:
            return
        
        self.__index = TreeIndex(inIndex)
        if self.getNumberOfChildren() != 0:
            self.reindex(0)
            
    
    def _setParent(self, parentNode):
        if parentNode and not isinstance(parentNode, Node):
            raise TypeError('Parent node has to be only instance of Node!')
        
        if not self.__parent is None:
            if not self.__parent.detachChild(self):
                return
        
        if parentNode is None:
            self.__parent = None
            self.index = TreeIndex().depthNext()
        
        
        self.__parent = parentNode
        
    
    def isRoot(self):
        u"""
        @return @c True, если узел является корнем. Иначе - @c False.
        """
        
        return self.index.isRoot()
        
    
    def getSelfIndex(self):
        u"""Возвращает собственный индекс узла среди узлов на том же уровне
        у его родителя.
        
        @return int
        """
        
        return self.index.getSelfIndex()
    
    
    def getChildren(self):
        u"""@return (list if common.api.tree.treenode.Node) список потомков"""
        return self.__children
    
    
    def getChild(self, inSelfIndex):
        u"""Возвращает потомка по его собственному индексу.
        Если задан некорректный индекс, возвращает *None*
        
        @param inSelfIndex (int) собственный индекс потомка
        @return list of common.api.tree.treenode.Node
        """
        
        if inSelfIndex < 0 or inSelfIndex >= self.__children.size():
            return None

        return self.__children[inSelfIndex]
    
 
    def setChildren(self, children):
        u"""Устанавливает список потомков. При этом происходит их 
        переиндексация.
        
        @param children (list of common.api.tree.treenode.Node) список узлов
        """
        
        self.__children = children
        self.reindex(0)
    
 
    def getNumberOfChildren(self):
        u"""Возвращает количество потомков
        @return int
        """
        return len(self.__children)
    
     
    def addChild(self, child):
        u"""Добавляет потомка в конец списка потомков. 
        При этом рассчитывается новый индекс.
        
        @param child (common.api.tree.treenode.Node) узел
        
        @return (common.api.tree.treeindex.TreeIndex) индекс, который
        был присвоен потомку
        """
        
        return self.insertChildAt(child, -1)
    
     
    def insertChildAt(self, inNode, inPosition):
        u"""Добавляет узел *inNode* в позицию *inPosition* в списке потомков
        узла
        
        Если *inPosition* < 0, то узел добавляется в конец списка потомков.
        
        @param inNode (common.api.tree.treenode.Node) узел
        @param inPosition (int) собственный индекс элемента-потомка,
        перед которым надо вставить новый элемент. Если < 0, то добавляет 
        в конец
        
        @return (common.api.tree.treeindex.TreeIndex) индекс, который был
        присвоен потомку или некорректный индекс, если вставка завершилась
         неудачей (если передан некорректый *inPosition*)
        """
        
        assert(not inNode is self)
        
        it = None
        # если нет потомков, то просто добавляем
        if not self.__children and inPosition < 0:
            self.__children.append(inNode)
            it = self.getNumberOfChildren()
            # иначе
        elif inPosition < 0:
            # если нужно добавить в конец, то получаем индекс последнего элемента
            # и присваиваем его новому
            inNode.index = self.__children[-1].index.widthNext()
            self.__children.append(inNode)
            it = self.getNumberOfChildren()
        else:
            # проверяем индекс
            if inPosition < 0 and inPosition >= len(self.__children):
                return TreeIndex()

            self.__children.insert(inPosition, inNode)
            # получаем итератор вставленного элемента для переиндексации
            it = inPosition
        

        inNode._setParent(self)
        
        if it > 0:
            it -= 1
        
        self.reindex(it)
        return inNode.index
    
    
    def _detachByItem(self, inItem):
        u"""Открепляет узел *inItem*
    
        Узел остаётся существовать, но удаляется из списка потомков и 
        становится корневым элементом собственного поддерева
        
        @param inItem (common.api.tree.treenode.Node) - узёл-потомок
        
        @return *True*, если узел найден и *inItem is not None*. Иначе - *False*.
        """
        
        if inItem is None:
            return False

        it = None
        foundNodeIndex = -1
        # здесь осуществляется поиск именно по указателю.
        try:
            foundNodeIndex = self.__children.index(inItem)
        except:
            foundNodeIndex = -1
        
        if not foundNodeIndex is None:
            it = foundNodeIndex
            
        # если не нашли в потомках, возвращаем false
        if it is None:
            return False

        # удаляем из списка
        del self.__children[it]
        # сначала сбрасываем __parent, чтобы 
        # снова не выполнялся detach
        inItem.__parent = None
        inItem._setParent(None) 

#         node.setParent(null)
        # удаляем из списка потомков
        if it + 1 != self.getNumberOfChildren():
            if it - 1 >= 0:
                it -= 1
            self.reindex(it)
        
        return True
    
    
    def _detachByIndex(self, inSelfIndex):
        u"""Открепляет узел c собственным индексом *inSelfIndex*
    
        Узел остаётся существовать, но удаляется из списка потомков и 
        становится корневым элементом собственного поддерева
        
        @param inSelfIndex (int) собственный индекс потомка
        @return (common.api.tree.treenode.Node) если узел найден в потомках или
        *None*
        """
        
        if inSelfIndex < 0 or inSelfIndex >= self.getNumberOfChildren():
            return None

        item = self.__children[inSelfIndex]
        if self.detachChild(item):
            return item

        return None
    
    
    def detachChild(self, indexOrItem):
        u"""Отсоединяет потомка от узла.
        
        Отсоединённый узёл остаётся существовать и становится корневым 
        узлом собственного поддерева.
        
        @param indexOrItem (int or common.api.tree.treenode.Node) узел или
        собственный индекс
        
        @return (bool)
        - True, если удаление прошло успешно; 
        - False, если *indexOrItem* не найден в списке потомков или является
        некорректным собственным индексом
        """
        
        if isinstance(indexOrItem, Node):
            return self._detachByItem(indexOrItem)
        
        if isinstance(indexOrItem, int):
            return self._detachByIndex(indexOrItem)
        
        raise TypeError()
    
    
    def __eq__(self, other):
        """Оператор ==.
        Узлы считаются равными, если равны их индексы.
        """
        return self.index == other.index
    
    
    def _removeChildByItem(self, inItem):
        u"""Удаляет потомка.
        
        @param inItem (common.api.tree.treenode.Node) узел-потомок
        @return True, если узел найден, иначе - False
        """
    
        if inItem is None:
            return False

        it = None
        
        try:
            it = self.__children.index(inItem)
            self.__children[it].__parent = None
            self.__children[it] = None
            del self.__children[it]
        except:
            return False
        
        if it + 1 != self.getNumberOfChildren():
            if it - 1 >= 0:
                it -= 1
            self.reindex(it)
        
        return True
    
    
    def _removeChildByIndex(self, index):
        u"""Удаляет потомка.
        
        @param index (int) собстыенный индекс потомка
        @return True, если узел найден, иначе - False
        """
         
        if index < 0 or index >= self.getNumberOfChildren():
            return False

        item = self.__children[index]
        return self.removeChild(item)
    
    
    def removeChild(self, indexOrItem):
        u"""Удаляет потомка.
        
        
        @param indexOrItem (int or common.api.tree.treenode.Node) узел или
        собственный индекс
        
        @return (bool)
        - True, если удаление прошло успешно; 
        - False, если *indexOrItem* не найден в списке потомков или является
        некорректным собственным индексом
        """
        
        if isinstance(indexOrItem, Node):
            return self._removeChildByItem(indexOrItem)
        
        if isinstance(indexOrItem, int):
            return self._removeChildByIndex(indexOrItem)
        
        raise TypeError()
    
    
    def removeAllChildren(self):
        u"""Удаляет всех потомков"""
        
        for ch in self.__children:
            ch.__parent = None
            
        del self.__children[:]
    
 
    def __str__(self, *args, **kwargs):
        res = "{'index': %s, 'data': %s, 'children': [%s]} " % (str(self.index), str(self.data),  ', '.join(str(ch) for ch in self.__children)) 
        return res
    
    
    def reindex(self, it):
        u"""Выполняет переиндексацию потомков с заданного собственного индекса 
        *it*.
        
        Специальный вызов этого метода обычно не требуется, т.к. он вызывается
        при добавлении потомков (см. #addChild, #insertChildAt, #setChildren).  
        
        @param it (int) собственный индекс потомка, с которого начинается
        переиндексация
        """
        
        if it is None:
            return
        
        # если нет предыдущего, значит начало
        if it - 1 < 0:
            self.__children[0].index = self.__index.depthNext()
#             ((children.begin()))->setIndex(index.depthNext())
        
        for i in xrange(it, self.getNumberOfChildren() - 1):
            currentNode = self.__children[i]
            nextNode = self.__children[i+1]
            
            nextNode.index = currentNode.index.widthNext()
            

if __name__ == "__main__":
    root = Node()
    
    print root
    print root.isRoot()
    
    n1 = Node(root, 'd1')
    print root
    print n1
    
    n2 = Node(root, 'd2')
    n3 = Node(root, 'd3')
    
    n21 = Node(n2, 'd21')
    n22 = Node(n2, 'd22')
    
    n31 = Node(n3, 'd31')
    n32 = Node(n3, 'd32')
    n33 = Node(n3, 'd33')
    
    print root
    
    # после detach n3 должен стать рутом и уйти из потомков root,
    # также все потомки n3 должны переиндексироваться
    
    root.detachChild(n3)
    print root
    print n3
    
    # теперь вставляем n3 в n21 в начало
    print n2.insertChildAt(n3, 0)
    print n2
    print n3
    
    print n2.removeChild(0)
    print n2
    print n3
    
