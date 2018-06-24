package frontend.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class FilterableList<T> extends JList<T>
{
    private final List<Consumer<List<T>>> filterListeners;
    private final Model model;

    public FilterableList(List<T> values)
    {
        this.filterListeners = new ArrayList<>();
        this.model = new Model(values);
        this.setModel(this.model);
        this.addKeyListener(this.model);
        this.setOpaque(false); // fixes painting the filter overlay
        this.setBackground(Color.WHITE);
    }
    
    public void addFilterListener(Consumer<List<T>> listener)
    {
        this.filterListeners.add(listener);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (!this.model.filterActive) {
            return;
        }

        final int PARENTWIDTH, PARENTXOFFSET, PARENTYOFFSET;
        final Container parent = this.getParent();
        if (parent instanceof JViewport) {
            final Point offset = ((JViewport) parent).getViewPosition();
            PARENTWIDTH = parent.getWidth();
            PARENTXOFFSET = offset.x;
            PARENTYOFFSET = offset.y;
        } else {
            PARENTWIDTH = this.getWidth();
            PARENTXOFFSET = 0;
            PARENTYOFFSET = 0;
        }

        final Color oldcol = g.getColor();
        g.setColor(new Color(0xFCFF84));
        final String filter = this.model.filterText;
        final Rectangle2D r = g.getFontMetrics().getStringBounds(filter, g);
        final int WIDTH = (int) r.getWidth() + 6;
        final int HEIGHT = (int) r.getHeight() + 3;
        final int X = PARENTXOFFSET + (PARENTWIDTH - WIDTH) / 2;
        final int Y = PARENTYOFFSET + 3;
        g.fillRect(X + 1, Y + 1, WIDTH - 2, HEIGHT - 2);
        g.setColor(Color.BLACK);
        g.drawLine(X + 1, Y, X + WIDTH - 2, Y);
        g.drawLine(X + 1, Y + HEIGHT - 1, X + WIDTH - 2, Y + HEIGHT - 1);
        g.drawLine(X, Y + 1, X, Y + HEIGHT - 2);
        g.drawLine(X + WIDTH - 1, Y + 1, X + WIDTH - 1, Y + HEIGHT - 2);
        g.drawString(filter, X + 3, Y + HEIGHT - 3);
        g.setColor(oldcol);
    }

    private class Model implements ListModel<T>, KeyListener
    {
        private final List<T> values;
        private final List<T> filteredValues;
        private final List<ListDataListener> dataListeners;

        private String filterText;
        private boolean filterActive;

        private Model(List<T> values)
        {
            this.values = values;
            this.filteredValues = new ArrayList<>(values);
            this.dataListeners = new ArrayList<>();
            this.filterText = "";
        }

        @Override
        public int getSize()
        {
            return this.filteredValues.size();
        }

        @Override
        public T getElementAt(int index)
        {
            if (index < 0 || this.getSize() <= index) {
                return null;
            }
            return this.filteredValues.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l)
        {
            this.dataListeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l)
        {
            this.dataListeners.add(l);
        }
        
        private void removeFilter()
        {
            if (!this.filterActive) {
                return;
            }
            this.filterActive = false;
            this.filterText = "";
            this.updateFilter();
        }
        
        private void updateFilter()
        {
            final T selectedValue = FilterableList.this.getSelectedValue();
            final int prevsize = this.filteredValues.size();
            this.filteredValues.clear();
            int newSelectedIndex = 0;
            for (T data : this.values) {
                if (this.filterActive &&
                    !data.toString().toLowerCase().contains(this.filterText))
                {
                    continue;
                }
                this.filteredValues.add(data);
                if (data.equals(selectedValue)) {
                    newSelectedIndex = this.filteredValues.size() - 1;
                }
            }
            final int cursize = this.filteredValues.size();
            final int changedsize = Math.min(cursize, prevsize);
            if (cursize != prevsize) {
                for (ListDataListener listener : this.dataListeners) {
                    listener.contentsChanged(new ListDataEvent(
                        this,
                        cursize - prevsize > 0
                            ?ListDataEvent.INTERVAL_ADDED
                            :ListDataEvent.INTERVAL_REMOVED,
                        prevsize,
                        cursize - 1
                    ));
                }
            }
            if (prevsize != 0) {
                for (ListDataListener listener : this.dataListeners) {
                    listener.contentsChanged(new ListDataEvent(
                        this,
                        ListDataEvent.CONTENTS_CHANGED,
                        0,
                        changedsize - 1
                    ));
                }
            }
            final boolean wasChanged = prevsize != 0 || cursize != prevsize;
            if (wasChanged) {
                for (Consumer<List<T>> l : FilterableList.this.filterListeners) {
                    l.accept(this.filteredValues);
                }
            }
            final int selectedIndex = newSelectedIndex;
            SwingUtilities.invokeLater(() -> {
                if (this.filteredValues.size() != 0) {
                    FilterableList.this.setSelectedIndex(selectedIndex);
                    FilterableList.this.ensureIndexIsVisible(selectedIndex);
                }
                if (!wasChanged) {
                    FilterableList.this.repaint();
                }
            });
        }

        @Override
        public void keyTyped(KeyEvent e)
        {
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            if (this.filterActive) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    this.removeFilter();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (e.isControlDown() || this.filterText.length() == 1) {
                        this.removeFilter();
                        return;
                    }
                    final int len = this.filterText.length() - 1;
                    this.filterText = this.filterText.substring(0, len);
                    this.updateFilter();
                    return;
                }
            }
            
            final char c = e.getKeyChar();
            if (!(('0' <= c && c <= '9') ||
                  ('a' <= c && c <= 'z') ||
                  ('A' <= c && c <= 'Z') || c == ' ' || c == '_'))
            {
                return;
            }
            
            this.filterActive = true;
            this.filterText += Character.toLowerCase(c);
            this.updateFilter();
        }
    }
}
