package com.onquantum.rockstar.svprimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 8/10/15.
 */
public class SLayer {
    private int orderLayer = 0;

    private List<SShape>shapeList = Collections.synchronizedList(new ArrayList<SShape>());

    public List<SShape> getShapeList() {
        return shapeList;
    }

    public void addShape(SShape shape) {
        synchronized (this) {
            shapeList.add(shape);
        }
    }

    public <T extends SShape> T getElementByIndex(int index, Class<T>type) {
        synchronized (shapeList.get(index)) {
            return (T)shapeList.get(index);
        }
    }
}
