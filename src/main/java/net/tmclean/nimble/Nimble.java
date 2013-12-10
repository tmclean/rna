package net.tmclean.nimble;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.tmclean.nimble.jaxrs.ModelBuilder;
import net.tmclean.nimble.jaxrs.RESTBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

public final class Nimble 
{
	private static final Map<String, Class<?>> modelReigstry = new HashMap<String, Class<?>>( 0 );

	public static final RESTBuilder newEndpoint() throws NotFoundException, CannotCompileException
	{
		return new RESTBuilder( ClassPool.getDefault() );
	}
	
	public static final RESTBuilder newEndpoint( ClassPool pool ) throws NotFoundException, CannotCompileException
	{
		return new RESTBuilder( pool );
	}
	
	public static final void registerModel( String name, Class<?> clazz )
	{
		modelReigstry.put( name, clazz );
	}
	
	public static final ModelBuilder newModel() throws NotFoundException, CannotCompileException
	{
		return new ModelBuilder();
	}
	
	public static final Collection<String> getModelNames()
	{
		return modelReigstry.keySet();
	}
	
	public static final Class<?> getModel( String modelName )
	{
		return modelReigstry.get( modelName );
	}
	
	public static final String getModelClassName( String modelName )
	{
		return modelReigstry.get( modelName ).getName();
	}
}
