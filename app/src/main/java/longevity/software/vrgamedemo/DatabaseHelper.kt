package longevity.software.vrgamedemo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), SaveProgressInterface {

    companion object {
        const val DATABASE_NAME = "VrGameDatabase.db"
        const val DATABASE_VERSION = 1

        object SaveDataContract {

            object SaveData : BaseColumns {
                const val TABLE_NAME = "SaveDataTable"
                const val COLUMN_NAME_TILE = "tile"
                const val COLUMN_NAME_OFFSET_X = "offset_x"
                const val COLUMN_NAME_OFFSET_Y = "offset_y"
                const val COLUMN_NAME_OFFSET_Z = "offset_z"
            }
        }

        private const val SQL_CREATE_SAVE_TABLE =
            "CREATE TABLE ${SaveDataContract.SaveData.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${SaveDataContract.SaveData.COLUMN_NAME_TILE} TEXT, " +
                    "${SaveDataContract.SaveData.COLUMN_NAME_OFFSET_X} TEXT, "+
                    "${SaveDataContract.SaveData.COLUMN_NAME_OFFSET_Y} TEXT, "+
                    "${SaveDataContract.SaveData.COLUMN_NAME_OFFSET_Z} TEXT)"

        private const val SQL_DELETE_SAVE_TABLE = "DROP TABLE IF EXISTS ${SaveDataContract.SaveData.TABLE_NAME}"

    }

    /**
     * function to create the database.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_SAVE_TABLE)
    }

    /**
     * function called when the database is upgraded
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Do nothing for now.
    }

    /**
     * function called when the database is downgraded
     */
    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    override fun getTile() : String {

        val db = readableDatabase

        // values to return from the query
        val projection = arrayOf(BaseColumns._ID, SaveDataContract.SaveData.COLUMN_NAME_TILE)

        val cursor = db.query(
            SaveDataContract.SaveData.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        ).also {
            with( it ) {
                if ( 0 == it.count ) {
                    // create a new save file
                } else {

                }
            }
        }

        return "Tile_0_0.vtf"
    }

    override fun getPlayerOffset(): Position3Float {
        return Position3Float(0.0f, 0.0f, 0.0f)
    }
}