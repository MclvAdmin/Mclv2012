/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import org.mclv.spacial.config.Constants;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import edu.wpi.first.wpilibj.Timer;
/**
 *
 * @author romana
 */
public class PackLog {
    private Hashtable log;
    private boolean hasTime;
    private Hashtable packLog;
    private Hashtable timeLog;
    public PackLog(){
        log = new Hashtable();
    }
    public PackLog(boolean hasTime){
        this.hasTime = hasTime;
        log = new Hashtable();
        packLog = new Hashtable();
        timeLog = new Hashtable();
    }
    public void newPack(Package pack){ //@TODO make interface for packages that contain >1 new datum
        /*if(pack instanceof PackObscure){
            pack = ((PackObscure) pack).getPack();
        }*/
        Class packClass = pack.getClass();
        /*if(Constants.debug){
            //System.out.println("New Pack: " +packClass.getName());
            if(packClass == HumanInputPackage.class){
                System.out.println("Human Pack: Joy1 test axis: ");
                System.out.println("Axis 1:" + ((HumanInputPackage) pack).axis(HumanInputPackage.DRIVE, HumanInputPackage.LEFT, 1));
            }
            if(packClass == DrivePackage.class){
                //System.out.println("Drive Pack: left, right:");
                //System.out.println(((DrivePackage) pack).left() + " | " + ((DrivePackage) pack).right());
            }
            else
            if(packClass == PhasePackage.class){
                String phase = "";
                int gPhase = ((PhasePackage) pack).gamePhase();
                if(gPhase == Constants.NULL_GPHASE){
                    phase = "Null";
                }
                else if(gPhase == Constants.AUTO_GPHASE){
                    phase = "Auto";
                }
                else if(gPhase == Constants.TELEOP_GPHASE){
                    phase = "Teleop";
                }
                System.out.println(phase + " phase");
            }
            if(packClass == DoublePackage.class){
                System.out.println(packClass.getName());
            }
        }*/
        if(log.containsKey(packClass)){
            int[] entry = (int[]) log.get(packClass);
            entry[0] += pack.dataSize();
        }  
        if(hasTime){
            Vector entry = new Vector(0);
            entry.addElement(pack);
            entry.addElement(new Double(Timer.getFPGATimestamp()));
            packLog.put(pack.id(), entry);
        }
        if(log.containsKey(packClass)){
            int[] entry = (int[]) log.get(packClass);
            entry[0] += pack.dataSize();
        }
        else{
            int[] entry = {pack.dataSize(),0};
            log.put(packClass, entry);
        }
    }
    public void consumePack(Package pack){
        Class packClass;
        if(pack != null){
            packClass = pack.getClass(); //@TODO find cause of nullpointerexception
        }
        else{
            packClass = Package.class;
        }
        if(log.containsKey(packClass)){
            int[] entry = (int[]) log.get(packClass);
            entry[1] += pack.dataSize();
        }
        else{
            int[] entry = {0,pack.dataSize()}; //impossible but w.e.
            log.put(packClass, entry);
        }
        
        if(hasTime){
            double waitTime = Timer.getFPGATimestamp() - ((Double) ((Vector) packLog.get(pack.id())).elementAt(1)).doubleValue();
            packLog.remove(pack.id());
            if(timeLog.containsKey(packClass)){
                Vector entry = ((Vector) timeLog.get(packClass)); //@TODO make more efficient to save memory
                entry.addElement(new Double(waitTime));
            }
            else{
                Vector entry = new Vector(0);
                entry.addElement(new Double(waitTime));
                timeLog.put(packClass, entry);
            }
        }
    }
    public int newData(Class packClass){
        if(log.containsKey(packClass)){
            int[] entry = (int[]) log.get(packClass);
            return entry[0];
        }
        return 0; //@TODO add execption
    }
    public int consumedData(Class packClass){
        if(log.containsKey(packClass)){
            int[] entry = (int[]) log.get(packClass);
            return entry[1];
        }
        return 0; //@TODO add execption
    }
    public double efficiency(Class packClass){
        return ((double) consumedData(packClass))/((double) newData(packClass));
    }
    public boolean hasTime(){
        return hasTime;
    }
    public PackageList packsSeen(){
        Class[] returnArray = new Class[log.size()];
        Enumeration keys = log.keys();
        for(int i = 0; i<returnArray.length; i++){
            returnArray[i] = (Class) keys.nextElement();
        }
        return new PackageList(returnArray);
    }
}
