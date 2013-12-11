package net.tmclean.nimble.test;

import org.apache.log4j.PropertyConfigurator;

public class Main 
{
	private static final String DEPLOY_URL = "http://localhost:9000";
	
	private static SyntheticServer server = null;
	
	public static void main( String[] args ) throws Throwable
	{
		PropertyConfigurator.configure( Main.class.getResourceAsStream( "log4j.properties" ) );
		
		server = new SyntheticServer( DEPLOY_URL );
		server.start();
	}
}