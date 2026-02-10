// Write a program which checks 5th or 8th ON or OFF.

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL Check(UINT iNo)
{

    /* int Mask1 = 0x1, Mask2 = 0x1;
    int Result1 = 0, Result2 = 0;

    Mask1 = Mask1 << 4;
    Mask2 = Mask2 << 7;

    Result1 = Mask1 & iNo;
    Result2 = Mask2 & iNo;

    if((Result1 == Mask1) && (Result2 == Mask2))
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    } */

    /* 
    int Mask1 = 0x1;
    int Result1 = 0;

    if(((Result1 = (Mask1 << 4) & iNo) == (Mask1 << 4)) && ((Result1 = (Mask1 << 7) & iNo) == (Mask1 << 7)))
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
    */

    UINT Mask = (1 << 4) | (1 << 7);

    if((iNo & Mask) == Mask)
    {
    return TRUE;
    }
    return FALSE;

}

int main()
{
    int iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = Check(iValue);

    if(iRet == TRUE)
    {
        printf("Yes, both bits are on");
    }
    else
    {
        printf("No, both bits are not on");
    }


    return 0;
}