/**
 * 
 */
package org.kolbas.files;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


public class FileSaver {
	private final String fname;
	private final BufferedWriter writer;

	public FileSaver(String fname) throws UnsupportedEncodingException,
			FileNotFoundException {
		this.fname = fname;
		writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(this.fname), "UTF8"));
	}
	
	public void write(String value) throws IOException
	{
		writer.write(value+"\r\n");
	}
	
	public void close() throws IOException {
		writer.close();
	}

}
