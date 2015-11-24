package net.tmclean.rna.core;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class FieldBuilder extends Builder<FieldBuilder, ClassBuilder>
{
	private String       name         = null;
	private CtClass      type         = null;
	private String       valueSrc     = null;
	private ClassBuilder classBuilder = null;
	private CtClass      cClass       = null;
	
	public FieldBuilder( ClassBuilder classBuilder )
	{
		super( classBuilder.getPool() );
		
		logger.debug( "Creating new FieldBuilder" );
		
		this.classBuilder = classBuilder;
		this.cClass = this.classBuilder.getCTClass();
	}
	
	public FieldBuilder named( String name )
	{
		logger.debug( "Setting field name to {}", name );
		
		this.name = name;
		return this;
	}
	
	public FieldBuilder ofType( Class<?> type ) throws NotFoundException
	{
		logger.debug( "Setting field type to {}", type.getName() );
		
		this.type = getPool().get( type.getName() );
		return this;
	}
	
	public FieldBuilder withValue( String valueSrc )
	{
		this.valueSrc = valueSrc == null ? null : valueSrc.isEmpty() ? null : valueSrc;

		logger.debug( "Setting field initialization source to {}", valueSrc );
		
		return this;
	}
	
	@Override
	public ClassBuilder apply() throws CannotCompileException, NotFoundException
	{	
		logger.debug( "Applying field to class" );
		
		CtField field = new CtField( type, name, cClass );
		
		logger.debug( "Setting field modifiers" );
		
		field.setModifiers( getModifier() );
		
		if( valueSrc == null )
		{
			logger.debug( "Adding uninitialized field to class" );
			cClass.addField( field );
		}
		else
		{
			logger.debug( "Addinig field to class with initialization source {}", valueSrc );
			cClass.addField( field, valueSrc );
		}
		
		return this.classBuilder;
	}
	
	public CtClass getcClass() { return cClass; }
	public void setcClass(CtClass cClass) { this.cClass = cClass; }
}
