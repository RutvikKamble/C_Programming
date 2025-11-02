// WAP to find even factorial o given number

#include<stdio.h>

int EvenFactor(int iNo)
{
    int iCnt = 0;
    int iFact = 0;

    for(iCnt = 1, iFact = 1; iCnt < iNo; iCnt++)
    {
        if(iCnt%2 == 0)
        {
            iFact = iFact * iCnt;
        }
    }    
    return iFact;

}   // Time Complexity is = N

int main()
{
    int iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = EvenFactor(iValue);

    printf("Even factorial of number is : %d",iRet);

    return 0;
}
