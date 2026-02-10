// Write a program which checks Whether 7th, 15th, and 21th, 28th bit is ON or OFF.

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL Check(UINT iNo)
{
    UINT Mask1 = 0x1, Mask2 = 0x1, Mask3 = 0x1, Mask4 = 0x1;   
    UINT Result1 = 0, Result2 = 0, Result3 = 0, Result4 = 0;

    Mask1 = Mask1 << 6;
    Mask2 = Mask2 << 14;
    Mask3 = Mask3 << 20;
    Mask4 = Mask4 << 27;

    Result1 = Mask1 & iNo;
    Result2 = Mask2 & iNo;
    Result3 = Mask3 & iNo;
    Result4 = Mask4 & iNo;

    if((Result1 == Mask1) && (Result2 == Mask2) && (Result3 == Mask3) && (Result4 == Mask4))
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
    UINT Mask = (1<<6) | (1<<14) | (1<<20) | (1<<27);
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
        printf("Yes, Above all bits are On");
    }
    else
    {
        printf("No, Avove all bits are not On");
    }

    return 0;
}