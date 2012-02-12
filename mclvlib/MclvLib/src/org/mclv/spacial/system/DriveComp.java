/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.system;

import org.mclv.spacial.pack.DrivePackage;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.FSpace;
import com.sun.squawk.util.MathUtils;
import org.mclv.spacial.util.Optimizable;
import org.mclv.spacial.pack.HumanInputPackage;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageList;
import org.mclv.spacial.pack.PhasePackage;
import org.mclv.spacial.pack.PriorityPackage;
import org.mclv.spacial.pack.SensorInputPackage;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.StreamTask;
/**
 *
 * @author God
 */
public class DriveComp implements StreamTask,PhaseNotify,Optimizable{
    private static int bufferLength = Constants.COMPUTATION_BUFFER_LENGTH;
    private static DrivePackage nullPack = DrivePackage.NULL_PACKAGE;
    private PackLog packLog;
    private PackageBuffer humanBuffer = new PackageBuffer(HumanInputPackage.class, bufferLength);
    private PackageBuffer sensorBuffer = new PackageBuffer(SensorInputPackage.class, bufferLength);
    private int newHuman = 0;
    private int newSensor = 0;
    private Class[] priorityList;
    private int gamePhase = Constants.NULL_GPHASE;
    private long timeLeft = 0;
    private long absoluteTime = 0;
    private FSpace mySpace = null;
    public DriveComp(){
        packLog = new PackLog();
        //humanBuffer = new HumanInputPackage[bufferLength];
        //sensorBuffer = new SensorInputPackage[bufferLength];
        priorityList = new Class[2];
        priorityList[0] = HumanInputPackage.class;
        priorityList[1] = SensorInputPackage.class;
    }
    public void run(){
        if(mySpace == null){
            System.out.println("NULL");
            mySpace = FSpace.spaceByAction(this);
        }
        else{
            DrivePackage outPack = computeByPhase();
            //FSpace destination = (FSpace.adjacentByAction(this)).matchPack(DrivePackage.class);
            //FSpace destination = mySpace.adjacentSpaces().matchPack(DrivePackage.class);
            //destination.feedData(outPack);
        
            mySpace.sendAdjacentPack(outPack);
            //System.out.println("CompSent: " + outPack.left() + " " + outPack.right());
            //FSpace.allSpaces().sendPack(outPack);
        }
    }
    public PackageList acceptedPacks(){
        Class[] classes = new Class[3];
        classes[0] = HumanInputPackage.class;
        classes[1] = PhasePackage.class;
        classes[2] = SensorInputPackage.class;
        return new PackageList(classes);
    }
    private DrivePackage computeByPhase(){
        if(gamePhase == Constants.NULL_GPHASE){
            return nullPack;
        }
        else if(gamePhase == Constants.AUTO_GPHASE){
            return computeAuto();
        }
        else if(gamePhase == Constants.TELEOP_GPHASE){
            return computeTeleop();
        }
        return nullPack;
    }
    private DrivePackage computeAuto(){ //@TODO write auto computation method. Take input as task packages
        return nullPack;
    }
    private DrivePackage computeTeleop(){
        HumanInputPackage pack = (HumanInputPackage) humanBuffer.lastPack();
        if(pack == null){
            return nullPack;
        }
        packLog.consumePack(pack);
        boolean[] leftButtons = pack.buttons(HumanInputPackage.DRIVE, HumanInputPackage.LEFT);
        boolean[] rightButtons = pack.buttons(HumanInputPackage.DRIVE, HumanInputPackage.RIGHT);
        double leftAxis = pack.axis(HumanInputPackage.DRIVE, HumanInputPackage.LEFT, Constants.DRIVE_AXIS);
        double rightAxis = pack.axis(HumanInputPackage.DRIVE, HumanInputPackage.RIGHT, Constants.DRIVE_AXIS);
        if(leftButtons[2]){
            leftAxis /= 2;
            rightAxis /= 2;
        }
        else if(leftButtons[3]){
            leftAxis = sqrtCurve(leftAxis);
            rightAxis = sqrtCurve(rightAxis);
        }
        else if(leftButtons[4]){
            leftAxis = squareCurve(leftAxis);
            rightAxis = squareCurve(rightAxis);
        }
        else if(leftButtons[1] &! rightButtons[1]){
            leftAxis = 1;
            rightAxis = 1;
        }
        else if(rightButtons[1] &! leftButtons[1]){
            leftAxis = -1;
            rightAxis = -1;
        }
        //MIX
        if(Math.abs(leftAxis - rightAxis) < Constants.MIX_THRESHOLD){
            double average = (leftAxis + rightAxis)/2;
            leftAxis = average;
            rightAxis = average;
        }
        return new DrivePackage(leftAxis, rightAxis);
    }
    private double sqrtCurve(double in){
        boolean neg = false;
        if(in < 0){
            neg = true;
            in = -in;
        }
        in = Math.sqrt(in);
        if(neg){
            in = -in;
        }
        return in;
    }
    private double squareCurve(double in){
        boolean neg = false;
        if(in < 0){
            neg = true;
            in = -in;
        }
        in = MathUtils.pow(in, 2);
        if(neg){
            in = -in;
        }
        return in;
    }
    public synchronized void feedData(Object data){
        if(data instanceof Package){
            packLog.newPack((Package) data);
            if(data instanceof PriorityPackage){
                priorityList = ((PriorityPackage) data).sortedPriority();
                packLog.consumePack((Package) data);
            }
            else if(data instanceof HumanInputPackage){
                HumanInputPackage pack = (HumanInputPackage) data;
                humanBuffer.addPack(pack);
                //System.out.println("Efficiency: " + packLog.efficiency(HumanInputPackage.class)); //@TODO write loop time monitor
                //addHumanBuffer(pack);
            }
            else if(data instanceof SensorInputPackage){
                SensorInputPackage pack = (SensorInputPackage) data;
                //addSensorBuffer(pack);
                sensorBuffer.addPack(pack);
                packLog.consumePack((Package) data);
            }
            else if(data instanceof PhasePackage){
                //System.out.println("Phase Pack");
                PhasePackage pack = (PhasePackage) data;
                gamePhase = pack.gamePhase();
                if(pack.hasTime()){
                    timeLeft =  pack.timeLeft();
                    absoluteTime = pack.absoluteTime();
                }
                packLog.consumePack((Package) data);
            }
        }
        else if(data instanceof HumanInputPackage[]){
            
        }
        else if(data instanceof SensorInputPackage[]){
            
        }
    }
    /*private void addHumanBuffer(HumanInputPackage pack){
        System.arraycopy(humanBuffer, 0, humanBuffer, 1, humanBuffer.length - 1);
        humanBuffer[0] = pack;
        newHuman++;
    }
    private void addSensorBuffer(SensorInputPackage pack){
        System.arraycopy(sensorBuffer, 0, sensorBuffer, 1, sensorBuffer.length - 1);
        sensorBuffer[0] = pack;
        newSensor++;
    }*/
    public PackLog packLog(){
        return packLog;
    }
    public double efficiency(){ //Use linear combination instead
        if(gamePhase == Constants.TELEOP_GPHASE){
            return packLog.efficiency(HumanInputPackage.class);
        }
        else if(gamePhase == Constants.AUTO_GPHASE){
            return packLog.efficiency(SensorInputPackage.class);
        }
        return 1;
    }
}
