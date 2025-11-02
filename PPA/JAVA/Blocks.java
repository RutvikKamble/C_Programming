class Demo
{
    public int i;                   //Instance Variable
    static public int j = 21;       //Class Variable

    static
    {
        System.out.println("Inside static block");
        j = 21;
    }

    public Demo()
    {
        System.out.println("Inside Constructor");
        this.i = 11;
    }

    public void fun()           //Instance Method
    {
        System.out.println("fun i:"+this.i);
        System.out.println("fun j:"+Demo.j);   
    }

    public void gun()           //class Method
    {
        // System.out.println("gun i:"+this.i);        // Not Allowed
        System.out.println("gun j:"+Demo.j);
    }
}

class Blocks
{
    public static void main(String A[])
    {
        System.out.println("Inside main ");
        
        Demo dobj1 = new Demo();
        Demo dobj2 = new Demo();
        Demo dobj3 = new Demo();
    }
}