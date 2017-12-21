import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.text.*;



public class sender1 {
	
	LimitedQueue1<Event1> messageQueue;
	LinkedList<PacketList1> packetQueue;
	public static LimitedQueue1<PacketList1> transmitBuffer;
	LinkedList<Statistics_Queue> statQueue;
	//LimitedQueue<Message_Queue> queue;
	double clock;               //clock should be in micro seconds
	double Ls = 32768;
	double lambda;
	//RandomGeneration rg1 = new RandomGeneration();
	long startTime = System.currentTimeMillis();
	int[] numbers = new int[65536];
	int[] buf = new int[65536];
	int i = 0;
	int pp = 1;
	int IDLE = 1;
	int BUSY = 2;
	int packetDropCount, numMessages = 0;
	double numPackets = 0;
	double vacateTime = 0.0, vt = 0.0, waitingTime = 0.0;
	double startProcessingTime = 0;
	int count1 = 0;
	int numPacket = 0;
	int qlength = 0;
	int flag = 0;
	double avgDelay = 0.0;
	String logging = " ";
	double messageQueueSize;
	double processingTime = 0.0;
	double processingRate = 0.0;
	double utlizationFactor = 0.0;
	double throughputTime = 0.0;
	
	void Initialize (double lambda)
    {
        packetQueue = new LinkedList<PacketList1> ();
        transmitBuffer = new LimitedQueue1<PacketList1> (196608);
        messageQueue = new LimitedQueue1<Event1> (327680);
        statQueue = new LinkedList<Statistics_Queue> ();
        //queue = new LimitedQueue<Message_Queue>(196608);
        clock = 0.0;
        packetDropCount = 0;
        //numArrivals = numDepartures = 0;
        //totalWaitTime = totalSystemTime = 0.0;
        newArrival(lambda);
    }
    
	void newArrival (double lambda)
	{
		//double ls = Ls;
		// The next arrival occurs when we add an interrarrival to the the current time.
		double nextArrivalTime = clock + InterarrivalTime(lambda);
		clock = nextArrivalTime;
		double msgLength = ExponentialDistriLength(Ls);
		Ls = msgLength;
		if (Ls > 65536)
			msgLength = 65536;
		numMessages++;
//		for (int i = 0; i < msgLength; i++)
//		{
//			buf[i] = numbers[i];
//		}
		//System.out.println("number of messages" + numMessages);
		handleArrival(nextArrivalTime, msgLength);
			
	 }
	 void handleArrival(double nextArrivalTime, double msgLength)
	 {
		 int count = 0;
		 qlength = messageQueue.size();
		 		 
		 if (startProcessingTime != 0)
		 {
		     if(vt <= vacateTime)
		     {
			     pp = BUSY;
			     vt++; 
			     //System.out.println("time:"+ (System.currentTimeMillis()-startProcessingTime));
			     //System.out.println("vacatetime:" + vacateTime);
		     }
		     else
		     {
		    	 pp = IDLE;
		     }
		 }
		 
		 if ( pp == IDLE)
		 {
		     pp = BUSY;
		     if (qlength > 0)
		     { 
		    	 Event1 e = messageQueue.poll();
		    	 processingTime = (e.msgLength * 8 * 2)/1000000000;
		    	 //waitingTime = vacateTime - e.eventTime;
		    	 waitingTime = (processingTime + e.eventTime) - e.eventTime;
		    	 startProcessingTime = e.eventTime;
		    	 vacateTime = e.eventTime + e.msgLength;
		    	 //System.out.println("2nd pp");
		    	 messageQueueSize = messageQueueSize - e.msgLength;
		    	 protocol_processor(e.msgLength, vacateTime, waitingTime);
		     }
		     else
		     {
		    	 vacateTime = nextArrivalTime + msgLength ;
			     startProcessingTime = nextArrivalTime;
			     processingTime = (msgLength * 8 * 2)/1000000000;
		    	 //System.out.println("1st event handling");
		    	 protocol_processor(msgLength, vacateTime, 0.0);
		     }
		 }
		 else if ((messageQueueSize < 196608) && (flag == 0))
		 {
			 //System.out.println("Adding Events");
			 count1++;
			 //System.out.println(count1);
		     messageQueue.add (new Event1 (count+1, nextArrivalTime, Event.ARRIVAL, msgLength));
		     messageQueueSize = messageQueueSize + msgLength;
		 }
		 else if(messageQueueSize >= 196608)
		 {
		     msgLength = 0;
//				for(int j =0; j < msgLength; j++)
//				{
//					buf[j] = '\0';
//				}
		     
		    	 packetDropCount = numMessages - numPacket;
			 //System.out.println("drop count" + packetDropCount);
		 }
//		 else
//			 System.out.println("Waiting to be processed");
	 }
	 void protocol_processor(double msgLength, double vacateTime, double waitingTime)
	 {
		 double pLength;
		 double packetLength;
		 numPackets = msgLength / 1526 ;
		 double li = 0;
		 double delay = 0.0;
		 double waitPP = 0.0;
		 int tLength = transmitBuffer.size();
		 numPacket ++;
		 System.out.println("Number in PP:" +numPacket);
		 DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		 Date date = new Date();
		
		 	 
		 for (pLength = 0.0; li < numPackets; pLength++)
		 {
			 if ((msgLength - pLength) < 1526)
			 {
				 packetLength = 1526;
				 packetQueue.add(new PacketList1(li, packetLength));
				 logging = logging + "\n" + "Frame Number:" + li + "Frame Length" + packetLength + dateFormat.format(date);
				 //logger.info(logging);
				 li++;
			 }
			 else{
				 pLength = pLength + 1526;
				 packetLength = 1526;
				 packetQueue.add(new PacketList1(li, packetLength));
				 logging = logging + "\n" + "Frame Number:" + li + "Frame Length" + packetLength + dateFormat.format(date);
				 li++;
			 }
			 
		 }		
		 
		 transmitBuffer.addAll(packetQueue);	
		 packetQueue.clear();
		 
//		 for(;;)
//		 {
//			 waitPP++;
//			 if(transmitBuffer.size() == 0)
//				 break;
//		 }
//		 ListIterator<PacketList> listIterator = transmitBuffer.listIterator();
//		 while (listIterator.hasNext())
//		 {
//			 System.out.println(listIterator.next());
//			 System.out.println(transmitBuffer.element().packet_number);
//		 }
//		 for (int i = 0; i < transmitBuffer.size(); i++) {
//	            System.out.println(transmitBuffer.get(i).packet_number);
//	      }
//			 System.out.println(packetQueue.size());
		 //System.out.println(numPacket);
		 //processingTime = (msgLength * 8 * 2)/1000000000;	
		 delay = waitingTime + processingTime + waitPP;
		 statQueue.add(new Statistics_Queue(delay, waitingTime, processingTime));
		 pp = IDLE;
	 }
	 
	double InterarrivalTime (double lambda)
	{
		Random rand = new Random();
		double L = 1/lambda;
		double variate = -Math.exp(L) * Math.log(rand.nextDouble());
		//return exponential (arrivalRate);
		return variate;
	}
	double ExponentialDistriLength(double Ls)
	{
		double messageLength = Ls * Math.exp(1/Ls);
		return messageLength;
	}
    void beginSimulation (String lam)
    {
        
        Logger logger = Logger.getLogger("sendermodule");
		FileHandler fh;
		lambda = Double.parseDouble(lam);
		Initialize (lambda);
		 
			
			try {
	            
	            // This block configure the logger with handler and formatter
	            fh = new FileHandler("sm-log.log", true);
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
        for(;;){
        //while(false||((System.currentTimeMillis()-startTime) < 10000))
        	for(;;)
        	{
        		//System.out.println("loop" + (System.currentTimeMillis()-startTime));
        		newArrival(lambda);
        		if (numMessages == 10)
        			break;
        	}
        	flag = 1;
        	qlength = messageQueue.size();
        	//System.out.println("begin" + qlength);
        	for(;;)
        	{
        		//System.out.println("2nd loop");
        		handleArrival(0.0, 0.0);
        		//System.out.println("begin" + qlength);
        		if(qlength == 0)
        			break;
        	}
            for (int i = 0; i < statQueue.size(); i++)
            {
            	System.out.println("Packet Number:" + i + " " + "Delay:" + statQueue.get(i).delay);
            	avgDelay = avgDelay + statQueue.get(i).delay;
            	System.out.println("\n");
            	System.out.println("Packet Number:" + i + " " + "Waiting Time:" + statQueue.get(i).waitingTime);
            	System.out.println("\n");
            	System.out.println("Packet Number:" + i + " " + "Service Time:" + statQueue.get(i).serviceTime);
            	System.out.println("\n");
            }
            avgDelay = avgDelay/numMessages;
            System.out.println("Average Delay Of The System:" + avgDelay);
            System.out.println("Drop Count in Sender Side: " + (numMessages - numPacket));
            processingRate = 1 / processingTime;
            utlizationFactor = lambda/processingRate;
            throughputTime = (1/(1-utlizationFactor)) * processingTime;
            System.out.println("Processing Time" + processingTime + " " +"Processing Rate:" + processingRate + " " + "Utlization Factor" + utlizationFactor + " " +"Throughput Time" + throughputTime);
            logger.info(logging);
        	flag = 0;
        	if(flag == 0)
        		break;
        }
    }
//    void Message()
//	{
//		while(false||(System.currentTimeMillis()-startTime)<10000)
//		{
//			numbers[i] = (int)(Math.random()*20 + 1);
//			i++;
//			if (i == 65536)
//				i = 0;
//		}//end for loop
//		//System.out.println("Numbers Generated: " + Arrays.toString(numbers));
//		//System.out.println(numbers.length);
//	}
	
//	public static void main (String[] argv)
//	{
//		int maxTimeSimulation = 0;
//		//maxTimeSimulation = Integer.parseInt(argv[0]);
//		
//	    sender1 sender = new sender1 ();
//	    sender.beginSimulation (10);
//	    //System.out.println (sender);
//	} 
}

class Event1 {
    public static int ARRIVAL = 1;
    public static int DEPARTURE = 2;
    int type = -1;                     // Arrival or departure.
    int msgNumber = 0;                 // message number
    double eventTime;                  // When it occurs.
    double msgLength;                  // message length
    
    
    public Event1 (int msgNumber, double eventTime, int type, double msgLength)
    {
    	this.msgNumber = msgNumber;
    	this.eventTime = eventTime;
    	this.type = type;
    	this.msgLength = msgLength;
    }
}

class PacketList1{
	public double packet_number;
	double packet_length;
	
	public PacketList1(double packet_number, double packet_length)
	{
		this.packet_number = packet_number;
		this.packet_length = packet_length; 
	}
}

class LimitedQueue1<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue1(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }
}

class Statistics_Queue {
	double delay;
	double waitingTime;
	double serviceTime;
	
	public Statistics_Queue (double delay, double waitingTime, double serviceTime)
	{
		this.delay = delay;
		this.waitingTime = waitingTime;
		this.serviceTime = serviceTime;
	}
}

	
	
	
	


