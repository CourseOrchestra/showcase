from common.navigator import navigatorsParts, seriesNavigator


isInitContext = True
try:
    import initcontext
except ImportError:
    isInitContext = False

if isInitContext:
    from common.htmlhints.htmlHintsInit import permInit
    context = initcontext()
      
    if not isinstance(context, (str, unicode)):
        permInit(context)
    
navigatorsParts['numberSeries'] = seriesNavigator
