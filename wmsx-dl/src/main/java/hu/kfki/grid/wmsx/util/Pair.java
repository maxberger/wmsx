/*
 * Pair.java
 *
 * Created on 30.05.2007, 14:20:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hu.kfki.grid.wmsx.util;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Max Berger
 */
public class Pair implements Map.Entry, Serializable {
    
    Object key;
    Object value;
    
    public Pair(final Object k, final Object v) {
        this.key = k;
        this.value = v;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public Object setValue(Object arg0) {
        Object old = this.value;
        this.value = arg0;
        return old;
    }
    
}
