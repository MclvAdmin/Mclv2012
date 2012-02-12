/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.util;

import edu.wpi.first.wpilibj.Timer;
/**
 *
 * @author God
 */
public class Sampler extends Thread{
    private static final int FAIL_MAX = 10;
    private int failureThreshold = FAIL_MAX;
    private Sampleable sample;
    private Object[] buffer;
    private int rate;
    private long period;
    
    private int newData = 0;
    private int consumedData = 0;
    //private int fail = 0;
    private long compTimeMilli = 2;
    public Sampler(Sampleable sample, int rate, int timeBuffer){
        this.sample = sample;
        this.rate = rate;
        period = (long) (1000/((double) rate));
        buffer = new Object[rate*timeBuffer];
        this.start();
    }
    public void run(){
        while(true){
            sample();
            try{Thread.sleep(Math.max(period - compTimeMilli, 0));}catch(InterruptedException e){}
        }
    }
    public double[] getEntries(double timePeriod)/* throws FailureException*/{
        int entries = (int) timePeriod*rate;
        double[] returnArray = new double[entries];
        System.arraycopy(buffer, 0, returnArray, 0, entries);
        return returnArray;
    }
    public void setRate(int rate){
        this.rate = rate;
        period = (long) (1000/((double) rate));
    }
    public Object[] getBuffer()/* throws FailureException*/{
        consumedData += buffer.length;
        return buffer;
    }
    /*public Object getAverage(){
        Object[] bufferCopy = (Object[]) buffer.clone();
        if(bufferCopy instanceof Double[]){ //@TODO add alternate buffer while computing
            Double[] data = (Double[]) bufferCopy;
            double ave = 0;
            for(int i = 0; i<data.length; i++){
                ave = ave + data[i].doubleValue();
            }
            ave /= data.length;
            return new Double(ave);
        }
        else{
            if()
        }
        return null;
    }*/
    public Object getLastSample(){
        consumedData++;
        return buffer[0];
    }
    public double efficiency(){
        return ((double) consumedData)/((double) newData);
    }
    private void sample(){
        double compTimeDouble = Timer.getFPGATimestamp();
        addObj(sample.getObj());
        compTimeDouble = Timer.getFPGATimestamp() - compTimeDouble;
        compTimeMilli = (long) (1000*compTimeDouble);
    }
    private void addObj(Object entry){
        System.arraycopy(buffer, 0, buffer, 1, buffer.length - 1);
        buffer[0] = entry;
        newData++;
    }
}
