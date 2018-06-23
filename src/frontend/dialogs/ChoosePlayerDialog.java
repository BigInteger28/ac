package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

import backend.Player;
import resources.PlayerResource;
import frontend.util.SwingUtil;

import static javax.swing.JScrollPane.*;

public class ChoosePlayerDialog extends JDialog
{
    public static Player show(
        Window parentWindow,
        int playerNumber,
        List<PlayerResource> playerList)
    {
        final String title = "Select player " + playerNumber;

        final JDialog dialog = new JDialog(parentWindow);
        
        final JLabel lbl = new JLabel(title);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(SwingUtil.deriveFont(lbl.getFont(), true, 1.2f));
        lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        final PlayerResource[] result = { null };
        final JList<PlayerResource> list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setModel(new ListModel<PlayerResource>() {
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
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus)
            {
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                this.setText(((PlayerResource) value).getName());
                return this;
            }
        });
        list.setSelectedIndex(0);
        final JScrollPane scrollpane = new JScrollPane(list);
        scrollpane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setPreferredSize(new Dimension(550, 300));
        SwingUtilities.invokeLater(() -> list.requestFocusInWindow());
        
        final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final JButton btnCancel = new JButton("Cancel");
        final JButton btnOk = new JButton("Ok");
        btnCancel.addActionListener(e -> SwingUtil.close(dialog));
        btnOk.addActionListener(e -> {
            result[0] = list.getSelectedValue();
            SwingUtil.close(dialog);
        });
        pnlButtons.add(btnCancel);
        pnlButtons.add(btnOk);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.add(lbl, BorderLayout.NORTH);
        dialog.add(scrollpane);
        dialog.add(pnlButtons, BorderLayout.SOUTH);
        dialog.setTitle(title);
        dialog.setModal(true);
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
        dialog.dispose();
        
        if (result[0] == null) {
            return null;
        }
        
        return result[0].createPlayer(playerNumber);
    }
}
