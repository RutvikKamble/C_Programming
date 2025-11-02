class ThreadPriority
{
    public static void main(String A[])
    {
        System.out.println("Inside main method");
        Thread tobj = Thread.currentThread();
        String name = tobj.getName();
        System.out.println("Name of current thread is: "+name);
        int Priority = tobj.getPriority();
        System.out.println("Priority of current thread is : "+tobj.getPriority());
        
        tobj.setPriority(10);  // Changeg Priority
        Priority = tobj.getPriority();
        System.out.println("Priority of current thread is : "+tobj.getPriority());
    }
}