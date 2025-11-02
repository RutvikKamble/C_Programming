// WAP to find the sum of even and odd digit separately in a number

import javax.lang.model.util.SimpleElementVisitor6;

class logic 
{
    void CheckPrime(int iNo)
    {
        int iCnt = 0, iEven = 0, iOdd = 0;

        for(iCnt = 1; iCnt <= iNo; iCnt++)
        {
            if(iCnt % 2 == 0)
            {
                iEven = iEven + iCnt;
            }
            else
            {
                iOdd = iOdd + iCnt;
            }
        }
        System.out.println("The sum of even number is : \t"+ iEven);
        System.out.println("The sum of odd number is : \t"+ iOdd);
    
    }
}

class program04
{
    public static void main(String args[])
    {
        logic obj = new logic();
        obj.CheckPrime(6);

    }
}