/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial;

/**
 *
 * @author God
 */
public class Priority {
    private Class packageClass;
    private int priority;
    public Priority(Class packageClass, int priority){
        this.packageClass = packageClass;
        this.priority = priority;
    }
    public int priority(){
        return priority;
    }
    public Class priorityClass(){
        return packageClass;
    }
}
