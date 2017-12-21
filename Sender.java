
import java.util.*;
import java.text.*;

public class Sender {
	
	LimitedQueue<Event> messageQueue;
	LinkedList<PacketList> packetQueue;
	LimitedQueue<PacketList> transmitBuffer;
	//LimitedQueue<Message_Queue> queue;
	double clock;               //clock should be in micro seconds
	double Ls = 32768;
	double lambda = 100;
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
	double vacateTime = 0.0;
	long startProcessingTime = 0;
	int count1 = 0;
	int numPacket = 0;
	int qlength = 0;
	int flag = 0;
	
	void Initialize ()
    {
        packetQueue = new LinkedList<PacketList> ();
        transmitBuffer = new LimitedQueue<PacketList> (196608);
        messageQueue = new LimitedQueue<Event> (196608);
        //queue = new LimitedQueue<Message_Queue>(196608);
        clock = 0.0;
        packetDropCount = 0;
        //numArrivals = numDepartures = 0;
        //totalWaitTime = totalSystemTime = 0.0;
        newArrival();
    }
    
	void newArrival ()
	{
		//double ls = Ls;
		// The next arrival occurs when we add an interrarrival to the the current time.
		double nextArrivalTime = clock + InterarrivalTime(lambda);
		double msgLength = ExponentialDistriLength(Ls);
		Ls = msgLength;
		if (Ls > 65536)
			msgLength = 65536;
		numMessages++;
//		for (int i = 0; i < msgLength; i++)
//		{
//			buf[i] = numbers[i];
//		}
		System.out.println("number of messages" + numMessages);
		handleArrival(nextArrivalTime, msgLength);
			
	 }
	 void handleArrival(double nextArrivalTime, double msgLength)
	 {
		 int count = 0;
		 qlength = messageQueue.size();
		 		 
		 if (startProcessingTime != 0)
		 {
		     if(((System.currentTimeMillis()-startProcessingTime) <= (vacateTime)))
		     {
			     pp = BUSY; 
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
		    	 Event e = messageQueue.poll();
		    	 startProcessingTime = System.currentTimeMillis();
		    	 vacateTime = e.eventTime + e.msgLength;
		    	 System.out.println("2nd pp");
		    	 protocol_processor(e.msgLength, vacateTime);
		     }
		     else
		     {
		    	 vacateTime = nextArrivalTime + msgLength ;
			     startProcessingTime = System.currentTimeMillis();
		    	 //System.out.println("1st event handling");
		    	 protocol_processor(msgLength, vacateTime);
		     }
		 }
		 else if ((qlength < 196608) && (flag == 0))
		 {
			 //System.out.println("Adding Events");
			 count1++;
			 //System.out.println(count1);
		     messageQueue.add (new Event (count+1, nextArrivalTime, Event.ARRIVAL, msgLength));
		 }
		 else if(qlength == 196608)
		 {
		     msgLength = 0;
//				for(int j =0; j < msgLength; j++)
//				{
//					buf[j] = '\0';
//				}
			 packetDropCount++;
			 //System.out.println("drop count" + packetDropCount);
		 }
//		 else
//			 System.out.println("Waiting to be processed");
	 }
	 void protocol_processor(double msgLength, double vacateTime)
	 {
		 double pLength;
		 double packetLength;
		 numPackets = msgLength / 1526 ;
		 double li = 0;
		 int tLength = transmitBuffer.size();
		 numPacket ++;
		 //System.out.println("Number in PP:" +numPacket);
		 	 
		 for (pLength = 0.0; li <= numPackets; pLength++)
		 {
			 if ((msgLength - pLength) < 1526)
			 {
				 packetLength = 1526;
				 packetQueue.add(new PacketList(li, packetLength));
				 li++;
			 }
			 else{
				 pLength = pLength + 1526;
				 packetLength = 1526;
				 packetQueue.add(new PacketList(li, packetLength));
				 li++;
			 }
		 }		 
		 transmitBuffer.addAll(packetQueue);	
//		 ListIterator<PacketList> listIterator = transmitBuffer.listIterator();
//		 while (listIterator.hasNext())
//		 {
//			 System.out.println(listIterator.next());
//			 System.out.println(transmitBuffer.element().packet_number);
//		 }
		 for (int i = 0; i < transmitBuffer.size(); i++) {
	            System.out.println(transmitBuffer.get(i).packet_length);
	      }
			 //System.out.println(transmitBuffer.size());
			 //System.out.println(li);
		 pp = IDLE;
	 }
	 
	double InterarrivalTime (double lambda)
	{
		double L = 1/lambda;
		double variate = Math.exp(L);
		//return exponential (arrivalRate);
		return variate;
	}
	double ExponentialDistriLength(double Ls)
	{
		double messageLength = Ls * Math.exp(1/Ls);
		return messageLength;
	}
    void beginSimulation (int maxTimeSimulation)
    {
        Initialize ();
        while(false||((System.currentTimeMillis()-startTime) < 10000))
        //for(;;)
        {
        	//System.out.println("loop" + (System.currentTimeMillis()-startTime));
        	newArrival();
        	if (numMessages == 2)
        		break;
        }
        flag = 1;
        qlength = messageQueue.size();
        System.out.println("begin" + qlength);
        for(;;)
        {
        	//System.out.println("2nd loop");
        	handleArrival(0.0, 0.0);
        	//System.out.println("begin" + qlength);
        	if(qlength == 0)
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
//	    Sender sender = new Sender ();
//	    sender.beginSimulation (10);
//	    //System.out.println (sender);
//	} 
}

class Event {
    public static int ARRIVAL = 1;
    public static int DEPARTURE = 2;
    int type = -1;                     // Arrival or departure.
    int msgNumber = 0;                 // message number
    double eventTime;                  // When it occurs.
    double msgLength;                  // message length
    
    
    public Event (int msgNumber, double eventTime, int type, double msgLength)
    {
    	this.msgNumber = msgNumber;
    	this.eventTime = eventTime;
    	this.type = type;
    	this.msgLength = msgLength;
    }
}

class PacketList{
	public double packet_number;
	double packet_length;
	
	public PacketList(double packet_number, double packet_length)
	{
		this.packet_number = packet_number;
		this.packet_length = packet_length; 
	}
}

class LimitedQueue<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }
}

//class Message_Queue {
//	int[] buf1 = new int[65536];
//	double arrivalTime;
//	double vacateTime;
//	
//	public Message_Queue (int buf1[], double arrivalTime, double vacateTime)
//	{
//		this.buf1 = buf1;
//		this.arrivalTime = arrivalTime;
//		this.vacateTime = vacateTime;
//	}
//}

	
	
	
	
