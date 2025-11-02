// WAP which returns difference between Even factorial and odd
// factorial of given number

#include<stdio.h>

int FactorDiff(int iNo)
{
    int iCnt = 0, iNum1 = 0, iNum2 = 0, iDiff = 0;

    for(iCnt = 1,iNum1 = 1, iNum2 = 1; iCnt <= iNo; iCnt++)
    {
        if(iCnt%2 != 0)
        {
            iNum1 = iNum1 * iCnt;
        }
        else
        {
            iNum2 = iNum2 * iCnt;
        }
        
        iDiff = iNum2 - iNum1 ;
    }   
    return iDiff;

}   // Time Complexity is = N

int main()
{
    int iValue = 0,iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = FactorDiff(iValue);

    printf("Factorial difference is %d",iRet);

    return 0;
}
