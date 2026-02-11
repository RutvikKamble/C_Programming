// Write a program which accept one number and position from user and ON that bit. Return Modified number.

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL OnBit(UINT iNo, int pos)
{
    int Mask = 0x1;
    int Result = 0;

    Mask = Mask << (pos-1);

    Result = iNo | Mask;

    return Result;
}

int main()
{
    UINT iValue = 0, iRet = 0;
    int iNo = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    printf("Enter the number which bit you have to OFF : ");
    scanf("%d",&iNo);

    iRet = OnBit(iValue,iNo); 

    if(iRet != iValue)
    {
        printf("The Modifide Value is : %d",iRet);
    }
    else
    {
        printf("the %dthe bit is already ON",iNo);
    }

    return 0;
}