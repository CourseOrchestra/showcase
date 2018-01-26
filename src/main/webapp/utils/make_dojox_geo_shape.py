import osgeo.ogr as ogr
import sys, json

def uniteFeatures(newFeatureGeometry, featureJson=None):
    newFeatureEnvelope = newFeatureGeometry.GetEnvelope()
    bbox = ( newFeatureEnvelope[0], newFeatureEnvelope[2], newFeatureEnvelope[1], newFeatureEnvelope[3] )
    coordinates = []
    if featureJson:
        featureJson["type"] = "MultiPolygon"
        # bbox1
        bbox1 = featureJson["bbox"]
        left1 = bbox1[0]
        bottom1 = bbox1[1]
        right1 = bbox1[2]
        top1 = bbox1[3]
        # bbox2
        bbox2 = bbox
        left2 = bbox2[0]
        bottom2 = bbox2[1]
        right2 = bbox2[2]
        top2 = bbox2[3]
        # bbox (union of bbox1 and bbox2)
        left = min(left1,left2)
        bottom = min(bottom1,bottom2)
        right = max(right1,right2)
        top = max(top1,top2)
        bbox = (left, bottom, right, top)
        # redefine coordinates
        wholeCoordinates = featureJson["coordinates"]
        if isinstance(wholeCoordinates[0][0], list):
            wholeCoordinates.append(coordinates)
        else:
            wholeCoordinates = [wholeCoordinates, coordinates]
    else:
        featureJson = dict(type="Polygon")
        wholeCoordinates = coordinates
    # calculate center
    center = (bbox[0]+bbox[2]/2., bbox[1]+bbox[3]/2.)
    #center = featureGeometry.Centroid().GetPoint(0)
    # compose geometry
    newFeatureGeometryJson = json.loads(newFeatureGeometry.ExportToJson())
    for lineStringCoords in newFeatureGeometryJson["coordinates"]:
        lineStringArray = []
        for point in lineStringCoords:
            lineStringArray.append( (point[0],point[1]) )
        coordinates.append(lineStringArray)
    
    featureJson["center"] = center
    featureJson["bbox"] = bbox
    featureJson["coordinates"] = wholeCoordinates
    return featureJson

if len(sys.argv) == 1: exit(0)
shpFileName = sys.argv[1]


ogrData = ogr.Open(shpFileName)
layer = ogrData[0]

#layerExtent = layer.GetExtent()
#layerExtent = ( layerExtent[0], layerExtent[2], layerExtent[1], layerExtent[3] )
#featureNames = set()
features = {}


for featureIndex in range(0, layer.GetFeatureCount()):
    feature = layer.GetFeature(featureIndex)
    id = feature.GetFieldAsString("coursecode")
    # no duplications because it is a set!
    #featureNames.add(id)
    featureGeometry = feature.GetGeometryRef()
    if id in features: uniteFeatures(featureGeometry, features[id])
    else: features[id] = uniteFeatures(featureGeometry)

#jsonShape = dict( layerExtent=layerExtent, featureNames=tuple(featureNames), featureType="POLYGON", features=features )
# fill the geometries array in
geometries = []
for id in features:
	features[id]['id'] = id
	geometries.append(features[id])

outputFile=open("russia_geometries.js", "w")
outputFile.write("dojo.provide(\"courseApp.data.geo.russia_geometries\");\n")
outputFile.write("courseApp.data.geo.russiaGeometries =\n")
outputFile.write(json.dumps(geometries, ensure_ascii=False))

