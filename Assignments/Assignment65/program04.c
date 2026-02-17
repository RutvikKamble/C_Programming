// Write a program which accept one number from user and Toggle 7th and 10th bit of that number. Return modified number.


#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


int ToggleBit(UINT iNo)
{
    int Mask = 0;
    int Mask1 = 0x1;
    int Mask2 = 0x1;
    int Result = 0;

    Mask1 = Mask1 << 6;
    Mask2 = Mask2 << 9;

    Mask = Mask1 | Mask2;

    Result = iNo ^ Mask;

    return Result;
    
}

int main()
{
    UINT iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = ToggleBit(iValue); 

    if(iRet != iValue)
    {
        printf("Modified Value is : %d",iRet);
    }

    return 0;
}