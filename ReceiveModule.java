
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ReceiveModule {
	
	public static LimitedQueue2<FrameList> receiveBuffer = new LimitedQueue2<FrameList> (6000);
	public static int rpp = 1;
	public static int IDLE = 1;
	public static int BUSY = 2;
	public static double receiveBufferSize = 0.0;
	public static double i, j;
	public static double dropAtReceiver = 0.0;
	public static double numFrame = 0.0;
	public static double r, delay; 
	public static double waitTime = 0.000000;
	public static int firstFrameKept = 0;
	public static double clock = 0.0, oneFrameTime;
	public static int flag = 0;
	public static MacModule MacRm = new MacModule();
	public static FrameList fmMac;
	public static LinkedList<FrameList> frameQueue = new LinkedList<FrameList> ();
	public static LinkedList<Statistics_Queue_Receiver> statQueueRcv = new LinkedList<Statistics_Queue_Receiver> ();
	public static int rblength;
	public static double avgDelay = 0.0;
	public static double serviceTime = 0.000064;
	
//	public static void main(String[] args)
//    {
//		ReceiveModule RM = new ReceiveModule();
//		//RM.generate_frame();	
//		ReceiveHandleThread mythread = new ReceiveHandleThread();
//		mythread.start();
//		int count = 0;
//        for(;;)
//        {
//            RM.RMProcessing();
//            count ++;
//            if(receiveBuffer.size() == 0)
//            	break;
//        }
//        //mythread.stop();
//        RM.stats(count);
//        
//    }
	
	public static void startReceiving()
	{
		ReceiveHandleThread mythread = new ReceiveHandleThread();
		//generate_frame();	
		mythread.start();
		int count = 0;
        for(;;)
        {
            RMProcessing();
            count ++;
            stats();
            if(count == 10)
            	break;
        }
        //mythread.stop();
        
	}
	
	
	 public static void stats()
	{
		//Logger logger = Logger.getLogger("receiverbuffer");
	    //FileHandler fh;
	    //String logging = " ";
	    //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    //Date date = new Date();
	    
		  for (int i = 0; i < statQueueRcv.size(); i++)
          {
			
          	System.out.println("Frame Number:" + i + " " + "Delay:" + statQueueRcv.get(i).delay);
          	avgDelay = avgDelay + statQueueRcv.get(i).delay;
          	//logging = logging + "\n" + "Frame Number:" + i + " " + "Delay:" + statQueueRcv.get(i).delay + dateFormat.format(date);
          	System.out.println("\n");
          	System.out.println("Frame Number:" + i + " " + "Waiting Time:" + statQueueRcv.get(i).waitingTime);
          	System.out.println("\n");
          	System.out.println("Frame Number:" + i + " " + "Service Time:" + statQueueRcv.get(i).serviceTime);
          	System.out.println("\n");
          }
//		  try {
//	            
//	            // This block configure the logger with handler and formatter
//	            fh = new FileHandler("receiverbuffer-log.log", true);
//	            logger.addHandler(fh);
//	            //logger.setLevel(Level.ALL);
//	            SimpleFormatter formatter = new SimpleFormatter();
//	            fh.setFormatter(formatter);
//	           
//	            // the following statement is used to log any messages
//	           
//	           // logger.entering("MyLogger", "main");
//	           
//	             Thread.sleep(2000);
//	        } catch (SecurityException e) {
//	            e.printStackTrace();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        } catch (InterruptedException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//		  logger.info(logging);
          avgDelay = avgDelay/15;
          System.out.println("Average Delay Of The System:" + avgDelay);
          //System.out.println(dropAtReceiver);
          //dropAtReceiver = (count - 1) - dropAtReceiver;
          System.out.println("Drop at Receiver:" + dropAtReceiver);
          
	}
//	public static void generate_frame()
//	{
//		for (int i =0; i < 14; i++)
//		{	
//			frameQueue.add(new FrameList(i, 1600, 0, 0));
//		}
////		for (int i = 0; i < frameQueue.size(); i++) {
////            System.out.println(frameQueue.get(i).packet_length);
////      }
//	}
	public static void RMProcessing()
	{	
		if(flag == 0)
		{
			r = generate_uniform_distribution();
			flag = 1;
		}
		if(receiveBuffer.size() == 0)
		{	
			fmMac = MacRm.frameQueue.poll();
			//fmMac = frameQueue.poll();
			oneFrameTime = clock + 0.0000128;
			clock = oneFrameTime;
			//System.out.println("fuck" + fmMac.frame_number);
			//System.out.println("in size 0");
			Receiver(fmMac.frame_number, oneFrameTime, 1);
			//System.out.println("in size 0");
		}
		else{
			FrameList fm = receiveBuffer.poll();
			receiveBufferSize = receiveBufferSize - 1600;
			
			Receiver(fm.frame_number, 0.0, 0);}
	}
	
	public static void Receiver(double frame, double oneFrameTime, int flagFrist)
	  {
		  //for(;;)
		  //{
			  //r = generate_uniform_distribution();
			  //int rblength1 = receiveBuffer.size(); 
			  if (rpp  == IDLE)
			  {
				  Receive_Packet_Processor(frame, oneFrameTime, flagFrist);
//				  if (r < frame)
//				  {
//					  if (rblength1 == 0)
//					  {	  
//					      flag = 1;
//					      Receive_Packet_Processor(r, frame, flag);
//					  }
//					  for (i = 0.0; i < frame - r; i++)
//					  {
//						  receiveBuffer.add(new FrameList(i, 1600));
//					  }	  
//					  receiveBufferSize = receiveBufferSize + (i * 1600);
//				  }
//				  else
//				  {
//					  flag = 2;
//					  Receive_Packet_Processor(r, frame, flag);
//				  }
				  //System.out.println(frame + "number of packets are given to upper layer" );
			  }
//			  else if (rpp == BUSY)
//			  {
//				  //for (j = 0.0; j < frame; j++)
//				  //{
//				  receiveBuffer.add(new FrameList(frame, 1600, oneFrameTime));
//				  //}
//				  receiveBufferSize = receiveBufferSize + 1600; 
//				  Receive_Packet_Processor(0.0, 0.0);
//			  }
			  else if(receiveBufferSize >= 6000)
			  {
				  frame = 0.0;
				  //r = 0.0;
				  dropAtReceiver++;
			  }
			  else if(rpp == BUSY){
				  Receive_Packet_Processor(frame, oneFrameTime, flagFrist);
			  }
		  //}
		  
	  }
	public static void Receive_Packet_Processor(double frame, double oneFrameTime, int flagFirst)
	  {
		  
		  String[] buf =  new String[10];
		  rblength = receiveBuffer.size();
		  //System.out.println("Service Time is: " + String.format("%.6f", serviceTime));
		  //int j = 1;
		  if (flagFirst == 0)
		  {	  
			  numFrame++;
			  //System.out.println("Inside flag 0");
		  }
		  else if(flagFirst == 1)
		  {
			  
			  numFrame = 1;
		  }
		  
		  if(r <= numFrame)
		  {
//			  if(rblength > 0)
//			  {
				  
				  for(int i = 0; i < r; i++)
				  {
					  //FrameList fm = receiveBuffer.poll();
					  //receiveBufferSize = receiveBufferSize - 1600;
					  buf[i] = "Frame" + i;
					  if (i > 0)
					  {	  
						  waitTime = serviceTime + waitTime;
						  String.format("%.6f", waitTime);
						  delay = waitTime + serviceTime;
						  statQueueRcv.add(new Statistics_Queue_Receiver(i, delay, waitTime, serviceTime));
					  }
				  }
				  
			 // }
//			  else{
//				  for (int i = 0; i < r; i++)
//				  {
//				      buf[i] = "Frame" + i;
//				  }
//			  }
			  for (int i = 0; i < r; i++)
			  {	  
				  System.out.println("My segment for upper layer is:" + buf[i]);
			  }
//			  for (i = 0.0; i < frame - r; i++)
//			  {
//			      receiveBuffer.add(new FrameList(i, 1600));
//			  }	  
//			  receiveBufferSize = receiveBufferSize + (i * 1600);
			  rpp = IDLE;
			  flag = 0;
			  numFrame = 0;
		  }
		  else
		  {
			  
			  if (rblength == 0)
			  {  
			      receiveBuffer.add(new FrameList(frame, 1600, oneFrameTime, 1));
			      receiveBufferSize = receiveBufferSize + 1600;
			      delay = 0 + serviceTime;
			      firstFrameKept = 1;
			      statQueueRcv.add(new Statistics_Queue_Receiver(frame, delay, 0, serviceTime));
			  }
			  //System.out.println("hey" + firstFrameKept);
			  rpp = BUSY;
			  //System.out.println("Waiting in the RPP");
			  //System.out.println(receiveBuffer.get(0).packet_length);
			  //return;
		  }
//		  if(flag == 1)
//		  {
//			  for (int i = 0; i < r; i++)
//			  {
//				  buf[i] = "Frame" + i;
//			  }
//			  System.out.println("My segment is for upper layer is:" + buf);
//		  }
//		  else if(flag ==2)
//		  {
//			  return;
//		  }
//		  else if (rblength > 0)
//		  {
//			  FrameList f = receiveBuffer.poll();
//			  for(double i = 0.0; i < frame; )
//		  }
//		  for(double i = 0.0; i < frame; i++)
//		  {
//			  buf[i] = frame;
//		  }
	  }
	public static double generate_uniform_distribution()
	  {
		  Random rand = new Random();
		  double rFrame = rand.nextInt(5) + 1;
		  return rFrame;
	  }
}
class FrameList{
	public double frame_number;
	double packet_length;
	double frameArrival;
	int flagFirst;
	
	public FrameList(double frame_number, double packet_length, double frameArrival, int flagFirst)
	{
		this.frame_number = frame_number;
		this.packet_length = packet_length;
		this.frameArrival = frameArrival;
		this.flagFirst = flagFirst;
	}
}

class LimitedQueue2<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue2(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }
}

class Statistics_Queue_Receiver {
	double frameNumber;
	double delay;
	double waitingTime;
	double serviceTime;
	
	public Statistics_Queue_Receiver (double frameNumber, double delay, double waitingTime, double serviceTime)
	{
		this.frameNumber = frameNumber;
		this.delay = delay;
		this.waitingTime = waitingTime;
		this.serviceTime = serviceTime;
	}
}

 class ReceiveHandleThread extends Thread{
	ReceiveModule rmthread = new ReceiveModule();
	int count = 0;
	//FileOutputStream out;
	
	
	public void run(){
		Logger logger = Logger.getLogger("receiverbuffer");
		FileHandler fh;
		String logging = " ";
		
		try {
            
            // This block configure the logger with handler and formatter
            fh = new FileHandler("receiverbuffer-log.log", true);
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
	  
		for (;;)
		{
			
			//System.out.println(rmthread.firstFrameKept);
			if(rmthread.rpp == rmthread.BUSY)
			{
				count ++;
				//System.out.println("hello");
				rmthread.fmMac = rmthread.MacRm.frameQueue.poll();
				//rmthread.fmMac = rmthread.frameQueue.poll();
				rmthread.oneFrameTime = rmthread.clock + ((1600 * 8 ) / 1000000000);
				rmthread.clock = rmthread.oneFrameTime;
				rmthread.receiveBuffer.add(new FrameList(rmthread.fmMac.frame_number, 1600, rmthread.oneFrameTime, 0));
				logging = logging + "\n" + "Frame Number:" + count;
				rmthread.receiveBufferSize = rmthread.receiveBufferSize + 1600;
				logger.info(logging);
			}
//			for (int i = 0; i < rmthread.receiveBuffer.size(); i++) {
//	            System.out.println(rmthread.receiveBuffer.get(i).frame_number);}q
//	      }
			//System.out.println("Count in thread:" count);
			if (count == 10)
				break;
		}
	}
}