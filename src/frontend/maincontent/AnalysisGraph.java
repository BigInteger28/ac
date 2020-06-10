package frontend.maincontent;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import frontend.util.SwingUtil;

public class AnalysisGraph extends JPanel
{
	public static JPanel bordered()
	{
		JPanel bordered = new JPanel(new BorderLayout());
		bordered.setBorder(new CompoundBorder(new EmptyBorder(10, 0, 10, 10), SwingUtil.titledBorder("Analysis")));
		bordered.add(new AnalysisGraph());
		return bordered;
	}
}
