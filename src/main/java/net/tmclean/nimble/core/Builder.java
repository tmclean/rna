package net.tmclean.nimble.core;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

public abstract class Builder<T,V> 
{
	private ClassPool pool = null;
	private int modifier = 0;
	
	public Builder( ClassPool pool )
	{
		this.pool = pool;
	}
	
	@SuppressWarnings("unchecked")
	public T setPublic()
	{
		this.modifier = Modifier.PUBLIC;
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public T setPrivate()
	{	
		this.modifier = Modifier.PRIVATE;
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public T setProtected()
	{
		this.modifier = Modifier.PROTECTED;
		return (T)this;
	}
	
	protected int getModifier() { return this.modifier; }
	
	public abstract V apply() throws CannotCompileException, NotFoundException;
	
	public ClassPool getPool(){ return pool; }
	public void setPool( ClassPool pool ){ this.pool = pool; }
}
