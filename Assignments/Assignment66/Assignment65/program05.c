// Write a program which accept one number from user and and ON it first 4 bit. Return modified number.


#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


int OffBit(UINT iNo)
{
    int Mask = 0x1;
    int Mask1 = 0x1, Mask2 = 0x1, Mask3 = 0x1, Mask4 = 0x1;
    int Result = 0;

    Mask2 = Mask2 << 1;
    Mask3 = Mask3 << 2;
    Mask4 = Mask4 << 3;

    Mask = Mask1 | Mask2 | Mask3 | Mask4;

    Result = iNo | Mask;

    return Result;
    
}

int main()
{
    UINT iValue = 0, iRet = 0;
    int iNo = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = OffBit(iValue); 

    if(iRet != iValue)
    {
        printf("Modified Value is : %d",iRet);
    }
    else
    {
        printf("The first 4 bit's are already On");
    }

    return 0;
}