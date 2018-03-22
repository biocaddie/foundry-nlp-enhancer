package edu.uth.clamp.nlp.configurable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.tools.tokenize.Tokenizer;

import org.apache.uima.util.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.StringParam;

public abstract class ComponentConf implements Configurable {
	Set<String> requiredComp = null;
	public StringParam componentName;

	public ComponentConf( String componentName ) {
		requiredComp = new HashSet<String>();
		this.componentName = new StringParam( "Component name"
				, componentName
				, componentName
				, "This is the component name." );
	}

	public void requireComp( String compName ) {
		requiredComp.add( compName );
	}

	public boolean isRequiredComponent( String name ) {
		if( requiredComp == null ) {
			return false;
		}
		return requiredComp.contains( name );
	}

	public Set<String> getRequiredCompSet() {
		return requiredComp;
	}

	public String getCompName() {
		return componentName.value();
	}
	
	public Param[] getParams() {
		List<Param> ret = new ArrayList<Param>();
		List<Field> allFields = new ArrayList<Field>();
		Class c = this.getClass();
		while( c != null ) {
			for( Field field : c.getDeclaredFields() ) {
				allFields.add( field );
				if( !Modifier.isPublic( field.getModifiers() ) ) {
	    			continue;
	    		}
				try {
					Object obj = field.get( this );
					if( obj instanceof Param ) {
						ret.add( (Param) obj );
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			c = c.getSuperclass();
		}
		return ret.toArray( new Param[ ret.size() ] );
	}
	
	public String toString() {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement( this.getClass().getName() );

	    Element required = root.addElement( "requiredComps" );
	    for( String comp : requiredComp ) {
	    	required.addElement( "requiredComp" )
	    		.addAttribute( "comp", comp );	    	
	    }
	    
		Class c = this.getClass();
		while( c != null ) {
			for( Field field : c.getDeclaredFields() ) {
				if( !Modifier.isPublic( field.getModifiers() ) ) {
	    			continue;
	    		}
				try {
					Object obj = field.get( this );
					if( obj instanceof Param ) {
						Element element = root.addElement( field.getName() );
						Param param = ( Param ) obj;
			    	    element.addElement( "type" ).addText( param.getClass().getName() );
			    	    element.addElement( "name" ).addText( param.getName() );
			    	    element.addElement( "valueStr" ).addText( param.getValueStr() );
			    	    element.addElement( "defaultValue" ).addText( param.getDefaultValue() );
			    	    element.addElement( "description" ).addText( param.getDescription() );
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			c = c.getSuperclass();
		}
	    
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
	    XMLWriter writer;
		try {
			writer = new XMLWriter( ret,
					new OutputFormat( "  ", true, "UTF-8" ) );
		    writer.write(document);
		    writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return ret.toString();
	}
	

	
	public int fromString( String confStr ) {
		InputStream stream = new ByteArrayInputStream(confStr.getBytes(StandardCharsets.UTF_8));
		SAXReader reader = new SAXReader();
        try {
			Document document = reader.read( stream );
			Element root = document.getRootElement();
			
			for( Object obj : root.elements() ) {
				Element e = (Element)obj;
				System.out.println( e.getName() );
				if( e.getName().equals( "requiredComps" ) ) {
					Iterator com = e.elementIterator("requiredComp");
					while( com.hasNext() ) {
						Element comp = (Element)com.next();
						String compStr = comp.attributeValue( "comp" );
						requiredComp.add( compStr );
					}
					continue;
				}
			
				Field field = this.getClass().getField( e.getName() );
				String type = e.elementText( "type" );
				System.out.println( type );
				System.out.println( e.elementText( "name" ) );
				System.out.println( e.elementText( "valueStr" ) );
				System.out.println( e.elementText( "defaultValue" ) );
				System.out.println( e.elementText( "description" ) );
				Param param = (Param) Class.forName( type )
						.getConstructor( String.class, String.class, String.class, String.class )
						.newInstance(
						e.elementText( "name" )
						, e.elementText( "valueStr" )
						, e.elementText( "defaultValue" )
						, e.elementText( "description" ) );
				field.set( this, param );				
			}
        } catch( DocumentException e ) {
        	e.printStackTrace();
        } catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return 0;
	}
}
