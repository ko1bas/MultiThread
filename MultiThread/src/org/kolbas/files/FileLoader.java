package org.kolbas.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class FileLoader{
	BufferedReader reader;

	public FileLoader(String fname) throws IOException, FileNotFoundException {
		reader = new BufferedReader(new FileReader(new File(fname)));
	}

	
	public FileLoader(String[] args, int start) throws IOException,
			FileNotFoundException {

		if (start < 0)
			start = 0;
		FileInputStream tmp;
		Vector<InputStream> inputStreams = new Vector<InputStream>();
		for (int i = start; i < args.length; i++) {
			tmp = new FileInputStream(args[i]);
			inputStreams.add(tmp);
		}
		Enumeration<InputStream> enu = inputStreams.elements();
		SequenceInputStream sis = new SequenceInputStream(enu);
		reader = new BufferedReader(new InputStreamReader(sis));
	}

	public List<String> nextStrings(int N) {
		try {
			List<String> res = new ArrayList<String>(N);

			for (int i = 0; i < N; i++) {
				String buf = reader.readLine();
				if (buf == null)
					break;
				res.add(buf);
			}
			return res;

		} catch (IOException e) {
			return (List<String>) Collections.EMPTY_LIST;
		}
	}

	public String next() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			return null;
		}
	}

	public void close() throws IOException {
		reader.close();
	}

}
