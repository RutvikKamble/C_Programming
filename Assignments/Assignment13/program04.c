// // Find Sum of first N natural number

#include<stdio.h>

int SumNaturalNumber(int iNo)
{
    int iCnt = 0, iCount = 0;

    for(iCnt = 1; iCnt <= iNo; iCnt++)
    {
        iCount = iCount + iCnt;
    }
    return iCount;
}

int main()
{
    int iValue = 0, iSum = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    printf("Sum of n Natural number ; %d",SumNaturalNumber(iValue));

    return 0;
}

// Time Complexity  = N