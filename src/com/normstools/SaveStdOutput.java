package com.normstools;


import java.io.*;

//------------------------------------------------------------------------
public class SaveStdOutput extends PrintStream {
  final static boolean    debug = false;      // controls debug output

  static OutputStream logfile;
  static PrintStream oldStdout = null;
  static PrintStream oldStderr = null;

  private boolean    echoOutput = true;     //Also output to old setting

  // Constructor - we're the only one that can use it!
  private SaveStdOutput(PrintStream ps, boolean echoOutput) {
	   super(ps);
    this.echoOutput = echoOutput;   
//     System.out.println("SaveStdOutput constructor called");
  } // end Constructor

  //------------------------------------------------------------
  // Starts copying stdout and stderr to the file f.
  public static void start(String f) throws IOException {
   	  // Create/Open logfile.
  	  OutputStream os = new PrintStream(
  	               new BufferedOutputStream(
  	                   new FileOutputStream(f, true)));  // append to current
      doCommon(os, true);        
 } // end start()

  // Copy STDOUT and STDERR to an output stream
  public static void start(OutputStream os) {
      doCommon(os, true);
  } // end start()
  public static void start(OutputStream os, boolean eO) {
      doCommon(os, eO);
  } // end start()

  //-------------------------------------------------------
 // Finish up
 private static void doCommon(OutputStream os, boolean echoOutput) {
      // Only allow to be called once
      if (oldStdout != null) {
          if (debug)
              System.err.println("SaveStdOutput start() called twice");
          return;                    // Exit if already open
      }
    logfile = os;
  	// Save old settings.
  	oldStdout = System.out;
  	oldStderr = System.err;

  	// Start redirecting the output.
  	System.setOut(new SaveStdOutput(System.out, echoOutput));
  	System.setErr(new SaveStdOutput(System.err, echoOutput));
  } // end doCommon()

  //--------------------------------------
  // Restores the original settings.
  public static void stop() {
      if (oldStdout == null) {
          if (debug)
              System.err.println("SaveStdOutput stop() called before start()");
          return;
      }
  	  System.setOut(oldStdout);
      oldStdout = null;              //Clear
  	  System.setErr(oldStderr);
      try {
	       logfile.close();
      } catch (Exception ex) {
          System.err.println("SaveStdOutput stop() ex " + ex.getMessage());
          ex.printStackTrace();
      }
  } // end stop()

  //   Override the PrintStream write methods
  public void write(int b) {
      try {
	        logfile.write(b);
      } catch (Exception e) {
          e.printStackTrace();
          setError();
      }
    if (echoOutput)
 	   super.write(b);
  } // end write()

  // PrintStream override.
  public void write(byte buf[], int off, int len) {
      try {
	        logfile.write(buf, off, len);
      } catch (Exception e) {
          e.printStackTrace();
          setError();
      }
    if (echoOutput)
 	   super.write(buf, off, len);  
  }  // end write()

  //-------------------------------------------------------------------
  // Following for testing SaveStdOutput class: Comment out when done!
  public static void main(String[] args) {
      try {
          // Start capturing characters into the log file.
          SaveStdOutput.start("log.txt");

          // Test it.
          System.out.println("Here's is some stuff to stdout. " 
                                  + new java.util.Date());
          System.err.println("Here's is some stuff to stderr.");
          System.out.println("Let's throw an exception...");
          new Exception().printStackTrace();
          throw new Exception("this is thrown");
      } catch (Exception e) {
          e.printStackTrace();
      } finally {
          // Stop capturing characters into the log file 
          // and restore old setup.
          SaveStdOutput.stop();
      }
      System.out.println("This should be to console only!");
  } // end main() */
}  // end class SaveStdOutput