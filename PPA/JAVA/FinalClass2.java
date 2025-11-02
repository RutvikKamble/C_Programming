//Composition

final class Base
{
    public void fun()
    {
        System.out.println("Inside fun");
    }
}

class Derived
{
    public Base bobj;       //Composition

    public Derived()
    {
        bobj = new Base();
    }
}

class FinalClass2
{
    public static void main(String A[])
    {
        Base dobj = new Base(); 
        dobj.fun(); 
    }
}