

public class NIC {
	public static void main (String[] argv)
	{
		long t= System.currentTimeMillis();
		long end = t+15000;
		//while(System.currentTimeMillis() < end) {
			
			System.out.println("*************The Send Module output ***************");
			sender1 sender = new sender1 ();
			sender.beginSimulation (argv[0]);
	    
			System.out.println("*************The MAC Module output **************** ");
			MacModule mm = new MacModule();
			mm.startMM();
	    
			System.out.println("*************The Receive Module output ************* ");
			ReceiveModule RM = new ReceiveModule();
			RM.startReceiving();
		//}
	}
}
