import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class MacModule {
    int MM = 0;
    int BUSY =1;
    int IDLE=0;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    double packetl = 1600*8; //number of bits
    double transRate = 1000000000; //1 Gbps
    double timeslot = (packetl)/transRate; //in secs
    double Pr;
    double TxProb;
    double Ps;
    double RxProb;
    double duplex;
    double MaxFrame = 100;
    double MMproctime = (1600*8*0.3)/1000000000;
   
    public static LinkedList<FrameList> frameQueue = new LinkedList<FrameList> ();
    sender1 snd = new sender1();
    int timeCount, timeCount1;
    Logger logger = Logger.getLogger("macmodule");
	FileHandler fh;
	String logging = " ";
    
    
      
    void calcProbabilty()
    {
        Pr = Math.random();
        TxProb = 1-Pr;
        Ps = Math.random();
        RxProb = Math.random();
        duplex = Math.random();
      
    }
   
    void startMM()
    {
        int count =0;
        
    	
    	try {
            
            // This block configure the logger with handler and formatter
            fh = new FileHandler("controller-log.log", true);
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
         for(;;)
         {
             count++;
             calcProbabilty();
            
             MMProcessing();
             if(count == 10)   // have to change
                 break;
         }
    }
   
    void MMProcessing()
    {
        double PacketReceive = 0.0;
        System.out.println("one time slot " +timeslot+ " secs");
        System.out.println("proc time "+MMproctime);
        System.out.println("maxframe "+MaxFrame);
      
        System.out.println("Pr "+Pr);
      
        System.out.println("dup "+duplex);
        double n = Math.floor(timeslot/MMproctime);
        System.out.println("no of frames "+n);
        double loop =0;
        if(duplex<Pr)
       
        {  
            while(loop< n)
            {

                MM= BUSY;
               
                    PacketReceive = Pr * MaxFrame;
                    System.out.println("receiving frames "+PacketReceive );
//                   
                        doRx(PacketReceive);
                        loop++;
           }
//                     
                 
                }              
       
        else
        {
            while(loop< n)
           
           {
          
            MM = IDLE;
          
                System.out.println("transmitting");
           
                if(snd.transmitBuffer.size() > 0)
                {
                			logging = "Frame" + loop + "Received from SM";
                            doTx();
                            loop++;
                            logger.info(logging);
                }
               
            }
                   
        }}
       
 
       
    void doTx()
    {
        
        //System.out.println("inside dotx " + snd.transmitBuffer.size());
        if (snd.transmitBuffer.size() > 0)
        {
             PacketList1 p = snd.transmitBuffer.poll();
             double frameLength =p.packet_length;
             long Tg = (74 * 8)/1000000000;
             try {
                 Thread.sleep(Tg * 1000);
             } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
        }
        System.out.println("Packets are sent to the network");
       
    }
    void doRx(double PacketReceive)
    {
        double PacketSent = Ps * PacketReceive;
        double frameSent = Math.ceil(PacketSent);
        
        for (int i =0; i < PacketSent; i++)
        {   
            frameQueue.add(new FrameList(i, 1600, 0, i));
           
        }
        for (int j = 0; j < (PacketReceive - PacketSent); j ++)
        {
        	logging = "Frame" + j + "Sent to RM";
        }
        logger.info(logging);
        System.out.println("correct pkt sent to RM");
    }
}