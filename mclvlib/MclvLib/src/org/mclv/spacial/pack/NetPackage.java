/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import org.mclv.spacial.net.PrimitiveStore;
import org.mclv.spacial.util.Array;

/**
 *
 * @author God
 */
public abstract class NetPackage extends Package{
    public static final byte[] START = {(byte) 191,(byte) 206,(byte) 204,(byte) 158,(byte) 223};
    public static final byte[] END = {(byte) 109,(byte) 99,(byte) 108,(byte) 118};
    public static final byte SEND = (byte) 1;
    public static final byte REQUEST = (byte) 2;
    public static final byte SEND_CONFIG = (byte) 3;
    public static final byte REQUEST_CONFIG = (byte) 4;
    public static final byte GAME = (byte) 6;
    public static final byte STATUS = (byte) 6;
    public static final byte INT = (byte)  0;
    public static final byte DOUBLE = (byte) 1;
    public static final byte LONG = (byte) 2;
    public static final byte BYTE = (byte) 3;
    public static final byte[] NULL_ADDRESS = {(byte) 0, (byte) 0};
    protected byte type;
    protected byte[] address = new byte[2];
    protected PrimitiveStore prims = new PrimitiveStore();
    protected byte[] packBytes;
    protected byte[] rawData = new byte[0];
    private boolean compute = false;
    public NetPackage(byte type, byte[] address){
        super();
        this.type = type;
        this.address = address;
        //packBytes = toByte(); //Compute on construct
    }
    public NetPackage(byte[] data){
        type = data[0];
        System.arraycopy(data, 1, address, 0, address.length);
        rawData = Array.section(3,data);
        /*if(type == SEND || type == SEND_CONFIG){
            parseData(data);
        }*/
    }
    public boolean primed(){
        return compute;
    }
    public void compute(){
        if(packBytes == null){
            packBytes = toByte();
        }
        if(prims.params() == 0 && rawData != null){
            parseData(rawData);
        }
        compute = true;
    }
    public PrimitiveStore store(){
        return prims;
    }
    public byte type(){
        return type;
    }
    public byte[] address(){
        return address;
    }
    private void parseData(byte[] data){
        //Parsing data
        byte params = data[0];
        data = Array.section(1, data);
        byte[] types = new byte[params];
        System.arraycopy(data, 0, types, 0, params);
        data = Array.section(params, data);
        System.out.println("Parsing data. Params: " + ((int) params));
        for(int i = 0; i<(int) params; i++){
            int length = lengthByType(types[i]);
            byte[] prim = Array.segment(data,0,length);
            addByType(types[i],prim);
            data = Array.section(length,data);
        }
    }
    public byte[] toByte(){
        if(packBytes != null){
            return packBytes;
        }
        return toByte(prims);
    }
    private byte[] toByte(PrimitiveStore prims){
        byte[] data = new byte[0];
        byte[] typeA = {type};
        byte[] params = new byte[1];
        byte[] types = new byte[0];
        byte[] rawPrim = new byte[0];
        if(type == SEND || type == SEND_CONFIG){
            //byte[] params = {(byte) prims.params()};
            params[0] = (byte) prims.doubles();
            byte[] thisType = {INT};
            int[] ints = prims.getInts();
            for(int i = 0; i<ints.length; i++){ //@TODO write send conversions
                types = Array.merge(types,thisType);
                rawPrim = Array.merge(rawPrim,Array.toByta(ints[i]));
            }
            thisType[0] = DOUBLE;
            double[] doubles = prims.getDoubles();
            for(int i = 0; i<doubles.length; i++){ //@TODO write send conversions
                types = Array.merge(types,thisType);
                rawPrim = Array.merge(rawPrim,Array.toByta(Double.doubleToLongBits(doubles[i])));
            }
            thisType[0] = LONG;
            long[] longs = prims.getLongs();
            for(int i = 0; i<longs.length; i++){ //@TODO write send conversions
                types = Array.merge(types,thisType);
                rawPrim = Array.merge(rawPrim,Array.toByta(longs[i]));
            }
            thisType[0] = BYTE;
            byte[] bytes = prims.getBytes();
            for(int i = 0; i<bytes.length; i++){ //@TODO write send conversions
                types = Array.merge(types,thisType);
                byte[] thisByte = {bytes[i]};
                rawPrim = Array.merge(rawPrim,thisByte);
            }
        }
        else{
            params[0] = (byte) 0;
        }
        byte[][] newData = {START,typeA,address,params,types,rawPrim,END};
        data = Array.massMerge(newData);
        return data;
    }
    public String toString(){
        String ret = "NetPackage: package ID " + super.id() + "\n";
        ret += "    TYPE: ";
        ret += type + "\n";
        ret += "    ADDRESS: ";
        ret += address[0] + " " + address[1] + "\n";
        if(prims != null){
            ret += "   " + prims.toString() + "\n";
        }
        return ret;
    }
    public void addByType(byte type, byte[] data){
        switch(type){
            case DOUBLE: double[] d = {byteToDouble(data)}; prims.addDoubles(d);
            case LONG: long[] l = {byteToLong(data)}; prims.addLongs(l);
            case INT: int[] i = {byteToInt(data)}; prims.addInts(i);
            case BYTE: byte[] b = {data[0]}; prims.addBytes(b);
        }
    }
    public static double byteToDouble(byte[] data){
        return Double.longBitsToDouble(byteToLong(data));
    }
    public static long byteToLong(byte[] data){
        long value = 0;
        for (int i = 0; i < data.length; i++){
            value += (data[i] & 0xff) << (8 * i);
        }
        return value;
    }
    public static int byteToInt(byte[] data){
        int ret = ((0xFF & data[0]) << 24) | ((0xFF & data[1]) << 16) |
            ((0xFF & data[2]) << 8) | (0xFF & data[3]);
        return ret;
    }
    private static int lengthByType(byte type){
        switch(type){
            case DOUBLE: return 8;
            case LONG: return 8;
            case INT: return 4;
            case BYTE: return 1;
        }
        return 0;
    }
}
