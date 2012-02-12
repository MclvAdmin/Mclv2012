/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.system;

import org.mclv.spacial.*;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.pack.PhasePackage;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import java.util.Hashtable;
/**
 *
 * @author 1155
 */
public class PhaseNotifier implements Runnable{
    private boolean time = false;
    private int lastPhase = Constants.NULL_GPHASE;
    private int thisPhase = Constants.NULL_GPHASE;
    private Integer nullPhase = new Integer(Constants.NULL_GPHASE);
    private Integer autoPhase = new Integer(Constants.AUTO_GPHASE);
    private Integer teleopPhase = new Integer(Constants.TELEOP_GPHASE);
    private DriverStation ds = DriverStation.getInstance();
    private double start;
    private Hashtable times = new Hashtable();
    public PhaseNotifier(){
        start = Timer.getFPGATimestamp();
        double[] entry = {Timer.getFPGATimestamp()};
        double[] nullEntry = new double[0];
        times.put(nullPhase, entry);
        times.put(autoPhase, nullEntry);
        times.put(teleopPhase, nullEntry);
    }
    public void run(){
        PhasePackage pack = makePack();
        if(pack.gamePhase() != thisPhase){
            //updateTime();
            lastPhase = thisPhase;
            thisPhase = pack.gamePhase();
            FSpace.allSpaces().sendPack(pack);
        }
    }
    public double phaseTime(){
        double time = 0;
        if(thisPhase == Constants.NULL_GPHASE){
            time = Timer.getFPGATimestamp() - ((double[]) times.get(nullPhase))[0];
        }
        else if(thisPhase == Constants.AUTO_GPHASE){
            time = Timer.getFPGATimestamp() - ((double[]) times.get(autoPhase))[0];
        }
        else if(thisPhase == Constants.TELEOP_GPHASE){
            time = Timer.getFPGATimestamp() - ((double[]) times.get(teleopPhase))[0];
        }
        return time;
    }
    private PhasePackage makePack(){
        int phase = Constants.NULL_GPHASE;
        if(ds.isAutonomous()){
            phase = Constants.AUTO_GPHASE;
        }
        else if(ds.isOperatorControl()){
            phase = Constants.TELEOP_GPHASE;
        }
        PhasePackage returnPack = new PhasePackage(phase);
        return returnPack;
    }
    private void updateTime(){//@TODO consider gap between game periods
        if(thisPhase == Constants.NULL_GPHASE){
            if(lastPhase == Constants.AUTO_GPHASE){
                double[] entry = expand((double[]) times.get(autoPhase));
                entry[1] = Timer.getFPGATimestamp();
                times.put(autoPhase, entry);
            }
            else if(lastPhase == Constants.TELEOP_GPHASE){
                double[] entry = expand((double[]) times.get(teleopPhase));
                entry[1] = Timer.getFPGATimestamp();
                times.put(teleopPhase, entry);
            }
        }
        else{
            if(thisPhase == Constants.AUTO_GPHASE){
                double[] entry = expand((double[]) times.get(autoPhase));
                entry[0] = Timer.getFPGATimestamp();
                times.put(autoPhase, entry);
            }
            else if(thisPhase == Constants.TELEOP_GPHASE){
                double[] entry = expand((double[]) times.get(teleopPhase));
                entry[0] = Timer.getFPGATimestamp();
                times.put(teleopPhase, entry);
            }
            
            if(lastPhase == Constants.NULL_GPHASE){
                double[] entry = expand((double[]) times.get(nullPhase));
                entry[1] = Timer.getFPGATimestamp();
                times.put(nullPhase, entry);
            }
            else if(lastPhase == Constants.AUTO_GPHASE){
                double[] entry = expand((double[]) times.get(autoPhase));
                entry[1] = Timer.getFPGATimestamp();
                times.put(autoPhase, entry);
            }
            else if(lastPhase == Constants.TELEOP_GPHASE){
                double[] entry = expand((double[]) times.get(teleopPhase));
                entry[1] = Timer.getFPGATimestamp();
                times.put(teleopPhase, entry);
            }
        }
    }
    private static double[] expand(double[] a){
        if(a.length<2){
            double[] returnArray = new double[a.length + 1];
            System.arraycopy(a, 0, returnArray, 0, a.length);
            return returnArray;
        }
        else{
            return a;
        }
    }
}
