package frontend.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import frontend.util.Callback;
import resources.EngineSourceManager;
import resources.PlayerResource;

import static javax.swing.JScrollPane.*;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PlayerList extends JPanel
{
    private final FilterableList<PlayerResource> list;
    private final List<PlayerResource> playerList;
    private final HashMap<PlayerResource.Type, JLabel> playerTypeLabels;

    public PlayerList(List<PlayerResource> playerList)
    {
        this.playerTypeLabels = new HashMap<>();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = PlayerResource.Type.values().length;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        final JPanel statusBar = new JPanel(new GridBagLayout());
        for (PlayerResource.Type t : PlayerResource.Type.values()) {
            final JLabel label = new JLabel();
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            this.playerTypeLabels.put(t, label);
            final JPanel pnlCol = new JPanel();
            pnlCol.setPreferredSize(new Dimension(10, 5));
            pnlCol.setBackground(t.color);
            pnlCol.setOpaque(true);
            final JPanel pnlSub = new JPanel(new BorderLayout());
            pnlSub.add(pnlCol, BorderLayout.WEST);
            pnlSub.add(label);
            statusBar.add(pnlSub);
            c.gridx++;
        }

        this.playerList = playerList;
        this.list = new FilterableList<>(playerList);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setCellRenderer(new Renderer());
        this.list.addFilterListener(this::updateStatusBar);
        this.updateStatusBar(playerList);
        final JScrollPane scrollpane = new JScrollPane(this.list);
        scrollpane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> this.list.requestFocusInWindow());
        
        this.setLayout(new BorderLayout());
        this.add(scrollpane);
        this.add(statusBar, BorderLayout.SOUTH);
    }
    
    private void updateStatusBar(List<PlayerResource> shownList)
    {
        final PlayerResource.Type[] vals = PlayerResource.Type.values();
        final int[] count = new int[vals.length];
        for (PlayerResource r : shownList) {
            count[r.getType().ordinal()]++;
        }
        for (int i = 0; i < count.length; i++) {
            final String text = String.format("%s (%d)", vals[i].name, count[i]);
            this.playerTypeLabels.get(vals[i]).setText(text);
        }
    }
    
    public void addChooseListener(Callback listener)
    {
        this.list.addChooseListener(listener);
    }
    
    public void setSelectedPlayer(String name)
    {
        this.list.setSelectedIndex(0);

        if (name == null) {
            return;
        }
        
        int idx = 0;
        for (PlayerResource r : this.playerList) {
            if (name.equals(r.getName())) {
                this.list.setSelectedIndex(idx);
                this.list.ensureIndexIsVisible(idx);
                return;
            }
            idx++;
        }
    }
    
    public PlayerResource getSelectedPlayerResource()
    {
        return this.list.getSelectedValue();
    }
    
    private static class Renderer extends JPanel
        implements ListCellRenderer<PlayerResource>
    {
        private final JPanel pnlType;
        private final JLabel lblName;
        private final JLabel lblLocation;

        private Renderer()
        {
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
            JList<? extends PlayerResource> list,
            PlayerResource value,
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
            this.pnlType.setBackground(value.getType().color);
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
