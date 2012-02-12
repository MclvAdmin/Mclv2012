/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.hardware;

import org.mclv.spacial.config.Constants;
import org.mclv.spacial.hardware.BusManager;
import edu.wpi.first.wpilibj.Gyro;
import org.mclv.spacial.util.Sampleable;
/**
 *
 * @author God
 */
public class Gyroscope implements Sampleable{
    private Gyro gyro;
    public Gyroscope(){
        gyro = new Gyro(Constants.ANALOG_SLOT,BusManager.newAnalog());
    }
    public Object getObj(){
        Double returnVal = new Double(gyro.getAngle());
        return returnVal;
    }
}
