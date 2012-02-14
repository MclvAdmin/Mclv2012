/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.math;

/**
 * This is a general definition of a function. Functions are defined in terms of doubles
 * @author God
 */
public interface Function {
    public double fAt(double var) throws DomainException;
    public boolean inDomain(double var);
}
