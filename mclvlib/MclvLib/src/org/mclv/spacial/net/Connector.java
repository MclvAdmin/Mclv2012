package org.mclv.spacial.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import org.mclv.spacial.FList;
import org.mclv.spacial.FSpace;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.system.LoopTask;
//import javax.microedition.io.Connector;

/**
 *
 * @author God
 */
public class Connector implements Runnable{
    private int[] ports = Constants.PORTS;
    private FSpace mySpace;
    private FList connectSpaces;
    public Connector(){
        this(new FSpace(null));
    }
    public Connector(FSpace topSpace){
        connectSpaces = new FList();
        for(int i = 0; i<ports.length; i++){
            TCPConnection con = new TCPConnection(ports[i]);
            LoopTask conTask = new LoopTask(con,10,0);
            FSpace conSpace = new FSpace(conTask,topSpace);
        }
    }
    public void run(){
        
    }
    /*public boolean connected(int port){
        if(!hasPort(port) || !connecting(port)){
            return false;
        }
        return false;
    }
    private boolean hasPort(int port){
        for(int i = 0; i<ports.length; i++){
            if(ports[i] == port){
                return true;
            }
        }
        return false;
    }
    private boolean connecting(int port){
        TCPConnection connection = (TCPConnection) connectionByPort.get(new Integer(port));
        return connection.connecting();
    }*/
}
