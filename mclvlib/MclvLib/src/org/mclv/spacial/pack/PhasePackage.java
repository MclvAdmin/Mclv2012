/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class PhasePackage extends Package{
    private int gamePhase;
    private long timeLeft = 0; //all time in milliseconds
    private long absoluteTime = 0;
    private boolean hasTime = false;
    public PhasePackage(int gamePhase){
        super();
        this.gamePhase = gamePhase;
    }
    public PhasePackage(int gamePhase, long timeLeft, long absoluteTime){
        hasTime = true;
        this.gamePhase = gamePhase;
        this.timeLeft = timeLeft;
        this.absoluteTime = absoluteTime;
    }
    public int gamePhase(){
        return gamePhase;
    }
    public boolean hasTime(){
        return hasTime;
    }
    public long timeLeft(){
        return timeLeft;
    }
    public long absoluteTime(){
        return absoluteTime;
    }
    public int dataSize(){
        return 1;
    }
}
