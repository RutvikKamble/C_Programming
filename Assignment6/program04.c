// Write a program which accept three numbers ant print its multiplication.

#include<stdio.h>

int Multiplication(int iNo1, int iNo2, int iNo3)
{
    int mul = 0;

    if(iNo1 == 0)
    {
        iNo1 = 1;
    }
    if(iNo2 == 0)
    {
        iNo2 = 1;
    }
    if(iNo3 == 0)
    {
        iNo3 = 1;
    }

    mul = iNo1 * iNo2 * iNo3;

    return mul;
}

int main()
{
    int iValue1 = 0, iValue2 = 0, iValue3 = 0, Result = 0;

    printf("Enter three number : ");
    scanf("%d %d %d",&iValue1,&iValue2,&iValue3);

    Result = Multiplication(iValue1,iValue2,iValue3);

    printf("Multiplication is : %d",Result);

    return 0;
}