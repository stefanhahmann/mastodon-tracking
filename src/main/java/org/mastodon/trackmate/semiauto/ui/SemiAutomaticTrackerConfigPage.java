package org.mastodon.trackmate.semiauto.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.mastodon.app.ui.settings.ModificationListener;
import org.mastodon.app.ui.settings.SelectAndEditProfileSettingsPage;
import org.mastodon.app.ui.settings.SettingsPanel;
import org.mastodon.app.ui.settings.style.StyleProfile;
import org.mastodon.app.ui.settings.style.StyleProfileManager;
import org.mastodon.grouping.GroupHandle;
import org.mastodon.revised.mamut.MamutProject;
import org.mastodon.revised.mamut.WindowManager;
import org.mastodon.util.Listeners;
import org.mastodon.util.Listeners.SynchronizedList;
import org.scijava.Context;

import bdv.spimdata.SpimDataMinimal;
import mpicbg.spim.data.SpimDataException;

public class SemiAutomaticTrackerConfigPage extends SelectAndEditProfileSettingsPage< StyleProfile< SemiAutomaticTrackerSettings > >
{

	public SemiAutomaticTrackerConfigPage( final String treePath, final SemiAutomaticTrackerSettingsManager settingsManager, final SpimDataMinimal spimData, final GroupHandle groupHandle )
	{
		super( treePath,
				new StyleProfileManager<>( settingsManager, new SemiAutomaticTrackerSettingsManager( false ) ),
				new SemiAutomaticTrackerSettingsEditPanel( settingsManager.getDefaultStyle(), spimData, groupHandle ) );
	}

	static class SemiAutomaticTrackerSettingsEditPanel implements SemiAutomaticTrackerSettings.UpdateListener, SelectAndEditProfileSettingsPage.ProfileEditPanel< StyleProfile< SemiAutomaticTrackerSettings > >
	{

		private final SemiAutomaticTrackerSettings editedSettings;

		private final SemiAutomaticTrackerConfigPanel settingsPanel;

		private boolean trackModifications = true;

		private final SynchronizedList< ModificationListener > modificationListeners;

		public SemiAutomaticTrackerSettingsEditPanel( final SemiAutomaticTrackerSettings initialSettings, final SpimDataMinimal spimData, final GroupHandle groupHandle )
		{
			editedSettings = initialSettings.copy( "Edited" );
			settingsPanel = new SemiAutomaticTrackerConfigPanel( spimData, editedSettings, groupHandle );
			modificationListeners = new Listeners.SynchronizedList<>();
			editedSettings.updateListeners().add( this );
		}

		@Override
		public Listeners< ModificationListener > modificationListeners()
		{
			return modificationListeners;
		}

		@Override
		public void loadProfile( final StyleProfile< SemiAutomaticTrackerSettings > profile )
		{
			trackModifications = false;
			editedSettings.set( profile.getStyle() );
			trackModifications = true;
		}

		@Override
		public void storeProfile( final StyleProfile< SemiAutomaticTrackerSettings > profile )
		{
			trackModifications = false;
			editedSettings.setName( profile.getStyle().getName() );
			trackModifications = true;
			profile.getStyle().set( editedSettings );
		}

		@Override
		public JPanel getJPanel()
		{
			return settingsPanel;
		}

		@Override
		public void settingsChanged()
		{
			if ( trackModifications )
				modificationListeners.list.forEach( ModificationListener::modified );
		}
	}

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, SpimDataException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		Locale.setDefault( Locale.ROOT );
		System.setProperty( "apple.laf.useScreenMenuBar", "true" );

		final String bdvFile = "../TrackMate3/samples/mamutproject/datasethdf5.xml";
//		final MamutProject project = new MamutProjectIO().load( projectFile );
		final MamutProject project = new MamutProject( null, new File( bdvFile ) );

		final Context context = new Context();
		final WindowManager windowManager = new WindowManager( context );
		windowManager.getProjectManager().open( project );
		final SpimDataMinimal spimData = ( SpimDataMinimal ) windowManager.getAppModel().getSharedBdvData().getSpimData();

		final GroupHandle groupHandle = windowManager.getAppModel().getGroupManager().createGroupHandle();

		final SemiAutomaticTrackerSettingsManager styleManager = new SemiAutomaticTrackerSettingsManager();

		final SettingsPanel settings = new SettingsPanel();
		settings.addPage( new SemiAutomaticTrackerConfigPage( "Semi-automatic tracker settings", styleManager, spimData, groupHandle ) );

		final JDialog dialog = new JDialog( ( Frame ) null, "Settings" );
		dialog.getContentPane().add( settings, BorderLayout.CENTER );

		settings.onOk( () -> dialog.setVisible( false ) );
		settings.onCancel( () -> dialog.setVisible( false ) );

		dialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		dialog.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				settings.cancel();
			}
		} );

		dialog.pack();
		dialog.setVisible( true );
	}
}
