# coding: utf-8
import ru.curs.lyra.BasicCardForm as BasicCardForm

class CardForm(BasicCardForm):
    u'''Basic class for a card form'''
    def __init__(self, context):
        BasicCardForm.__init__(self, context)
    
    def setContext(self, session, main, add, elemetId):
        self.session = session
        self.main = main
        self.add = add
        self.elemetId = elemetId
        
        
    def _beforeShow(self, context):
        '''Override this method to implement some actions 
        to be performed before the grid is shown or refreshed, 
        e. g. position cursor to a certain record'''
        pass
        
        
    typedict = {'INT': 'int',
            'REAL': 'decimal',
            'VARCHAR': 'string',
            'TEXT': 'string',
            'BLOB': 'string',
            'DATETIME': 'dateTime',
            'BIT': 'boolean'
            }
    hdr = '''<?xml-stylesheet href="xsltforms/xsltforms.xsl" type="text/xsl"?>
    <!--?xsltforms-options debug="yes"?-->
    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:ev="http://www.w3.org/2001/xml-events">
       <head>
          <title>Lyra form</title>
          <xf:model id="xformId_mainModel" xmlns="">
            '''
    body1 = '''
   </head>
   <body>
   <div>
    <xf:trigger style="height: 32px; width: 32px; padding-left: 0px; padding-right: 0px;">
        <img src="solutions/default/resources/first.png" />
         <xf:label></xf:label><xf:action ev:event="DOMActivate">
                    <xf:send submission="first"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
          <img src="solutions/default/resources/prev.png" />
         <xf:label></xf:label>
         <xf:action ev:event="DOMActivate">
                    <xf:send submission="previous"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
         <img src="solutions/default/resources/next.png" />
         <xf:label></xf:label>
        <xf:action ev:event="DOMActivate">
                    <xf:send submission="next"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
          <img src="solutions/default/resources/last.png" />
         <xf:label></xf:label>
         <xf:action ev:event="DOMActivate">
                    <xf:send submission="last"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
       <img src="solutions/default/resources/add.png" />
         <xf:label></xf:label>
        <xf:action ev:event="DOMActivate">
                    <xf:send submission="new"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
        <img src="solutions/default/resources/remove.png" />
         <xf:label></xf:label>
        <xf:action ev:event="DOMActivate">
                    <xf:send submission="del"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
      <img src="solutions/default/resources/revert.png" />
         <xf:label></xf:label>
        <xf:action ev:event="DOMActivate">
                    <xf:send submission="revert"/>
                                    </xf:action>
      </xf:trigger>
      <xf:trigger>
      <img src="solutions/default/resources/save.png" />
         <xf:label></xf:label>
        <xf:action ev:event="DOMActivate">
                    <xf:send submission="save"/>
                                    </xf:action>
      </xf:trigger>

      </div>
'''

    body2 = '''
   </body>
</html>
'''

    def _buildBinds(self, formTemplate):
        formTemplate += '''
            <xf:bind nodeset="*[@type='BIT']" type="xs:boolean" />
            <xf:bind nodeset="*[@type='INT']" type="xs:int" />
            <xf:bind nodeset="*[@type='REAL']" type="xs:decimal" />
            <xf:bind nodeset="*[@type='DATETIME']" type="xs:dateTime" />
            <xf:bind nodeset="*[@type='VARCHAR']" type="xs:string"
            constraint="fn:string-length(.) &lt;= @scale"/>
            <xf:bind nodeset="*[@required='true']" required="true()"/>
            '''
        return formTemplate
    
    def _buildControls(self, formTemplate):
        
        for c in self.getFieldsMeta().values():
            if c.isVisible():
                formTemplate += """<div class="baseInput200 break">\n"""
                tag = 'textarea' if c.getType().toString() == 'TEXT' else 'input'
                formTemplate += '''  <xf:%s ref="instance('xformId_mainInstance')/%s">\n''' % (tag, c.getName())
                formTemplate += '    <xf:label>%s</xf:label>\n' % c.getCaption()
                formTemplate += '  </xf:%s></div>\n' % tag

        return formTemplate


    def _buildForm(self):
        formTemplate = self.hdr
        formTemplate += '<xf:instance xmlns="" id="xformId_mainInstance" />'
        # БИНДЫ ТУТ
        formTemplate = self._buildBinds(formTemplate)

        formTemplate += '''<xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionFirst.cl" id="first" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>

        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionPrev.cl" id="previous" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>

        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionNext.cl" id="next" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>

        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionLast.cl" id="last" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>

        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionNew.cl" id="new" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>

        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionDel.cl" id="del" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>
        
        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionRevert.cl" id="revert" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>
        
        <xf:submission action="secured/submit?proc=lyra.lyraplayer.submissionSave.cl" id="save" method="post" instance="xformId_mainInstance" ref="instance('xformId_mainInstance')" replace="instance">
        </xf:submission>
        '''

        formTemplate += "</xf:model>\n"

        formTemplate += self.body1
        
        formTemplate = self._buildControls(formTemplate)
        
        formTemplate += self.body2
        return formTemplate

    def getActions(self):
        return u'<properties/>'
    
    def _beforeSending(self, c):
        '''Override this method to implement some actions 
        to be performed before data XML serialization and sending to client'''
        pass

    def _afterReceiving(self, c):
        '''Override this method to implement some actions 
        to be performed after XML data being received from client and deserialized'''
        pass
