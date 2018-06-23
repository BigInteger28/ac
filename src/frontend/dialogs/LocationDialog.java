package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import frontend.FrontendController;
import frontend.util.SwingUtil;
import resources.EngineSourceManager;

public class LocationDialog
{
    public static void show(FrontendController controller)
    {
        final JDialog dialog = new JDialog(controller.getWindow());
        
        final List<File> locationList = EngineSourceManager.getLocations();
        final List<ListDataListener> listDataListeners = new ArrayList<>();

        final JList<File> list = new JList<>();
        list.setPreferredSize(new Dimension(550, 300));
        list.setModel(new ListModel<File>() {
            @Override
            public int getSize()
            {
                return locationList.size();
            }
            @Override
            public File getElementAt(int index)
            {
                return locationList.get(index);
            }
            @Override
            public void addListDataListener(ListDataListener l)
            {
                listDataListeners.add(l);
            }
            @Override
            public void removeListDataListener(ListDataListener l)
            {
                listDataListeners.remove(l);
            }
        });
        list.setCellRenderer(new DefaultListCellRenderer());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JPanel pnlListControl = new JPanel(new GridBagLayout());
        final JButton btnAdd = new JButton("+");
        final JButton btnRemove = new JButton("-");
        btnAdd.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser(); 
            fc.setDialogTitle("Choose a directory");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            if (fc.showOpenDialog(dialog) != JFileChooser.APPROVE_OPTION) { 
                return;
            }
            final File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }
            if (file.exists() && file.isDirectory()) {
                locationList.add(file);
                for (ListDataListener l : listDataListeners) {
                    l.contentsChanged(new ListDataEvent(
                        btnRemove,
                        ListDataEvent.INTERVAL_REMOVED,
                        locationList.size() - 1,
                        locationList.size() - 1
                    ));
                }
            }
        });
        btnRemove.addActionListener(e -> {
            final int idx = list.getSelectedIndex();
            if (0 <= idx && idx < locationList.size()) {
                locationList.remove(idx);
                for (ListDataListener l : listDataListeners) {
                    l.contentsChanged(new ListDataEvent(
                        btnRemove,
                        ListDataEvent.INTERVAL_REMOVED,
                        idx,
                        idx
                    ));
                }
            }
        });
        pnlListControl.add(btnAdd, c); c.gridy++;
        pnlListControl.add(btnRemove, c); c.gridy++;
        c.weighty = 1;
        pnlListControl.add(new JLabel(), c);
        final JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.add(pnlListControl, BorderLayout.WEST);
        pnlMain.add(list);

        final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final JButton btnCancel = new JButton("Cancel");
        final JButton btnOk = new JButton("Ok");
        btnCancel.addActionListener(e -> SwingUtil.close(dialog));
        btnOk.addActionListener(e -> {
            EngineSourceManager.setLocations(locationList);
            SwingUtil.close(dialog);
        });
        pnlButtons.add(btnCancel);
        pnlButtons.add(btnOk);
        
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.add(pnlMain);
        dialog.add(pnlButtons, BorderLayout.SOUTH);
        dialog.setTitle("Edit locations");
        dialog.setModal(true);
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setLocationRelativeTo(controller.getWindow());
        dialog.setVisible(true);
        dialog.dispose();
    }
}
