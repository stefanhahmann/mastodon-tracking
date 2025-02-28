/*-
 * #%L
 * mastodon-tracking
 * %%
 * Copyright (C) 2017 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.tracking.mamut.trackmate.wizard.descriptors;

import static org.mastodon.tracking.mamut.trackmate.wizard.descriptors.LogPanel.ERROR_COLOR;
import static org.mastodon.tracking.mamut.trackmate.wizard.descriptors.LogPanel.NORMAL_COLOR;
import static org.mastodon.tracking.mamut.trackmate.wizard.descriptors.LogPanel.WARN_COLOR;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import javax.swing.JLabel;

import org.mastodon.mamut.WindowManager;
import org.mastodon.tracking.mamut.detection.SpotDetectorOp;
import org.mastodon.tracking.mamut.trackmate.TrackMate;
import org.mastodon.tracking.mamut.trackmate.wizard.WizardPanelDescriptor;
import org.scijava.Context;
import org.scijava.Contextual;
import org.scijava.NullContextException;
import org.scijava.log.AbstractLogService;
import org.scijava.log.LogLevel;
import org.scijava.log.LogMessage;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.SciJavaPlugin;

public abstract class SpotDetectorDescriptor extends WizardPanelDescriptor implements SciJavaPlugin, Contextual
{

	@Parameter
	private Context context;

	// -- Contextual methods --

	@Override
	public Context context()
	{
		if ( context == null )
			throw new NullContextException();
		return context;
	}

	@Override
	public Context getContext()
	{
		return context;
	}

	/**
	 * Returns the classes of the detectors this panel can configure.
	 *
	 * @return the detectors this panel can configure.
	 */
	public abstract Collection< Class< ? extends SpotDetectorOp > > getTargetClasses();

	public abstract void setTrackMate( TrackMate trackmate );

	public abstract void setWindowManager( final WindowManager windowManager );

	/**
	 * Returns a default settings map, suitable to be configured with this
	 * descriptor.
	 *
	 * @return a default settings map.
	 */
	public abstract Map< String, Object > getDefaultSettings();

	protected final static class JLabelLogger extends AbstractLogService
	{

		private final JLabel lbl;

		protected JLabelLogger( final JLabel lbl )
		{
			this.lbl = lbl;
		}

		@Override
		protected void messageLogged( final LogMessage message )
		{
			lbl.setText( message.text() );
			final Color fg;
			switch ( message.level() )
			{
			default:
				fg = NORMAL_COLOR;
				break;
			case LogLevel.ERROR:
				fg = ERROR_COLOR;
				break;
			case LogLevel.WARN:
				fg = WARN_COLOR;
				break;
			}
			lbl.setForeground( fg );
		}
	}
}
