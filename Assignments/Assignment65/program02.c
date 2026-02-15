// Write a program which accept one number from user and OFF 7th bit of that number if it is on return modified number.


#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


int OffBit(UINT iNo)
{
    int Mask = 0x1;
    int Mask1 = 0x1, Mask2 = 0x1;
    int Result = 0;

    Mask1 = Mask1 << 6;
    Mask2 = Mask2 << 9;

    Mask = Mask1 | Mask2;

    Mask = ~Mask;

    Result = iNo & Mask;

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
        printf("The 7th and 10th bit is alredy OFF");
    }

    return 0;
}