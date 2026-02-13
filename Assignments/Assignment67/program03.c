// Write a program which accept one number from user and check whether 9th or 12th ON or OFF

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL Check(UINT iNo)
{
    UINT Mask1 = 0x1, Mask2 = 0x1;   
    UINT Result1 = 0, Result2 = 0;

    Mask1 = Mask1 << 8;
    Mask2 = Mask2 << 11;

    Result1 = Mask1 & iNo;
    Result2 = Mask2 & iNo;

    if((Result1 == Mask1) && (Result2 == Mask2))
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
}

/*
BOOL Check(UINT iNo)
{
    UINT Mask = (1<<8) | (1<<11);
    return ((iNo & Mask) == Mask);
}
*/

int main()
{
    UINT iValue = 0;
    BOOL iRet = FALSE;

    printf("Enter the number : ");
    scanf("%u",&iValue);

    iRet = Check(iValue);

    if(iRet == TRUE)
    {
        printf("Yes, Both bits are On");
    }
    else
    {
        printf("No, Both bits are not On");
    }

    return 0;
}