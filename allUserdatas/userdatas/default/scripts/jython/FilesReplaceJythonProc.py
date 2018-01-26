# coding=UTF-8
# don't work in jython # -*- coding UTF-8 -*-
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.runtime import AppInfoSingleton
from shutil import copyfile


class FilesReplaceJythonProc(JythonProc):
    def execute(self, context):
        self.context = context
        if (not context.getAdditional()):
            raise Exception("не нравится мне этот контекст!")
        print "add context is %s" % (context.getAdditional())
        root = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\xslttransforms\\"
        copyfile(root + context.getAdditional(), root + "active_bal.xsl")
