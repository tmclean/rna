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
	}

	public ClassBuilder named( String name ) throws NotFoundException, CannotCompileException
	{
		this.name = name;
		return this;
	}
	
	@Override
	public Class<?> apply() throws CannotCompileException, NotFoundException 
	{
		Class<?> c = super.apply();
		
		Nimble.registerModel( name, c );
		
		return c;
	}
}
