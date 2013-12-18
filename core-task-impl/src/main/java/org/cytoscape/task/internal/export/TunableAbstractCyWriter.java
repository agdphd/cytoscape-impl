package org.cytoscape.task.internal.export;

/*
 * #%L
 * Cytoscape Core Task Impl (core-task-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriterFactory;
import org.cytoscape.io.write.CyWriterManager;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;


/**
 * An abstract utility implementation of a Task that writes a user defined 
 * file to a file type determined by a provided writer manager.  This class
 * is meant to be extended for specific file types such that the appropriate
 * {@link org.cytoscape.io.write.CyWriter} can be identified.
 */
public abstract class TunableAbstractCyWriter<S extends CyWriterFactory,T extends CyWriterManager<S>> extends AbstractCyWriter<S,T> implements
		TunableValidator {

	/**
	 * The list of file type options generated by the file types
	 * available from the CyWriterManager.  This field should not
	 * be set directly, but rather handled by the {@link org.cytoscape.work.Tunable}
	 * processing.
	 */
	@Tunable(description = "Select the export file format")
	public ListSingleSelection<String> options;

	protected final String getExportFileFormat() {
		return options.getSelectedValue();
	}

	/**
	 * @param writerManager The CyWriterManager to be used to determine which
	 * {@link org.cytoscape.io.write.CyWriter} to be used to write the file chosen by the user. 
	 */
	public TunableAbstractCyWriter(T writerManager) {
		super(writerManager);
		final List<String> availableFormats = new ArrayList<String>(getFileFilterDescriptions());
		options = new ListSingleSelection<String>(availableFormats);
	}

	@Override
	public final ValidationState getValidationState(final Appendable msg) {
		if(getExportFileFormat()==null){
			try {
				msg.append("Select a file type.");
			} catch (final Exception e) {
				/* Intentionally empty. */
			}
			return ValidationState.INVALID;
		}
		
		if(outputFile==null){
			try {
				msg.append("Enter the file address.");
			} catch (final Exception e) {
				/* Intentionally empty. */
			}
			return ValidationState.INVALID;
		}
			
		boolean isFileChanged = false;
		// Make sure we have the right extension, if not, then force it:
		if (!fileExtensionIsOk(outputFile)){
			outputFile = addOrReplaceExtension(outputFile);
			isFileChanged = true;
		}
		if (isFileChanged && outputFile.exists()) {
			try {
				msg.append("File already exists, are you sure you want to overwrite it?");
			} catch (final Exception e) {
				/* Intentionally empty. */
			}
			return ValidationState.REQUEST_CONFIRMATION;
		} else
			return ValidationState.OK;
	}

	protected final boolean fileExtensionIsOk(final File file) {
		final String exportFileFormat = getExportFileFormat();
		if (exportFileFormat == null)
			return true;

		final CyFileFilter filter = getFileFilter(exportFileFormat);
		if (filter == null)
			return true;

		final String extension = FilenameUtils.getExtension(file.getName());
		if (extension.isEmpty())
			return false;

		return filter.getExtensions().contains(extension);
	}


	protected final File addOrReplaceExtension(final File file) {
		final CyFileFilter filter = getFileFilter(getExportFileFormat());
		if (filter == null)
			return file;

		final Iterator<String> extensions = filter.getExtensions().iterator();
		if (!extensions.hasNext())
			return file;

		final String extension = extensions.next();
		final String pathWithoutExtension = stripExtension(file.getAbsolutePath());

		return new File(pathWithoutExtension + "." + extension);
	}

	private static String stripExtension(final String fileName) {
		final String extension = FilenameUtils.getExtension(fileName);
		if (extension == null)
			return fileName;

		return fileName.substring(0, fileName.length() - 1 - extension.length());
	}
}
