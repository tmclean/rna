package net.tmclean.rna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tmclean.rna.jaxrs.ModelBuilder;
import net.tmclean.rna.jaxrs.RESTBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

public final class RNA 
{
	private static final Logger logger = LoggerFactory.getLogger( RNA.class );
	
	private static final Map<String, Class<?>> modelRegistry = new HashMap<String, Class<?>>( 0 );
	private static final Map<Class<?>, String> revModelRegistry = new HashMap<Class<?>, String>( 0 );
	private static final Map<String, Class<?>> resourceRegistry = new HashMap<String, Class<?>>( 0 );

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
	
	public static final void registerResource( String resourceName, Class<?> clazz )
	{
		logger.debug( "Registering new resource with name {} and class {}", resourceName, clazz.getName() );
		
		resourceRegistry.put( resourceName, clazz );
	}
	
	public static final List<String> getResourceNames()
	{
		logger.debug( "Retrieving resource names" );

		List<String> names = new ArrayList<String>( resourceRegistry.size() );
		names.addAll( resourceRegistry.keySet() );
		return names;
	}
	
	public static final Class<?> getResource( String resourceName )
	{
		logger.debug( "Getting resource class mapped to resource name '{}'", resourceName );
		
		return resourceRegistry.get( resourceName );
	}
	
	public static final String getResourceClassName( String resourceName )
	{
		logger.debug( "Getting resource class name mapped to resource {}", resourceName );
		
		return resourceRegistry.get( resourceName ).getName();
	}
	
	public static final void registerModel( String modelName, Class<?> clazz )
	{
		logger.debug( "Registering new model with model name '{}' and class {}", modelName, clazz.getName() );
		
		modelRegistry.put( modelName, clazz );
		revModelRegistry.put( clazz, modelName );
	}
	
	public static final ModelBuilder newModel() throws NotFoundException, CannotCompileException
	{
		logger.debug( "Creating new model" );
		
		return new ModelBuilder();
	}
	
	public static final List<String> getModelNames()
	{
		logger.debug( "Retrieving model names" );
		
		List<String> names = new ArrayList<String>( modelRegistry.size() );
		names.addAll( modelRegistry.keySet() );
		return names;
	}
	
	public static final Class<?> getModel( String modelName )
	{
		logger.debug( "Getting model class mapped to model name '{}'", modelName );
		
		return modelRegistry.get( modelName );
	}
	
	public static final String getModelClassName( String modelName )
	{
		logger.debug( "Getting model class name mapped to model {}", modelName );
		
		return modelRegistry.get( modelName ).getName();
	}
	
	public static final String getModelNameForClass( Class<?> modelClass )
	{
		logger.debug( "Getting model name mapped to class {}", modelClass.getName() );
		
		return revModelRegistry.get( modelClass );
	}
}
