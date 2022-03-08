/*
 * 05/11/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * I/O related utility methods.  We should think of a better location for
 * these methods.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class IOUtil {

	private static Map<String, String> DEFAULT_ENV;


	/**
	 * Private constructor to prevent instantiation.
	 */
	private IOUtil() {
	}


	/**
	 * Gets the environment of the current process.  Works with Java 1.4 as
	 * well as 1.5+.
	 *
	 * @return A mapping of environment variable names to values.
	 */
	private static Map<String, String> getDefaultEnvMap() {

		// If we've already created it...
		if (DEFAULT_ENV!=null) {
			return DEFAULT_ENV;
		}

		// In Java 5+, we can just get the environment directly
		try {
			DEFAULT_ENV = System.getenv();
		} catch (SecurityException e) { // In an applet perhaps?
			DEFAULT_ENV = Collections.emptyMap();
		}

		return DEFAULT_ENV;

	}


	/**
	 * Returns the value of an environment variable.  This method is here so
	 * we don't get an exception when calling <tt>System.getenv()</tt> in Java
	 * 1.4 (which we support).
	 *
	 * @param var The environment variable.
	 * @return The value of the variable, or <code>null</code> if it is not
	 *         defined.
	 */
	public static String getEnvSafely(String var) {
		String value = null;
		try {
			value = System.getenv(var);
		} catch (SecurityException e) { // In an applet perhaps?
			// Swallow
		}
		return value;
	}


	/**
	 * Returns the environment of the current process, with some variables
	 * possibly added/overwritten.  This method works even with Java 1.4.
	 *
	 * @param toAdd The environment variables to add/overwrite in the returned
	 *        array.  This array should have an even length, with even indices
	 *        containing variable names and odd indices containing the variable
	 *        values.
	 * @return The environment variables.  This array's entries will be of the
	 *         form "<code>name=value</code>", so it can be passed directly
	 *         into <code>Runtime.exec()</code>.
	 */
	public static String[] getEnvironmentSafely(String[] toAdd) {

		Map<String, String> env = getDefaultEnvMap();

		// Put any vars they want to explicitly specify
		if (toAdd!=null) {
			Map<String, String> temp = new HashMap<>(env);
			for (int i=0; i<toAdd.length; i+=2) {
				temp.put(toAdd[i], toAdd[i+1]);
			}
			env = temp;
		}

		// Create an array of "name=value" items, like Runtime.exec() wants
		int count = env.size();
		String[] vars = new String[count];
		int i = 0;
		for (Map.Entry<String, String> entry : env.entrySet()) {
			vars[i++] = entry.getKey() + "=" + entry.getValue();
		}

		return vars;

	}


	/**
	 * Runs a process, possibly capturing its stdout and/or stderr.
	 *
	 * @param p The process.
	 * @param stdout A buffer in which to put stdout, or <code>null</code> if
	 *        you don't want to keep it.
	 * @param stderr A buffer in which to keep stderr, or <code>null</code>
	 *        if you don't want to keep it.
	 * @return The return code of the process.
	 * @throws IOException If an IO error occurs.
	 */
	// TODO: Allow a timeout to be passed in
	public static int waitForProcess(Process p, StringBuilder stdout,
									StringBuilder stderr) throws IOException {

		InputStream in = p.getInputStream();
		InputStream err = p.getErrorStream();
		Thread t1 = new Thread(new OutputCollector(in, stdout));
		Thread t2 = new Thread(new OutputCollector(err, stderr));
		t1.start();
		t2.start();
		int rc = -1;

		try {
			rc = p.waitFor();
			t1.join();
			t2.join();
		} catch (InterruptedException ie) {
			p.destroy();
		} finally {
			in.close();
			err.close();
		}

		return rc;

	}


	/**
	 * Utility testing method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
        for (String arg : args) {
            String value = IOUtil.getEnvSafely(arg);
            System.out.println(arg + "=" + value);
        }
	}


}
