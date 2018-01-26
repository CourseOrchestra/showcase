from osgeo import ogr, osr, gdal
import sys, os

if len(sys.argv) < 3: exit(0)
shpInFileName = sys.argv[1]
shpOutFileName = sys.argv[2]


# Open the source shapefile.
srcFile = ogr.Open(shpInFileName)
srcLayer = srcFile[0]
layerDefn = srcLayer.GetLayerDefn()

# Define the source and destination projections, and a
# transformation object to convert from one to the other.
srcProjection = osr.SpatialReference()
srcProjection.SetFromUserInput('WGS84')
dstProjection = osr.SpatialReference()
#dstProjection.ImportFromWkt('PROJCS["lamb_gauss_con",GEOGCS["pulk_42",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Lambert_Conformal_Conic"],PARAMETER["False_Easting",0.0],PARAMETER["False_Northing",0.0],PARAMETER["Central_Meridian",100.0],PARAMETER["Standard_Parallel_1",70.0],PARAMETER["Standard_Parallel_2",50.0],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",54.0],UNIT["Meter",1.0]]')
dstProjection.ImportFromProj4('+proj=aea +lat_1=52 +lat_2=64 +lat_0=0 +lon_0=105 +x_0=18500000 +y_0=0 +ellps=krass +units=m +towgs84=28,-130,-95,0,0,0,0 +no_defs')
transform = osr.CoordinateTransformation(srcProjection, dstProjection)


# Create the dest shapefile, and give it the new projection.
driver = ogr.GetDriverByName("ESRI Shapefile")
if os.path.exists(shpOutFileName): driver.DeleteDataSource(shpOutFileName)
dstFile = driver.CreateDataSource(shpOutFileName)
dstLayer = dstFile.CreateLayer("layer", dstProjection)
# Copy fields to the layer
fieldIndices = range(layerDefn.GetFieldCount())
for fieldIndex in fieldIndices:
    dstLayer.CreateField(layerDefn.GetFieldDefn(fieldIndex))
# Reproject each feature in turn.
for i in range(srcLayer.GetFeatureCount()):
    feature = srcLayer.GetFeature(i)
    geometry = feature.GetGeometryRef()
    newGeometry = geometry.Clone()
    newGeometry.Transform(transform)
    newFeature = ogr.Feature(dstLayer.GetLayerDefn())
    newFeature.SetGeometry(newGeometry)
    # Copy field values
    for fieldIndex in fieldIndices:
        fieldName = layerDefn.GetFieldDefn(fieldIndex).GetName()
        fieldValue = feature.GetFieldAsString(fieldName)
        newFeature.SetField(fieldName, fieldValue)
    dstLayer.CreateFeature(newFeature)
    newFeature.Destroy()
# All done.
srcFile.Destroy()
dstFile.Destroy()
