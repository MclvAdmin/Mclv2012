/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.test;

/**
 *
 * @author God
 */
public class Jaguar { //DUMMYJAG
    private int port;
    public Jaguar(int port){
        this.port = port;
    }
    public void set(double val){
        System.out.println("Jaguar\nID\tOutput\n" + port + "\t" + val);
    }
}
