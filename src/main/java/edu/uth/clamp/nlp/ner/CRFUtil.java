package edu.uth.clamp.nlp.ner;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.cleartk.util.InputStreamHandler;

public class CRFUtil {
	
	public static CRFUtil instance = new CRFUtil();
	File crfsuite = null;
	
	CRFUtil() {
		String location = getCRFSuiteLocation();
		crfsuite = getTempFile( location );
		isRunnable();
	}
	
	public static int train( String featureFile, String modelFile, String argv ) {
		StringBuffer cmd = new StringBuffer();
		cmd.append(instance.crfsuite.getPath());
		cmd.append( " learn" );
		cmd.append( " -m " + modelFile );
		cmd.append(" " + argv );
		cmd.append( " " + featureFile );
		try {
			Process p = Runtime.getRuntime().exec(cmd.toString());
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	        
            String s;
		    while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
            }
            
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            stdError.close();
			stdInput.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return 0;
	}
	
	public static String predictWithProb( String featureFile, String modelFile ){
		StringBuffer cmd = new StringBuffer();
		cmd.append(instance.crfsuite.getPath());
		cmd.append( " tag" );
		cmd.append( " -p" );
		cmd.append( " -m " + modelFile );
		cmd.append( " " + featureFile );
		String ret = "";
		try {
			Process p = Runtime.getRuntime().exec(cmd.toString());
			//System.out.println( cmd.toString() );
			
			InputStream stdIn = p.getInputStream();
            InputStreamHandler<List<String>> ishIn = InputStreamHandler.getInputStreamAsList(stdIn);

            InputStream stdErr = p.getErrorStream();
            InputStreamHandler<StringBuffer> ishErr = InputStreamHandler.getInputStreamAsBufferedString(stdErr);

            try {
            	p.waitFor();
              	ishIn.join();
              	ishErr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stdErr.close();
            stdIn.close();
            return ishIn.getBuffer().toString().replaceAll("(^\\[)|([,])|(]$)", "\n").trim().replace(" ",  "") + "\n\n";

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		return ret;
	}
	
	public static String predict( String featureFile, String modelFile ){
		StringBuffer cmd = new StringBuffer();
		cmd.append(instance.crfsuite.getPath());
		cmd.append( " tag" );
		cmd.append( " -m " + modelFile );
		cmd.append( " " + featureFile );
		String ret = "";
		try {
			Process p = Runtime.getRuntime().exec(cmd.toString());
			//System.out.println( cmd.toString() );
			
			InputStream stdIn = p.getInputStream();
            InputStreamHandler<List<String>> ishIn = InputStreamHandler.getInputStreamAsList(stdIn);

            InputStream stdErr = p.getErrorStream();
            InputStreamHandler<StringBuffer> ishErr = InputStreamHandler.getInputStreamAsBufferedString(stdErr);

            try {
            	p.waitFor();
              	ishIn.join();
              	ishErr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stdErr.close();
            stdIn.close();
            return ishIn.getBuffer().toString().replaceAll("(^\\[)|([,])|(]$)", "\n").trim().replace(" ",  "") + "\n\n";

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		return ret;
	}
	
	
	String getCRFSuiteLocation() {
		String location = "";
		try {
			String osName = System.getProperty( "os.name" );
			String osArch = System.getProperty( "os.arch" );
			if( osName.toLowerCase().indexOf( "win" ) >= 0 ) {
				// This is windows;
				location = "edu/uth/clamp/nlp/crfsuite/win/crfsuite.exe";
			} else if( osName.toLowerCase().indexOf( "mac os x" ) >= 0 ) {
				// This is mac os x;
				location = "edu/uth/clamp/nlp/crfsuite/mac/crfsuite";
			} else if( osName.toLowerCase().indexOf( "linux" ) >= 0 ) {
				// This is linux;
				if( osArch.endsWith("64") ){
					location = "edu/uth/clamp/nlp/crfsuite/linux_64/crfsuite";
				} else {
					location = "edu/uth/clamp/nlp/crfsuite/linux_32/crfsuite";
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return location;
		
	}
	
	File getTempFile( String location ) {
		File crfsuite = null;
		//URL crfsuiteUrl = ClassLoader.getSystemResource(location);
        // IBO
        URL crfsuiteUrl = this.getClass().getResource("/" + location);

		// IBO
        System.out.println("location:" + location + " crfsuiteUrl:" + crfsuiteUrl);
        if( crfsuiteUrl == null ) {
			return null;
		}
        String name = FilenameUtils.getBaseName(crfsuiteUrl.getPath());
        InputStream is = null;
        OutputStream os = null;
        try {
        	crfsuite = File.createTempFile(name, ".temp" );
        	crfsuite.setExecutable( true );
     		crfsuite.deleteOnExit();
     		is = crfsuiteUrl.openStream();
            os = new FileOutputStream(crfsuite);
            copy(is, os);
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            closeQuietly( is );
            closeQuietly( os );
        }
        return crfsuite;
	}
	
	int isRunnable() {
		int ret = -1;
		StringBuffer cmd = new StringBuffer();
		cmd.append(crfsuite.getPath());
		cmd.append(" -h");
		try {
			Process p = Runtime.getRuntime().exec(cmd.toString());

			InputStream stdIn = p.getInputStream();
			InputStreamHandler<List<String>> ishIn = InputStreamHandler
					.getInputStreamAsList(stdIn);

			InputStream stdErr = p.getErrorStream();
			InputStreamHandler<StringBuffer> ishErr = InputStreamHandler
					.getInputStreamAsBufferedString(stdErr);

			try {
				p.waitFor();
				ishIn.join();
				ishErr.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String usage = ishIn.getBuffer().toString();
			if( usage.indexOf( "USAGE:" ) > 0 ) {
				ret = 0;
			}
			stdErr.close();
			stdIn.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ret;
	}

	public static void main( String[] argv ) {
		
		CRFUtil util = CRFUtil.instance;
		try {
            System.out.println( System.getProperty("os.name") );
            System.out.println( System.getProperty("os.arch") );
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            System.err.println("Caught a SecurityException reading the system property '" + "os.name"
                    + "'; the SystemUtils property value will default to null.");
            return ;
        }
		
		
		/*
		CrfSuiteWrapper wrapper;
		wrapper = new CrfSuiteWrapper();
		String model = "/Users/jwang16/train.model";
		String trainingDataFile = "/Users/jwang16/train.fea";
		String[] args = { "-a lbfgs -p c2=0.9" };
		try {
			wrapper.trainClassifier(model, trainingDataFile, args );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return;
	}
}
