import osgeo.ogr as ogr
import sys, json

if len(sys.argv) == 1: exit(0)
shpFileName = sys.argv[1]


ogrData = ogr.Open(shpFileName)
layer = ogrData[0]

_features = []


for featureIndex in range(0, layer.GetFeatureCount()):
	feature = layer.GetFeature(featureIndex)
	geometry = feature.GetGeometryRef()
	geometryEnvelop = geometry.GetEnvelope()
	geometryType = geometry.GetGeometryName()

	_feature = dict(geometry={})
	_features.append(_feature)
	_geometry = _feature["geometry"]
	_geometry["bbox"] = ( geometryEnvelop[0], geometryEnvelop[2], geometryEnvelop[1], geometryEnvelop[3] )

	geometryJson = json.loads(geometry.ExportToJson())
	_geometry["coordinates"] = geometryJson["coordinates"]
	if geometryType == "LINESTRING": _geometry["type"] = "LineString"
	elif geometryType == "POLYGON": _geometry["type"] = "Polygon"
	elif geometryType == "MULTILINESTRING":_geometry["type"] = "MultiLineString"
	elif geometryType == "MULTIPOLYGON":_geometry["type"] = "MultiPolygon"
	
	name = feature.GetFieldAsString("name")
	highway = feature.GetFieldAsString("highway")
	railway = feature.GetFieldAsString("railway")
	if name or highway or railway:
		_feature["properties"] = {}
		if name: _feature["properties"]["name"] = name
		if highway: _feature["properties"]["highway"] = highway
		if railway: _feature["properties"]["railway"] = railway


outputFile=open("result.js", "w")
outputFile.write("dojo.provide(\"courseApp.data.russia\");\n")
outputFile.write("courseApp.data.russiaGeometries =\n")
outputFile.write(json.dumps(_features, ensure_ascii=False))

