package longevity.software.vrgamedemo

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class ObjectAndMaterialFileParser(context: Context, objFile: String) {

    private val mFinalVertices = mutableListOf<Float>()
    private val mFinalNormals = mutableListOf<Float>()
    private val mFinalUVs = mutableListOf<Float>()
    private val mFinalIndices = mutableListOf<Short>()

    private var mFinalAmbientColour = mutableListOf<Float>()
    private var mFinalDiffuseColour = mutableListOf<Float>()
    private var mFinalSpecularColour = mutableListOf<Float>()
    private var mFinalSpecularExponent = mutableListOf<Float>()
    private var mFinalDissolve = mutableListOf<Float>()

    private val mMaterials = HashMap<String, MaterialData>()

    init {
        val objectStream = context.assets.open(objFile)
        val reader = BufferedReader(InputStreamReader(objectStream))

        var materialName = ""

        val vertices = ArrayList<Triple<Float, Float, Float>>()
        val normals = ArrayList<Triple<Float, Float, Float>>()
        val uvs = ArrayList<Pair<Float, Float>>()

        // face map which has a key:
        // Pair {
        //      material name,
        //      Triple {
        //          vertex,
        //          uv,
        //          normal
        //      }
        //  }
        val faceMap = HashMap<Pair<String, Triple<Int, Int?, Int?>>, Short>()
        var nextIndex: Short = 0

        reader.forEachLine {

            if ( it.startsWith( "mtllib" ) ) {
                val mtlStream = context.assets.open(it.substringAfter(" "))

                parseMtlFile(BufferedReader(InputStreamReader(mtlStream)))
            }
            else if ( it.startsWith("v " ) ) {

                val split = it.split( " " )

                // split[0] is the prefix
                val vertex = Triple(split[1].toFloat(), split[2].toFloat(), split[3].toFloat())
                vertices.add(vertex)
            }
            else if ( it.startsWith("vn" ) ) {

                val split = it.split( " " )

                // split[0] is the prefix
                val normal = Triple(split[1].toFloat(), split[2].toFloat(), split[3].toFloat())
                normals.add(normal)
            }
            else if ( it.startsWith( "vt" ) ) {

                val split = it.split( " " )

                // split[0] is the prefix
                val uv = Pair(split[1].toFloat(), split[2].toFloat())
                uvs.add(uv)
            }
            else if ( it.startsWith( "usemtl" )) {

                materialName = it.substringAfter( " " )
            }
            else if ( it.startsWith( "f " ) ) {

                // get the material for this face
                val matData: MaterialData? = mMaterials.get(materialName)

                // perform the first split
                val split = it.split( " " )

                // get the first vertex, uv and normal
                val vun1 = split[1].split("/")
                val point1 = Pair(materialName ,
                                                                Triple(vun1[0].toInt(),
                                                                        vun1[1].toIntOrNull(),
                                                                        vun1[2].toIntOrNull()))

                val pointInFaceMap1 = faceMap[point1]

                if ( null != pointInFaceMap1 ) {

                    mFinalIndices.add(pointInFaceMap1)

                } else {

                    // add this sequence to the map
                    faceMap[point1] = nextIndex

                    // add vertex data
                    mFinalVertices.add(vertices[point1.second.first - 1].first)
                    mFinalVertices.add(vertices[point1.second.first - 1].second)
                    mFinalVertices.add(vertices[point1.second.first - 1].third)

                    // add uvs if present
                    if ( null != point1.second.second ) {
                        mFinalUVs.add(uvs[point1.second.second!! - 1].first)
                        mFinalUVs.add(uvs[point1.second.second!! - 1].second)
                    }

                    // add normals if present
                    if ( null != point1.second.third ) {
                        mFinalNormals.add(normals[point1.second.third!! - 1].first)
                        mFinalNormals.add(normals[point1.second.third!! - 1].second)
                        mFinalNormals.add(normals[point1.second.third!! - 1].third)
                    }

                    // add the material data
                    if ( null != matData ) {

                        mFinalAmbientColour.add(matData.mAmbientColour[0])
                        mFinalAmbientColour.add(matData.mAmbientColour[1])
                        mFinalAmbientColour.add(matData.mAmbientColour[2])

                        mFinalDiffuseColour.add(matData.mDiffuseColour[0])
                        mFinalDiffuseColour.add(matData.mDiffuseColour[1])
                        mFinalDiffuseColour.add(matData.mDiffuseColour[2])

                        mFinalSpecularColour.add(matData.mSpecularColour[0])
                        mFinalSpecularColour.add(matData.mSpecularColour[1])
                        mFinalSpecularColour.add(matData.mSpecularColour[2])

                        mFinalSpecularExponent.add(matData.mSpecularExponent)

                        mFinalDissolve.add(matData.mDissolve)
                    } else {
                        // populate with default colours
                        mFinalAmbientColour.add(1.0f)
                        mFinalAmbientColour.add(1.0f)
                        mFinalAmbientColour.add(1.0f)

                        mFinalDiffuseColour.add(1.0f)
                        mFinalDiffuseColour.add(1.0f)
                        mFinalDiffuseColour.add(1.0f)

                        mFinalSpecularColour.add(1.0f)
                        mFinalSpecularColour.add(1.0f)
                        mFinalSpecularColour.add(1.0f)

                        mFinalSpecularExponent.add(32.0f)

                        mFinalDissolve.add(1.0f)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }

                // get the second vertex, uv and normal
                val vun2 = split[2].split("/")
                val point2 = Pair(materialName,
                                                                Triple(vun2[0].toInt(),
                                                                    vun2[1].toIntOrNull(),
                                                                    vun2[2].toIntOrNull()))

                val pointInFaceMap2 = faceMap[point2]

                if ( null != pointInFaceMap2 ) {

                    mFinalIndices.add(pointInFaceMap2)

                } else {

                    // add this sequence to the map
                    faceMap[point2] = nextIndex

                    // add vertex data
                    mFinalVertices.add(vertices[point2.second.first - 1].first)
                    mFinalVertices.add(vertices[point2.second.first - 1].second)
                    mFinalVertices.add(vertices[point2.second.first - 1].third)

                    // add uvs if present
                    if ( null != point2.second.second ) {
                        mFinalUVs.add(uvs[point2.second.second!! - 1].first)
                        mFinalUVs.add(uvs[point2.second.second!! - 1].second)
                    }

                    // add normals if present
                    if ( null != point2.second.third ) {
                        mFinalNormals.add(normals[point2.second.third!! - 1].first)
                        mFinalNormals.add(normals[point2.second.third!! - 1].second)
                        mFinalNormals.add(normals[point2.second.third!! - 1].third)
                    }

                    // add the material data
                    if ( null != matData ) {

                        mFinalAmbientColour.add(matData.mAmbientColour[0])
                        mFinalAmbientColour.add(matData.mAmbientColour[1])
                        mFinalAmbientColour.add(matData.mAmbientColour[2])

                        mFinalDiffuseColour.add(matData.mDiffuseColour[0])
                        mFinalDiffuseColour.add(matData.mDiffuseColour[1])
                        mFinalDiffuseColour.add(matData.mDiffuseColour[2])

                        mFinalSpecularColour.add(matData.mSpecularColour[0])
                        mFinalSpecularColour.add(matData.mSpecularColour[1])
                        mFinalSpecularColour.add(matData.mSpecularColour[2])

                        mFinalSpecularExponent.add(matData.mSpecularExponent)

                        mFinalDissolve.add(matData.mDissolve)
                    } else {
                        // populate with default colours
                        mFinalAmbientColour.add(1.0f)
                        mFinalAmbientColour.add(1.0f)
                        mFinalAmbientColour.add(1.0f)

                        mFinalDiffuseColour.add(1.0f)
                        mFinalDiffuseColour.add(1.0f)
                        mFinalDiffuseColour.add(1.0f)

                        mFinalSpecularColour.add(1.0f)
                        mFinalSpecularColour.add(1.0f)
                        mFinalSpecularColour.add(1.0f)

                        mFinalSpecularExponent.add(32.0f)

                        mFinalDissolve.add(1.0f)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }

                // get the third vertex, uv and normal
                val vun3 = split[3].split("/")
                val point3 = Pair(materialName,
                                                                Triple(vun3[0].toInt(),
                                                                        vun3[1].toIntOrNull(),
                                                                        vun3[2].toIntOrNull()))
                val pointInFaceMap3 = faceMap[point3]

                if ( null != pointInFaceMap3 ) {

                    mFinalIndices.add(pointInFaceMap3)

                } else {

                    // add this sequence to the map
                    faceMap[point3] = nextIndex

                    // add vertex data
                    mFinalVertices.add(vertices[point3.second.first - 1].first)
                    mFinalVertices.add(vertices[point3.second.first - 1].second)
                    mFinalVertices.add(vertices[point3.second.first - 1].third)

                    // add uvs if present
                    if ( null != point3.second.second ) {
                        mFinalUVs.add(uvs[point3.second.second!! - 1].first)
                        mFinalUVs.add(uvs[point3.second.second!! - 1].second)
                    }

                    // add normals if present
                    if ( null != point3.second.third ) {
                        mFinalNormals.add(normals[point3.second.third!! - 1].first)
                        mFinalNormals.add(normals[point3.second.third!! - 1].second)
                        mFinalNormals.add(normals[point3.second.third!! - 1].third)
                    }

                    // add the material data
                    if ( null != matData ) {

                        mFinalAmbientColour.add(matData.mAmbientColour[0])
                        mFinalAmbientColour.add(matData.mAmbientColour[1])
                        mFinalAmbientColour.add(matData.mAmbientColour[2])

                        mFinalDiffuseColour.add(matData.mDiffuseColour[0])
                        mFinalDiffuseColour.add(matData.mDiffuseColour[1])
                        mFinalDiffuseColour.add(matData.mDiffuseColour[2])

                        mFinalSpecularColour.add(matData.mSpecularColour[0])
                        mFinalSpecularColour.add(matData.mSpecularColour[1])
                        mFinalSpecularColour.add(matData.mSpecularColour[2])

                        mFinalSpecularExponent.add(matData.mSpecularExponent)

                        mFinalDissolve.add(matData.mDissolve)
                    } else {
                        // populate with default colours
                        mFinalAmbientColour.add(1.0f)
                        mFinalAmbientColour.add(1.0f)
                        mFinalAmbientColour.add(1.0f)

                        mFinalDiffuseColour.add(1.0f)
                        mFinalDiffuseColour.add(1.0f)
                        mFinalDiffuseColour.add(1.0f)

                        mFinalSpecularColour.add(1.0f)
                        mFinalSpecularColour.add(1.0f)
                        mFinalSpecularColour.add(1.0f)

                        mFinalSpecularExponent.add(32.0f)

                        mFinalDissolve.add(1.0f)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }
            }
        }
    }

    /**
     * parses the Mtl file to populate the mMaterials array
     */
    private fun parseMtlFile(mtlReader: BufferedReader) {

        var currentMaterialName = ""

        mtlReader.forEachLine {

            // if we see newmtl then add a new MaterialData class to the mMaterials hashmap
            if ( it.startsWith( "newmtl" ) ) {

                currentMaterialName = it.substringAfter( " " )

                mMaterials.put(currentMaterialName, MaterialData(currentMaterialName))
            }
            else if ( it.startsWith( "Ka" ) ) {
                val ambient = it.split( " " )

                // update the last material added
                mMaterials.get(currentMaterialName).also {
                    if ( null != it ) {
                        it.mAmbientColour = floatArrayOf(
                            ambient[1].toFloat(),
                            ambient[2].toFloat(),
                            ambient[3].toFloat()
                        )
                    }
                }
            }
            else if ( it.startsWith( "Kd" ) ) {
                val diffuse = it.split( " " )

                // update the last material added
                mMaterials.get(currentMaterialName).also {
                    if ( null != it ) {
                        it.mDiffuseColour = floatArrayOf(
                            diffuse[1].toFloat(),
                            diffuse[2].toFloat(),
                            diffuse[3].toFloat()
                        )
                    }
                }
            }
            else if ( it.startsWith( "Ks" ) ) {
                val specular = it.split( " " )

                // update the last material added
                mMaterials.get(currentMaterialName).also {
                    if ( null != it ) {
                        it.mSpecularColour = floatArrayOf(
                            specular[1].toFloat(),
                            specular[2].toFloat(),
                            specular[3].toFloat()
                        )
                    }
                }
            }
            else if ( it.startsWith( "Ns" ) ) {
                val specularExp = it.split( " " )

                // update the last material added
                mMaterials.get(currentMaterialName).also {
                    if ( null != it ) {
                        it.mSpecularExponent = specularExp[1].toFloat()
                    }
                }
            }
            else if ( it.startsWith( "d" ) ) {
                val dissolve = it.split( " " )

                // update the last material added
                mMaterials.get(currentMaterialName).also {
                    if ( null != it ) {
                        it.mDissolve = dissolve[1].toFloat()
                    }
                }
            }
        }
    }

    /**
     * returns the Model data which has been parsed
     */
    fun getModelData(): ModelData {
        return ModelData(mFinalVertices.toFloatArray(),
            mFinalNormals.toFloatArray(),
            mFinalUVs.toFloatArray(),
            mFinalIndices.toShortArray(),
            mFinalAmbientColour.toFloatArray(),
            mFinalDiffuseColour.toFloatArray(),
            mFinalSpecularColour.toFloatArray(),
            mFinalSpecularExponent.toFloatArray(),
            mFinalDissolve.toFloatArray())
    }
}