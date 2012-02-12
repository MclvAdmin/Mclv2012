/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.net;

import java.util.Vector;
import org.mclv.spacial.pack.NetPackage;
import org.mclv.spacial.util.Array;

/**
 *
 * @author God
 */
public class PrimitiveStore { //@TODO add toString method;
    private static final int TYPES = 4;
    private static final int INT = NetPackage.INT;
    private static final int DOUBLE = NetPackage.DOUBLE;
    private static final int LONG = NetPackage.LONG;
    private static final int BYTE = NetPackage.BYTE;
    public int[] ints = new int[0];
    public long[] longs = new long[0];
    public byte[] bytes = new byte[0];
    public double[] doubles = new double[0];
    public PrimitiveStore(){}
    public int params(){
        return doubles() + longs() + ints() + bytes();
    }
    public Object[] wrappedPrims(){
        if(params() == 0){
            return null;
        }
        Object[] ret = new Object[params()];
        int startIndex = 0;
        for(int i = 0; i<ints(); i++){
            ret[i] = new Integer(ints[i]);
        }
        startIndex += ints();
        for(int i = 0; i<doubles(); i++){
            ret[i + startIndex] = new Double(doubles[i]);
        }
        startIndex += doubles();
        for(int i = 0; i<longs(); i++){
            ret[i + startIndex] = new Long(longs[i]);
        }
        startIndex += longs();
        for(int i = 0; i<bytes(); i++){
            ret[i + startIndex]= new Byte(bytes[i]);
        }
        return ret;
    }
    public int doubles(){
        int length = 0;
        if(doubles != null){
            length += doubles.length;
        }
        return length;
    }
    public int longs(){
        int length = 0;
        if(longs != null){
            length += longs.length;
        }
        return length;
    }
    public int ints(){
        int length = 0;
        if(ints != null){
            length += ints.length;
        }
        return length;
    }
    public int bytes(){
        int length = 0;
        if(bytes != null){
            length += bytes.length;
        }
        return length;
    }
    public void addDoubles(double[] doubles){
        this.doubles = Array.merge(this.doubles, doubles);
    }
    public void addInts(int[] ints){
        this.ints = Array.merge(this.ints, ints);
    }
    public void addBytes(byte[] bytes){
        this.bytes = Array.merge(this.bytes, bytes);
    }
    public void addLongs(long[] longs){
        this.longs = Array.merge(this.longs, longs);
    }
    public double[] getDoubles(){
        return doubles;
    }
    public byte[] getBytes(){
        return bytes;
    }
    public long[] getLongs(){
        return longs;
    }
    public int[] getInts(){
        return ints;
    }
    public String toString(){
        String ret = "Primitive Store\nData:\n";
        /*for(int i = 0; i<TYPES; i++){
            ret+= "\n";
            switch(i){
                case DOUBLE: ret+="DOUBLES:\n";for(int j = 0; j<doubles(); j++){ret+=};
                case INT: ;
                case LONG: ;
                case BYTE: ;
            }
        }*/
        Object[] prims = wrappedPrims();
        if(prims != null){
            for(int i = 0; i<prims.length; i++){
                ret+=primString(prims[i]) + "\n";
            } 
        }
        else{
            ret += "null\n";
        }
        return ret;
    }
    public String primString(Object prim){
        if(prim instanceof Double){
            return ((Double) prim).toString();
        }
        else if(prim instanceof Byte){
            return ((Byte) prim).toString();
        }
        else if(prim instanceof Integer){
            return ((Integer) prim).toString();
        }
        else if(prim instanceof Long){
            return ((Long) prim).toString();
        }
        else return "";
    }
}
