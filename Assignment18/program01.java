// WAP to chech whether a number is prime or not

class logic 
{
    void CheckPrime(int iNo)
    {
        int iCnt = 0;

        for(iCnt = 2; iCnt <= iNo/2; iCnt++)
        {
            if(iNo % iCnt == 0)
            {
                System.out.println("The number is not prime");
            }
            else
            {
                System.out.println("The number is prime");
            }
        }
    }
}

class program01
{
    public static void main(String args[])
    {
        logic obj = new logic();
        obj.CheckPrime(6);

    }
}