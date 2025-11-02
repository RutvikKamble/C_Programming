// Write a program which accept number from user and print its numbers line

#include<stdio.h>

void Display(int iNo)
{
    int iCnt = 0;
    int iRet = 0;

    iRet = iNo;
    iRet = -iRet;

    for(iCnt = iRet; iCnt <= iNo; iCnt++)
    {
        printf("%d\t",iCnt);
    }

}

int main()
{
    int iValue = 0;

    printf("Enter the first number : ");
    scanf("%d",&iValue);

    Display(iValue);

    return 0;
}