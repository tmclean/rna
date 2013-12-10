package net.tmclean.nimble.test;

public class Main 
{
	private static final String DEPLOY_URL = "http://localhost:9000";
	
	public static void main( String[] args ) throws Throwable
	{
		SyntheticServer server = new SyntheticServer( DEPLOY_URL );
		server.start();
	}
}