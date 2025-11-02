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
}
class Constructor
{
    public static void main(String A[])
    {
        Demo dobj1 = new Demo();                //Demo dobj1()

        Demo dobj2 = new Demo(11,21);       //Demo dobj2(11,21)
    }
}