package longevity.software.vrgamedemo

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class ObjectFileParser(context: Context, file: String) {

    private val mFinalVertices = mutableListOf<Float>()
    private val mFinalNormals = mutableListOf<Float>()
    private val mFinalUVs = mutableListOf<Float>()
    private val mFinalIndices = mutableListOf<Short>()

    /**
     * Init - parses the file in assets that matches the file string passed.
     */
    init {
        val inStream = context.assets.open(file)
        val reader = BufferedReader(InputStreamReader(inStream))

        val vertices = ArrayList<Triple<Float, Float, Float>>()
        val normals = ArrayList<Triple<Float, Float, Float>>()
        val uvs = ArrayList<Pair<Float, Float>>()

        val faceMap = HashMap<Triple<Int, Int?, Int?>, Short>()
        var nextIndex: Short = 0

        reader.forEachLine {

            if ( it.startsWith("v " ) ) {

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
            else if ( it.startsWith("f " ) ) {

                // perform the first split
                val split = it.split( " " )

                // get the first vertex, uv and normal
                val vun1 = split[1].split("/")
                val point1 = Triple(vun1[0].toInt(), vun1[1].toIntOrNull(), vun1[2].toIntOrNull())

                val pointInFaceMap1 = faceMap.get( point1 )

                if ( null != pointInFaceMap1 ) {

                    mFinalIndices.add(pointInFaceMap1)

                } else {

                    // add this sequence to the map
                    faceMap.put(point1, nextIndex)

                    // add vertex data
                    mFinalVertices.add(vertices[point1.first - 1].first)
                    mFinalVertices.add(vertices[point1.first - 1].second)
                    mFinalVertices.add(vertices[point1.first - 1].third)

                    // add uvs if present
                    if ( null != point1.second ) {
                        mFinalUVs.add(uvs[point1.second!! - 1].first)
                        mFinalUVs.add(uvs[point1.second!! - 1].second)
                    }

                    // add normals if present
                    if ( null != point1.third ) {
                        mFinalNormals.add(normals[point1.third!! - 1].first)
                        mFinalNormals.add(normals[point1.third!! - 1].second)
                        mFinalNormals.add(normals[point1.third!! - 1].third)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }

                // get the second vertex, uv and normal
                val vun2 = split[2].split("/")
                val point2 = Triple(vun2[0].toInt(), vun2[1].toIntOrNull(), vun2[2].toIntOrNull())
                val pointInFaceMap2 = faceMap.get( point2 )
                
                if ( null != pointInFaceMap2 ) {

                    mFinalIndices.add(pointInFaceMap2)

                } else {

                    // add this sequence to the map
                    faceMap.put(point2, nextIndex)

                    // add vertex data
                    mFinalVertices.add(vertices[point2.first - 1].first)
                    mFinalVertices.add(vertices[point2.first - 1].second)
                    mFinalVertices.add(vertices[point2.first - 1].third)

                    // add uvs if present
                    if ( null != point2.second ) {
                        mFinalUVs.add(uvs[point2.second!! - 1].first)
                        mFinalUVs.add(uvs[point2.second!! - 1].second)
                    }

                    // add normals if present
                    if ( null != point2.third ) {
                        mFinalNormals.add(normals[point2.third!! - 1].first)
                        mFinalNormals.add(normals[point2.third!! - 1].second)
                        mFinalNormals.add(normals[point2.third!! - 1].third)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }

                // get the third vertex, uv and normal
                val vun3 = split[3].split("/")
                val point3 = Triple(vun3[0].toInt(), vun3[1].toIntOrNull(), vun3[2].toIntOrNull())
                val pointInFaceMap3 = faceMap.get( point3 )
                
                if ( null != pointInFaceMap3 ) {

                    mFinalIndices.add(pointInFaceMap3)

                } else {

                    // add this sequence to the map
                    faceMap.put(point3, nextIndex)

                    // add vertex data
                    mFinalVertices.add(vertices[point3.first - 1].first)
                    mFinalVertices.add(vertices[point3.first - 1].second)
                    mFinalVertices.add(vertices[point3.first - 1].third)

                    // add uvs if present
                    if ( null != point3.second ) {
                        mFinalUVs.add(uvs[point3.second!! - 1].first)
                        mFinalUVs.add(uvs[point3.second!! - 1].second)
                    }

                    // add normals if present
                    if ( null != point3.third ) {
                        mFinalNormals.add(normals[point3.third!! - 1].first)
                        mFinalNormals.add(normals[point3.third!! - 1].second)
                        mFinalNormals.add(normals[point3.third!! - 1].third)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }
            }
        }
    }

    /**
     * returns the parsed vertices in float array form
     */
    fun getVertices(): FloatArray {
        return mFinalVertices.toFloatArray()
    }

    /**
     * returns the parsed uv coordinates in float array form
     */
    fun getUVs(): FloatArray {
        return mFinalUVs.toFloatArray()
    }

    /**
     * returns the parsed normals in float array form
     */
    fun getNormals(): FloatArray {
        return mFinalNormals.toFloatArray()
    }

    /**
     * returns the parsed indices in short array form
     */
    fun getIndices(): ShortArray {
        return mFinalIndices.toShortArray()
    }
}