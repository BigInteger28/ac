package frontend.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import frontend.util.Callback;
import resources.EngineSourceManager;
import resources.Resource;

import static javax.swing.JScrollPane.*;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ResourceList<T extends Resource> extends JPanel
{
    private final Resource.Type[] types;
    private final FilterableList<T> list;
    private final List<T> resourceList;
    private final HashMap<Integer, JLabel> typeLabels;

    public ResourceList(List<T> resouceList, Resource.Type[] types)
    {
        this.types = types;
        this.typeLabels = new HashMap<>();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = types.length;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        final JPanel statusBar = new JPanel(new GridBagLayout());
        for (int i = 0; i < types.length; i++) {
            final JLabel label = new JLabel();
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            this.typeLabels.put(i, label);
            final JPanel pnlCol = new JPanel();
            pnlCol.setPreferredSize(new Dimension(10, 5));
            pnlCol.setBackground(types[i].color);
            pnlCol.setOpaque(true);
            final JPanel pnlSub = new JPanel(new BorderLayout());
            pnlSub.add(pnlCol, BorderLayout.WEST);
            pnlSub.add(label);
            statusBar.add(pnlSub);
            c.gridx++;
        }

        this.resourceList = resouceList;
        this.list = new FilterableList<>(resourceList);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setCellRenderer(new Renderer<>(types));
        this.list.addFilterListener(this::updateStatusBar);
        this.updateStatusBar(resourceList);
        final JScrollPane scrollpane = new JScrollPane(this.list);
        scrollpane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> this.list.requestFocusInWindow());
        
        this.setLayout(new BorderLayout());
        this.add(scrollpane);
        this.add(statusBar, BorderLayout.SOUTH);
    }
    
    private void updateStatusBar(List<T> shownList)
    {
        final int[] count = new int[this.types.length];
        for (Resource r : shownList) {
            count[r.getType()]++;
        }
        for (int i = 0; i < count.length; i++) {
            final String text = String.format("%s (%d)", this.types[i].name, count[i]);
            this.typeLabels.get(i).setText(text);
        }
    }
    
    public void addChooseListener(Callback listener)
    {
        this.list.addChooseListener(listener);
    }
    
    public void filter(String filter)
    {
        this.list.filter(filter);
    }

    public void setSelectedIndex(int idx)
    {
        this.list.setSelectedIndex(idx);
    }
    
    public boolean setSelectedResource(String name)
    {
        this.list.setSelectedIndex(0);

        if (name == null) {
            return false;
        }
        
        int idx = 0;
        for (Resource r : this.resourceList) {
            if (name.equals(r.getName())) {
                this.list.setSelectedIndex(idx);
                this.list.ensureIndexIsVisible(idx);
                return true;
            }
            idx++;
        }

        return false;
    }
    
    public T getSelectedResource()
    {
        return this.list.getSelectedValue();
    }
    
    private static class Renderer<T extends Resource> extends JPanel
        implements ListCellRenderer<T>
    {
        private final Resource.Type[] types;
        private final JPanel pnlType;
        private final JLabel lblName;
        private final JLabel lblLocation;

        private Renderer(Resource.Type[] types)
        {
            this.types = types;

            this.setLayout(new BorderLayout());
            this.setOpaque(true);

            this.pnlType = new JPanel();
            this.pnlType.setPreferredSize(new Dimension(10, 10));
            this.pnlType.setOpaque(true);
            
            this.lblName = new JLabel();
            this.lblName.setBorder(new EmptyBorder(0, 10, 0, 10));

            this.lblLocation = new JLabel();
            this.lblLocation.setForeground(new Color(0x666666));
            this.lblLocation.setBorder(new EmptyBorder(0, 10, 0, 10));

            this.add(pnlType, BorderLayout.WEST);
            this.add(lblName);
            this.add(lblLocation, BorderLayout.EAST);
            this.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)));
        }

        @Override
        public Component getListCellRendererComponent(
            JList<? extends T> list,
            T value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            this.pnlType.setBackground(this.types[value.getType()].color);
            this.lblName.setText(value.getName());
            final String path = value.getPath();
            this.lblLocation.setVisible(path != null);
            if (path != null) {
                this.lblLocation.setText(this.shortenPath(path));
            }
            return this;
        }
        
        private String shortenPath(String path)
        {
            final List<File> locations = EngineSourceManager.getLocations();
            for (File l : locations) {
                final String abs = l.getAbsolutePath();
                if (path.startsWith(abs)) {
                    final int len = abs.length();
                    if (path.length() > len &&
                        (path.charAt(len) == '/' || path.charAt(len) == '\\'))
                    {
                        return path.substring(len + 1);
                    }
                    return path.substring(len);
                }
            }
            return path;
        }
    }
}