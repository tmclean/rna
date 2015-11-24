package net.tmclean.rna.core;

import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

public abstract class Builder<T,V> 
{
	protected Logger logger = null;
	
	private ClassPool pool     = null;
	private int       modifier = 0;
	
	public Builder( ClassPool pool )
	{
		logger = LoggerFactory.getLogger( this.getClass() );
		
		logger.debug( "Creating new Builder" );
		
		this.pool = pool;
	}
	
	@SuppressWarnings("unchecked")
	public T setPublic()
	{
		logger.debug( "Setting visibility to public" );
		
		this.modifier = Modifier.PUBLIC;
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public T setPrivate()
	{	
		logger.debug( "Setting visibility to private" );
		
		this.modifier = Modifier.PRIVATE;
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public T setProtected()
	{
		logger.debug( "Setting visibility to protected" );
		
		this.modifier = Modifier.PROTECTED;
		return (T)this;
	}
	
	protected int getModifier() { return this.modifier; }
	
	public abstract V apply() throws CannotCompileException, NotFoundException;
	
	public ClassPool getPool(){ return pool; }
	public void setPool( ClassPool pool ){ this.pool = pool; }
}
