package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import backend.Player;
import resources.DatabaseResource;
import resources.PlayerResource;
import frontend.VolatileLogger;
import frontend.components.PlayerList;
import frontend.util.Callback;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;

public class ChoosePlayerDialog extends JDialog
{
    public static Player show(
        Window parentWindow,
        int playerNumber,
        List<PlayerResource> playerList,
        List<DatabaseResource> dbList,
        String preselectedPlayer)
    {
        final String title = "Select player " + playerNumber;

        final JDialog dialog = new JDialog(parentWindow);
        
        final JLabel lbl = new JLabel(title);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(SwingUtil.deriveFont(lbl.getFont(), true, 1.2f));
        lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        final PlayerResource[] result = { null };
        final PlayerList list = new PlayerList(playerList);
        list.setPreferredSize(new Dimension(550, 300));
        list.setSelectedPlayer(preselectedPlayer);
        final Callback chooseListener = () -> {
            result[0] = list.getSelectedPlayerResource();
            SwingUtil.close(dialog);
        };
        list.addChooseListener(chooseListener);
        
        final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final JButton btnCancel = new JButton("Cancel");
        final JButton btnOk = new JButton("Ok");
        btnCancel.addActionListener(e -> SwingUtil.close(dialog));
        btnOk.addActionListener(e -> chooseListener.invoke());
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
        
        final Player player;
        try {
            player = result[0].createPlayer(playerNumber);
        } catch (Exception e) {
            final String name = result[0].getName();
            VolatileLogger.logf(e, "creating player '%s'", name);
            final String _message = SwingMsg.format(e);
            final String _title = "Could not create player";
            SwingMsg.err_ok(parentWindow, _title, _message);
            return show(parentWindow, playerNumber, playerList, dbList, name);
        }
        
        if (player.isHumanControlled()) {
            return player;
        }
        
        final String fileName = player.getName();
        final int lidx = fileName.lastIndexOf('.');
        final String playerName;
        if (lidx != -1) {
            playerName = fileName.substring(0, lidx);
        } else {
            playerName = fileName;
        }
        
        player.useDatabase(ChooseDatabaseDialog.show(
            parentWindow,
            playerNumber,
            dbList,
            playerName
        ));
        
        return player;
    }
}
