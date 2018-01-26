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
    print "looking for not ant packages in " + distRoot
    dists = [s for s in os.listdir(distRoot)]
    for dist in dists:
        if dist[0] == ".":
            continue;
        if dist.startswith("ant"):
            continue
        distPath = distRoot + "/" + dist
        if os.path.isdir(distPath):
            continue
        if not distPath.endswith(".zip"):
            continue
        print "Unpacking lib " + dist
        arc = ZipFile(distPath, mode = "r")
        arc.extractall(libRoot)
    print "packages initialization done!"

if __name__ == '__main__':
    print "Initializing external components..."
    initdirs()
    prepare()
    initPackages()
    print "Initialization done!"

