package net.tmclean.nimble;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tmclean.nimble.jaxrs.ModelBuilder;
import net.tmclean.nimble.jaxrs.RESTBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

public final class Nimble 
{
	private static final Logger logger = LoggerFactory.getLogger( Nimble.class );
	
	private static final Map<String, Class<?>> modelReigstry = new HashMap<String, Class<?>>( 0 );

	public static final RESTBuilder newEndpoint() throws NotFoundException, CannotCompileException
	{
		logger.debug( "Creating new endpoint with default ClassPool" );
		
		return new RESTBuilder( ClassPool.getDefault() );
	}
	
	public static final RESTBuilder newEndpoint( ClassPool pool ) throws NotFoundException, CannotCompileException
	{
		logger.debug( "Creating new endpoint with provided ClassPool" );
		
		return new RESTBuilder( pool );
	}
	
	public static final void registerModel( String modelName, Class<?> clazz )
	{
		logger.debug( "Registering new model with model name '{}' and class {}", modelName, clazz.getName() );
		
		modelReigstry.put( modelName, clazz );
	}
	
	public static final ModelBuilder newModel() throws NotFoundException, CannotCompileException
	{
		logger.debug( "Creating new model" );
		
		return new ModelBuilder();
	}
	
	public static final Collection<String> getModelNames()
	{
		logger.debug( "Retrieving model names" );
		
		return modelReigstry.keySet();
	}
	
	public static final Class<?> getModel( String modelName )
	{
		logger.debug( "Getting model class mapped to model name '{}'", modelName );
		
		return modelReigstry.get( modelName );
	}
	
	public static final String getModelClassName( String modelName )
	{
		logger.debug( "Getting model class name mapped to model 'name'", modelName );
		
		return modelReigstry.get( modelName ).getName();
	}
}
