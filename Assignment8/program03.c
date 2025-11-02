// Write a program to find factorial of given program

#include<stdio.h>

int DisplayFact(int iNo)
{
    int iCnt = 0;
    int iFact = 0;

    for(iCnt = 1,iFact = 1; iCnt <= iNo; iCnt++)
    {
        iFact = iFact * iCnt;
    }

    return iFact;
}

int main()
{
    int iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = DisplayFact(iValue);

    printf("Factorial of given number is : %d",iRet);    

    return 0;
}