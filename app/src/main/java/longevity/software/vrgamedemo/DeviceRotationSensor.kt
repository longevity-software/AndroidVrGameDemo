package longevity.software.vrgamedemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix
import java.util.concurrent.locks.ReentrantLock

class DeviceRotationSensor(context: Context): SensorEventListener {

    // lock to prevent the rotation matrix from being read and written to at the same time
    private val mRotationLock = ReentrantLock()

    private var mSensorManager: SensorManager

    private var mRotationMatrix = FloatArray(16)

    /**
     * init function initialises the sensor manager and sets the rotation matrix to an identity matrix.
     */
    init {

        // initialise the sensor manager
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // TODO - this sensor list returns the following on my device so does not work for device orientation.
        // 1: Accelerometer
        // 2: Proximity
        // 3: Significant motion
        var sensorList: List<Sensor>  = mSensorManager.getSensorList(Sensor.TYPE_ALL)

        // set the rotation matrix to the identity matrix
        Matrix.setIdentityM(mRotationMatrix, 0)
    }

    /**
     * function called to register the sensor changed listeners.
     */
    fun registerListeners() {
        // Set this activity as the listener for the game rotation
        mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)?.also { rotSensor ->
            mSensorManager.registerListener(this,
                rotSensor,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME)
        }
    }

    /**
     * function called to unregister all the listeners.
     */
    fun unregisterListeners() {

        // unregister all sensor listeners while the acticity is paused.
        mSensorManager.unregisterListener(this)
    }

    /**
     * this function is required to be overridden but I'm not doing anything in it at the moment.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO - do I need to do anything here
    }

    /**
     * function which is required to be overridden
     * double check the event type and update the rotation matrix.
     */
    override fun onSensorChanged(event: SensorEvent) {

        // double check the event type
        if ( Sensor.TYPE_GAME_ROTATION_VECTOR == event.sensor.type ) {

            // lock so we do not get the rotation matrix while it is being changed
            mRotationLock.lock()

            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(
                mRotationMatrix,
                event.values)

            // Unlock, now we have altered the rotation matrix
            mRotationLock.unlock()
        }
    }

    /**
     * function to return a copy of the calculated rotation matrix.
     */
    fun getLatestRotationMatrix(): FloatArray {

        // lock while we cope the rotation matrix
        mRotationLock.lock()

        // take a copy
        val rotMat = mRotationMatrix

        // unlock
        mRotationLock.unlock()

        return rotMat
    }
}