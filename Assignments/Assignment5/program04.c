// Check Positive Negative or Zeor

#include<stdio.h>

void CheckPosNeg(int iNo)
{
    int iCnt = 0;

    if(iNo > iCnt)
    {
        printf("It is positive number ");
    }
    else if (iNo < iCnt)
    {
        printf("It is Negative number ");
    }
    else
    {
        printf("It is Zero ");
    }
}

int main()
{
    int iValue = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    CheckPosNeg(iValue);

    return 0;
}