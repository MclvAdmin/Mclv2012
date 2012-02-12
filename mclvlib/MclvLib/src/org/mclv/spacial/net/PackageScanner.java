/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.net;

import edu.wpi.first.wpilibj.Timer;
import java.io.DataInputStream;
import java.io.IOException;
import org.mclv.spacial.pack.InPackage;
import org.mclv.spacial.pack.NetPackage;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageBuffer;
import org.mclv.spacial.util.Array;

/**
 *
 * @author God
 */
public class PackageScanner implements Runnable{
    private static final byte[] START = NetPackage.START;
    private static final byte[] END = NetPackage.END;
    private static double timeout = 0.25;
    private DataInputStream in;
    private PackageBuffer packBuffer;
    private int nextStart = 0;
    private int nextEnd = 0;
    private double startTime;
    public PackageScanner(DataInputStream in){
        this.in = in;
        packBuffer = new PackageBuffer(InPackage.class,1000);
    }
    public void run(){
        scanStream();
    }
    private void scanStream(){
        startTime = Timer.getFPGATimestamp();
        try{
            int len = in.available();
            //System.out.println("Data found: length: " + len);
            if(len >= START.length){
                byte[] newData = new byte[len];
                in.readFully(newData);
                if(hasCode(newData,START)){
                    int index = nextStart;
                    byte[] data = Array.section(index + START.length, newData);
                    InPackage pack = waitAndMake(data);
                    if(pack != null){
                        System.out.print(pack.toString());
                        packBuffer.addPack(pack);
                    }
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getClass());
        }
    }
    private InPackage waitAndMake(byte[] data){ //@TODO add timeout with TimerAction class
        boolean stop = false;
        while(!stop){
            if(Timer.getFPGATimestamp() - startTime >= timeout){
                System.out.println("Timeout");
                return null;
            }
            if(!hasCode(data,END)){
                try{
                    if(in.available() >= END.length){
                        byte[] newData = new byte[in.available()];
                        in.readFully(newData);
                        data = Array.merge(data, newData);
                    }
                }
                catch(IOException e){}
            }
            else{
                data = Array.exclude(data,nextEnd,data.length - 1);
                stop = true;
            }
        }
        return new InPackage(data);
    }
    private boolean hasCode(byte[] data, byte[] code){
        //System.out.println("Data: length: " + data.length);
        for(int i = 0; i<data.length; ++i){
            //System.out.println("Byte at index: " + i + " | " + data[i]);
        }
        if(!(data.length >= code.length)){
            return false;
        }
        for(int i = 0; i<data.length - code.length; i++){ //Offset by one?
            if(i == data.length - code.length){
                System.out.println("Index: " + i);
            }
            if(Array.arrayEquals(code, Array.segment(data,i,i+code.length))){
                if(code == START){
                    nextStart = i;
                }
                else{
                    nextEnd = i;
                }
                return true;
            }
        }
        //.out.println("Not Found");
        return false;
    }
    public boolean hasPacks(){
        return packBuffer.hasData();
    }
    public Package[] newPackages(){
        return packBuffer.newBuffer();
    }
}
