package net.tmclean.nimble.jaxrs;

import javax.ws.rs.GET;

public class GetRESTMethodBuilder extends RESTMethodBuilder
{
	public GetRESTMethodBuilder( RESTBuilder builder, String path ) 
	{
		super( builder, GET.class, path );
		
		logger.debug( "Creating a new HTTP GET method for class" );
	}
}
