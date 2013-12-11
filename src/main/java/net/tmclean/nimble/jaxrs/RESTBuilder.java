package net.tmclean.nimble.jaxrs;

import javax.ws.rs.Path;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import net.tmclean.nimble.core.ClassAnnotationBuilder;
import net.tmclean.nimble.core.ClassBuilder;

public class RESTBuilder extends ClassBuilder
{
	public RESTBuilder( ClassPool pool ) throws NotFoundException, CannotCompileException
	{
		super( pool );
		
		logger.debug( "Building new REST resource" );
		
		setPublic();
	}
	
	public RESTBuilder at( String path ) throws CannotCompileException, NotFoundException
	{
		logger.debug( "Setting resource path to {}", path );
		
		ClassAnnotationBuilder cAnnBuilder = this.annotatedBy( Path.class );
		cAnnBuilder.withStringParam( "value", path );
		cAnnBuilder.apply();
		
		return this;
	}
	
	public GetRESTMethodBuilder withGet( String path )
	{
		logger.debug( "Adding new HTTP GET method at path {}", path );
		
		return new GetRESTMethodBuilder( this, path );
	}
}
