abstract class Base
{
    public int i,j;
    public int Addition(int a, int b)
    {
        return a+b;
    }
    abstract public int Substracton(int a, int b);   
}

class AbstractDemo2
{
    public int x;

    public static void main(String A[])
    {
        Base bp = new Base();       //Error
    }
}
  
