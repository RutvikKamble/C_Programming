// Write a program which accept one number from user and Toggle contents of first and last nibble of that number.
// (Nibble means group of four bytes)

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL ToggleNibble(UINT iNo)
{
    UINT Mask = 0x1;
    UINT Result = 0;

    Mask = 0xf000000f;

    Result = iNo ^ Mask;

    return Result;
}

int main()
{
    UINT iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = ToggleNibble(iValue); 

    printf("The Modifide Value is : %d",iRet);
    
    return 0;
}