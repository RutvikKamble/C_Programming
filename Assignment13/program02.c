// // Print all even numbers up to N

#include<stdio.h>

void PrintNo(int iNo)
{
    int iCnt = 0;

    for(iCnt = 1; iCnt <= iNo/2; iCnt++)
    {
        if(iCnt%2 == 0)
        {
            printf("%d\t",iCnt);
        }
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

// Time Complexity  = N/2