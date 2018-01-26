# coding: utf-8

from suds.client import Client


def mainproc():
    request = '''<command type="getDP" param="b.xml"/>'''
    url = 'http://localhost/Showcase/forall/webservices?wsdl'
    client = Client(url)
    print client
    print client.service.handle(request = request, procName = "ws/GetFile.py")

if __name__ == "__main__":
    mainproc()
