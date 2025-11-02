// Either Extends Thread or implement Runnable

class Demo extends Thread
{
    public  void run()
    {
        System.out.println("Inside run method");
    }
}

class ThreadUserDefined1
{
    public static void main(String A[])
    {
        Demo dobj = new Demo();
        dobj1.start();
    }
}