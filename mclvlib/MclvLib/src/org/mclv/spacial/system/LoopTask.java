/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial.system;

import edu.wpi.first.wpilibj.Timer;
import org.mclv.spacial.TaskObscure;
import org.mclv.spacial.Killable;
import org.mclv.spacial.util.Optimizable;
import org.mclv.spacial.config.Constants;

/**
 *
 * @author romana
 */
public class LoopTask extends Thread implements Killable,TaskObscure{
    private Runnable action;
    private boolean infinite = false;
    private boolean live = true;
    private long period;
    private long sleep;
    private int loops;
    private int optInt;
    private int sinceLastOpt = 0;
    private double targetE;
    private double acceptableDeviation;
    private boolean optimized = false;
    
    private double lastCompute = 0;
    private int timeInt = Constants.TIME_INTERVAL;
    private int timeTracker = 0;
    public LoopTask(Runnable action, long period, int loops){
        this.period = period;
        this.loops = loops;
        this.action = action;
        if(loops == 0){
            infinite = true;
        }
        this.start();
    }
    public LoopTask(Optimizable action, double targetE, double acceptableDeviation, long period, int loops){
        this.period = period;
        this.loops = loops;
        this.action = action;
        this.targetE = targetE;
        optInt =(int) period*Constants.OPT_INTERVAL;
        this.acceptableDeviation = acceptableDeviation;
        optimized = true;
        if(loops == 0){
            infinite = true;
        }
        this.start();
    }
    public void run(){
        if(infinite){
            while(live){
                runSleep();
            }
        }
        else{
            for(int i = 0; i<loops; i++){
                runSleep();
            }
        }
    }
    public void set(boolean live){
        this.live = live;
    }
    public void setPeriod(long period){
        this.period = period;
    }
    public void setLoops(int loops){
        this.loops = loops;
        if(loops == 0){
            infinite = true;
        }
        else{
            infinite = false;
        }
    }
    private void runSleep(){
        timeTracker %= timeInt;
        if(timeTracker == 0){
            lastCompute = Timer.getFPGATimestamp(); //@TODO complete according to 'sinceLastOpt'
            action.run();
            lastCompute = Timer.getFPGATimestamp() - lastCompute;
            sleep = Math.max(period - toLongMillis(lastCompute),0);
        }
        else{
            action.run();
        }
        if(optimized){
            if(sinceLastOpt >= optInt){
                sinceLastOpt = -1;
                optimize();
            }
        }
        try{Thread.sleep(sleep);}catch(InterruptedException e){}
        sinceLastOpt++;
    }
    private void optimize(){
        double e = ((Optimizable) action).efficiency();
        if(Math.abs(e - targetE) > acceptableDeviation){
            if(e < targetE){
                period = Math.max(period - 1,Constants.MIN_PERIOD);
            }
            else{
                period++;
            }
            //System.out.println("PERIOD: " + period);
        }
    }
    public Runnable action(){
        if(action instanceof TaskObscure){
            return ((TaskObscure) action).action();
        }
        return action;
    }
    private long toLongMillis(double time){
        return (long) ((long) 1000*time);
    }
}
