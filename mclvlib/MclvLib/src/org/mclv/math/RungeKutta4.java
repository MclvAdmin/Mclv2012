/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.math;

/**
 *
 * @author God
 */
public abstract class RungeKutta4 implements Function{
    private double initialY;
    private double initialT;
    private double h;
    public RungeKutta4(double initialY, double initialT,double stepSize){
        h = stepSize;
        this.initialY = initialY;
        this.initialT = initialT;
    }
    public double fAt(double tFinal){ //t must be greater than initial t
        double y = initialY;
        int steps = (int) ((tFinal - initialT)/h);
        double t = initialT;
        double dy;
        double k1;
        double k2;
        double k3;
        double k4;
        for(int i = 0; i<steps; i++){
            k1 = F(t,y);
            k2 = F(t + 0.5*h, y + 0.5*h*k1);
            k3 = F(t + 0.5*h, y + 0.5*h*k2);
            k4 = F(t + h, y + h*k3);
            dy = h*(k1 + 2*k2 + 2*k3 + k4)/6;
            t += h;
            y += dy;
        }
        return y;
    }
    public abstract double F(double t, double y);
    public boolean inDomain(double val){
        return true;
    }
}
