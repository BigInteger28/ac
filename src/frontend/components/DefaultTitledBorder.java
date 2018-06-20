package frontend.components;

import javax.swing.border.TitledBorder;
import java.awt.*;

class DefaultTitledBorder extends TitledBorder
{

    DefaultTitledBorder(String title) {
        super(title);
        this.setTitleColor(new Color(0x0000FF));
    }

}
