// Print all number from 1 to n.

#include<stdio.h>

void PrintNo(int iNo)
{
    int iCnt = 0;

    for(iCnt = 1; iCnt <= iNo; iCnt++)
    {
        printf("%d\t",iCnt);
    }
}

int main()
{
    int iValue = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    PrintNo(iValue);

    return 0;
}

// Time Complexity  = N