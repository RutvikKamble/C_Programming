// Write a program which checks whether 15th bit is On or Off.

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL Check(UINT iNo)
{
    int Mask = 0x1;
    int Result = 0;

    Mask = Mask << 14;

    Result = Mask & iNo;

    if(Result == Mask)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }

   /*  int Mask = 0x00008000;
    int Result = 0;

    Result = Mask & iNo;

    if(Result == Mask)
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }     */
}

int main()
{
    int iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = Check(iValue);

    if(iRet == TRUE)
    {
        printf("15th bit is on");
    }
    else
    {
        printf("15th bit is off");
    }


    return 0;
}