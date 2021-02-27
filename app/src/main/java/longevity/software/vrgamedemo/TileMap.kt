package longevity.software.vrgamedemo

import android.content.Context

class TileMap(context: Context, modelLoader: ModelLoader, tile: String) : DrawableInterface, PlayerPositionTileMapInterface {

    private val NUMBER_OF_TILES = 9
    private val CENTER_TILE_INDEX = 4
    private val TILE_ABOVE_OFFSET = -3
    private val TILE_BELOW_OFFSET = 3
    private val TILE_LEFT_OFFSET = -1
    private val TILE_RIGHT_OFFSET = 1

    private val TILE_SIZE : Float = 20.0f
    private val HALF_TILE_SIZE = TILE_SIZE / 2.0f

    private val mTileOffsets = arrayOf(
        Position3Float(-TILE_SIZE, 0.0f, -TILE_SIZE),
        Position3Float(0.0f, 0.0f, -TILE_SIZE),
        Position3Float(TILE_SIZE, 0.0f, -TILE_SIZE),
        Position3Float(-TILE_SIZE, 0.0f, 0.0f),
        Position3Float(0.0f, 0.0f, 0.0f),
        Position3Float(TILE_SIZE, 0.0f, 0.0f),
        Position3Float(-TILE_SIZE, 0.0f, TILE_SIZE),
        Position3Float(0.0f, 0.0f, TILE_SIZE),
        Position3Float(TILE_SIZE, 0.0f, TILE_SIZE)
    )

    private var mTiles = Array<Tile?>(9) {null}

    private val mModelLoader = modelLoader

    private val mContext = context

    /**
     * Initialise the tiles to default ones for now
     */
    init {

        // set the center tile
        mTiles[CENTER_TILE_INDEX] = VrTileFormatParser(mContext, tile, modelLoader).getParsedTile()

        // set the other tiles
        mTiles[0] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileUpLeft(), modelLoader).getParsedTile()
        mTiles[1] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightUp(), modelLoader).getParsedTile()
        mTiles[2] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileUpRight(), modelLoader).getParsedTile()
        mTiles[3] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightLeft(), modelLoader).getParsedTile()
        mTiles[5] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightRight(), modelLoader).getParsedTile()
        mTiles[6] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileDownLeft(), modelLoader).getParsedTile()
        mTiles[7] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightDown(), modelLoader).getParsedTile()
        mTiles[8] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileDownRight(), modelLoader).getParsedTile()
    }

    /**
     * Function to initialise the Tiles models
     */
    override fun initialise() {

        // add all the models to the arraylist of model data
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i]?.initialise()
            mTiles[i]?.setTileOffset(mTileOffsets[i].X(), mTileOffsets[i].Z())
        }
    }

    /**
     * Function to get the players position on the tile map
     */
    override fun getPlayerPositionOnTileMap(current : Position3Float, directionVector : Vector3Float, distance: Float) : Position3Float {

        // constants used to adjust the tiles based on player movement
        val DONT_MOVE = 0x00
        val MOVE_UP = 0x01
        val MOVE_DOWN = 0x02
        val MOVE_LEFT = 0x04
        val MOVE_RIGHT = 0x08

        // diaganols
        val MOVE_UP_AND_RIGHT = MOVE_UP or MOVE_RIGHT
        val MOVE_UP_AND_LEFT = MOVE_UP or MOVE_LEFT
        val MOVE_DOWN_AND_RIGHT = MOVE_DOWN or MOVE_RIGHT
        val MOVE_DOWN_AND_LEFT = MOVE_DOWN or MOVE_LEFT

        // get the directionVector normalised
        // this is multiplied by the fluidity to get the actual position the player
        // intends to travel to
        val normalisedDirection = directionVector.getNormalised()

        // ignore the Y portion for now as the map is only 2D
        var startPosition = Position3Float(current.X(), 0.0f, current.Z())

        var currentTile = CENTER_TILE_INDEX

        // track the distance traveled and the remaining distance to determine
        // when to exit the loop
        var remainingDistance = distance
        var distanceTravelled = 1.0f

        // default the end position to where the player starts
        var endPosition = startPosition

        // while the player still has distance to travel and has travelled in the last iteration
        while ( (remainingDistance > 0.0f)
                && (distanceTravelled > 0.0f) ){

            // load as a val as it is used at the end of the loop
            val tileFluidity = mTiles[currentTile]!!.getTileFluidity()

            // how far will we travel on the current tile
            val tileDist = tileFluidity * remainingDistance
            val travelVector = normalisedDirection * tileDist

            // where will we travel to if we can
            val travelPosition = startPosition + travelVector

            // if we are moving to another tile then this variable tracks which direction
            var moveDirection = DONT_MOVE

            // work out what direction the player is travelling
            if (travelPosition.X() > HALF_TILE_SIZE) {
                moveDirection = moveDirection or MOVE_RIGHT
            } else if (travelPosition.X() < -HALF_TILE_SIZE) {
                moveDirection = moveDirection or MOVE_LEFT
            }

            if (travelPosition.Z() > HALF_TILE_SIZE) {
                moveDirection = moveDirection or MOVE_DOWN
            } else if (travelPosition.Z() < -HALF_TILE_SIZE) {
                moveDirection = moveDirection or MOVE_UP
            }

            // work out the point that the player leaves the tile
            // default to where the player is travelling to as this will be
            // used if the player is not exiting the center tile
            var intersectionPoint = travelPosition

            // set the end position and update intersectionPoint if necessary
            when (moveDirection) {
                MOVE_UP -> {
                    // we are moving up only so no need to check where it crosses the top edge
                    intersectionPoint = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE)
                    )

                    val nextTile  = currentTile + TILE_ABOVE_OFFSET

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - Position3Float(0.0f, 0.0f, -TILE_SIZE))
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_DOWN -> {
                    // we are moving down only so no need to check where it crosses the bottom edge
                    intersectionPoint = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    val nextTile  = currentTile + TILE_BELOW_OFFSET

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - Position3Float(0.0f, 0.0f, TILE_SIZE))
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_LEFT -> {
                    // we are moving left only so no need to check where it crosses the left edge
                    intersectionPoint = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    val nextTile  = currentTile + TILE_LEFT_OFFSET

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - Position3Float(-TILE_SIZE, 0.0f, 0.0f))
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_RIGHT -> {
                    // we are moving right only so no need to check where it crosses the right edge
                    intersectionPoint = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    val nextTile  = currentTile + TILE_RIGHT_OFFSET

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - Position3Float(TILE_SIZE, 0.0f, 0.0f))
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_UP_AND_RIGHT -> {
                    // find the intersection point for both edges
                    val intersectionUp = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE)
                    )

                    val intersectionRight = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    // get the distance to each point
                    val upDistance = startPosition.distanceTo(intersectionUp)
                    val rightDistance = startPosition.distanceTo(intersectionRight)

                    // create variables used later
                    var nextTile = currentTile
                    var offset: Position3Float

                    // check for identical distances
                    if ( upDistance == rightDistance ) {
                        // distances are the same so move directly diagonal
                        intersectionPoint = intersectionUp  // both are the same so it doesn't matter which we use.

                        // the next tile is up and right
                        nextTile += (TILE_ABOVE_OFFSET + TILE_RIGHT_OFFSET)

                        // set the offset to remove from the intersection point
                        offset = Position3Float(TILE_SIZE, 0.0f, -TILE_SIZE)
                    }
                    else if ( upDistance < rightDistance ) {
                        // up distance is the closest so move to the tile above
                        intersectionPoint = intersectionUp

                        // the next tile is up
                        nextTile += TILE_ABOVE_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(0.0f, 0.0f, -TILE_SIZE)
                    }
                    else {
                        // right edge must be the closest
                        intersectionPoint = intersectionRight

                        // the next tile is right
                        nextTile += TILE_RIGHT_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(TILE_SIZE, 0.0f, 0.0f)
                    }

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - offset)
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_UP_AND_LEFT -> {
                    // find the intersection point for both edges
                    val intersectionUp = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE)
                    )

                    val intersectionLeft = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    // get the distance to each point
                    val upDistance = startPosition.distanceTo(intersectionUp)
                    val leftDistance = startPosition.distanceTo(intersectionLeft)

                    // create variables used later
                    var nextTile = currentTile
                    var offset: Position3Float

                    // check for identical distances
                    if ( upDistance == leftDistance ) {
                        // distances are the same so move directly diagonal
                        intersectionPoint = intersectionUp  // both are the same so it doesn't matter which we use.

                        // the next tile is up and right
                        nextTile += (TILE_ABOVE_OFFSET + TILE_LEFT_OFFSET)

                        // set the offset to remove from the intersection point
                        offset = Position3Float(-TILE_SIZE, 0.0f, -TILE_SIZE)
                    }
                    else if ( upDistance < leftDistance ) {
                        // up distance is the closest so move to the tile above
                        intersectionPoint = intersectionUp

                        // the next tile is up
                        nextTile += TILE_ABOVE_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(0.0f, 0.0f, -TILE_SIZE)
                    }
                    else {
                        // leftt edge must be the closest
                        intersectionPoint = intersectionLeft

                        // the next tile is right
                        nextTile += TILE_LEFT_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(-TILE_SIZE, 0.0f, 0.0f)
                    }

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - offset)
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_DOWN_AND_RIGHT -> {
                    // find the intersection point for both edges
                    val intersectionDown = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    val intersectionRight = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    // get the distance to each point
                    val downDistance = startPosition.distanceTo(intersectionDown)
                    val rightDistance = startPosition.distanceTo(intersectionRight)

                    // create variables used later
                    var nextTile = currentTile
                    var offset: Position3Float

                    // check for identical distances
                    if ( downDistance == rightDistance ) {
                        // distances are the same so move directly diagonal
                        intersectionPoint = intersectionDown  // both are the same so it doesn't matter which we use.

                        // the next tile is down and right
                        nextTile += (TILE_BELOW_OFFSET + TILE_RIGHT_OFFSET)

                        // set the offset to remove from the intersection point
                        offset = Position3Float(TILE_SIZE, 0.0f, TILE_SIZE)
                    }
                    else if ( downDistance < rightDistance ) {
                        // down distance is the closest so move to the tile above
                        intersectionPoint = intersectionDown

                        // the next tile is down
                        nextTile += TILE_BELOW_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(0.0f, 0.0f, TILE_SIZE)
                    }
                    else {
                        // right edge must be the closest
                        intersectionPoint = intersectionRight

                        // the next tile is right
                        nextTile += TILE_RIGHT_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(TILE_SIZE, 0.0f, 0.0f)
                    }

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - offset)
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                MOVE_DOWN_AND_LEFT -> {
                    // find the intersection point for both edges
                    val intersectionDown = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    val intersectionLeft = getIntersectionPoint2D(
                        startPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, -HALF_TILE_SIZE),
                        travelPosition,
                        Position3Float(-HALF_TILE_SIZE, 0.0f, HALF_TILE_SIZE)
                    )

                    // get the distance to each point
                    val downDistance = startPosition.distanceTo(intersectionDown)
                    val leftDistance = startPosition.distanceTo(intersectionLeft)

                    // create variables used later
                    var nextTile = currentTile
                    var offset: Position3Float

                    // check for identical distances
                    if ( downDistance == leftDistance ) {
                        // distances are the same so move directly diagonal
                        intersectionPoint = intersectionDown  // both are the same so it doesn't matter which we use.

                        // the next tile is down and right
                        nextTile += (TILE_BELOW_OFFSET + TILE_LEFT_OFFSET)

                        // set the offset to remove from the intersection point
                        offset = Position3Float(-TILE_SIZE, 0.0f, TILE_SIZE)
                    }
                    else if ( downDistance < leftDistance ) {
                        // down distance is the closest so move to the tile above
                        intersectionPoint = intersectionDown

                        // the next tile is down
                        nextTile += TILE_BELOW_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(0.0f, 0.0f, TILE_SIZE)
                    }
                    else {
                        // left edge must be the closest
                        intersectionPoint = intersectionLeft

                        // the next tile is right
                        nextTile += TILE_LEFT_OFFSET

                        // set the offset to remove from the intersection point
                        offset = Position3Float(-TILE_SIZE, 0.0f, 0.0f)
                    }

                    // don't actually enter the next tile if it has no fluidity
                    if ( mTiles[nextTile]!!.getTileFluidity() > 0.0f ) {
                        currentTile = nextTile
                        endPosition = (intersectionPoint - offset)
                    }
                    else {
                        endPosition = intersectionPoint
                    }
                }
                else -> endPosition = travelPosition
            }

            distanceTravelled = startPosition.distanceTo(intersectionPoint)

            // subtract how far we actually moved accounting for the fluidity
            // TODO - possible divide by Zero if tileFluidity is somehow Zero
            remainingDistance -= (distanceTravelled / tileFluidity)

            // update the start position ready for the next loop
            startPosition = endPosition
        }

        // has the tile changed
        if ( CENTER_TILE_INDEX != currentTile ) {

            // if the current tile is no longer the center then readjust
            // for now we redraw all, there is probably a more efficient way of doing this though
            mTiles[CENTER_TILE_INDEX] = VrTileFormatParser(mContext, mTiles[currentTile]!!.getTileName(), mModelLoader).getParsedTile()

            // set the other tiles
            mTiles[0] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileUpLeft(), mModelLoader).getParsedTile()
            mTiles[1] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightUp(), mModelLoader).getParsedTile()
            mTiles[2] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileUpRight(), mModelLoader).getParsedTile()
            mTiles[3] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightLeft(), mModelLoader).getParsedTile()
            mTiles[5] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightRight(), mModelLoader).getParsedTile()
            mTiles[6] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileDownLeft(), mModelLoader).getParsedTile()
            mTiles[7] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileStraightDown(), mModelLoader).getParsedTile()
            mTiles[8] = VrTileFormatParser(mContext, mTiles[CENTER_TILE_INDEX]!!.getTileDownRight(), mModelLoader).getParsedTile()

            // tiles have shifted so reset the offsets
            for (i in 0 until NUMBER_OF_TILES) {
                mTiles[i]?.setTileOffset(mTileOffsets[i].X(), mTileOffsets[i].Z())
            }
        }

        // return the end X and Z components but keep the current Y component
        return Position3Float(endPosition.X(), current.Y(), endPosition.Z())
    }


    /**
     * Draws all the tiles
     */
    override fun draw(vpMatrix: FloatArray, lightPos: Triple<Float, Float, Float>, lightColour: Triple<Float, Float, Float>, cameraPos: Triple<Float, Float, Float>) {

        // draw all the tiles
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i]?.draw(vpMatrix, lightPos, lightColour, cameraPos)
        }
    }

    /**
     * function to test if a point is valid on the tile map
     */
    fun isPositionValidOnTileMap(pos: Position3Float) : Boolean {
        // position is valid if it is on the tile map
        return !(( Math.abs(pos.X()) > ( TILE_SIZE + HALF_TILE_SIZE ) )
                || ( Math.abs(pos.Z()) > ( TILE_SIZE + HALF_TILE_SIZE ) ))
    }

    /**
     * function to return if the position is on an empty tile.
     */
    fun isPositionOnAnEmptyTile(pos: Position3Float) : Boolean {

        val tileIndex = getIndexOfTileAtPosition(pos).also {
            if (null != it) {
                if (mTiles[it]!!.tileIsEmpty()) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * function to bring the tile to life if it is empty
     */
    fun bringTileToLife(pos: Position3Float) {

        val tileIndex = getIndexOfTileAtPosition(pos).also {
            if (null != it) {

                val tile = mTiles[it]!!
                // tile is on the map so is it an empty tile
                if (tile.tileIsEmpty()) {

                    // get the tile prefix
                    val tileName = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                    val tilePrefix = tileName.substringBefore("_")
                    val tileX = tileName.substringBeforeLast("_").substringAfter("_").toInt()
                    val tileY = tileName.substringAfterLast("_").substringBefore(".vtf").toInt()

                    var newTileName = tilePrefix
                    var newStraightUp = ""
                    var newStraightDown = ""
                    var newStraightLeft = ""
                    var newStraightRight = ""
                    var newUpLeft = ""
                    var newUpRight = ""
                    var newDownLeft = ""
                    var newDownRight = ""

                    when (it) {
                        0 -> {
                            newTileName += "_" + (tileX - 1).toString() + "_" + (tileY - 1).toString() + ".vtf"
                            newStraightDown = mTiles[CENTER_TILE_INDEX]!!.getTileStraightLeft()
                            newStraightRight = mTiles[CENTER_TILE_INDEX]!!.getTileStraightUp()
                            newDownRight = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newUpRight = mTiles[1]!!.getTileStraightUp()
                            newStraightUp = mTiles[1]!!.getTileUpLeft()
                            newDownLeft = mTiles[3]!!.getTileStraightLeft()
                            newStraightLeft = mTiles[3]!!.getTileUpLeft()

                            if ("Empty_tile.vtf" != newStraightUp) {
                                // parse the file to get the tile which is left of it
                                val tempTile = VrTileFormatParser(mContext, newStraightUp, mModelLoader).getParsedTile()
                                newUpLeft = tempTile.getTileStraightLeft()
                            }
                            else if ("Empty_tile.vtf" != newStraightLeft) {
                                // parse the file to get the tile which is up from it
                                val tempTile = VrTileFormatParser(mContext, newStraightLeft, mModelLoader).getParsedTile()
                                newUpLeft = tempTile.getTileStraightUp()
                            }
                            else {
                                newUpLeft = "Empty_tile.vtf"
                            }
                        }
                        1 -> {
                            newTileName += "_" + (tileX).toString() + "_" + (tileY - 1).toString() + ".vtf"
                            newStraightDown = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newStraightRight = mTiles[CENTER_TILE_INDEX]!!.getTileUpRight()
                            newDownRight = mTiles[CENTER_TILE_INDEX]!!.getTileStraightRight()
                            newUpRight = mTiles[2]!!.getTileStraightUp()
                            newStraightUp = if ("Empty_tile.vtf" == mTiles[2]!!.getTileUpLeft()) { mTiles[0]!!.getTileUpRight() } else {mTiles[2]!!.getTileUpLeft()}
                            newDownLeft = mTiles[3]!!.getTileName()
                            newStraightLeft = mTiles[0]!!.getTileName()
                            newUpLeft = mTiles[0]!!.getTileStraightUp()
                        }
                        2 -> {
                            newTileName += "_" + (tileX + 1).toString() + "_" + (tileY - 1).toString() + ".vtf"
                            newStraightDown = mTiles[CENTER_TILE_INDEX]!!.getTileStraightRight()
                            newStraightRight = mTiles[5]!!.getTileUpRight()
                            newDownRight = mTiles[5]!!.getTileStraightRight()
                            newUpLeft = mTiles[1]!!.getTileStraightUp()
                            newStraightUp = mTiles[1]!!.getTileUpRight()
                            newDownLeft = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newStraightLeft = mTiles[1]!!.getTileName()

                            if ("Empty_tile.vtf" != newStraightUp) {
                                // parse the file to get the tile which is left of it
                                val tempTile = VrTileFormatParser(mContext, newStraightUp, mModelLoader).getParsedTile()
                                newUpRight = tempTile.getTileStraightRight()
                            }
                            else if ("Empty_tile.vtf" != newStraightRight) {
                                // parse the file to get the tile which is up from it
                                val tempTile = VrTileFormatParser(mContext, newStraightRight, mModelLoader).getParsedTile()
                                newUpRight = tempTile.getTileStraightUp()
                            }
                            else {
                                newUpRight = "Empty_tile.vtf"
                            }
                        }
                        3 -> {
                            newTileName += "_" + (tileX - 1).toString() + "_" + (tileY).toString() + ".vtf"
                            newStraightDown = mTiles[CENTER_TILE_INDEX]!!.getTileDownLeft()
                            newStraightRight = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newDownRight = mTiles[CENTER_TILE_INDEX]!!.getTileStraightDown()
                            newUpRight = mTiles[CENTER_TILE_INDEX]!!.getTileStraightUp()
                            newStraightUp = mTiles[CENTER_TILE_INDEX]!!.getTileUpLeft()
                            newDownLeft = mTiles[6]!!.getTileStraightLeft()
                            newUpLeft = mTiles[0]!!.getTileStraightLeft()
                            newStraightLeft = if ("Empty_tile.vtf" == mTiles[0]!!.getTileDownLeft()) { mTiles[6]!!.getTileUpLeft() } else {mTiles[0]!!.getTileDownLeft()}
                        }
                        5 -> {
                            newTileName += "_" + (tileX + 1).toString() + "_" + (tileY).toString() + ".vtf"
                            newStraightDown = mTiles[CENTER_TILE_INDEX]!!.getTileDownRight()
                            newStraightLeft = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newDownRight = mTiles[8]!!.getTileStraightRight()
                            newUpRight = mTiles[2]!!.getTileStraightRight()
                            newStraightUp = mTiles[CENTER_TILE_INDEX]!!.getTileUpRight()
                            newDownLeft = mTiles[CENTER_TILE_INDEX]!!.getTileStraightDown()
                            newUpLeft = mTiles[CENTER_TILE_INDEX]!!.getTileStraightUp()
                            newStraightRight = if ("Empty_tile.vtf" == mTiles[2]!!.getTileDownRight()) { mTiles[8]!!.getTileUpRight() } else {mTiles[2]!!.getTileDownRight()}
                        }
                        6 -> {
                            newTileName += "_" + (tileX - 1).toString() + "_" + (tileY + 1).toString() + ".vtf"
                            newStraightDown = mTiles[7]!!.getTileDownRight()
                            newStraightRight = mTiles[CENTER_TILE_INDEX]!!.getTileStraightDown()
                            newDownRight = mTiles[7]!!.getTileStraightDown()
                            newUpLeft = mTiles[3]!!.getTileStraightLeft()
                            newStraightUp = mTiles[CENTER_TILE_INDEX]!!.getTileStraightLeft()
                            newStraightLeft = mTiles[3]!!.getTileDownLeft()
                            newUpRight = mTiles[CENTER_TILE_INDEX]!!.getTileName()

                            if ("Empty_tile.vtf" != newStraightDown) {
                                // parse the file to get the tile which is left of it
                                val tempTile = VrTileFormatParser(mContext, newStraightDown, mModelLoader).getParsedTile()
                                newDownLeft = tempTile.getTileStraightLeft()
                            }
                            else if ("Empty_tile.vtf" != newStraightLeft) {
                                // parse the file to get the tile which is up from it
                                val tempTile = VrTileFormatParser(mContext, newStraightLeft, mModelLoader).getParsedTile()
                                newDownLeft = tempTile.getTileStraightDown()
                            }
                            else {
                                newDownLeft = "Empty_tile.vtf"
                            }
                        }
                        7 -> {
                            newTileName += "_" + (tileX).toString() + "_" + (tileY + 1).toString() + ".vtf"
                            newStraightLeft = mTiles[CENTER_TILE_INDEX]!!.getTileDownLeft()
                            newDownRight = mTiles[8]!!.getTileStraightDown()
                            newUpRight = mTiles[CENTER_TILE_INDEX]!!.getTileStraightRight()
                            newStraightUp = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newDownLeft = mTiles[6]!!.getTileStraightDown()
                            newUpLeft = mTiles[CENTER_TILE_INDEX]!!.getTileStraightLeft()
                            newStraightRight = mTiles[CENTER_TILE_INDEX]!!.getTileDownRight()
                            newStraightDown = if ("Empty_tile.vtf" == mTiles[6]!!.getTileDownRight()) { mTiles[8]!!.getTileDownLeft() } else {mTiles[6]!!.getTileDownRight()}
                        }
                        8 -> {
                            newTileName += "_" + (tileX + 1).toString() + "_" + (tileY + 1).toString() + ".vtf"
                            newStraightDown = mTiles[7]!!.getTileDownRight()
                            newStraightRight = mTiles[5]!!.getTileDownRight()
                            newUpRight = mTiles[5]!!.getTileStraightRight()
                            newUpLeft = mTiles[CENTER_TILE_INDEX]!!.getTileName()
                            newStraightUp = mTiles[CENTER_TILE_INDEX]!!.getTileStraightRight()
                            newDownLeft = mTiles[7]!!.getTileStraightDown()
                            newStraightLeft = mTiles[CENTER_TILE_INDEX]!!.getTileStraightDown()

                            if ("Empty_tile.vtf" != newStraightDown) {
                                // parse the file to get the tile which is left of it
                                val tempTile = VrTileFormatParser(mContext, newStraightDown, mModelLoader).getParsedTile()
                                newDownRight = tempTile.getTileStraightRight()
                            }
                            else if ("Empty_tile.vtf" != newStraightRight) {
                                // parse the file to get the tile which is up from it
                                val tempTile = VrTileFormatParser(mContext, newStraightRight, mModelLoader).getParsedTile()
                                newDownRight = tempTile.getTileStraightDown()
                            }
                            else {
                                newDownRight = "Empty_tile.vtf"
                            }
                        }
                    }

                    // now update all the new tiles neighbours to point to it
                    if ( "Empty_tile.vtf" != newUpLeft ) {
                        val tempTile = VrTileFormatParser(mContext, newUpLeft, mModelLoader).getParsedTile()
                        tempTile.setTileDownRight(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newStraightUp ) {
                        val tempTile = VrTileFormatParser(mContext, newStraightUp, mModelLoader).getParsedTile()
                        tempTile.setTileStraightDown(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newUpRight ) {
                        val tempTile = VrTileFormatParser(mContext, newUpRight, mModelLoader).getParsedTile()
                        tempTile.setTileDownLeft(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newStraightLeft ) {
                        val tempTile = VrTileFormatParser(mContext, newStraightLeft, mModelLoader).getParsedTile()
                        tempTile.setTileStraightRight(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newStraightRight ) {
                        val tempTile = VrTileFormatParser(mContext, newStraightRight, mModelLoader).getParsedTile()
                        tempTile.setTileStraightLeft(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newDownLeft ) {
                        val tempTile = VrTileFormatParser(mContext, newDownLeft, mModelLoader).getParsedTile()
                        tempTile.setTileUpRight(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newStraightDown ) {
                        val tempTile = VrTileFormatParser(mContext, newStraightDown, mModelLoader).getParsedTile()
                        tempTile.setTileStraightUp(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }
                    if ( "Empty_tile.vtf" != newDownRight ) {
                        val tempTile = VrTileFormatParser(mContext, newDownRight, mModelLoader).getParsedTile()
                        tempTile.setTileUpLeft(newTileName)
                        tempTile.saveTileToFile(mContext)
                    }

                    val newTile = Tile(
                        "Grass.obj",
                        "None",
                        mModelLoader,
                        newTileName,
                        newUpLeft,
                        newStraightUp,
                        newUpRight,
                        newStraightLeft,
                        newStraightRight,
                        newDownLeft,
                        newStraightDown,
                        newDownRight,
                        1.0f,
                        ArrayList<Triple<String, Position3Float, Float>>()
                    ).also {
                        it.saveTileToFile(mContext)
                    }

                    newTile.setTileOffset(mTileOffsets[it]!!.X(), mTileOffsets[it]!!.Z())

                    mTiles[it] = newTile
                }
            }
        }
    }

    /**
     * Function to get the point that two lines intersect
     * Only call this function if the two lines are not parallel
     * TODO - Pass in and return new Position2Float instances
     */
    private fun getIntersectionPoint2D(startA: Position3Float, startB: Position3Float, endA: Position3Float, endB: Position3Float) : Position3Float {

        val x1 = startA.X()
        val x2 = endA.X()
        val x3 = startB.X()
        val x4 = endB.X()

        val y1 = startA.Z()
        val y2 = endA.Z()
        val y3 = startB.Z()
        val y4 = endB.Z()

        // See wikipedia page for formula origin https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        val intersectionXNumerator = ((((x1 * y2) - (y1 * x2)) * (x3 - x4)) - ((x1 - x2) * ((x3 * y4) - (y3 * x4))))
        val intersectionYNumerator = ((((x1 * y2) - (y1 * x2)) * (y3 - y4)) - ((y1 - y2) * ((x3 * y4) - (y3 * x4))))
        val intersectionDivisor = (((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4)))

        // TODO - check for Zero divisor
        return Position3Float((intersectionXNumerator / intersectionDivisor), 0.0f, (intersectionYNumerator / intersectionDivisor))
    }

    /**
     * Function which determines which tile to place the object and then add it to that tile.
     */
    fun placeObjectInMap(modelName: String, pos: Position3Float, rot: Float) {

        val tileIndex = getIndexOfTileAtPosition(pos)

        if ( null != tileIndex) {

            val positionOnTile = pos - mTileOffsets[tileIndex]

            mTiles[tileIndex]!!.addModel(modelName, positionOnTile, rot)

            // change has been made so save it to file.
            mTiles[tileIndex]!!.saveTileToFile(mContext)
        }
    }

    private fun getIndexOfTileAtPosition(pos: Position3Float) : Int? {

        // find out which tile the object is in
        // double check it is on the tile map
        if ( isPositionValidOnTileMap(pos) ) {

            val TOP_ROW = 0x01
            val CENTER_ROW = 0x02
            val BOTTOM_ROW = 0x04
            val LEFT_COLUMN = 0x08
            val CENTER_COLUMN = 0x10
            val RIGHT_COLUMN = 0x20

            var tileBits = 0x00

            // now which tile is it in
            if (pos.X() > HALF_TILE_SIZE) {
                tileBits = tileBits or RIGHT_COLUMN
            } else if (pos.X() < -HALF_TILE_SIZE) {
                tileBits = tileBits or LEFT_COLUMN
            } else {
                tileBits = tileBits or CENTER_COLUMN
            }

            if (pos.Z() > HALF_TILE_SIZE) {
                tileBits = tileBits or BOTTOM_ROW
            } else if (pos.Z() < -HALF_TILE_SIZE) {
                tileBits = tileBits or TOP_ROW
            } else {
                tileBits = tileBits or CENTER_ROW
            }

            when (tileBits) {
                (TOP_ROW or LEFT_COLUMN) -> {
                    return 0
                }
                (TOP_ROW or CENTER_COLUMN) -> {
                    return 1
                }
                (TOP_ROW or RIGHT_COLUMN) -> {
                    return 2
                }
                (CENTER_ROW or LEFT_COLUMN) -> {
                    return 3
                }
                (CENTER_ROW or RIGHT_COLUMN) -> {
                    return 5
                }
                (BOTTOM_ROW or LEFT_COLUMN) -> {
                    return 6
                }
                (BOTTOM_ROW or CENTER_COLUMN) -> {
                    return 7
                }
                (BOTTOM_ROW or RIGHT_COLUMN) -> {
                    return 8
                }
                else -> return CENTER_TILE_INDEX
            }
        }

        return null
    }
}