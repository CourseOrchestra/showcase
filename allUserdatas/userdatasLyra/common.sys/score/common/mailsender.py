#   coding=utf-8

from common.grainssettings import SettingsManager

import xml.etree.ElementTree
import StringIO
import re
import smtplib
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText
from email.Utils import formatdate
from email.header import Header


def test(context):
    class DummyFlute:

        def __init__(self):
            self.params = """<letter>
    <header>
        <template>template3.txt</template><!-- Имя шаблона -->
        <to>iponomarev@mail.ru</to> <!-- Далеее идёт любая комбинация тэгов to и cc, в которых перечислены получатели -->
        <cc>informer@rambler.ru</cc>
    </header>
    <body>
        <field name="id">1123</field> <!-- id сообщения -->
        <field name="uid">1123</field> <!-- сквозной номер сообщения -->
        <field name="datecreated">2012-01-01</field><!-- Дата создания (в любом формате, на самом деле, меня интересует лишь текст) -->
        <repeat id="attachments"> <!-- В данном примере повторяющийся блок состоит всего из одного-единственного поля, поэтому код кажется избыточным.
        однако, в каждом тэге repeat может быть определено сколько угодно полей, а также - потенциально - другие тэги repeat...-->
            <field name="attachment">foo.doc</field>
        </repeat>
        <repeat id="attachments">
            <field name="attachment">bar.doc</field>
        </repeat>
        <field name="url">http://www.yandex.ru</field>
    </body>
</letter>
"""
    dummyFlute = DummyFlute()
    sendmail(context, dummyFlute)
    print 'test run!'


class MailsenderSettings(object):
    DEFAULT_PORT = 25

    def __init__(self, templatespath=None, smtphost=None, port=None, mailfrom=None, is_auth=None, login=None, password=None):
        self.templatespath = templatespath
        self.smtphost = smtphost
        self.port = port
        self.mailfrom = mailfrom
        self.is_auth = is_auth
        self.login = login
        self.password = password

    @property
    def port(self):
        return self._port or self.DEFAULT_PORT

    @port.setter
    def port(self, value):
        if value:
            value = int(value)
        else:
            value = None

        self._port = value


    @classmethod
    def fromConfig(cls, settings_manager):

        obj = cls()

        obj.templatespath = settings_manager.getGrainSettings('mailsender/templatespath', 'common')[0]
        obj.mailfrom = settings_manager.getGrainSettings('mailsender/mailfrom', 'common')[0]
        obj.smtphost = settings_manager.getGrainSettings('mailsender/smtphost', 'common')[0]
        obj.port = settings_manager.getGrainSettings('mailsender/port', 'common')[0]
        obj.login = settings_manager.getGrainSettings('mailsender/login', 'common')[0]
        is_auth = settings_manager.getGrainSettings('mailsender/isauth', 'common')[0]

        if not is_auth:
            is_auth = False
        else:
            if is_auth in ('True', 'true'):
                is_auth = True
            elif is_auth in ('False', 'false'):
                is_auth = False
            else:
                ValueError('Unexpected is_auth property value: {}. One of True/true or False/false expected.')

        obj.is_auth = is_auth

        obj.password = settings_manager.getGrainSettings('mailsender/password', 'common')[0]

        return obj

    def __repr__(self):
        return u'{cls}({args})'.format(type(self).__name__, self.__dict__)


# 2. Now we are parsing template and preparing a message subject and body
class Parser(object):
    BODY = 1
    REPEATBLOCK = 2
    REPEMPTYBLOCK = 3
    IFDEFBLOCK = 4
    ELSEBLOCK = 5

    def __init__(self, data):
        self._data = data
        self.subject = ''
        self.body = ''
        self.__reset()
        self.PARSEMAP = {Parser.BODY: lambda line: self.__parsebody(line),
                         Parser.REPEATBLOCK: lambda line: self.__parserepeat(line),
                         Parser.REPEMPTYBLOCK: lambda line: self.__parseempty(line),
                         Parser.IFDEFBLOCK: lambda line: self.__parseifdef(line),
                         Parser.ELSEBLOCK: lambda line: self.__parseelse(line)}

    def parseline(self, line):
        # Every remark is skipped
        m = re.search("^REM:", line)
        if m is not None:
            return
        self.PARSEMAP[self.state](line)

    def __reset(self):
        self.curdata = self._data
        self.state = Parser.BODY
        self.ifblock = []
        self.elseblock = []
        self.ifValue = False

    def __finalizeRepeat(self):
        if len(self.curdata) > 0:
            for c in self.curdata:
                for l in self.ifblock:
                    self.body += (l % c)
        else:
            for l in self.elseblock:
                self.body += (l % self._data)
        self.__reset()

    def __finalizeIf(self):
        block = self.ifblock if self.ifValue else self.elseblock
        for l in block:
            self.body += (l % self.curdata)
        self.__reset()

    def __parsebody(self, line):
        m = re.search("^SUBJECT: *(.*)", line)
        if m is not None:
            self.subject = m.group(1) % self._data
            return
        m = re.search("^REPEAT\(([^)]+)\):", line)
        if m is not None:
            if m.group(1) in self._data.keys():
                self.curdata = self._data[m.group(1)]
            else:
                self.curdata = []
            self.state = Parser.REPEATBLOCK
            return
        m = re.search("^IFDEF\(([^)]+)\):", line)
        if m is not None:
            self.ifValue = m.group(1) in self._data.keys()
            self.state = Parser.IFDEFBLOCK
            return

        self.body += (line % self.curdata)

    def __parserepeat(self, line):
        m = re.search("^END:", line)
        if m is not None:
            self.__finalizeRepeat()
            return
        m = re.search("^EMPTY:", line)
        if m is not None:
            self.state = Parser.REPEMPTYBLOCK
            return

        self.ifblock.append(line)

    def __parseempty(self, line):
        m = re.search("^END:", line)
        if m is not None:
            self.__finalizeRepeat()
            return
        self.elseblock.append(line)

    def __parseifdef(self, line):
        m = re.search("^ELSE:", line)
        if m is not None:
            self.state = Parser.ELSEBLOCK
            return
        m = re.search("^END:", line)
        if m is not None:
            self.__finalizeIf()
            return
        self.ifblock.append(line)

    def __parseelse(self, line):
        m = re.search("^END:", line)
        if m is not None:
            self.__finalizeIf()
            return
        self.elseblock.append(line)

    def parse(self, template_path):
        if not template_path:
            raise ValueError('Template path is not set!')

        template = open(template_path, 'r')
        try:
            for line in template.readlines():
                self.parseline(line)
        finally:
            template.close()


class MailParams(object):
    def __init__(self, template_path, mailfrom=None, to=None, cc=None, bcc=None):
        self.template = template_path
        self.mailfrom = mailfrom
        self.to = to or []
        self.cc = cc or []
        self.bcc = bcc or []

        self._data = {}
        self._curdata = self._data

    @property
    def data(self):
        return self._data

    @property
    def all_recipients(self):
        """(list) Все получатели одним списком: to, cc, bcc
        """
        return self.to + self.cc + self.bcc

    @classmethod
    def fromXML(cls, xml_str, cfg):
        """Создаёт объект MailParams из XML-строки.

        Параметры:
            - xml_str (string) - параметры в формате XML
            - cfg (common.mailsender.MailsenderSettings) - настройки
        """

        xmlcontext = xml.etree.ElementTree.iterparse(StringIO.StringIO(xml_str), ['start', 'end'])

        o = cls(None)

        for evttype, e in xmlcontext:
            tagname = e.tag
            if evttype == 'start':
                if tagname == 'repeat':
                    o._curdata = {}
            elif evttype == 'end':
                if tagname == "template":
                    o.template = cfg.templatespath + e.text
                elif tagname == "to":
                    o.to.append(e.text)
                elif tagname == "cc":
                    o.cc.append(e.text)
                elif tagname == "bcc":
                    o.bcc.append(e.text)
                elif tagname == "from":
                    o.mailfrom = "\"%s\" <%s>" % (Header(e.text, 'utf-8'), cfg.mailfrom)
                elif tagname == "field":
                    o._curdata[e.attrib['name']] = '' if e.text is None else e.text.encode('utf-8')
                elif tagname == "repeat":
                    repeatid = e.attrib['id']
                    if repeatid not in o._data.keys():
                        o._data[repeatid] = []
                    o._data[repeatid].append(o._curdata)
                    o._curdata = o._data

        if not o.mailfrom and cfg.mailfrom:
            o.mailfrom = cfg.mailfrom

        return o

def sendmail(context, flute):
    settingsObject = SettingsManager(context)

    cfg = MailsenderSettings.fromConfig(settingsObject)

    # to = []
    # cc = []
    # bcc = []
    #
    # data = {}
    # curdata = data
    #
    # mailfrom = cfg.mailfrom
    # template = None

    params_str = flute.params.encode('utf-16')
    mp = MailParams.fromXML(params_str, cfg)

    # # 1. Parse XML and make from it a convenient data sctructure
    # xmlcontext = xml.etree.ElementTree.iterparse(StringIO.StringIO(flute.params.encode('utf-16')), ['start', 'end'])
    #
    # for evttype, e in xmlcontext:
    #     tagname = e.tag
    #     if evttype == 'start':
    #         if tagname == 'repeat':
    #             curdata = {}
    #     elif evttype == 'end':
    #         if tagname == "template":
    #             template = cfg.templatespath + e.text
    #         elif tagname == "to":
    #             to.append(e.text)
    #         elif tagname == "cc":
    #             cc.append(e.text)
    #         elif tagname == "bcc":
    #             bcc.append(e.text)
    #         elif tagname == "from":
    #             mailfrom = "\"%s\" <%s>" % (Header(e.text, 'utf-8'), cfg.mailfrom)
    #         elif tagname == "field":
    #             curdata[e.attrib['name']] = '' if e.text is None else e.text.encode('utf-8')
    #         elif tagname == "repeat":
    #             repeatid = e.attrib['id']
    #             if not (repeatid in data.keys()):
    #                 data[repeatid] = []
    #             data[repeatid].append(curdata)
    #             curdata = data

    parser = Parser(mp.data)
    parser.parse(mp.template)

    # 3. All done, sending message
    msg = MIMEMultipart()
    msg["To"] = ",".join(mp.to)
    msg["CC"] = ",".join(mp.cc)
    msg["From"] = mp.mailfrom
    msg["Subject"] = Header(parser.subject, 'utf-8')
    msg['Date'] = formatdate(localtime=True)

    # attach a message
    part1 = MIMEText(parser.body, 'plain', 'utf-8')
    msg.attach(part1)
    server = smtplib.SMTP(cfg.smtphost, cfg.port)
    # server.set_debuglevel(1)  # connection log
    if cfg.is_auth:
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(cfg.login, cfg.password)
    try:
        server.sendmail(mp.mailfrom, mp.all_recipients, msg.as_string())
    finally:
        server.close()
