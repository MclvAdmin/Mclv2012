/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.net;

import com.sun.squawk.util.MathUtils;
import java.util.Hashtable;
import java.util.Vector;
import org.mclv.spacial.FList;
import org.mclv.spacial.FSpace;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.hardware.BusManager;
import org.mclv.spacial.hardware.Jaguar;
import org.mclv.spacial.pack.BooleanPackage;
import org.mclv.spacial.pack.DoublePackage;
import org.mclv.spacial.pack.InPackage;
import org.mclv.spacial.pack.NetPackage;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.pack.PackageList;
import org.mclv.spacial.pack.SyncPackage;
import org.mclv.spacial.system.LoopTask;

/**
 *
 * @author God
 */
public class RemoteHardware implements Runnable,StreamTask{
    private static final byte MOTOR_TYPE = BusManager.MOTOR_TYPE;
    private static final byte SOLENOID_TYPE = BusManager.SOLENOID_TYPE;
    private static final byte SERVO_TYPE = BusManager.SERVO_TYPE;
    private static final byte INERTIAL_TYPE = BusManager.INERTIAL_TYPE;
    private static final byte SEND = NetPackage.SEND;
    private static final byte REQUEST = NetPackage.REQUEST;
    private static final byte SEND_CONFIG = NetPackage.SEND_CONFIG;
    private static final byte REQUEST_CONFIG = NetPackage.REQUEST_CONFIG;
    private static final byte INT = NetPackage.INT;
    private static final byte DOUBLE = NetPackage.DOUBLE;
    private static final byte LONG = NetPackage.LONG;
    private static final byte BYTE = NetPackage.BYTE;
    private static final int TYPES = 4;
    private FList hardware;
    private FSpace hardwareSpace;
    private FSpace mySpace;
    private Hashtable typeByHType = new Hashtable();
    private Hashtable hardwareByType = new Hashtable();
    private PackLog packLog = new PackLog();
    private PackageBuffer inBuffer = new PackageBuffer(InPackage.class,1000);
    public RemoteHardware(){
        for(byte i = 0; i<TYPES; i++){
            hardwareByType.put(new Byte(i), new Vector(0));
        }
    }
    public void run(){
        if(mySpace == null){
            mySpace = FSpace.spaceByAction(this);
        }
        else if(hardwareSpace == null){
            hardwareSpace = new FSpace(mySpace);
        }
        else{
            if(inBuffer.hasData()){
                actPacks();
            }
        }
    }
    private void actPacks(){
        Package[] packs = inBuffer.newBuffer();
        for(int i = 0; i<packs.length; i++){
            InPackage thisPack = (InPackage) packs[i];
            byte type = thisPack.type();
            if(type == SEND){
                assign(thisPack.address(),thisPack.store().wrappedPrims());
            }
            else if(type == SEND_CONFIG){
                config(thisPack.address(),thisPack.store().wrappedPrims());
            }
            //Add new hardware?
        }
    }
    public void assign(byte[] address,Object[] data){
        Package pack;
        byte type = address[0];
        if(assignable(type)){
            for(int i = 0; i<data.length; i++){
                pack = packByType(type, data[i]);
                hardware.sendPack(new SyncPackage(pack,group(address)));
            }
        }
    }
    public void config(byte[] address,Object[] data){
        if(!hasAddress(address)){
            newHardware(address, data);
        }
    }
    private boolean assignable(byte type){
        if(type == MOTOR_TYPE || type == SERVO_TYPE || type == SOLENOID_TYPE){
            return true;
        }
        return false;
    }
    private Package packByType(byte type, Object data){
        switch(type){
            case MOTOR_TYPE: return new DoublePackage(((Double) data).doubleValue());
            case SERVO_TYPE: return new DoublePackage(((Double) data).doubleValue());
            case SOLENOID_TYPE: return new BooleanPackage(((Boolean) data).booleanValue());
        }
        return null;
    }
    private void newHardware(byte[] address, Object[] data){
        System.out.println("New Hardware: " + address[0]);
        int group = group(address);
        Runnable hardwareElement = null;
        Vector thisVect = (Vector) hardwareByType.get(new Byte(address[0]));
        thisVect.addElement(new Byte(address[1]));
        switch(address[0]){
            case MOTOR_TYPE: Jaguar jag = new Jaguar(); jag.setSyncGroup(group); hardwareElement = jag;
            case SOLENOID_TYPE: ;
            case SERVO_TYPE: ;
            case INERTIAL_TYPE: ;
        }
        LoopTask task = new LoopTask(hardwareElement,10,0);
        hardware.addSpace(new FSpace(task,hardwareSpace));
    }
    private int group(byte[] address){
        int val = 0;
        for(int i = 0; i<address.length; i++){
            val += (address[i] + 128)*MathUtils.pow(i, i + 3);
        }
        return val;
    }
    public boolean hasAddress(byte[] address){
        if(!hardwareByType.containsKey(new Byte(address[0]))){
            return false;
        }
        if(!((Vector) hardwareByType.get(new Byte[address[0]])).contains(new Byte(address[1]))){
            return false;
        }
        return true;
    }
    public PackLog packLog(){
        return packLog;
    }
    public PackageList acceptedPacks(){
        Class[] list = {InPackage.class};
        return new PackageList(list);
    }
    public void feedData(Object data){
        if(data instanceof Package){
            packLog.newPack((Package) data);
            if(data instanceof InPackage){
                System.out.println("Hardware Data");
                inBuffer.addPack((InPackage) data);
            }
        }
    }
}
