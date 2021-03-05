package org.mastodon.tracking.mamut.trackmate.wizard;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Descriptor1 extends WizardPanelDescriptor
{

	private static final String ID = "panel 1";

	public Descriptor1()
	{
		panelIdentifier = ID;
		targetPanel = new Descriptor1Panel();
	}

	private class Descriptor1Panel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public Descriptor1Panel()
		{
			final JLabel jLabel = new JLabel( "Heyyyy" );
			add( jLabel );
		}
	}
}
