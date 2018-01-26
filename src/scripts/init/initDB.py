# coding: utf-8
'''
Created on 21.02.2012

@author: den
'''
import os
from zipfile import ZipFile
import subprocess
import sys


def checkErrorLog():
    errorlog = open("error.log", mode = "r")
    noerrors = errorlog.read() == ""
    errorlog.close()
    return noerrors


def initDB(args):
    root = os.path.abspath(os.path.curdir)
    errorlog = open("error.log", mode = "w")
    print "Look for sql files in " + root
    scripts = [s for s in os.listdir(root)]
    scripts.sort()
    for script in scripts:
        if not checkErrorLog():
            raise Exception("Errors occur during previous stage. Break script execution. See error.log")
        fname, ext = os.path.splitext(script)
        extracted = False
        try:
            if ext == ".zip":
                print "Unpacking script " + script
                arc = ZipFile(script, mode = "r")
                script = fname + ".sql"
                sql = open(script, mode = "w")
                sql.write(arc.read(script))
                sql.close()
                arc.close()
                extracted = True
            fname, ext = os.path.splitext(script)
            if ext == ".sql":
                print "Running script " + script
                if fname.startswith("1"):
                    db = "-d master"
                else:
                    db = "-d showcase"
                command = "sqlcmd.exe "
                for arg in args:
                    command += arg + " "
                command += db + " -i " + script
                subprocess.call(command, stderr = errorlog)
        finally:
            if extracted:
                os.remove(script)
    errorlog.close()
    if checkErrorLog():
        os.remove("error.log")
    print "DB initialization done"

if __name__ == '__main__':
    if len(sys.argv) > 1:
        initDB(sys.argv[1:])
    else:
        initDB(["-U sa", "-P F708420Dx", "-S CASTLE\\R2"])
