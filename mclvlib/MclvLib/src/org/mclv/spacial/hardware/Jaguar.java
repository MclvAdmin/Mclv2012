/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.hardware;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import java.util.Random;
import java.util.Vector;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.pack.DoublePackage;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.pack.PackageList;
import org.mclv.spacial.pack.SyncPackage;
/**
 *
 * @author God
 */
public class Jaguar implements Runnable,StreamTask,Syncable{ //@TODO make killable/safe halt
    private static final int SAMPLE_SIZE = Constants.SAMPLE_SIZE;
    private static final boolean randomSample = Constants.RANDOM_SAMPLE;
    private boolean average = Constants.AVERAGE;
    private PackLog packLog;
    private int port;
    private edu.wpi.first.wpilibj.Jaguar jag;
    private CANJaguar cJag;
    private Vector newData;
    private PackageBuffer outBuffer;
    private int totalNew = 0;
    private int totalOut = 0;
    private Random rand;
    private boolean canEnabled = Constants.CAN_DRIVE;
    private boolean canActive = canEnabled;
    private double maxRate;
    private double maxStep;
    private double lastVal = 0;
    private double lastTime = Timer.getFPGATimestamp();
    private boolean safe = false;
    private int syncGroup = 0; //@TODO implement syncGroup
    private boolean sync = false;
    private boolean invert = false;
    public Jaguar(int syncGroup){
        this(false,syncGroup);
    }
    public Jaguar(boolean invert, int syncGroup){
        this(false,invert);
        this.syncGroup = syncGroup;
        sync = true;
    }
    public Jaguar(boolean safe, boolean invert, int syncGroup){
        this(safe,invert);
        this.syncGroup = syncGroup;
        sync = true;
    }
    public Jaguar(){
        this(false);
    }
    public Jaguar(boolean invert){
        this(false,invert);
    }
    public Jaguar(boolean safe, boolean invert){ //@TODO examine jaguar class in context of new buffer structure
        this(Constants.JAG_RATE, Constants.JAG_MAX_STEP);
        this.safe = safe;
        this.invert = invert;
    }
    public Jaguar( double maxRate, double maxStep){
        outBuffer = new PackageBuffer(DoublePackage.class,Constants.COMPUTATION_BUFFER_LENGTH);
        this.maxRate = maxRate;
        this.maxStep = maxStep;
        packLog = new PackLog();
        rand = new Random();
        port = BusManager.newPwm();
        jag = new edu.wpi.first.wpilibj.Jaguar(port);
        if(canEnabled){
            try{ 
                port = BusManager.newCan();
                cJag = new CANJaguar(port);
            }
            catch(CANTimeoutException e){
                canEnabled = false; //disables if fails initialization
            }
        }
        sendOut(0);
        newData = new Vector(0);
    }
    public void run(){
        output();
    }
    private void output(){
        if(outBuffer.hasData()){
            Package[] list = outBuffer.newBuffer();
            double outVal = 0;
            if(average){
                if(!randomSample){
                    for(int i = 0; i<list.length; i++){
                        outVal = outVal + ((DoublePackage) list[i]).doubleValue();
                    }
                    outVal /= list.length;
                }
                else{
                    for(int i = 0; i<Math.min(SAMPLE_SIZE, list.length); i++){
                        int index = Math.max((int) rand.nextDouble()*SAMPLE_SIZE - 1,0);
                        index = Math.min(index, list.length - 1);
                        outVal = outVal + ((DoublePackage) list[i]).doubleValue();
                    }
                    outVal /= SAMPLE_SIZE;
                }
            }
            else{
                if(list.length>0){
                    outVal = ((DoublePackage) list[0]).doubleValue();
                }
            }
            if(invert){
                outVal = -outVal;
            }
            out(outVal);
        }
        else{
            out(0);
        }
        totalOut++;
    }
    private void out(double val){
        if(safe){
            safeOut(val);
        }
        else{
            sendOut(val);
        }
    }
    private void safeOut(double val){ //Handles value stepping @TODO merge with sendOut to use CAN sensor data
        double valDiff = Math.abs(lastVal - val);
        double timeDiff = lastTime - Timer.getFPGATimestamp();
        if(valDiff/timeDiff > maxRate){
            if(lastVal < val){
                val = lastVal + timeDiff*maxRate;
            }
            else{
                val = lastVal - timeDiff*maxRate;
            }
        }
        else{
            if(lastVal < val){
                val = lastVal + maxStep;
            }
            else{
                val = lastVal - maxStep;
            }
        }
        sendOut(val);
        lastTime = Timer.getFPGATimestamp();
        lastVal = val;
    }
    private void sendOut(double val){ //Handles hardware selection
        //System.out.println("Jag output: "+ val);
        if(canActive){
            try{
                cJag.setX(val);
            }
            catch(CANTimeoutException e){
                canActive = false;
            }
        }
        else if(!canEnabled){
            jag.set(val);
        }
        else{ //@TODO analyze impact of this test in drive with disconnect
            try{
                canActive = true;
                cJag.setX(0);
                sendOut(val);
            }
            catch(CANTimeoutException e){
                canActive = false;
            }
        }
    }
    public double efficiency(){
        return ((double) totalOut)/((double) totalNew);
    }
    public void addOut(double val){
        newData.addElement(new Double(val));
        totalNew++;
    }
    public void feedData(Object pack){
        if(pack instanceof Package){
            packLog.newPack((Package) pack);
            if(sync && pack instanceof SyncPackage){
                if(((SyncPackage) pack).match(DoublePackage.class,syncGroup)){
                    feedData((DoublePackage) ((SyncPackage) pack).pack()); //Must cast as double package or outBuffer will not accept
                }
            }  
            if(pack instanceof DoublePackage){
                outBuffer.addPack((Package) pack);
            }
        }
    }
    public void setSyncGroup(int group){
        syncGroup = group;
        this.sync = true;
    }
    public int syncGroup(){
        return syncGroup;
    }
    public PackageList acceptedPacks(){
        Class[] list = {DoublePackage.class};
        if(sync){
            list = new Class[2];
            list[0] = SyncPackage.class;
            list[1] = DoublePackage.class;
        }
        return new PackageList(list);
    }
    public PackLog packLog(){
        return packLog;
    }
}
