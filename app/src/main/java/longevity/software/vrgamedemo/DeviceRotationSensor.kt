package longevity.software.vrgamedemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.concurrent.locks.ReentrantLock

class DeviceRotationSensor(context: Context): SensorEventListener, LookControlInterface {

    // lock to prevent the rotation matrix from being read and written to at the same time
    private val mRotationLock = ReentrantLock()

    private var mSensorManager: SensorManager

    private var mPitch: Float
    private var mYaw: Float

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

        // set the rotation angles
        mPitch = 0.0f
        mYaw = 0.0f
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

            // TODO - generate pitch, roll and yaw angles

            // Unlock, now we have altered the rotation matrix
            mRotationLock.unlock()
        }
    }

    /**
     * overriden function from the LookControlInterface
     * returns a copy of the calculated pitch.
     */
    override fun getLatestPitch(): Float {

        mRotationLock.lock()

        val pitch = mPitch

        mRotationLock.unlock()

        return pitch
    }

    /**
     * overriden function from the LookControlInterface
     * returns a copy of the calculated yaw.
     */
    override fun getLatestYaw(): Float {

        mRotationLock.lock()

        val yaw = mYaw

        mRotationLock.unlock()

        return yaw
    }
}