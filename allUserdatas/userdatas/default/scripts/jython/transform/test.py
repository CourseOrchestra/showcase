# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.runtime import AppInfoSingleton

# init vars
main = None
add = None
session = None
filterContext = None
elementId = None


class test(JythonProc):
    def getRawData(self, context, element):
        global main, add, session, filterContext, elementId
        if context.getMain():
            main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = element
        return mainproc()


def mainproc():
    root = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\xslttransforms\\"
    transform = open(root + "xformsxslttransformation_test.xsl", "r")
    return unicode(transform.read(), "utf-8")

if __name__ == "__main__":
    mainproc()
