package edu.uth.clamp.nlp.configurable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import edu.uth.clamp.nlp.structure.DocProcessor;

public abstract class NLPProcessorConf extends ComponentConf {
	private static final String ENCODING  = "UTF-8";

	public StringParam procType;
	public StringParam procName = null;

	public NLPProcessorConf( String componentName, String procType ) {
		super( componentName );
		this.procType = new StringParam( "processor type", procType, procType, "This is the type of this processor" );
		this.procName = new StringParam( "processor name", procType, procType, "This is the name of this processor" );
	}
	
	@Override
	public boolean equals( Object o ) {
		if( !( o instanceof NLPProcessorConf ) ) {
			return false;
		}
		return this.getProcName().equals( ((NLPProcessorConf)o).getProcName() )
				&& this.getProcType().equals( ((NLPProcessorConf)o).getProcType() );
	}

	/**
	 * @return  processor type, like "Rule based sentence detector, CRF based NER...";
	 */
	public String getProcType() {
		return procType.value();
	}
	
	/**
	 * @return  name of processor, used by end user, like ChemicalNER, Tokenize by space...";
	 */
	public String getProcName() { 
		return procName.value();
	}
	
	/**
	 * @param projectName the name of the processor;
	 */
	public void setProcName( String procName ) {
		this.procName.setValueStr( procName );;
	}
	
	static public NLPProcessorConf createFromFile( String fileName ) {
		try {
			String text = FileUtils.file2String( new File( fileName ), ENCODING );
			InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
			SAXReader reader = new SAXReader();
			Document document = reader.read( stream );
			Element root = document.getRootElement();
			NLPProcessorConf conf = (NLPProcessorConf) Class.forName( root.getName() ).newInstance();

			for( Object obj : root.elements() ) {
				Element e = (Element)obj;
				System.out.println( e.getName() );
				if( e.getName().equals( "requiredComps" ) ) {
					Iterator com = e.elementIterator("requiredComp");
					while( com.hasNext() ) {
						Element comp = (Element)com.next();
						String compStr = comp.attributeValue( "comp" );
						conf.requireComp( compStr );
					}
					continue;
				}
			
				Field field = conf.getClass().getField( e.getName() );
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
				field.set( conf, param );				
			}
			return conf;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
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
		
		return null;
	}

	public abstract Object create();
}
