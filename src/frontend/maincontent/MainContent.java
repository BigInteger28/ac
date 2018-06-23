package frontend.maincontent;

import java.awt.BorderLayout;
import java.awt.Container;

import frontend.FrontendController;

public class MainContent
{
    public static void addTo(
        Container container,
        FrontendController controller)
    {
        container.setLayout(new BorderLayout());
        container.add(new Ribbon(controller).createComponent(), BorderLayout.NORTH);
        container.add(new GamePanel(controller));
    }
}
