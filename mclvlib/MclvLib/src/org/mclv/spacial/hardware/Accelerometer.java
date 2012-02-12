/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.hardware;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.util.Sampleable;
/**
 *
 * @author God
 */
public class Accelerometer implements Sampleable{
    private ADXL345_I2C accel;
    public Accelerometer(){
        accel = new ADXL345_I2C(Constants.DIGITAL_SLOT, ADXL345_I2C.DataFormat_Range.k16G);
    }
    public Object getObj(){
        double[] xyz = {accel.getAcceleration(ADXL345_I2C.Axes.kX),accel.getAcceleration(ADXL345_I2C.Axes.kY),accel.getAcceleration(ADXL345_I2C.Axes.kZ)};
        return xyz;
    }
}
