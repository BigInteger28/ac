package frontend.components;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

class TripleBorder
{

    static Border create(Border outer, Border mid, Border inner)
    {
        return new CompoundBorder(new CompoundBorder(outer, mid), inner);
    }

}
