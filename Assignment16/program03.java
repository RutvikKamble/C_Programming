// WAP to find the factorial of a number using a for loop.

class Logic
{
    int iCnt = 0, iFact = 0;

    void calculateSum(int iNo)
    {
        for(iCnt = 1, iFact = 1 ; iCnt <= iNo; iCnt++)
        {
            iFact = iFact * iCnt;
        }
        System.out.println(iFact);
    }
}

class program03
{
    public static void main(String args[])
    {
        Logic obj = new Logic();
        obj.calculateSum(7);
    }
}