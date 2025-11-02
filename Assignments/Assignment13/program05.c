// // Find Sum of first N even number

#include<stdio.h>

int SumNaturalNumber(int iNo)
{
    int iCnt = 0, iCount = 0;

    for(iCnt = 1; iCnt <= iNo; iCnt++)
    {   
        if(iCnt % 2 == 0)
        {
            iCount = iCount + iCnt;
        }
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