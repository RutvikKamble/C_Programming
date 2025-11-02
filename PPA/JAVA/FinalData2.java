class Demo
{
    public int i = 11;
    final public int j = 21;
}

class FinalData1
{
    public static void mian(String A[])
    {
        Demo dobj = new Demo();

        System.out.println(dobj.i);
        System.out.println(dobj.j);

        dobj.i++;
        //dobj.j++;       //Error
    }
}