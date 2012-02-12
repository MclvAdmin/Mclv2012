/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.util;

import org.mclv.spacial.TaskObscure;
/**
 *
 * @author romana
 */
public class EfficiencyOptimizer implements Runnable,TaskObscure{ //@TODO retool to function as meta-scale efficiency setpoint optimizer
    private Optimizable action;
    private double targetE;
    private double acceptableDeviation;
    public EfficiencyOptimizer(Optimizable action, double targetE, double acceptableDeviation){
        
    }
    public void run(){
        action.run();
        optimize();
    }
    private void optimize(){
        double e = action.efficiency();
        if(Math.abs(e - targetE) > acceptableDeviation){
            if(e > targetE){
                
            }
        }
    }
    public Runnable action(){
        return action;
    }
}
