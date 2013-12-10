package net.tmclean.nimble.core;

import java.util.UUID;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

public class ClassBuilder extends Builder<ClassBuilder, Class<?>>
{
	private CtClass cClass = null;
	
	public ClassBuilder( ClassPool pool ) throws NotFoundException, CannotCompileException
	{
		super( pool );
		
		cClass = pool.makeClass( this.getClass().getPackage().getName() + ".syntetic.SYN_" + UUID.randomUUID().toString().replace( "-", "" ) );
		withConstructor( null );
	}
	
	public ClassBuilder( ClassPool pool, String packageName )
	{
		super( pool );

		cClass = pool.makeClass( packageName + "." + UUID.randomUUID().toString().replace( "-", "" ) );
	}
	
	public ClassBuilder( ClassPool pool, String packageName, String className )
	{
		super( pool );

		cClass = pool.makeClass( packageName + "." + className );
	}
	
	public ClassAnnotationBuilder annotatedBy( Class<?> annotationClass )
	{	
		return new ClassAnnotationBuilder( this, annotationClass );
	}
	
	public ClassBuilder withConstructor( String src, Class<?> ... args ) throws NotFoundException, CannotCompileException
	{
		CtClass[] parameters = new CtClass[ args.length ];
		
		for( int i = 0; i < args.length; i++ )
			parameters[i] = getPool().get( args[i].getName() );
		
		CtConstructor cons = new CtConstructor( parameters, cClass );
		
		if( src != null && !src.trim().isEmpty() )
			cons.setBody( src );
		else
			cons.setBody( "{}" );
		
		cClass.addConstructor( cons );
		
		return this;
	}
	
	public MethodBuilder withMethod()
	{
		return new MethodBuilder( this );
	}
	
	public FieldBuilder withField()
	{
		return new FieldBuilder( this );
	}
	
	public ClassBuilder withBeanField( String fieldName, Class<?> fieldType, String valueSrc ) throws CannotCompileException, NotFoundException
	{
		MethodBuilder getterBuilder = new MethodBuilder( this );
		MethodBuilder setterBuilder = new MethodBuilder( this );
		
		FieldBuilder fieldBuilder = new FieldBuilder( this );
		
		fieldBuilder.setPrivate().named( fieldName ).ofType( fieldType ).withValue( valueSrc ).apply();
		
		setterBuilder.setPublic()
					 .named( genBeanFieldMethod( "set", fieldName ) )
					 .takes( "value", fieldType )
					 .withSource( "{ this." + fieldName + "=$1; }" )
					 .build()
					 .apply();
		
		getterBuilder.setPublic()
				     .named( genBeanFieldMethod( "get", fieldName ) )
				     .withSource( "{ return this." + fieldName + "; }" )
				     .returns( fieldType )
				     .build()
				     .apply();
		
		return this;
	}
	
	@Override
	public Class<?> apply() throws CannotCompileException, NotFoundException
	{
		return cClass.toClass();
	}
	
	private String genBeanFieldMethod( String prefix, String fieldName )
	{
		return prefix + ( String.valueOf( fieldName.charAt( 0 ) ).toUpperCase() ) + fieldName.substring( 1 );
	}
	
	public CtClass getCTClass() { return this.cClass; }
}
