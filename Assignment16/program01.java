// WAP to calculate the sum of first N natural numbers.

class Logic
{
    int iCnt = 0, iSum = 0;

    void calculateSum(int iNo)
    {
        for(iCnt = 1; iCnt <= iNo; iCnt++)
        {
            iSum = iSum + iCnt;
        }
        System.out.println(iSum);
    }
}

class program01
{
    public static void main(String args[])
    {
        Logic obj = new Logic();
        obj.calculateSum(7);
    }
}