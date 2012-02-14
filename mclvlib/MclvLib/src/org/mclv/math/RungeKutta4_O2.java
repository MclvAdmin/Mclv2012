/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.math;

/**
 *
 * @author God
 */
public abstract class RungeKutta4_O2 {
    private double[] initialY;
    public double[] initialY1;
    private double initialT;
    private double h;
    public RungeKutta4_O2(double[] initialY, double[] initialY1, double initialT,double stepSize){
        h = stepSize;
        this.initialY = initialY;
        this.initialY1 = initialY1;
        this.initialT = initialT;
    }
    public double[][] fAt(double tFinal){ //t must be greater than initial t
        int steps = (int) ((tFinal - initialT)/h);
        double[] vars = initialY;
        double[] vel = initialY1;
        double t = initialT;
        for(int i = 0; i<steps; i++){
            step(t,h,vars,vel);
            t += h;
        }
        double[][] ret = {vars,vel};
        return ret;
    }
    public abstract double F(int i,double y, double y1, double t);
    public boolean inDomain(double val){
        return true;
    }
    public double[][] inverse(double[] initialY, double[] destY, double[] derivMin,double[] destDeviation, double dStep, double tMax){
        //The goal here is to find pseudo-partial derivatives of destination error WRT derivative value.
        for(int i = 0; i < destY.length; i++){
            double[] thisVar = new double[1];
            double[] thisVel = new double[1];
            double startVel = derivMin[i];
            double bestVel;
            while(true){
                double t = 0;
                double deltaE;
                double thisE;
                double lastE = 0;
                boolean lastSign = false;
                thisVel[0] = startVel;
                thisVar[0] = initialY[i];
                bestVel = thisVel[0];
                for(int j = 0; true; j++){
                    thisE = (destY[i] - thisVar[0]);
                    if(j >= 1){
                    deltaE = thisE - lastE;
                        if((deltaE > 0 && lastSign == false) || t>tMax){
                            break;
                        }
                    lastSign = sign(deltaE);
                    }
                    lastE = thisE;
                    step(i,t,h,thisVar,thisVel);
                    t+=h;
                }
                if(Math.abs(thisVar[0] - destY[i]) <= destDeviation[i]){
                    break;
                }
                startVel += dStep;
            }
            destY[i] = thisVar[0];
            derivMin[i] = bestVel;
        }
        double[][] ret = {destY,derivMin};
        return ret;
    }
    private boolean sign(double val){
        if(val >= 0){
            return true;
        }
        return false;
    }
  /**
    * Calculated a step of the integration of an ODE with
    * 4th order RK.
    *
    * @param t  independent variable
    * @param dt step in the independent variable
    * @param var array holding the dependent variable
    * @param vel array holding the first derivative
    *
   **/
    public void step (int index, double t, double dt,double[] var,double[] vel) {
    double k1_var, k1_vel;
    double k2_var, k2_vel;
    double k3_var, k3_vel;
    double k4_var, k4_vel;

    for (int i=0; i < var.length; i++) {
      k1_var = vel[i] * dt;
      k1_vel = F(index,var[i],vel[i],t)*dt;

      k2_var =  (vel[i] + 0.5*k1_vel)*dt;
      k2_vel =F(index,var[i] + 0.5*k1_var,
                         vel[i] + 0.5*k1_vel,
                              t+0.5*dt)*dt;

      k3_var =  (vel[i] + 0.5*k2_vel)*dt;
      k3_vel = F(index,var[i] + 0.5*k2_var,
                         vel[i] + 0.5*k2_vel,
                              t+0.5*dt)*dt;

      k4_var =  (vel[i] + k3_vel)*dt;
      k4_vel = F(index,var[i] + k3_var,
                      vel[i] + k3_vel,
                           t+dt)*dt;

      var[i] = var[i] +
                 (k1_var + 2.0*k2_var
                       + 2.0*k3_var + k4_var)/6.0;
      vel[i] = vel[i] +
                 (k1_vel + 2.0*k2_vel
                       + 2.0*k3_vel + k4_vel)/6.0;
    }
  } // step
  public void step (double t, double dt,double [] var,double [] vel) {
    double k1_var, k1_vel;
    double k2_var, k2_vel;
    double k3_var, k3_vel;
    double k4_var, k4_vel;

    for (int i=0; i < var.length; i++) {
      k1_var = vel[i] * dt;
      k1_vel = F(i,var[i],vel[i],t)*dt;

      k2_var =  (vel[i] + 0.5*k1_vel)*dt;
      k2_vel =F(i,var[i] + 0.5*k1_var,
                         vel[i] + 0.5*k1_vel,
                              t+0.5*dt)*dt;

      k3_var =  (vel[i] + 0.5*k2_vel)*dt;
      k3_vel = F(i,var[i] + 0.5*k2_var,
                         vel[i] + 0.5*k2_vel,
                              t+0.5*dt)*dt;

      k4_var =  (vel[i] + k3_vel)*dt;
      k4_vel = F(i,var[i] + k3_var,
                      vel[i] + k3_vel,
                           t+dt)*dt;

      var[i] = var[i] +
                 (k1_var + 2.0*k2_var
                       + 2.0*k3_var + k4_var)/6.0;
      vel[i] = vel[i] +
                 (k1_vel + 2.0*k2_vel
                       + 2.0*k3_vel + k4_vel)/6.0;
    }
  } // step
}
