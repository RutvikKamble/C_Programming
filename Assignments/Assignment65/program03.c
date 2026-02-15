// Write a program which accept one number from user and Toggle 7th of that number. Return modified number.


#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


int ToggleBit(UINT iNo, int pos)
{
    int Mask = 0x1;
    int Result = 0;

    Mask = Mask << (pos-1);

    Result = iNo ^ Mask;

    return Result;
    
}

int main()
{
    UINT iValue = 0, iRet = 0;
    int iNo = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    printf("Enter the number which bit you have to Toggle : ");
    scanf("%d",&iNo);

    iRet = ToggleBit(iValue,iNo); 

    if(iRet != iValue)
    {
        printf("Modified Value is : %d",iRet);
    }

    return 0;
}