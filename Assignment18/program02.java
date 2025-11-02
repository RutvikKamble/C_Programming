// WAP to print all even number upto N

class logic 
{
    void CheckPrime(int iNo)
    {
        int iCnt = 0;

        for(iCnt = 1; iCnt <= iNo; iCnt++)
        {
            if(iCnt % 2 == 0)
            {
                System.out.println("\t"+iCnt);
            }
        }
    }
}

class program02
{
    public static void main(String args[])
    {
        logic obj = new logic();
        obj.CheckPrime(12);

    }
}