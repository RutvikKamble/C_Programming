// Write a program which accept total marks & obtained marks from user and claculate percentage.

#include<stdio.h>

int Percentage(int iNo1, int iNo2)
{
    int iNum1 = 0;
    int iNum2 = 0;

    if(iNo1 == 0)
    {
        printf("Invalid Input :");

        return 0;
    }

    if(iNo2 == 0)
    {
        iNo2 = 1;
    }
    iNum1 = 100;

    iNum2 = iNo1/iNum1;
    iNum1 = iNo2/iNum2;

    return iNum1;
}

int main()
{
    int iValue1 = 0, iValue2 = 0;
    int iRet = 0;

    printf("Enter Total marks : ");
    scanf("%d",&iValue1);

    printf("Enter Obtained marks : ");
    scanf("%d",&iValue2);

    iRet = Percentage(iValue1,iValue2);

    printf("Percentage is :%d ",iRet);

    return 0;
}