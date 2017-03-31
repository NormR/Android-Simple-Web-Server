package com.normsstuff.simpleserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

import com.normstools.SaveStdOutput;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleWebServerActivity extends Activity implements MessageDisplayer {
	final String Version = "Version date: March 31, 2017 @ 1450\n";

	//----------------------------------------------------------------------------
    final String rootDir = Environment.getExternalStorageDirectory().getPath();
    final String LogFilePath = rootDir + "/SimpleWebServerLog_"; //.txt";
    
    static final int PORT = 8080;
    private String host = null; //"127.0.0.1";
    private String root = rootDir + "/SimpleWebServer/"; 
    private String savedFilesFolder = root + "SavedFiles";  // where upload saves files
    
    //  Constant String for Bundles etc
    private final String SavedFolderName = "savedFolderName.txt";  // name of where we save the above
    private final String RunningServer_K = "runningServer";

    SimpleWebServer server;
    boolean runningServer = false;  // remember if we are running the server
    boolean debug = true;           // control debug output
    boolean addFilenamePrefix = true;  // tell server to add temp prefix to saved files
    boolean useService = false;   // controls if we run the Server in a Service
    boolean serviceIsRunning = false;

    
    //- - - - - - - - - - - - - - - - - - - - - - -
    // Define inner class to handle exceptions
    class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e){
           java.util.Date dt =  new java.util.Date();
           String fn = ExcpFilePathPfx + "exception_" + sdf.format(dt) + ".txt";
           try{ 
              PrintStream ps = new PrintStream( fn );
              e.printStackTrace(ps);
              ps.close();
              System.out.println("SWS wrote trace to " + fn);
              e.printStackTrace(); // capture here also???
              SaveStdOutput.stop(); // close here vs calling flush() in class 
           }catch(Exception x){
              x.printStackTrace();
           }
           lastUEH.uncaughtException(t, e); // call last one  Gives: "Unfortunately ... stopped" message
           return;    //???? what to do here
        }
    }

    //---------------------------------------------------------------------------------------
	// Logging stuff
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss", Locale.US);
    SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss", Locale.US); // showTime
    Thread.UncaughtExceptionHandler lastUEH = null;
	final String ExcpFilePathPfx = root + "logs/SimpleWebServerlog_";

	private TextView hello;
	private Handler handler = new Handler();

	// Request codes for startActivity
	final int FolderChosenForSave = 112;
    final int RESULT_SETTINGS = 31;
	final int DELAYED_MESSAGE = 321;

    
    //---------------------------------------------------------
    //  Utility classes to allow Service to send message
	class UpdateUI implements Runnable
	{
		String updateString;
		
		public UpdateUI(String updateString) {
			this.updateString = updateString;
		}
		public void run() {
			//  Append new to old
	        String text = hello.getText().toString();	
			hello.setText(text + "\n" + updateString);
		}
	}

	class MyResultReceiver extends ResultReceiver
	{
		public MyResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
//			System.out.println("oRR resCode="+resultCode +", bndl="+resultData); //<<<<<<<<
			if(resultCode == 100){
				runOnUiThread(new UpdateUI(resultData.getString("text")));
			}
			else if(resultCode == 200){
				runOnUiThread(new UpdateUI(resultData.getString("end")));
			}
			else{
				runOnUiThread(new UpdateUI("Result Received "+resultCode));
			}
		}
	}  // end class
	
	MyResultReceiver resultReceiver;



    //------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_web_server);
		
	    hello = (TextView) findViewById(R.id.hello);
		
		getPreferences();
		
        if(debug) {
	        try {
	            java.util.Date dt =  new java.util.Date();
				SaveStdOutput.start(LogFilePath + sdf.format(dt) + ".txt");
				System.out.println("\n>>>>>>>>SWS Started logging at " + dt
						          + " " +Version);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        // Catch exceptions and write to a separate file
        lastUEH = Thread.getDefaultUncaughtExceptionHandler(); // save previous one
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
        // end trying to catch exceptions 

		//------------------------------------------
		// Make sure have our folder
		File testSWSFolder = new File(root);
		if(!testSWSFolder.exists()){
			boolean res = testSWSFolder.mkdir();
			System.out.println("SWS created folder="+testSWSFolder + " " + res);
		}
		
		// Get the previous Saved files folder
		try{
			FileInputStream fis = openFileInput(SavedFolderName);
			byte[] bfr = new byte[600]; // should never be this long
			int nbrRd = fis.read(bfr);
			String theText = new String(bfr, 0, nbrRd);
			System.out.println("SWS read savedFolder=" + theText);
			fis.close();
			savedFilesFolder = theText;  // set the folder from the previous session
		}catch(Exception x){
			x.printStackTrace();
		}


		// Test if restarted
		if(savedInstanceState != null){
			if(debug) System.out.println(">>>> SWS onCreate restarted<<<");
			runningServer = savedInstanceState.getBoolean(RunningServer_K);
			if(runningServer) {
		    	//  Need to restore Button states
				Button btn = (Button)findViewById(R.id.stopBtn);
				btn.setEnabled(true);  //<<<<<< Q&D for now
			}
		}
		
        System.setProperty("java.io.tmpdir", savedFilesFolder);  //<<<<<<<<< where to write uploads
        
        // Some more data about how we were started
       	Intent intent = getIntent();
    	System.out.println("SWS onCreate() intent="+intent 
			    			+ "\n >>data="+intent.getData()
			    			+ "\n >>savedInstanceState=" + savedInstanceState
			    			+ "\n >>extras="+ intent.getExtras());

        // Were we started by an Intent?
		Bundle bndl = intent.getExtras();
		if(bndl != null){
    		Set<String> set = bndl.keySet();
    		System.out.println(" >> SWS bndl keySet=" + Arrays.toString(set.toArray()));
        }	

		
	}  // end onCreate()
	
	//===============================
	  @Override
	  protected void onResume() {
	    super.onResume();
		System.out.println("SWS onResume() RunServerService running=" 
	                       + isMyServiceRunning(RunServerService.class)); 
	    
	    getPreferences();

	    TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
	    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
	    if(ipAddress==0) {
	    	showMsg("IP address not found - Need to connect to internet");
	    	try{Thread.sleep(2000);}catch(Exception x){} // wait for user to see
	    	// Exit the program after a short delay
	        Handler handler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	                if(msg.what == DELAYED_MESSAGE) {
	                    SimpleWebServerActivity.this.finish();
	                }
	                super.handleMessage(msg);
	            }
	        };
	        Message message = handler.obtainMessage(DELAYED_MESSAGE);
	    	return;  //????
	    }
	    final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
	                                                  (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
	    textIpaddr.setText("http://" + formatedIpAddress + ":" + PORT
	    		            + "    " + getLocalIpAddress()); //<<<<<<< two lines ???
	    
		serviceIsRunning = isMyServiceRunning(RunServerService.class);
		if(serviceIsRunning) {
			showMessage("SWS >>> RunServerService is running at "+ new Date());
			Button btn = (Button)findViewById(R.id.stopBtn);
			btn.setEnabled(true);  //??Will this allow us to stop it?
			btn = (Button)findViewById(R.id.startBtn);
			btn.setEnabled(false); 
			//??? Has RunServerService's connection to showMessage() been broken?
		}
	    
        System.setProperty("java.io.tmpdir", savedFilesFolder);  //<<<<<<<<<set where to write uploads

	  } // end onResume()
	
	
    //-----------------------------------------
    private void getPreferences() {
 		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String debugKey = getResources().getString(R.string.set_debug_text_key);
		debug = preferences.getBoolean(debugKey, true);
		String addPrefixKey = getResources().getString(R.string.add_prefix_key);
		addFilenamePrefix = preferences.getBoolean(addPrefixKey, true);
        System.out.println("SWS getPref debug="+debug + ", addPrefix="+addFilenamePrefix);
        String useServiceKey = getResources().getString(R.string.use_service_key);
        useService = preferences.getBoolean(useServiceKey, false);
    }
    
    //--------------------------------------
    public String getLocalIpAddress() {
        try {
           for (Enumeration<?> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
              NetworkInterface intf = (NetworkInterface) en.nextElement();
              String val = "";
              for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                 InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                 System.out.println("SWS hostAddress="+inetAddress.getHostAddress().toString()); //<<<<<
                 if (!inetAddress.isLoopbackAddress()) {
                    val = inetAddress.getHostAddress().toString(); //  <<< Last vs first ???
                 }
              }
              return val;
           }
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return null;
     }
    
	//-----------------------------------------------------------------
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

    //========================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simple_web_server, menu);
		return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
        switch (item.getItemId()) {

        	case R.id.set_saved_folder:
	    		// Set the folder to save to
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	        	alert.setTitle("Set folder to save to");
	        	alert.setMessage("Enter folder name to upload to."
	        			+"\nFiles currently written to "+ savedFilesFolder);

	        	// Set an EditText view to get user input 
	        	final EditText input = new EditText(this);
	        	input.setText(savedFilesFolder);  // show current value
	        	alert.setView(input);

	        	alert.setPositiveButton("Set filename", new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int whichButton) {
		        	  String newFN = input.getText().toString();
		        	  if(newFN == null || newFN.length() == 0)
		        		  return;		// exit if user quit
		        	  	
		        	  String wpFileMsg = "New Folder will be created";
		        	  File testFile = new File(savedFilesFolder);
		        	  if(testFile.exists()){
		        		  wpFileMsg = "Folder exists";
		        	  }
		    		  Toast.makeText(SimpleWebServerActivity.this, "Files to be saved to: "
		        	                       + savedFilesFolder +"\n" + wpFileMsg,
	    		    		         Toast.LENGTH_LONG).show();
		    		  // Save name for next time???
		    		  try{
	    				FileOutputStream fos = openFileOutput(SavedFolderName, Context.MODE_PRIVATE);
	    				fos.write(savedFilesFolder.getBytes());
	    				fos.close();
		    		  }catch(Exception x){
		    			  x.printStackTrace();
		    		  }
		        	}
	        	});
	        	alert.setNeutralButton("Choose file", new DialogInterface.OnClickListener() {
	          	  	public void onClick(DialogInterface dialog, int whichButton) {
	          	  		// Choose the file to write to
	    	            Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
	    	            intent2.setType("file/*");
//	    	    		intent2.setDataAndTypeAndNormalize(Uri.parse("file://"), "*/*"); //???? too many

	    	            //  How to get String ids below???
	    	    		intent2.putExtra("START_PATH", new File(savedFilesFolder));
	    	    		intent2.putExtra(Intent.EXTRA_TITLE, "Choose folder to save to");  //????? Where does this go
//	    	    		System.out.println("selectBtn clicked intent="+intent2);
	    	    		System.out.println("SWS selectBtnClicked to choose folder to copy to, intent="+intent2);
	    	    		startActivityForResult(intent2, FolderChosenForSave);
	    	    		// Should the copy be done immediately after the file is chosen?
	          	  	}
	        	});


	        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        	    // Canceled.
	        	  }
	        	});

	        	alert.show();

        		return true;
        	
			case R.id.action_settings:
    			// Starts the Settings activity on top of the current activity
    			Intent intent2 = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent2, RESULT_SETTINGS);
            	return true;
				
		    case R.id.About_ID:
		        showMsg("Norm's Simple Web Server program\n"
		        		+ Version
		        		+ "email: radder@hotmail.com");
		        return true;
		
		    	
		    case R.id.Exit_ID:
		    	finish();
		    	return true;
		    	
            default:
                System.err.println("SWS unkn menuitem="+item.getItemId());
                break;
		    	
        }

		return super.onOptionsItemSelected(item);
	}
	
	//----------------------------------------------------------
	//  Handle what selected activity found
	@Override
	public void onActivityResult(int reqCode, int resCode, Intent data) {
		System.out.println("SWS onActRes reqCode="+reqCode 
				            + ", resCode="+resCode+", intent="+data);
		
		// Process results from started activities
		if(reqCode == RESULT_SETTINGS) {
	        getPreferences();  // retrieve what user did
	        return;
		}
		if(reqCode == FolderChosenForSave) {
			if(data == null){
				System.out.println("SWS oAR data=null");
				return;
			}
			String filePath = data.getStringExtra("FileName");
            String fldr = data.getData().getPath();         // Here is the standard place for response
            System.out.println("SWS onActRes filePath="+filePath+"< fldr="+fldr+"<");
            //>>>>>>>>> NEEDS WORK HERE
            return;
		}
	}

	
   	//--------------------------------------------------------------
	//  Save values to allow us to restore state when rotated
	@Override
	public void onSaveInstanceState(Bundle bndl) {
		super.onSaveInstanceState(bndl);
    	System.out.println("***** SWS onSaveInstanceState()");
    	
		bndl.putBoolean(RunningServer_K, runningServer);
   	
  	}
	
	//===============================================================
	public void startBtnClicked(View v) {
		Button btn = (Button)findViewById(R.id.stopBtn);
		btn.setEnabled(true);
		btn = (Button)findViewById(R.id.startBtn);
		btn.setEnabled(false);
//		showMsg("Should start server now");
		
		if(useService) {
			runAsService();
			
		}else {
		    File wwwroot = new File(root).getAbsoluteFile();
		    boolean quiet = false;
		    server =  new SimpleWebServer(this, host, PORT, wwwroot, quiet);
		    server.setAddFilenamePrefix(addFilenamePrefix);
		    server.setSaveFilesFolder(savedFilesFolder +"/"); //<<<< ??? Added ending /
	        try {
	           server.start();
	           runningServer = true;
	           showMsg("server started - host="+host +" port="+PORT + "\nwwwroot="+wwwroot
	        		   + "\naddPrefix="+addFilenamePrefix +", savedFolde="+savedFilesFolder);
	        } catch (IOException ioe) {
	          System.err.println(">*>*> SWS Couldn't start server:\n" + ioe);
	        }
		}
	}
	
	//--------------------------------------------------------
	//  Run SimpleWebServer in a Service
	private void runAsService(){
        final Intent intent = new Intent(getBaseContext(), RunServerService.class);
        intent.putExtra(RunServerService.TheRootID, root);
        intent.putExtra(RunServerService.SaveFolderID, savedFilesFolder);
		resultReceiver = new MyResultReceiver(handler); //????null);
		intent.putExtra("receiver", resultReceiver);
        startService(intent);
	}
   
	//----------------------------------------------
	public void stopBtnClicked(View v) {
		Button btn = (Button)findViewById(R.id.stopBtn);
		btn.setEnabled(false);
		btn = (Button)findViewById(R.id.startBtn);
		btn.setEnabled(true);
		showMsg("Stopping server");
		stopServer();
		showMessage("SWS >>> Server stopped "+isMyServiceRunning(RunServerService.class));
	}
	
	private void stopServer() {
		if(useService) {
			stopService(new Intent(this, RunServerService.class));
		}else{
			if(server != null)
				server.stop();
			server = null;
		}
		runningServer = false;
		
	}  // end stopServer()
	
	//--------------------------------------------------------------
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("SWS onPause() at "+ sdfTime.format(new java.util.Date()));
	}
	

	
    @Override
     public void onDestroy() {
     	super.onDestroy();
 		System.out.println("<<<<<<<<< SWS onDestroy stopping at " + new Date());
 		if(isFinishing())
 			stopServer();
 		if(debug)
 			SaveStdOutput.stop();
     }
 	 
    //====================================================
    @Override
    public void showMessage(String message) {
      final StringBuilder buf = new StringBuilder(message);

       handler.post(new Runnable() {
	       @Override
	       public void run() {
	           String text = hello.getText().toString();	
	           hello.setText(text + "\n" + buf);
	           System.out.println("SWS buf="+buf);        //<<<<<<<
	       }
      });
    }

 	//------------------------------------------
 	//  Show a message in an Alert box
 	private void showMsg(String msg) {

 		AlertDialog ad = new AlertDialog.Builder(this).create();
 		ad.setCancelable(false); // This blocks the 'BACK' button
 		ad.setMessage(msg);
 		ad.setButton(DialogInterface.BUTTON_POSITIVE, "Clear messsge", new DialogInterface.OnClickListener() {
 		    @Override
 		    public void onClick(DialogInterface dialog, int which) {
 		        dialog.dismiss();                    
 		    }
 		});
 		ad.show();
 	}


}
