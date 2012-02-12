/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class SensorInputPackage extends Package implements PackObscure{ //@TODO write sensor package
    private static final int ACCEL_TYPE = 0; //double a in m/s
    private static final int GYRO_TYPE = 1; //double angle in radians
    private static final int ENCODER_TYPE = 2; //double turn rate in radians/sec
    private static final int LINE_TYPE = 3; //boolean 
    private Package pack;
    public SensorInputPackage(int type, Package pack){
        super();
        this.pack = pack;
    }
    public int dataSize(){
        return 1;
    }
    public Package getPack(){ //contains the actual data in the form of a package-wrapped primitive
        return pack;
    }
}
