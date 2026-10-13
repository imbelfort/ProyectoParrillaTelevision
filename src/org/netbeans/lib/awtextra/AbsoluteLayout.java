package org.netbeans.lib.awtextra;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal implementation of org.netbeans.lib.awtextra.AbsoluteLayout to allow the
 * NetBeans-generated UI to run outside the IDE. It stores AbsoluteConstraints
 * passed in addLayoutComponent and positions components accordingly.
 */
public class AbsoluteLayout implements LayoutManager2, java.io.Serializable {
    private final Map<Component, AbsoluteConstraints> map = new HashMap<>();

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof AbsoluteConstraints) {
            map.put(comp, (AbsoluteConstraints) constraints);
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        // not used
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        map.remove(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int w = 0, h = 0;
        for (Component c : parent.getComponents()) {
            AbsoluteConstraints ac = map.get(c);
            if (ac != null) {
                if (ac.hasSize()) {
                    w = Math.max(w, ac.getX() + ac.getWidth());
                    h = Math.max(h, ac.getY() + ac.getHeight());
                } else {
                    Dimension d = c.getPreferredSize();
                    w = Math.max(w, ac.getX() + d.width);
                    h = Math.max(h, ac.getY() + d.height);
                }
            } else {
                Dimension d = c.getPreferredSize();
                w = Math.max(w, d.width);
                h = Math.max(h, d.height);
            }
        }
        Insets in = parent.getInsets();
        return new Dimension(w + in.left + in.right, h + in.top + in.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets in = parent.getInsets();
        for (Component c : parent.getComponents()) {
            AbsoluteConstraints ac = map.get(c);
            if (ac != null) {
                if (ac.hasSize()) {
                    c.setBounds(in.left + ac.getX(), in.top + ac.getY(), ac.getWidth(), ac.getHeight());
                } else {
                    Dimension d = c.getPreferredSize();
                    c.setBounds(in.left + ac.getX(), in.top + ac.getY(), d.width, d.height);
                }
            }
        }
    }

    @Override
    public float getLayoutAlignmentX(Container target) { return 0.5f; }

    @Override
    public float getLayoutAlignmentY(Container target) { return 0.5f; }

    @Override
    public void invalidateLayout(Container target) { /* no-op */ }

    @Override
    public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
}
