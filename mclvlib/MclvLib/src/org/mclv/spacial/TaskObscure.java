/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial;

/** Use to indicate that real runnable object is obscured by
 *  another class. I.E. LoopTask Runnable is seen by FSpace as the action, where the LoopTask's action is desired
 *
 * @author God
 */
public interface TaskObscure extends Runnable{
    public Runnable action();
}
