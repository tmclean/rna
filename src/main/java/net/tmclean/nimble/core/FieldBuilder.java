package net.tmclean.nimble.core;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class FieldBuilder extends Builder<FieldBuilder, ClassBuilder>
{
	private String  name     = null;
	private CtClass type     = null;
	private String  valueSrc = null;
	
	private ClassBuilder classBuilder = null;
	private CtClass cClass = null;
	
	public FieldBuilder( ClassBuilder classBuilder )
	{
		super( classBuilder.getPool() );
		
		this.classBuilder = classBuilder;
		this.cClass = this.classBuilder.getCTClass();
	}
	
	public FieldBuilder named( String name )
	{
		this.name = name;
		return this;
	}
	
	public FieldBuilder ofType( Class<?> type ) throws NotFoundException
	{
		this.type = getPool().get( type.getName() );
		return this;
	}
	
	public FieldBuilder withValue( String valueSrc )
	{
		this.valueSrc = valueSrc;
		return this;
	}
	
	@Override
	public ClassBuilder apply() throws CannotCompileException, NotFoundException
	{	
		CtField field = new CtField( type, name, cClass );
		field.setModifiers( getModifier() );
		
		if( valueSrc == null )
		{
			cClass.addField( field );
		}
		else
		{
			cClass.addField( field, valueSrc );
		}
		
		return this.classBuilder;
	}
	
	public CtClass getcClass() { return cClass; }
	public void setcClass(CtClass cClass) { this.cClass = cClass; }
}
