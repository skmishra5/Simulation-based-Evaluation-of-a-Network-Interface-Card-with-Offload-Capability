import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Logging {
	    
	    public static void main(String[] args) {
	        
	        Logger logger = Logger.getLogger("MyLog");
	        FileHandler fh;
	        
	        try {
	            
	            // This block configure the logger with handler and formatter
	            fh = new FileHandler("log.log", true);
	            logger.addHandler(fh);
	            //logger.setLevel(Level.ALL);
	            SimpleFormatter formatter = new SimpleFormatter();
	            fh.setFormatter(formatter);
	           
	            // the following statement is used to log any messages
	           
	           // logger.entering("MyLogger", "main");
	           
	             Thread.sleep(2000);
	        } catch (SecurityException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        logger.info("hi");
	        
	    }
	    
	}

