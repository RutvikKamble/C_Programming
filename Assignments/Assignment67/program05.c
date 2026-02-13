// Write a program which accept one number, and range of position from user, Toggle all bits from that range.

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


int ToggleBits(UINT Number, UINT pos1, UINT pos2)
{
    UINT Mask = 1;   
    UINT Result = 0;
    int i = 0;

    Mask = Mask << (pos1-1);

    for(i = pos1; i <= pos2; i++)
    {
        Number = Number ^ Mask;

        Mask = Mask << 1;
    }

    return Number;

}


int main()
{
    UINT iValue1 = 0, iNo1 = 0, iNo2 = 0;

    UINT iRet = 0;

    printf("Enter the number : ");
    scanf("%u",&iValue1);

    printf("Enter the range of bit : ");
    scanf("%u",&iNo1);
    scanf("%u",&iNo2);

    iRet = ToggleBits(iValue1,iNo1,iNo2);

    printf("Modified Number is %d",iRet);

    return 0;
}