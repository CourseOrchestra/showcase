# coding: Cp1251
'''
Created on 21.02.2012

@author: den
'''
import os
from zipfile import ZipFile
import shutil
import logging
import _winreg
import sys

log = logging.getLogger("init")

targetDir = "..\\devtime"
distRoot = os.path.abspath(os.path.curdir)
libRoot = os.path.abspath(os.path.curdir + "\\" + targetDir)

def logError(func, path, exc_info):
    log.error(unicode(str(exc_info), "Cp1251"))

def initdirs():
    if not os.path.exists(libRoot):
        print "creating devtime dir: " + libRoot
        os.mkdir(libRoot)
    stream = open(libRoot + "/error.log", "w")
    logging.basicConfig(level = logging.INFO, stream = stream)

def prepare():
    print "cleaning devtime dir: " + libRoot
    dists = [s for s in os.listdir(libRoot)]
    for dist in dists:
        dirPath = libRoot + "/" + dist
        if os.path.isdir(dirPath):
            shutil.rmtree(dirPath, ignore_errors = False, onerror = logError)
    print "devtime dir cleaning done!"

def initPackages():
    print "looking for ant packages in " + distRoot
    dists = [s for s in os.listdir(distRoot)]
    for dist in dists:
        if dist[0] == ".":
            continue;
        distPath = distRoot + "/" + dist
        if os.path.isdir(distPath):
            continue
        if not distPath.endswith(".zip"):
            continue
        print "Unpacking lib " + dist
        arc = ZipFile(distPath, mode = "r")
        arc.extractall(libRoot)
    print "packages initialization done!"

def addLibs():
    print "adding libs in ant packages..."
    dists = [s for s in os.listdir(distRoot)]
    for dist in dists:
        distPath = distRoot + "/" + dist
        if os.path.isdir(distPath):
            if dist[0] == ".":
                continue;
            toPath = libRoot + "/" + dist.replace("___", "/")
            if not os.path.exists(toPath):
                os.mkdir(toPath)
            files = [s for s in os.listdir(distPath)]
            for file in files:
                if file[0] == ".":
                    continue;
                shutil.copy(distPath + "/" + file, toPath + "/" + file)
    print "libs added to ant packages..."

def setAntHomeAndPath():
    print "Checking for env vars..."
    antRoot = libRoot + "\\ant"
    envKey = "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"
    try:
        key = _winreg.OpenKey(_winreg.HKEY_LOCAL_MACHINE, envKey, 0, _winreg.KEY_ALL_ACCESS)
        try:
            oldValue = _winreg.QueryValueEx(key, "ANT_HOME")[0]
            log.info("old ANT_HOME registry value is " + str(oldValue))
        except:
            pass
        _winreg.SetValueEx(key, "ANT_HOME", 0, _winreg.REG_SZ, antRoot)
        if os.environ['PATH'].find(antRoot) == -1:
            oldValue = _winreg.QueryValueEx(key, "PATH")[0]
            log.info("old path registry value is " + str(oldValue))
            _winreg.SetValueEx(key, "PATH", 0, _winreg.REG_SZ, antRoot + "\\bin" + ";" + oldValue)
    finally:
        _winreg.CloseKey(key)


if __name__ == '__main__':
    print "Initializing ant..."
    initdirs()
    prepare()
    initPackages()
    addLibs()
    if (len(sys.argv) == 1) or (sys.argv[1] != 'noreg'):
        setAntHomeAndPath()
    print "Initialization done!"

