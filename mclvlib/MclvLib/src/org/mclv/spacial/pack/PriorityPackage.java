/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import java.util.Hashtable;
import org.mclv.spacial.Priority;
/**
 *
 * @author God
 */
public class PriorityPackage extends Package{
    private Hashtable classByPriority;
    private int[] priorityList;
    public PriorityPackage(Priority[] matrix){
        super();
        priorityList = new int[matrix.length];
        for(int i = 0; i<matrix.length; i++){
            classByPriority.put(new Integer(matrix[i].priority()), matrix[i].priorityClass());
            priorityList[i] = matrix[i].priority();
        }
        sort();
    }
    public Class[] sortedPriority(){
        Class[] returnArray = new Class[priorityList.length];
        for(int i = 0; i<priorityList.length; i++){
            returnArray[i] = (Class) classByPriority.get(new Integer(priorityList[i]));
        }
        return returnArray;
    }
    private void sort(){ //using bubble for now, because.
        for(int i = 0; i<priorityList.length; i++){
            for(int j = 0; j + 1<priorityList.length; j++){
                int thisEntry = priorityList[j];
                int nextEntry = priorityList[j + 1];
                if(thisEntry > nextEntry){
                    priorityList[j] = nextEntry;
                    priorityList[j + 1] = thisEntry;
                }
            }
        }
    }
    public int dataSize(){
        return 1;
    }
}
