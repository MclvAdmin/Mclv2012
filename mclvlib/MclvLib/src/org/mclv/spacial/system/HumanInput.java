/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial.system;

import org.mclv.spacial.config.Constants;
import org.mclv.spacial.hardware.BusManager;
import org.mclv.spacial.util.Sampler;
import org.mclv.spacial.FSpace;
import edu.wpi.first.wpilibj.Joystick;
import java.util.Vector;
import org.mclv.spacial.pack.HumanInputPackage;
import org.mclv.spacial.hardware.JoySamp;
/**
 *
 * @author romana
 */
public class HumanInput implements Runnable{
    private FSpace mySpace;
    private static int driveSticks = Constants.DRIVE_STICKS;
    private static int auxSticks = Constants.AUX_STICKS;
    private Joystick[] lr;
    private Joystick[] aux;
    private Sampler[] lrSample;
    private Sampler[] auxSample;
    public HumanInput(int sampleRate, int bufferSeconds){
        lr = new Joystick[driveSticks];
        lrSample = new Sampler[driveSticks];
        aux = new Joystick[auxSticks];
        auxSample = new Sampler[auxSticks];
        for(int i = 0; i<lr.length; i++){
            int port = BusManager.newUsb();
            System.out.println("Port: " + port);
            lr[i] = new Joystick(port);
            lrSample[i] = new Sampler(new JoySamp(lr[i]),sampleRate,bufferSeconds); //rate = variable, 5 seconds of data buffered. Sampler constructor configured to start thread automatically
        }
        for(int i = 0; i<aux.length; i++){
            aux[i] = new Joystick(BusManager.newUsb());
            auxSample[i] = new Sampler(new JoySamp(aux[i]),sampleRate,bufferSeconds); //rate = variable, 5 seconds of data buffered
        }
    }
    public void run(){
        if(mySpace == null){
            mySpace = FSpace.spaceByAction(this);
        }
        outputToNext();
    }
    private void outputToNext(){
        /*FSpace outSpace = mySpace.adjacentSpaces().matchPack(HumanInputPackage.class);
        if(outSpace != null){
            outSpace.feedData(makeOutputPackage()); //@todo explore assembling packet over time/sampling joy values
        }*/
        mySpace.sendAdjacentPack(makeOutputPackage());
    }
    private HumanInputPackage makeOutputPackage(){ //combines all samples in a manner prescribed by HumanInputPackage
        Vector drive = new Vector(0);
        Vector aux = new Vector(0);
        for(int i = 0; i<lrSample.length; i++){
            drive.addElement(lrSample[i].getLastSample());
        }
        for(int i = 0; i<auxSample.length; i++){
            aux.addElement(auxSample[i].getLastSample());
        }
        HumanInputPackage pack = new HumanInputPackage(drive, aux);
        return pack;
    }
}