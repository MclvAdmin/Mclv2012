/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class PackageList {
    private Class[] list;
    public PackageList(Class[] list){
        this.list = list;
    }
    public boolean hasPackageType(Class packageType){
        for(int i = 0; i<list.length; i++){
            if(packageType == list[i]){
                return true;
            }
        }
        return false;
    }
    public Class[] list(){
        return list;
    }
}
