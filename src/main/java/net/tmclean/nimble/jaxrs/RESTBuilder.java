package net.tmclean.nimble.jaxrs;

import javax.ws.rs.Path;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import net.tmclean.nimble.Nimble;
import net.tmclean.nimble.core.ClassAnnotationBuilder;
import net.tmclean.nimble.core.ClassBuilder;

public class RESTBuilder extends ClassBuilder
{
	private String resourceName = null;
	
	public RESTBuilder( ClassPool pool ) throws NotFoundException, CannotCompileException
	{
		super( pool );
		
		logger.debug( "Building new REST resource" );
		
		setPublic();
	}
	
	public RESTBuilder named( String resourceName )
	{
		this.resourceName = resourceName;
		
		return this;
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
	
	@Override
	public Class<?> apply() throws CannotCompileException, NotFoundException 
	{
		Class<?> clazz = super.apply();
		
		Nimble.registerResource( resourceName, clazz );
		
		return clazz;
	}
}
