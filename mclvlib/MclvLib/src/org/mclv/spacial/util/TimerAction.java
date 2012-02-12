/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial.util;

import edu.wpi.first.wpilibj.Timer;
import org.mclv.spacial.system.LoopTask;
/**
 *
 * @author God
 */
public class TimerAction implements Runnable{
    private Timeout thread;
    private double startTime;
    private long period;
    private LoopTask task;
    public TimerAction(){}
    public void arm(Timeout thread){
        this.thread = thread;
        period = thread.period();
        startTime = Timer.getFPGATimestamp();
        task = new LoopTask(this,1,0);
    }
    public void run(){
        if((long) 1000*(Timer.getFPGATimestamp() - startTime) > period){
            if(thread != null){
                System.out.println("NEW Timeout");
                thread.timeout();
            }
        }
    }
    public void restart(){
        startTime = Timer.getFPGATimestamp();
    }
    public void stop(){
        thread = null;
        task.set(false);
        task = null;
    }
}
