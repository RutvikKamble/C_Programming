class Demo
{
    public int i;
    final public int j;

    public Demo()
    {
        this.i = 11;
        this.j = 21;

    }
}


class FinalData1
{
    public static void mian(String A[])
    {
        Demo dobj = new Demo();

        System.out.println(dobj.i);
        System.out.println(dobj.j);

        dobj.i++;
        dobj.j++;       // Error        // We cant change the final Data(Characteristics)
    }
}