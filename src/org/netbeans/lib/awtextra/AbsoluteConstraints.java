package org.netbeans.lib.awtextra;

import java.io.Serializable;

/**
 * Minimal shim of NetBeans AbsoluteConstraints to allow running outside NetBeans.
 * Supports (x,y) and (x,y,width,height) usages.
 */
public class AbsoluteConstraints implements Serializable {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean hasSize;

    public AbsoluteConstraints(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
        this.hasSize = false;
    }

    public AbsoluteConstraints(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hasSize = true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean hasSize() { return hasSize; }
}
