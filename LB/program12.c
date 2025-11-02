/*
START
    Accept number and store as no
    Divide no by 2
    If the remender is 0
        then display as Even
    otherwise
        display as odd
STOP
*/

#include<stdio.h>

void CheckEvenOdd(int iNo)
{
    int iRem = 0;

    int iRem = iNo % 2;

    if (iRem == 0)
    {
        printf("It is Even Number\n");
    }
    else
    {
        printf("It is Odd Number\n");
    }
}

int main()
{
    int iValue = 0;

    printf("Ener a number : ");
    scanf("%d",&iValue);

    CheckEvenOdd(iValue);

    return 0;
}