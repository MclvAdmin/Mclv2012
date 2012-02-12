/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class PackageBuffer {
    private Package[] packBuffer;
    private Class packType;
    private int newData = 0;
    public PackageBuffer(Class packType, int size){
        packBuffer = new Package[size];
        this.packType = packType;
    }
    public void addPack(Package pack){
        if(pack.getClass() == packType){
            addBuffer(pack);
        }
    }
    private void addBuffer(Package pack){
        System.arraycopy(packBuffer, 0, packBuffer, 1, packBuffer.length - 1);
        packBuffer[0] = pack;
        newData++;
    }
    public Package lastPack(){ //@TODO add nullpack
        newData = 0;
        return packBuffer[0];
    }
    public Class packType(){
        return packType;
    }
    public boolean hasData(){
        if(newData>0){
            return true;
        }
        for(int i = 0; i<packBuffer.length; i++){
            if(packBuffer[i] != null){
                return true;
            }
        }
        return false;
    }
    public int newData(){
        return newData;
    }
    public synchronized Package[] newBuffer(){ //@TODO Synchronize, possible issue while copying packBuffer
        int size = newData;
        Package[] out = new Package[size];
        System.arraycopy(packBuffer, 0, out, 0, size);
        newData = 0;
        return out;
    }
    public Package[] buffer(){
        newData = 0;
        return packBuffer;
    }
}
