/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.hardware;

import java.util.Hashtable;
import org.mclv.spacial.config.Constants;
/**
 *
 * @author God
 */
public class BusManager {
    public static final byte MOTOR_TYPE = 0;
    public static final byte SOLENOID_TYPE = 1;
    public static final byte SERVO_TYPE = 2;
    public static final byte INERTIAL_TYPE = 3;
    public static final int PWM = 0;
    public static final int CAN = 1;
    public static final int USB = 2;
    public static final int ANALOG = 3;
    public static final int DIGITAL = 4;
    private static Hashtable assignment = new Hashtable();
    public static int newPwm(){
        return getPort(PWM,Constants.DIGITAL_SLOT);
    }
    public static int newAnalog(){
        return getPort(ANALOG,Constants.ANALOG_SLOT);
    }
    public static int newCan(){
        return getPort(CAN,0);
    }
    public static int newUsb(){
        return getPort(USB,0);
    }
    public static int getPort(int type, int slot){
        Integer slotInt = new Integer(slot);
        if(assignment.containsKey(slotInt)){
            Integer typeInt = new Integer(type);
            Hashtable entry = (Hashtable) assignment.get(slotInt);
            if(entry.containsKey(typeInt)){
                Hashtable typeEntry = (Hashtable) entry.get(typeInt);
                int typeMax = maxByType(type);
                for(int i = typeMin(type); i<=typeMax; i++){
                    Integer portInt = new Integer(i);
                    if(!typeEntry.containsKey(portInt)){
                        typeEntry.put(portInt, Boolean.TRUE);
                        return i;
                    }
                }
            }
            else{
                entry.put(typeInt, new Hashtable());
                return getPort(type,slot);
            }
        }
        else if(intContains(Constants.SLOTS_USED,slot)){
            Hashtable entry = new Hashtable();
            entry.put(new Integer(type), new Hashtable());
            assignment.put(slotInt, entry);
            return getPort(type,slot);
        }
        throw new NullPointerException();
    }
    public static void releasePort(int type, int slot, int port){
        
    }
    private static int maxByType(int type){
        switch(type){
            case PWM: return 10;
            case ANALOG: return 10;
            case CAN: return 10;
            case USB: return 4;
            case DIGITAL: return 10;
        }
        return 0;
    }
    private static int typeMin(int type){
        switch(type){
            case PWM: return 1;
            case ANALOG: return 1;
            case CAN: return 1;
            case USB: return 1;
            case DIGITAL: return 1;
        }
        return 0;
    }
    private static boolean intContains(int[] a, int b){
        for(int i = 0; i<a.length; i++){
            if(a[i] == b){
                return true;
            }
        }
        return false;
    }
}
