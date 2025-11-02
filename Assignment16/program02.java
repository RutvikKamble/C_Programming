// WAP to check whether a given number is even or odd.

class Logic
{
    int iCnt = 0, iSum = 0;

    void checkEvenOdd(int iNo)
    {
        if(iNo % 2 == 0)
        {
            System.out.println("Given number is Even");
        }
        else
        {
            System.out.println("Given number is Odd");
        }
    }
}

class program02
{
    public static void main(String args[])
    {
        Logic obj = new Logic();
        obj.checkEvenOdd(7);
    }
}