package wrm.server;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import wrm.server.View.Public;
import wrm.server.controller.Apps;
import wrm.server.controller.Commands;

import IceBreakRestServer.IceBreakRestServer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApplicationServer {
	
	
	
	
	private boolean keepRunning;
	public void stopRunning(){
		keepRunning = false;
	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public void start(int port) throws IOException {
	
		keepRunning = true;
		
		IceBreakRestServer rest = new IceBreakRestServer();
		//rest.debug = true;
		rest.setPort(port);
		
		Map<String, Object> requestMapping = new HashMap<String, Object>();
		
		
		requestMapping.put("/apps", new Apps());
		requestMapping.put("/commands", new Commands());
		
		
		
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		while(keepRunning){
			
			rest.getHttpRequest();
			
			Logger.info( rest.request);
			
			Object restResource = requestMapping.get(rest.resource);
			rest.setContentType("application/json");
			if (restResource == null)
				Logger.warn("Resource not found: {0}", rest.resource );
			else
			{
				switch(rest.method){
				case "GET": 
					doGet(rest, jsonMapper, restResource);
					break;
				case "POST": 
					doPost(rest, jsonMapper, restResource);
					break;
				
				}
			}
			
			//empty sign otherwise flush wont work!
			rest.write(" ");
			rest.flush();
			
		}
		
		
		
		
		
		 
		
	}

	private static void doPost(IceBreakRestServer rest,
			ObjectMapper jsonMapper, Object restResource) {
		
		
		Class<?> postType = findMethodType("post", restResource.getClass());
		
		try {
			
			Object value = jsonMapper.readValue(rest.payload, postType);
			
			Object result = restResource.getClass().getMethod("post", postType).invoke(restResource, value);
			
			
//			Object result = restResource.post(value);
			
			if (result != null)
				writeObject(result, jsonMapper, rest);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		
	
		
	}
	
	

	

	private static Class<?> findMethodType(String name, Class<?> class1) {
		
		for(Method m : class1.getMethods())
			if (m.getName().equals(name))
				return m.getParameterTypes()[0];
		return null;
		
	}

	private static void doGet(IceBreakRestServer rest, ObjectMapper jsonMapper,
			Object restResource) {

//		Class<?> getType = findMethodType("get", restResource.getClass());
		
		try {
			
//			Object value = jsonMapper.readValue(rest.payload, postType);
			
			Object result = restResource.getClass().getMethod("get", Map.class).invoke(restResource, rest.parms);
			
			
//			Object result = restResource.get(value);
			
			if (result != null)
				writeObject(result, jsonMapper, rest);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	

	private static void writeObject(Object result, ObjectMapper jsonMapper,
			IceBreakRestServer rest) {
		
		StringWriter sw = new StringWriter();
		try {
			jsonMapper.writer(new DefaultPrettyPrinter())
					.withView(View.Public.class)
					.writeValue(sw, result);
		} catch (IOException e) {
			e.printStackTrace();
		}

		rest.write(sw.toString());
		
	}

}
