package com.normsstuff.simpleserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.normsstuff.simpleserver.SimpleWebServer;
import com.normstools.SaveStdOutput;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.widget.Toast;


public class RunServerService extends Service implements MessageDisplayer {
	// Define some constants
	private static final int NOTIFY_ME_ID = 1377;
	final String rootDir = Environment.getExternalStorageDirectory().getPath();
	final String LogFilePathPfx = rootDir + "/RunServerService_log_";
	final String LogFilePathSfx = "_A.txt";

	final public static String SaveFolderID = "SaveFolder";
	final public static String TheRootID = "TheRoot";
	
	// Define values for server
	String root = "";
	String savedFilesFolder = "";
	boolean addFilenamePrefix = false;
	String host = null;
	
    SimpleWebServer server;
    
    String savedMessages = "";
    boolean debugging = false;       //  Control saving of printlns to own file??? NOTHING written
    
	private NotificationManager mgr = null;
    SimpleDateFormat sdfTime = new SimpleDateFormat("'T' HH:mm:ss", Locale.US); // show time
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss", Locale.US); // Builds Filename

    private Thread.UncaughtExceptionHandler lastUEH = null;  // For trapping exceptions
    
	ResultReceiver resultReceiver;  // Connection to GUI for showing a message

    
    //----------------------------------------------------
    // Define inner class to handle exceptions
    class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e){
           java.util.Date dt =  new java.util.Date();
           String fn = rootDir + "/RunServerService_" + sdf.format(dt) + "_Trace.txt";   // 2014-02-02T193504
           try{ 
              PrintStream ps = new PrintStream( fn );
              e.printStackTrace(ps);
              ps.close();
              System.out.println("ScrnOnOffSrcv  wrote trace to " + fn);
              e.printStackTrace(); // capture here also???
              SaveStdOutput.stop(); // close here vs calling flush() in class  ???
           }catch(Exception x){
              x.printStackTrace();
           }
           lastUEH.uncaughtException(t, e); // call last one  Gives: "Unfortunately ... stopped" message
           return;    //???? what to do here
        }
     }


			
 	//=============================================================================
    @Override
    public void onCreate() {
        super.onCreate();
        
        Toast.makeText(getBaseContext(), "RunServerSrvc starting in onCreate", Toast.LENGTH_SHORT).show();
        java.util.Date dt =  new java.util.Date();
        
        if(debugging) {
	      	 // Quick and dirty debugging
	        String fn = LogFilePathPfx + sdf.format(dt) + LogFilePathSfx;   // 2014-02-02T193504_version 
	
	        try {
				SaveStdOutput.start(fn);  // Start trap for println()s ??? Never used
	   	    } catch (IOException e) {
	   			e.printStackTrace();
	   	    }
        }  // end debugging stuff
	        
        // Set trap for exceptions
         lastUEH = Thread.getDefaultUncaughtExceptionHandler(); // save previous one
         Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());

    }  // end onCreate()
    
    //--------------------------------------------------------------------------
    //  Here we get the Intent used to start us
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { 
    	System.out.println("RSS onStartCmd intent="+intent + ", startId="+startId + ", flags="+flags);
    	
       	// intent == null if called by system if has been stopped
    	if(intent != null) 
    	{
    		resultReceiver = intent.getParcelableExtra("receiver");
            showMessage(">>> RunServerService started at " + sdf.format(new Date()) + " <<<");

    		Bundle bndl = intent.getExtras();
//    		if(debugging)
    		   System.out.println(" >>data="+intent.getData()
         			+ "\n >>extras=" + (bndl == null ? "null" : Arrays.toString(bndl.keySet().toArray())));
            savedFilesFolder = bndl.getString(SaveFolderID);
            root = bndl.getString(TheRootID);

    	}else{
    		Toast.makeText(getBaseContext(), "RunServerSrvc onStart called with null Intent",
    				        Toast.LENGTH_SHORT).show();	
    	}
    	
    	// Start the HTTP server
	    File wwwroot = new File(root).getAbsoluteFile();
	    boolean quiet = false;
	    server =  new SimpleWebServer(this, host, SimpleWebServerActivity.PORT, wwwroot, quiet);
	    server.setAddFilenamePrefix(addFilenamePrefix);
	    server.setSaveFilesFolder(savedFilesFolder +"/"); //<<<< ??? Added ending /
        try {
           server.start();
           showMsg("server started - host="+host +" port="+SimpleWebServerActivity.PORT + "\nwwwroot="+wwwroot
        		   + "\naddPrefix="+addFilenamePrefix +", savedFolde="+savedFilesFolder);
        } catch (IOException ioe) {
          System.err.println("RSS Couldn't start server:\n" + ioe);
        }
        
		/*********** Create notification ***********/
		mgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent4P = new Intent(getBaseContext(), SimpleWebServerActivity.class);
		// How to pass this to NotifyMessage -> Need flag on PI!!!!
		intent4P.putExtra("Notify at", sdfTime.format(new java.util.Date()));
		
		// This pending intent will open after notification click
		PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0, intent4P, 
				                                     PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification note = new Notification.Builder(getBaseContext())
							.setSmallIcon(R.drawable.ic_launcher_small)
							.setWhen(System.currentTimeMillis())
							.setTicker("Started Simple Server!")
							.setContentTitle("Norm's Simple Server is running")
							.setContentText("Touch to Open app for control @ "
									  + sdfTime.format(new java.util.Date()))
//							.setContentInfo("Norm's WakeUp")
							.setContentIntent(pi)
							.build();
		// See number of notification arrived
		note.number = 2;
//		mgr.notify(NOTIFY_ME_ID, note);
		startForeground(NOTIFY_ME_ID, note);

  	
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
 	    return Service.START_STICKY;
    } // end onStartCommand()
	
    //---------------------------------------------------------
    @Override
    public IBinder onBind(Intent intent) {
    	System.out.println("??? RSS onBind() called intent="+intent);
        return null;
    }
    
    //-------------------------------------------------------------------
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	System.out.println(">>>RunServerService in onDestroy() at " + new Date());
    	if(mgr != null) {
    		mgr.cancel(NOTIFY_ME_ID);
    	}
    	if(server != null)
    		server.stop();
    	
    	if(debugging) {
    		SaveStdOutput.stop();	
    	}
        Toast.makeText(getBaseContext(), "RunServerSrvc stopping in onDestroy", 
        		       Toast.LENGTH_LONG).show();
        
		System.out.println("<<<<< RSS  Service Stopped at " + new Date());
    }
    //=================================================================
	// ???? How can this method get to GUI on SimpleWebServer
    
    @Override
    public void showMessage(String message) {
//   	   System.out.println("RSS sM message="+message);
   	   savedMessages += message + "\n";   // save ????
   	   Bundle bundle = new Bundle();
   	   bundle.putString("text", message);
   	   resultReceiver.send(100, bundle);
/*    	
       final StringBuilder buf = new StringBuilder(message);

       handler.post(new Runnable() {
	       @Override
	       public void run() {
	           String text = hello.getText().toString();	
	           hello.setText(text + "\n" + buf);
	           System.out.println("buf="+buf);        //<<<<<<<
	       }
      });
*/      
    }

 	//------------------------------------------
 	//  Show a message in an Alert box ??? Can this work in Service???
 	private void showMsg(String msg) {
 		System.out.println("RSS showMsg msg="+msg);
/* 		
 		AlertDialog ad = new AlertDialog.Builder(getBaseContext()).create();
 		ad.setCancelable(false); // This blocks the 'BACK' button
 		ad.setMessage(msg);
 		ad.setButton(DialogInterface.BUTTON_POSITIVE, "Clear messsge", new DialogInterface.OnClickListener() {
 		    @Override
 		    public void onClick(DialogInterface dialog, int which) {
 		        dialog.dismiss();                    
 		    }
 		});
 		ad.show();
*/ 		
 	}


}
