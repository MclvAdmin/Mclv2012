/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.system;

import org.mclv.spacial.hardware.Gyroscope;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.hardware.Accelerometer;
import org.mclv.spacial.FSpace;
import org.mclv.spacial.util.Sampler;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.pack.NavPackage;
import org.mclv.spacial.pack.PositionPackage;
import org.mclv.spacial.pack.PackageList;
import org.mclv.spacial.StreamTask;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author God
 */
public class Inertial implements StreamTask,PhaseNotify,Runnable{
    private static final double PI = Math.PI;
    private static final double G = 9.81;
    private Sampler accel;
    private Sampler gyro;
    private PackLog packLog;
    private LoopTask myTask;
    private long period;
    private PackageBuffer navBuffer = new PackageBuffer(NavPackage.class,Constants.INERTIAL_BUFFER);
    private FSpace mySpace;
    
    private double[] xy = {0,0};
    private double[] vxy = {0,0};
    private double rawAngle = 0; 
    private double angle = PI/2; //robot assumed to be perpendicular to lateral field axis at start
    private double lastUpdate;
    
    public Inertial(double targetEfficiency){ //@TODO add center of rotation data
        this();
    }
    public Inertial(){
        accel = new Sampler(new Accelerometer(),Constants.INERTIAL_RATE,5);
        gyro = new Sampler(new Gyroscope(),Constants.INERTIAL_RATE,5);
        packLog = new PackLog();
        period = (long) (((double) 1000)/((double) Constants.INERTIAL_RATE));
        myTask = new LoopTask(this,period,0);
    }
    public void run(){
        if(mySpace == null){
            mySpace = FSpace.spaceByAction(this);
            lastUpdate = Timer.getFPGATimestamp();
        }
        else{
            updatePosition();
            mySpace.sendAdjacentPack(new PositionPackage(xy,angle));
        }
    }
    private void updatePosition(){
        double[] axy = (double[]) accel.getLastSample();
        rawAngle = toRadians(((Double) gyro.getLastSample()).doubleValue());
        double refAngle = angleInRange(rawAngle);
        angle = angleInRange(rawAngle + PI/2);
        axy[0] = G*(axy[0]*Math.cos(refAngle) + axy[0]*Math.cos(refAngle + PI/2));
        axy[1] = G*(axy[1]*Math.sin(refAngle) + axy[1]*Math.sin(refAngle + PI/2));
        computePosition(axy);
    }
    private void computePosition(double[] axy){
        double t = Timer.getFPGATimestamp() - lastUpdate;
        for(int i = 0; i<2; i++){
            xy[i] = xy[i] + vxy[i]*t + 0.5*axy[i]*t*t; //The easy way :(
        }
        lastUpdate = Timer.getFPGATimestamp();
    }
    private void altComputePosition(){
        
    }
    private double toRadians(double degrees){
        return degrees*PI/180;
    }
    private double angleInRange(double angle){
        angle = angle%(2*PI);
        if(angle < 0){
            angle = 2*PI - angle;
        }
        return angle;
    }
    public PackageList acceptedPacks(){
        Class[] accepted = {};
        return new PackageList(accepted);
    }
    public PackLog packLog(){
        return packLog;
    }
    public void feedData(Object data){
        if(data instanceof Package){
            packLog.newPack((Package) data);
        }
    }
}
