package frontend.components;

import javax.swing.border.TitledBorder;
import java.awt.*;

public class DefaultTitledBorder extends TitledBorder
{

    public DefaultTitledBorder(String title) {
        super(title);
        this.setTitleColor(new Color(0x0000FF));
    }

}
