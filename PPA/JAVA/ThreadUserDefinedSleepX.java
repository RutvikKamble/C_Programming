// Code to Demonstrate Thread Scheduling

class Demo extends Thread
{
    public  void run() // throws InterruptedException // overridden method does not throw InterruptedException
    {
        int i = 0;
        try
        {
            for(i = 1; i<=5; i++)
             {
            Thread.sleep(1000); // 1 secand
            System.out.println("Inside run method of "+Thread.currentThread().getName());
             }
        }
        catch(InterruptedException iobj)
        {
        }
    }
}

class ThreadUserDefinedSleepX
{
    public static void main(String A[])
    {

        System.out.println("Inside main method");
        Demo dobj1 = new Demo();
        Demo dobj2 = new Demo();

        dobj1.setName("First");
        dobj2.setName("Second");


        // Checked Exception
        try
        {
        dobj1.start();
        dobj1.join();

        dobj2.start();
        dobj2.join();
        }        
        catch(InterruptedException iobj)
        {
        }

        System.out.println("End of main Thread");
    }
}