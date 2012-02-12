/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.system;

import org.mclv.spacial.FSpace;
import org.mclv.spacial.FList;
import org.mclv.spacial.pack.DrivePackage;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.util.Sampler;
import org.mclv.spacial.hardware.Jaguar;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.DoublePackage;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.pack.PackageList;
import org.mclv.spacial.pack.PhasePackage;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.pack.SyncPackage;

/**
 *
 * @author God
 */
public class DriveManager implements StreamTask,Runnable,PhaseNotify{
    //private Jaguar[][] driveJags;
    private FList[] motors = {new FList(),new FList()};
    private FSpace[] motorSpaces = {new FSpace(null), new FSpace(null)}; //left and right spaces
    private FSpace motorSpace = new FSpace(null);
    private Sampler[] jagTemp;
    private PackageBuffer driveBuffer;
    private PackLog packLog;
    private int gPhase = Constants.NULL_GPHASE;
    public DriveManager(boolean[] inv){
        packLog = new PackLog();
        driveBuffer = new PackageBuffer(DrivePackage.class, 100);
        /*driveJags = new Jaguar[2][2];
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                driveJags[i][j] = new Jaguar();
            }
        }*/
        /*for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                motors[i].addSpace(new FSpace(new LoopTask(new Jaguar(i),10,0),motorSpaces[i]));
            }
        }*/
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                motors[i].addSpace(new FSpace(new LoopTask(new Jaguar(Constants.JAG_SAFE,inv[i],i),10,0),motorSpace));
            }
        }
    }
    public DriveManager(){
        this(defaultInv());
    }
    public PackageList acceptedPacks(){
        Class[] list = {DrivePackage.class,PhasePackage.class};
        return new PackageList(list);
    }
    public void run(){
        if(gPhase != Constants.NULL_GPHASE){
            if(driveBuffer.hasData()){
                DrivePackage lastPack = (DrivePackage) driveBuffer.lastPack();
                double[] out = {lastPack.left(),lastPack.right()};
                if(gPhase == Constants.AUTO_GPHASE){
                    outputDirect(out);
                }
                else{
                    outputSafe(out);
                }
                packLog.consumePack(lastPack);
            }
        }
    }
    public void feedData(Object pack){
        if(pack instanceof Package){
            packLog.newPack((Package) pack);
            if(pack instanceof DrivePackage){
                driveBuffer.addPack((Package) pack);
            }
            else if(pack instanceof PhasePackage){
                gPhase = ((PhasePackage) pack).gamePhase();
                packLog.consumePack((Package) pack);
            }
        }
    }
    private void outputDirect(double[] out){
        /*for(int i = 0; i<driveJags.length; i++){
            for(int j = 0; i<driveJags[i].length; j++){
                driveJags[i][j].addOut(out[i]);
            }
        }*/
        //System.out.println("Left: " + out[0] + "Right: " + out[1]);
        for(int i = 0; i<motorSpaces.length; i++){
            //motorSpaces[i].subSpaces().sendPack(new DoublePackage(out[i]));
            motorSpace.subSpaces().sendPack(new SyncPackage(new DoublePackage(out[i]),i));
        }
        //FSpace.allSpaces().sendPack(new DoublePackage(out[0]));
    }
    private void outputSafe(double[] out){
        outputDirect(out);
    }
    public PackLog packLog(){
        return packLog;
    }
    private static boolean[] defaultInv(){
        boolean[] returnArray = {false,false};
        return returnArray;
    }
}
