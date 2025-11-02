public class Demo
{
    public int i;
    static public int j = 21;

    public Demo()
    {
        this.i = 11;
    }
}

class StaticMethod
{
    public static void main()
    {
        System.out.println(Demo.j);

        Demo dobj = new Demo();
        System.out.println(dobj.i);
    }
}
