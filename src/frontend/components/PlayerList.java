package frontend.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListDataListener;

import frontend.util.Callback;
import resources.EngineSourceManager;
import resources.PlayerResource;

import static javax.swing.JScrollPane.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class PlayerList extends JPanel
{
    private final JList<PlayerResource> list;
    private final List<PlayerResource> playerList;

    public PlayerList(List<PlayerResource> playerList)
    {
        this.playerList = playerList;
        this.list = new JList<>();
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setModel(new ListModel<PlayerResource>() {
            @Override
            public int getSize()
            {
                return playerList.size();
            }
            @Override
            public PlayerResource getElementAt(int index)
            {
                return playerList.get(index);
            }
            @Override
            public void addListDataListener(ListDataListener l)
            {
            }
            @Override
            public void removeListDataListener(ListDataListener l)
            {
            }
        });
        this.list.setCellRenderer(new Renderer());
        final JScrollPane scrollpane = new JScrollPane(this.list);
        scrollpane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> this.list.requestFocusInWindow());
        
        this.setLayout(new BorderLayout());
        this.add(scrollpane);
    }
    
    public void addChooseListener(Callback listener)
    {
        this.list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() > 1) {
                    listener.invoke();
                }
            }
        });
        this.list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER ||
                    e.getKeyChar() == '\n' ||
                    e.getKeyChar() == '\r')
                {
                    listener.invoke();
                }
            }
        });
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
            this.pnlType.setBackground(new Color(value.getTypeColor()));
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
