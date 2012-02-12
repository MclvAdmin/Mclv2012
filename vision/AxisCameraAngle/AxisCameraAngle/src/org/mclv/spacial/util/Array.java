/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.util;

/**
 *
 * @author God
 */
public class Array {
    public static byte[] section(int offset, byte[] data){
        byte[] out = new byte[data.length - offset];
        System.arraycopy(data, offset, out, 0, out.length);
        return out;
    }
    public static byte[] segment(byte[] data, int start, int end){
        byte[] ret = new byte[end - start];
        for(int i = 0; i<ret.length; i++){
            ret[i] = data[i + start];
        }
        return ret;
    }
    public static boolean arrayEquals(byte[] a, byte[] b){
        for(int i = 0; i<a.length; i++){
            if(a[i] != b[i]){
                return false;
            }
        }
        return true;
    }
    public static byte[] exclude(byte[] data, int begin, int end){
        byte[] ret = new byte[data.length - (end - begin)];
        int dummy = 0;
        for(int i = 0; i<ret.length; i++){
            while(dummy>begin && dummy<end){
                dummy++;
            }
            ret[i] = data[dummy];
            dummy++;
        }
        return ret;
    }
    public static Object[] merge(Object[] a, Object[] b){
        Object[] ret = new Object[a.length + b.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }
    public static byte[] merge(byte[] a, byte[] b){
        byte[] ret = new byte[a.length + b.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }
    public static double[] merge(double[] a, double[] b){
        double[] ret = new double[a.length + b.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }
    public static int[] merge(int[] a, int[] b){
        int[] ret = new int[a.length + b.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }
    public static long[] merge(long[] a, long[] b){
        long[] ret = new long[a.length + b.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }
    public static byte[] massMerge(byte[][] arrays){
        int length = 0;
        for(int i = 0; i<arrays.length; i++){
            length += arrays[i].length;
        }
        byte[] ret = new byte[length];
        length = 0;
        for(int i = 0; i<arrays.length; i++){
            System.arraycopy(arrays[i], 0, ret,length,arrays[i].length);
            length += arrays[i].length;
        }
        return ret;
    }
    public static byte[] shortsToBytes(short[] shorts){
        byte[] bytes = new byte[shorts.length];
        for(int i = 0; i<shorts.length; i++){
            bytes[i] = (byte) (shorts[i] - 128);
        }
        return bytes;
    }
    public static byte[] toByta(long data) {
        return new byte[] {
            (byte)((data >> 56) & 0xff),
            (byte)((data >> 48) & 0xff),
            (byte)((data >> 40) & 0xff),
            (byte)((data >> 32) & 0xff),
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
    public static byte[] toByta(int data) {
        return new byte[] {
            (byte)(data >>> 24),
            (byte)(data >>> 16),
            (byte)(data >>> 8),
            (byte) data
        };
   }
}
