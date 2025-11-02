// Chech Even of Odd

#include<stdio.h>

void CheckEvenOdd(int iNo)
{
    if(iNo%2 == 0)
    {
        printf("It is even number");
    }
    else
    {
        printf("It is Odd number");
    }
}

int main()
{
    int iValue = 0;

    printf("Enter the Number : ");
    scanf("%d",&iValue);

    CheckEvenOdd(iValue);

    return 0;
}