package net.tmclean.nimble.jaxrs;

import javax.ws.rs.GET;

public class GetRESTMethodBuilder extends RESTMethodBuilder
{
	public GetRESTMethodBuilder( RESTBuilder builder, String path ) 
	{
		super( builder, GET.class, path );
	}
}
