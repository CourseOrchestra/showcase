# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.celesta.showcase.utils import XMLJSONConverter


def pluginDataAndSettings(context, main, add, filterinfo, session, elementId):
    print 'Get plugin data and setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId    
    
    data = u'''
    <items>
            <item id="1" name="La&quot;zy load item" leaf="false" checked="false"/>
            <item id="2" name="Расходование денежных средств"                          column1="Значение2"  column2="solutions/default/resources/imagesingrid/test.jpg" cls="folder">
                <children>
                    <item id="21" name="Оплата поставщикам за товар" leaf="true"       column1="Значение21" column2="solutions/default/resources/imagesingrid/TreeGridLeaf.png"  attr1="a" checked="false"/>
                    <item id="22" name="Расходы по таможенному оформлению" leaf="true" column1="Значение22" column2="solutions/default/resources/imagesingrid/test.jpg"  attr1="b" checked="false"/>
                    <item id="23" name="Расходы  на аренду, коммунальные услуги"       column1="Значение3"  column2="solutions/default/resources/imagesingrid/TreeGridLeaf.png"                    cls="folder">
                        <children>
                            <item id="231" name="Аренда недвижимости" leaf="true" attr1="c" checked="false"/>
                            <item id="232" name="Коммунальные услуги" leaf="true" attr1="d" checked="false"/>
                            <item id="233" name="Расходы на содержание сооружений и оборудования" leaf="true" attr1="e" checked="false"/>
                        </children>
                    </item>
                    <item id="24" name="Расходы на персонал" cls="folder">
                        <children>
                            <item id="241" name="Расходы на оплату труда" leaf="true" attr1="f" checked="false"/>
                            <item id="242" name="Страхование персонала, мед.услуги" leaf="true" attr1="g" checked="false"/>
                            <item id="243" name="Матпомощь, подарки" leaf="true" attr1="m" checked="false"/>
                        </children>
                    </item>
                    <item id="25" name="Услуги связи" leaf="true" attr1="k" checked="false"/>
                    <item id="26" name="Маркетинг и реклама" leaf="true" attr1="l" checked="false"/>
                    <item id="27" name="Обеспечение безопасности" leaf="true" attr1="n" checked="false"/>
                </children>   
            </item>
            <item id="3" name="Поступление денежных средств" cls="folder">
                <children>
                    <item id="31" name="Доход от продажи товара" leaf="true" attr1="o" checked="false"/>
                    <item id="32" name="Расходы по таможенному оформлению" leaf="true" attr1="p" checked="false"/>
                    <item id="33" name="Возврат денежных средств от контрагентов" leaf="true" attr1="r" checked="false"/>                
                    <item id="34" name="Проценты по депозитам" leaf="true" attr1="s" checked="false"/>
                </children>   
            </item>
    </items>'''
    settings = u'''
    <properties>
    </properties>
    '''
    
#    context.warning('dd2');
#    context.message('dd1', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
#    context.error('dd4', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
    
    #res = JythonDTO(data, settings, UserMessageFactory().build(555, u"Плагин (DataAndSettings) успешно построен из Celesta"))
    
    json = XMLJSONConverter.xmlToJson(data)
    print 'json "%s".' % json
    
    xml = XMLJSONConverter.jsonToXml(json)
    print 'xml "%s".' % xml
    
    json = XMLJSONConverter.xmlToJson(xml)
    print 'json2 "%s".' % json
    
    
    res = JythonDTO(data, settings)    
    return res

def pluginData(context, main, add, filterinfo, session, params):
    print 'Get plugin data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'params "%s".' % params
    
    data = u'''
    <items>
        <item id="child1" name="Lazy loaded item child1" leaf="true"/>
        <item id="child2" name="Lazy loaded item child2" leaf="true"/>
    </items>'''
    
    

#    context.message('dd11', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
#    context.warning('dd22');    
    context.error('dd44', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
    
    
    #res = JythonDTO(data, UserMessageFactory().build(555, u"Плагин (Data) успешно построен из Celesta"))
    res = JythonDTO(data)
    
    
    return res



