/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.system;

import org.mclv.spacial.FSpace;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.pack.DrivePackage;
import org.mclv.spacial.pack.NavPackage;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.pack.PackageList;
import org.mclv.spacial.pack.PhasePackage;
import org.mclv.spacial.pack.PositionPackage;
import com.sun.squawk.util.MathUtils;

/**
 *
 * @author God
 */
public class Navigator implements StreamTask,PhaseNotify,Runnable{
    private static final double PI = Math.PI;
    private int gPhase = Constants.NULL_GPHASE;
    private PackLog packLog = new PackLog();
    private FSpace mySpace;
    private PackageBuffer navBuffer = new PackageBuffer(NavPackage.class,Constants.INERTIAL_BUFFER);
    //private PackageBuffer positionBuffer = new PackageBuffer(PositionPackage.class,Constants.INERTIAL_BUFFER);
    
    private boolean nav = true;
    
    private double distanceDev;
    private double angleDev;
    private double[] dest;
    private double[] xy;
    private double angle;
    
    private static double turn = Constants.TURN_RATE;
    private static double straight = Constants.STRAIGHT_RATE;
    public Navigator(){
        this(Constants.DISTANCE_DEV,Constants.ANGLE_DEV);
    }
    public Navigator(double distanceDev, double angleDev){
        this.distanceDev = distanceDev;
        this.angleDev = angleDev;
    }
    
    public void feedData(Object data){
        if(data instanceof Package){
            packLog.newPack((Package) data);
            if(data instanceof PhasePackage){
                gPhase = ((PhasePackage) data).gamePhase();
                packLog.consumePack((Package) data); //@TODO cast as specific type of package?
            }
            else if(data instanceof NavPackage){
                if(((NavPackage)data).halt()){
                    stopNav(); //allows external control of nav loop
                }
                else{
                    nav = true;
                    navBuffer.addPack((Package) data);
                }
            }
            else if(data instanceof PositionPackage){
                //positionBuffer.addPack((Package) data);
                xy = ((PositionPackage) data).s();
                angle = ((PositionPackage) data).angle();
            }
        }
    }

    public PackLog packLog(){
        return packLog;
    }

    public PackageList acceptedPacks(){
        Class[] list = {PhasePackage.class, NavPackage.class, PositionPackage.class};
        return new PackageList(list);
    }

    public void run(){
       if(mySpace == null){
            mySpace = FSpace.spaceByAction(this);
        }
        else{
            if(gPhase != Constants.NULL_GPHASE){
               if(xy != null && navBuffer.hasData()){
                   while(nav){
                       navigate(); //automatically breaks nav loop
                   }
               }
               mySpace.sendAdjacentPack(navigate());
            }
        }
    }
    private DrivePackage navigate(){
        double[] out = {0,0};
        if(!atDest()){
            if(!aligned()){
                if(angleToDest() < 0){
                    out[0] = turn;
                    out[1] = -turn;
                }
                else{
                    out[0] = -turn;
                    out[1] = turn;
                }
            }
            else{
                out[0] = straight;
                out[1] = straight;
            }
        }
        else{
            stopNav();
        }
        return new DrivePackage(out);
    }
    private boolean atDest(){
        if(distanceToDest() < distanceDev){
            return true;
        }
        return false;
    }
    private boolean aligned(){
        if(angleToDest() < angleDev){
            return true;
        }
        return false;
    }
    private double distanceToDest(){
        return Math.sqrt((xy[0] - dest[0])*(xy[0] - dest[0]) + (xy[1] - dest[1])*(xy[1] - dest[1]));
    }
    private double angleToDest(){
        return refAngleToDest() - angle;
    }
    private double refAngleToDest(){
        double a = 0;
        a = MathUtils.acos(Math.abs(xy[0] - dest[0])/distanceToDest());
        if(xy[1] < dest[1]){
            if(xy[0] < dest[0]){
                a = (3/2)*PI - a;
            }
            else{
                a = (3/2)*PI + a;
            }
        }
        else{
            if(xy[0] < dest[0]){
                a = PI - a;
            }
        }
        return a;
    }
    private double angleInRange(double a){
        a %= 2*PI;
        if(a<0){
            a = 2*PI + a;
        }
        return a;
    }
    private void stopNav(){
        nav = false;
        if(mySpace != null){
            mySpace.sendAdjacentPack(DrivePackage.NULL_PACKAGE);
        }
    }
}
