# coding: UTF-8
u'''@package common.api.tree.treeindex Модуль содержит класс индекса узла
дерева

Created on 11 сент. 2015 г.

@author tugushev.r
'''

from copy import deepcopy
from itertools import izip
from __builtin__ import cmp

class TreeIndex(object):
    u"""Описывает индекс узла дерева.
 
    Минимальным допустимым собственным значением индекса является 0.
    """
    
    def __init__(self, param=None):
        u"""Конструктор. Создаёт пустой индекс.
        @param param (string) строковое представлени индекса. Например:
        '0.1.4.1'
        @param param (list of int) представление в виде списка. Например:
        [0, 1, 4, 1]
        @param param (commonapi.tree.treeindex.TreeIndex) другой индекс 
        """
        self.__index = []
        
        if param is None:
            return
        
        if isinstance(param, basestring):
            self.__index = [int(s) for s in param.split('.')]
            return
        
        if isinstance(param, list):
            self.__index = param
            return
        
        if isinstance(param, TreeIndex):
            self.copy(param)
            return
        
        raise TypeError('param has to be one of: string (ex: "0.1.4.1"), list of int (ex: [0, 1, 4, 1]) or another TreeIndex object')
        
        
    def copy(self, inFrom):
        """Копирует другой индекс.
        @param inFrom (commonapi.tree.treeindex.TreeIndex) копируемый индекс
        @throw TypeError если *inForm* не является индексом 
        """
        if not isinstance(inFrom, TreeIndex):
            raise TypeError()
        
        self.__index = inFrom.getValue()


    def getParent(self):
        u"""Фактически является декрементом "в глубину".
        
        @return индекс родителя. Корневой индекс возвращает пустой индекс
        """
        
        if not self.isValid() or self.isRoot():
            return TreeIndex()

        i = TreeIndex(self)
        i.__index = i.__index[:-1]
        
        return i


    def getSelfIndex(self):
        u"""Возвращает собственный индекс.
        
        Индекс элемента относительно элементов своего уровня.
        Корневой индекс возвращает 0
        
        @return int  
        """
        
        return self.__index[-1] if self.__index else -1 


    def widthInc(self):
        u"""Увеличивает собственный индекс "в ширину" на 1.
        Корневой индекс возвращает сам себя, для него разрешён только инкремент
        "в глубину"
     
        @return  ссылка на себя
        """
        if not self.isValid() or self.isRoot():
            return self;
        
        self.__index[-1] += 1
        
        return self


    def widthDec(self):
        u"""Уменьшает собственный индекс "в ширину" на 1.
     
        Если при уменьшении должно получиться значение меньше -1, то оно 
        остаётся -1.
         
        Корневой индекс возвращает сам себя, для него разрешён только инкремент
        "в глубину"
        @return  ссылка на себя
        @throw Exception если индекс является некорректым
        
        @warning
        При значениях меньше -1 индекс считается недопустимым и не должен
        использоваться
        """
        
        if not self.isValid():
            raise Exception("Tree Index is not valid!");
        
        if self.isRoot():
            return self;

        if self.getSelfIndex() - 1 < -1:
            return self;

        self.__index[-1] -=1
        
        return self

   
    def depthInc(self):
        u"""Увеличивает собственный индекс "в глубину" на 1.
        Например, если исходный индекс 1.2.2, то результатом операции будет
        1.2.2.1
        Если индекс недопустимый, то возвращает ссылку на себя и инкремент не
        выполняется.
         
        @return  ссылка на себя
        """
        
        # если пустой индекс, то создаём корневой индекс
        if not self.__index:
            self.__index.append(0)
            return self
        
        if not self.isValid():
            return self

        self.__index.append(0)

        return self;


    def widthNext(self):
        u"""То же самое, что и #widthInc, только не изменяет собственное 
        значение
          
        @return (commonapi.tree.treeindex.TreeIndex) следующий индекс на своём
        уровне иерархии
        """
        
        i = TreeIndex(self)
        i.widthInc()
        return i


    def setWidth(self, inSelfIndex):
        """Изменяет собственный индекс.
        
        Собственный индекс не может быть < -1.
        Если индекс некорректный или корневой, то изменение собственного
        индекса не происходит, и просто возвращается ссылка на себя
        
        @param inSelfIndex (int) новый собственный индекс 
        @return (commonapi.tree.treeindex.TreeIndex) ссылка на себя
        """
        if not self.isValid() or self.isRoot():
            return self
        
        selfIndex = -1 if inSelfIndex < -1 else inSelfIndex 
                    
        self.__index[-1] = selfIndex

        return self


    def depthNext(self):
        u"""То же самое, что и #depthInc, только не изменяет собственное 
        значение
        @return (commonapi.tree.treeindex.TreeIndex) первый индекс на
        следующем уровне иерархии
        """
        
        i = TreeIndex(self)
        i.depthInc()
        return i


    
    def rank(self): 
        u"""Порядок индекса.
        
        Порядком индекса называется его уровень в иерархии, т.е. в глубину,
        Т.о., чем младше индекс, тем больше его порядок
        @return  порядок индекса
        
        @note Порядок индексов элементов у разных родителей может совпадать,
        т.к. эта метрика не учитывает собственный индекс
        """
        return len(self.__index) 
    

    def getValue(self):
        """Возвращает копию представления индекса в виде списка.
        @return list of int
        """
        return deepcopy(self.__index)
    
    
    def __str__(self, *args, **kwargs):
        """Оператор str().
        @return (string) код Дьюи без учёта корневого элемента.
        Для некорректного индекса возвращается пустая строка.
        Для корневого - "root"
        """
        rank = self.rank()
        if (rank < 1):
            return "";
        if (rank == 1):
            return "root";

        res = ".".join(str(v) for v in self.__index[1:])
        return res


    def isValid(self):
        u"""@return  (bool) True, если корректный индекс, иначе - False"""
        
        if not self.__index:
            return False

        return self.getSelfIndex() >= 0


    def isRoot(self):
        """@return (bool) True, если индекс является корневым, иначе - False"""
        if self.rank() != 1:
            return False
        
        return self.getSelfIndex() == 0
    
    
    def clear(self):
        """Очищает индекс; индекс становится некорректным"""
        self.__index = []
    
    
    def __cmp__(self, other):
        """Оператор сравнения.
        
        - Если ранги индексов не равны, то меньшим является индекс с бОльльшим
        рангом.
        - Корневые индексы равны, независимо от того.
        - Если ранги равны, то индексы считаются равными, если равны все уровни
        иерархии. Если хотя бы один уровень, начиная с нулевого и до значения 
        собственного индекса, меньше, чем соответствующий уровень другого 
        индекса, то индекс считается меньшим. Например:
        <br>
        1.1.2 == 1.1.2 ==> True
        1.1.2.1 < 1.1.2 ==> True, т.к. ранг 1.1.2.1 больше ранга 1.1.2 (4 > 3)
        1.2.1 > 1.1.2 ==> True, т.к. 1.2.\<...> > 1.1.\<...> 
        
        @return (int) 
        * -1, если индекс меньше *other* 
        * 0, если равен
        * 1, если больше
        
        """
        if self.rank() != other.rank():
            return cmp(other.rank(), self.rank())
            
        if self.isRoot() and other.isRoot():
            return 0
        
        for t in izip(self.__index, other.__index):
            cmpRes = cmp(*t)
            if cmpRes != 0:
                return cmpRes
        
        return 0
        
        
# if __name__ == "__main__":
#     
#     i1 = TreeIndex()
#     
#     flag = (not i1.isRoot())
#     flag = (not i1.isValid())
#     
#     i1.depthInc()
#     
#     flag = i1.isRoot()
#     flag = i1.isValid()
#     
#     print i1
#     
#     print i1.depthInc()
#     print i1.widthInc()
#     print i1.widthInc()
#     print i1.depthInc().widthInc()
#     
#     print i1.getParent()
#     
#     print i1.depthNext().widthInc(), '\t', i1
#     print i1.widthDec()
# 
#     i2 = TreeIndex()
#     i2.copy(i1)
#     
#     print i1 == i2
#     i3 = i2.depthNext()
#     
#     print i3 > i2
#     print i3 < i2

    
#     /**Оператор сравнения.
#     *
#     * Равными считаются индексы, совпадающие с точностью до иерархии и собственного индекса.
#     *
#     * Например: 0.1.2.1 = 0.1.2.1 и 0.1.2.2 != 0.1.2.1 и 0.1.2.2. != 0.1.2.2.1
#     */
#     public boolean equals(TreeIndex obj) {
#         return this.index.equals(obj.index);
#     }
#     
#     /**Выполняет сравнение индексов поэлементно.
#     *
#     * Индекс считается меньше индекса inOther, если:
#     *  - ранг inOther меньше или
#     *  - если ранги одинаковые, то индекс одного из родителей больше inOther или
#     *  - если один родитель, то собственный индекс меньше собственного индекса inOther
#     */
#     public boolean lt(final TreeIndex inOther) {
#         if (inOther.rank() != rank()) 
#             return inOther.rank() < rank();
#             
#         if (inOther.isRoot() && isRoot())
#             return false;
# 
#         for (int i = 0; i < inOther.index.size(); i++)
#             if (inOther.index.get(i) > this.index.get(i))
#                 return true;
# 
#         return false;
#     }
#     
#     public boolean gt(final TreeIndex obj) {
#         return !this.equals(obj) && !this.lt(obj);
#     }
#     
#     @Override
#     protected TreeIndex clone() throws CloneNotSupportedException {
#         // TODO Auto-generated method stub
#         return new TreeIndex(this);
#         
#     }
# 
# };