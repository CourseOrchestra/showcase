# coding: utf-8

import json
from ru.curs.showcase.app.server.rest import JythonRestResult

def entryProc(context, requestType, requestUrl, requestData, requestHeaders, urlParams, clientIP):
    myjson = {"a": {"b":"bbb", "c":"ccc"}}
    return JythonRestResult(json.dumps(myjson), 200)