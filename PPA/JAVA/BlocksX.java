class Demo
{
    public int i;                   //Instance Variable
    static public int j = 21;       //Class Variable

    static
    {
        System.out.println("Inside static block");
        j = 21;
    }

    {
        System.out.println("Inside Initializer block");
    }

    public Demo()
    {
        System.out.println("Inside Constructor");
        this.i = 11;
    }

    public Demo(int a)
    {
        System.out.println("Inside Parametrised Constructor");
        this.i = 11;
    }
}

class BlocksX
{
    public static void main(String A[])
    {
        System.out.println("Inside main ");
        
        Demo dobj1 = new Demo();
        Demo dobj2 = new Demo();
        Demo dobj3 = new Demo();
        Demo dobj4 = new Demo(11);
    }
}