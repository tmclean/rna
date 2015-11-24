package net.tmclean.rna.driver;

import org.apache.log4j.PropertyConfigurator;

public class Driver 
{
	private static final String DEPLOY_URL = "http://localhost:9000";
	
	private static SyntheticServer server = null;
	
	public static void main( String[] args ) throws Throwable
	{
		PropertyConfigurator.configure( Driver.class.getResourceAsStream( "log4j.properties" ) );
		
		server = new SyntheticServer( DEPLOY_URL );
		server.start();
	}
} 
