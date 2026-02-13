// Write a program which accept two number from user and display position of common ON bits from that two numbers

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


void CountCommon(UINT iNo1, UINT iNo2)
{
    int iCount = 1;
    UINT Mask = 0x1;
    UINT No = 0, Result = 0;

    No = iNo1 & iNo2;

    while(iCount <= 32)
    {
        Result = No & Mask;

        if(Result == Mask)
        {
            printf("Bit : %d\n",iCount);
        }
        Mask = Mask << 1;

        iCount++;
    }
    return;
}

int main()
{
    UINT iValue1 = 0, iValue2 = 0;
    int iNo = 0;

    printf("Enter the First number : ");
    scanf("%u",&iValue1);

    printf("Enter the Second number : ");
    scanf("%u",&iValue2);

    printf("The position of OF common bit's from the given numbers : \n");

    CountCommon(iValue1,iValue2); 

    
    return 0;
}