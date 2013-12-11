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
		
		String className = this.getClass().getPackage().getName() + ".syntetic.SYN_" + UUID.randomUUID().toString().replace( "-", "" );
		
		logger.debug( "Creating new ClassBuilder for class {}", className );
		
		cClass = pool.makeClass( className );
		
		// Default constructor is implied for now
		withConstructor( null );
	}
	
	public ClassBuilder( ClassPool pool, String packageName )
	{
		super( pool );
		
		logger.debug( "Creating new ClassBuilder in package {}", packageName );
		
		String className = packageName + "." + UUID.randomUUID().toString().replace( "-", "" );

		logger.debug( "Class name is {}", className );
		
		cClass = pool.makeClass( className );
	}
	
	public ClassBuilder( ClassPool pool, String packageName, String className )
	{
		super( pool );
		
		logger.debug( "Creating new ClassBuilder in package {} with class name {}", packageName, className );
		
		cClass = pool.makeClass( packageName + "." + className );
	}
	
	public ClassAnnotationBuilder annotatedBy( Class<?> annotationClass )
	{	
		logger.debug( "Annotation class with {}", annotationClass.getName() );
		
		return new ClassAnnotationBuilder( this, annotationClass );
	}
	
	public ClassBuilder withConstructor( String src, Class<?> ... args ) throws NotFoundException, CannotCompileException
	{
		src = src == null ? "" : src.trim();
		
		if( logger.isDebugEnabled() )
		{
			if( args == null || args.length == 0 )
			{
				if( src.isEmpty() )
					logger.debug( "Generating empty default constructor" );
				else
					logger.debug( "Generating default constructor is source {}", src );
			}
			else
			{
				Object[] argsStr = new Object[args.length];
				for( int i=0; i<args.length; i++ )
					argsStr[i] = args[i].getName() + "; ";
				
				if( src.isEmpty() )
					logger.debug( "Adding empty constructor with args {}" );
				else
					logger.debug( "Adding constructor with args {} and source {}", argsStr, src );
			}
		}
		
		CtClass[] parameters = new CtClass[ args.length ];
		
		for( int i = 0; i < args.length; i++ )
			parameters[i] = getPool().get( args[i].getName() );
		
		CtConstructor cons = new CtConstructor( parameters, cClass );
		
		if( src.isEmpty() )
		{
			logger.debug( "No body was specified, adding empty constructor body" );
			cons.setBody( "{}" );
		}
		else
		{
			logger.debug( "Setting constructor body to {}", src );
			cons.setBody( src );
		}
		
		logger.debug( "Adding constructor to class" );
		
		cClass.addConstructor( cons );
		
		return this;
	}
	
	public MethodBuilder withMethod()
	{
		logger.debug( "Creating new method" );
		
		return new MethodBuilder( this );
	}
	
	public FieldBuilder withField()
	{
		logger.debug( "Creating new field" );
		
		return new FieldBuilder( this );
	}
	
	public ClassBuilder withBeanField( String fieldName, Class<?> fieldType, String valueSrc ) throws CannotCompileException, NotFoundException
	{
		valueSrc = valueSrc == null ? "" : valueSrc.trim();
		
		if( valueSrc.isEmpty() )
			logger.debug( "Creating new uninitialized bean field with name {} and type {}", fieldName, fieldType.getName() );
		else
			logger.debug( "Creating new bean field with name {}, type {}, and init source of {}", fieldName, fieldType.getName(), valueSrc );
		
		MethodBuilder getterBuilder = new MethodBuilder( this );
		MethodBuilder setterBuilder = new MethodBuilder( this );
		
		FieldBuilder fieldBuilder = new FieldBuilder( this );
		
		logger.debug( "Creating private field {}", fieldName );
		
		fieldBuilder.setPrivate().named( fieldName ).ofType( fieldType ).withValue( valueSrc ).apply();
		
		logger.debug( "Creating setter" );
		
		setterBuilder.setPublic()
					 .named( genBeanFieldMethodName( "set", fieldName ) )
					 .takes( "value", fieldType )
					 .withSource( "{ this." + fieldName + "=$1; }" )
					 .build()
					 .apply();
		
		
		logger.debug( "Creating getter" );
		
		getterBuilder.setPublic()
				     .named( genBeanFieldMethodName( "get", fieldName ) )
				     .withSource( "{ return this." + fieldName + "; }" )
				     .returns( fieldType )
				     .build()
				     .apply();
		
		return this;
	}
	
	@Override
	public Class<?> apply() throws CannotCompileException, NotFoundException
	{
		logger.debug( "Generating Class object" );
		
		return cClass.toClass();
	}
	
	private String genBeanFieldMethodName( String prefix, String fieldName )
	{
		String methodName = prefix + ( String.valueOf( fieldName.charAt( 0 ) ).toUpperCase() ) + fieldName.substring( 1 );
		
		logger.debug( "Generating bean field method {}", methodName );
		
		return methodName;
	}
	
	public CtClass getCTClass() { return this.cClass; }
}
