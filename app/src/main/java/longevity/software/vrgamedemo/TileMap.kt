package longevity.software.vrgamedemo

import android.content.Context

class TileMap(context: Context, modelLoader: ModelLoader, save: SaveProgressInterface) : DrawableInterface, PlayerPositionTileMapInterface {

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

    private val mSave = save

    /**
     * Initialise the tiles to default ones for now
     */
    init {

        val tile = mSave.getTile()

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

            // save as a val as it is used at the end of the loop
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

            var tileIndex = CENTER_TILE_INDEX

            when (tileBits) {
                ( TOP_ROW or LEFT_COLUMN ) -> {
                    tileIndex = 0
                }
                ( TOP_ROW or CENTER_COLUMN ) -> {
                    tileIndex = 1
                }
                ( TOP_ROW or RIGHT_COLUMN ) -> {
                    tileIndex = 2
                }
                ( CENTER_ROW or LEFT_COLUMN ) -> {
                    tileIndex = 3
                }
                ( CENTER_ROW or RIGHT_COLUMN ) -> {
                    tileIndex = 5
                }
                ( BOTTOM_ROW or LEFT_COLUMN ) -> {
                    tileIndex = 6
                }
                ( BOTTOM_ROW or CENTER_COLUMN ) -> {
                    tileIndex = 7
                }
                ( BOTTOM_ROW or RIGHT_COLUMN ) -> {
                    tileIndex = 8
                }
            }

            val positionOnTile = pos - mTileOffsets[tileIndex]

            mTiles[tileIndex]!!.addModel(modelName, positionOnTile, rot)
        }
    }
}