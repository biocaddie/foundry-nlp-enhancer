
//
package gov.nih.nlm.nls.ner;

import java.util.Properties;

/**
 *
 */

public class MetaMapLiteConfig {
  private static java.util.Properties prop = new java.util.Properties();
  private static void loadProperties() {
    // get class loader
    ClassLoader loader = MetaMapLiteConfig.class.getClassLoader();
    if(loader==null)
      loader = ClassLoader.getSystemClassLoader();
    
    // assuming you want to load application.properties located in WEB-INF/classes/conf/
    String propFile = "conf/metamaplite.properties";
    java.net.URL url = loader.getResource(propFile);
    try{prop.load(url.openStream());}catch(Exception e){System.err.println("Could not load configuration file: " + propFile);}
  }
  
  //....
  // add your methods here. prop is filled with the content of conf/metamaplite.properties

  static Properties getProperties() {
    return prop;
  }
  
  // load the properties when class is accessed
  static {
    loadProperties();
  }
}
