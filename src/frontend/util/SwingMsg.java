package frontend.util;

import java.awt.Component;

import javax.swing.JOptionPane;

import static javax.swing.JOptionPane.*;

public class SwingMsg
{
    public static String format(Throwable t)
    {
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }

    public static void err_ok(Component parent, String title, String message)
    {
        JOptionPane.showMessageDialog(parent, message, title, ERROR_MESSAGE);
    }
}
