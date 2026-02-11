// Write a program which accept one number and position from user and check whether bit of that position ON or OFF
// If bit is one return TRUE otherwise teturn FALSE.


#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL CheckOnOff(UINT iNo, int pos)
{
    int Mask = 0x1;
    int Result = 0;

    Mask = Mask << (pos-1);

    Result = iNo & Mask;

    if(Result == Mask)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
}

int main()
{
    UINT iValue = 0, iRet = 0;
    int iNo = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    printf("Enter the number which bit you have to check ON or OFF : ");
    scanf("%d",&iNo);

    iRet = CheckOnOff(iValue,iNo); 

    if(iRet == TRUE)
    {
        printf("%dth bit is ON",iNo);
    }
    else
    {
        printf("%dth bit is OFF",iNo);
    }

    return 0;
}