/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import org.mclv.spacial.FList;
import org.mclv.spacial.FSpace;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.pack.*;
import org.mclv.spacial.system.LoopTask;
import org.mclv.spacial.util.Timeout;
import org.mclv.spacial.util.TimerAction;

/**
 *
 * @author God
 */
public class TCPConnection implements Runnable,StreamTask,Timeout{
    private static final String SERVER_IP = Constants.SERVER_IP;
    private NetPackageFactory factory;
    private int port;
    private SocketConnection sock;
    private PackLog packLog;
    private PackageBuffer outBuffer;
    private PackageBuffer tempBuffer;
    private PackageBuffer inBuffer;
    DataInputStream in = null;
    DataOutputStream out = null;
    PackageScanner sc;
    private FSpace mySpace;
    private boolean timeout = false;
    //private TimerAction timer;
    public TCPConnection(int port){
        packLog = new PackLog();
        outBuffer = new PackageBuffer(OutPackage.class,1000);
        tempBuffer = new PackageBuffer(OutPackage.class,200);
        inBuffer = new PackageBuffer(InPackage.class,1000);
        this.port = port;
        //timer = new TimerAction();
    }
    public void run(){
        PrimitiveStore store = new PrimitiveStore();
        double[] dub = {-1.0,1.0};
        int[] buttons = {1001235};
        store.addDoubles(dub);
        store.addInts(buttons);
        byte[] address = {(byte) 5,(byte) 1};
        feedData(new OutPackage(NetPackage.SEND,address,store));
        if(mySpace == null){
            System.out.println("null " + port);
            mySpace = FSpace.spaceByAction(this);
        }
        else if(factory == null){
            System.out.println("null " + port);
            FList list = new FList();
            list.addSpace(mySpace);
            factory = new NetPackageFactory(list);
            if(tempBuffer.hasData()){
                Package[] packs = tempBuffer.newBuffer();
                for(int i = 0; i<packs.length; i++){
                    factory.addPack((OutPackage) packs[i]);
                }
            }
            tempBuffer = null;
        }
        else{
            if(in == null || out == null){
                //if(!timeout){
                    connect();
                //}
                //else{
                    //@TODO non-network mode
                //}
            }
            else{
                //double time = Timer.getFPGATimestamp();
                //double packs = outBuffer.newData();
                sendPacks();
                //time = Timer.getFPGATimestamp() - time;
                //System.out.println("SEND RATE: " + (packs/time));
                
                distributeFromStream();
            }
        }
    }
    private void distributeFromStream(){
        if(sc.hasPacks()){
            Package[] packs = sc.newPackages();
            for(int i = 0; i<packs.length; i++){
                InPackage thisPack = (InPackage) packs[i];
                if(thisPack.type() == NetPackage.STATUS){
                    System.out.println("STAT PACK");
                    //timer.restart();
                }
                mySpace.sendAdjacentPack(thisPack);
            }
        }
    }
    private void sendPacks(){
        if(outBuffer.hasData()){
            Package[] packs = outBuffer.newBuffer();
            try{
                for(int i = 0; i<packs.length; i++){
                    out.write(((OutPackage) packs[i]).toByte());
                }
            } catch(IOException e){}
        }
    }
    public void connect(){
        //timer.arm(this);
        try{
            System.out.println("Connecting " + port);
            sock = (SocketConnection) Connector.open("socket://"+SERVER_IP + ":" + port);
            in = sock.openDataInputStream();
            out = sock.openDataOutputStream();
            sc = new PackageScanner(in);
            LoopTask scanTask = new LoopTask(sc,10,0);
            FSpace scanSpace = new FSpace(scanTask,mySpace.superSpaces().list(),null);
            System.out.println("Connected " + port);
            //timer.restart();
        }
        catch(IOException e){
            System.out.println(e.getClass());
            sock = null;
            in = null;
            out = null;
        }
        catch(NullPointerException f){
            //connect();
        }
    }
    public boolean connecting(){
        if(in == null || out == null){
            return true;
        }
        return false;
    }
    public void feedData(Object data){
        if(data instanceof Package){
            packLog.newPack((Package) data);
            if(data instanceof OutPackage){
                if(!((OutPackage) data).primed()){
                    if(factory != null){
                        factory.addPack((OutPackage) data);
                    }
                    else{
                        tempBuffer.addPack((OutPackage) data);
                    }
                }
                else{
                    outBuffer.addPack((OutPackage) data);
                }
            }
        }
    }
    public PackLog packLog(){
        return packLog;
    }
    public PackageList acceptedPacks(){
        Class[] list = {OutPackage.class};
        return new PackageList(list);
    }
    public long period(){
        return (long) ((long) 1000*Constants.STAT_TIMEOUT); //Thirty second timeout period;
    }
    public void timeout(){
        while(connecting()){}
        in = null;
        out = null;
    }
}
