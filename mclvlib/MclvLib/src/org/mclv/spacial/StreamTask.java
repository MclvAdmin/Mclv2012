/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial;

import java.util.Hashtable;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.PackageList;
/**
 *
 * @author romana
 */
public interface StreamTask {
    public void feedData(Object data);
    public PackLog packLog();
    public PackageList acceptedPacks();
}
