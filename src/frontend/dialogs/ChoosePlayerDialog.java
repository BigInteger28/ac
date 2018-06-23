package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

import backend.Player;
import frontend.resources.PlayerResource;
import frontend.util.SwingUtil;

public class ChoosePlayerDialog extends JDialog
{
    
    public static Player choosePlayer(Window parentWindow, int playerNumber)
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
        list.setPreferredSize(new Dimension(350, 300));
        list.setModel(new ListModel<PlayerResource>() {
            @Override
            public int getSize()
            {
                return 1;
            }
            @Override
            public PlayerResource getElementAt(int index)
            {
                return new PlayerResource(null);
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
                this.setText("<Human player>");
                return this;
            }
        });
        list.setSelectedIndex(0);
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
        dialog.add(list);
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
