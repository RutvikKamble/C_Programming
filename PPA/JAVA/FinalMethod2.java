class Base
{
    final public void fun()       // Defination
    {}
    final public void gun()       // Defination
    {}
}

class Derived extends Base
{
    public void run()       // Defination
    {}
    public void gun()       // Overriding       // We can't override the final method
    {}
}

class FinalMethod2
{
    public static void main(String A[])
    {
        //Base dobj = new Base();
    }
}