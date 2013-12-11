package net.tmclean.nimble.jaxrs;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import net.tmclean.nimble.Nimble;
import net.tmclean.nimble.core.ClassBuilder;

public class ModelBuilder extends ClassBuilder 
{
	private String name = null;
	
	public ModelBuilder() throws NotFoundException, CannotCompileException 
	{
		super( ClassPool.getDefault() );
		
		logger.debug( "Creating a new ModelBuilder" );
	}

	public ClassBuilder named( String name ) throws NotFoundException, CannotCompileException
	{
		logger.debug( "Setting model reference name to {}", name );
		
		this.name = name;
		return this;
	}
	
	@Override
	public Class<?> apply() throws CannotCompileException, NotFoundException 
	{
		logger.debug( "Building model {}", name );
		
		Class<?> c = super.apply();
		
		logger.debug( "Registering model {}", name );
		Nimble.registerModel( name, c );
		
		return c;
	}
}
