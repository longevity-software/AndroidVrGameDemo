package longevity.software.vrgamedemo

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class ObjectFileParser(context: Context, file: String) {

    private val mVertices = mutableListOf<Float>()
    private val mNormals = mutableListOf<Float>()
    private val mUVs = mutableListOf<Float>()
    private val mIndices = mutableListOf<Short>()

    init {
        val inStream = context.assets.open(file)
        val reader = BufferedReader(InputStreamReader(inStream))

        val vertices = ArrayList<Triple<Float, Float, Float>>()
        val normals = ArrayList<Triple<Float, Float, Float>>()
        val uvs = ArrayList<Pair<Float, Float>>()

        val faceMap = HashMap<Triple<Int, Int?, Int>, Short>()
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

                val split = it.split( " " )

                val split1 = split[1].split("/")
                val split2 = split[3].split("/")
                val split3 = split[2].split("/")

                val trip1 = Triple(split1[0].toInt(), split1[1].toIntOrNull(), split1[2].toInt())
                val trip2 = Triple(split2[0].toInt(), split2[1].toIntOrNull(), split2[2].toInt())
                val trip3 = Triple(split3[0].toInt(), split3[1].toIntOrNull(), split3[2].toInt())

                val fm1 = faceMap.get( trip1 )
                val fm2 = faceMap.get( trip2 )
                val fm3 = faceMap.get( trip3 )

                if ( null != fm1 ) {

                    mIndices.add(fm1)

                } else {

                    // add this sequence to the map
                    faceMap.put(trip1, nextIndex)

                    // add vertex data
                    mVertices.add(vertices[trip1.first - 1].first)
                    mVertices.add(vertices[trip1.first - 1].second)
                    mVertices.add(vertices[trip1.first - 1].third)

                    // add uvs if present
                    if ( null != trip1.second ) {
                        mUVs.add(uvs[trip1.second!! - 1].first)
                        mUVs.add(uvs[trip1.second!! - 1].second)
                    }

                    // add normals
                    mNormals.add(normals[trip1.third - 1].first)
                    mNormals.add(normals[trip1.third - 1].second)
                    mNormals.add(normals[trip1.third - 1].third)

                    // and finally add the index
                    mIndices.add(nextIndex++)
                }

                if ( null != fm2 ) {

                    mIndices.add(fm2)

                } else {

                    // add this sequence to the map
                    faceMap.put(trip2, nextIndex)

                    // add vertex data
                    mVertices.add(vertices[trip2.first - 1].first)
                    mVertices.add(vertices[trip2.first - 1].second)
                    mVertices.add(vertices[trip2.first - 1].third)

                    // add uvs if present
                    if ( null != trip2.second ) {
                        mUVs.add(uvs[trip2.second!! - 1].first)
                        mUVs.add(uvs[trip2.second!! - 1].second)
                    }

                    // add normals
                    mNormals.add(normals[trip2.third - 1].first)
                    mNormals.add(normals[trip2.third - 1].second)
                    mNormals.add(normals[trip2.third - 1].third)

                    // and finally add the index
                    mIndices.add(nextIndex++)
                }

                if ( null != fm3 ) {

                    mIndices.add(fm3)

                } else {

                    // add this sequence to the map
                    faceMap.put(trip3, nextIndex)

                    // add vertex data
                    mVertices.add(vertices[trip3.first - 1].first)
                    mVertices.add(vertices[trip3.first - 1].second)
                    mVertices.add(vertices[trip3.first - 1].third)

                    // add uvs if present
                    if ( null != trip3.second ) {
                        mUVs.add(uvs[trip3.second!! - 1].first)
                        mUVs.add(uvs[trip3.second!! - 1].second)
                    }

                    // add normals
                    mNormals.add(normals[trip3.third - 1].first)
                    mNormals.add(normals[trip3.third - 1].second)
                    mNormals.add(normals[trip3.third - 1].third)

                    // and finally add the index
                    mIndices.add(nextIndex++)
                }
            }
        }
    }

    fun getVertices(): FloatArray {
        return mVertices.toFloatArray()
    }

    fun getIndices(): ShortArray {
        return mIndices.toShortArray()
    }
}