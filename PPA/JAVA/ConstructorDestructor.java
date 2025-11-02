class Demo
{
    public int no1;
    public int no2;

    public Demo()
    {
        System.out.println("Inside Defaiutl Constructor");
    }

    public Demo(int i, int j)
    {
        System.out.println("Inside Parameterize Constructor");
    }

    protected void finalize()     
    {
        System.out.println("Inside finalize");
    }
}

class ConstructorDestructor
{
    public static void main(String Arr[])
    {
        Demo dobj1 = new Demo();                //Demo dobj1()
        Demo dobj2 = new Demo(11,21);       //Demo dobj2(11,21)

        dobj1 = null;
        dobj2 = null;
        
        System.gc();

        System.out.println("End of main");
    }
}