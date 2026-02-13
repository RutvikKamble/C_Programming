// Write a program which accept one number, two position from user and check whether bit at first and bit at second positon ON or OFF

#include<stdio.h>

typedef int BOOL;
typedef unsigned int UINT;

#define TRUE 1
#define FALSE 0


BOOL Check(UINT iNo, int iPos)
{
    UINT Mask = 0x1;   
    UINT Result = 0;

    Mask = Mask << (iPos-1);

    Result = Mask & iNo;

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
    UINT iValue1 = 0;
    int iValue2 = 0;
    int No = 0;
    int i = 0;
    BOOL iRet = FALSE;

    printf("Enter the number : ");
    scanf("%u",&iValue1);

    printf("Enter the how many bit's you have to check which are on or off : ");
    scanf("%d",&iValue2);

    printf("Enter %d Bit\n",iValue2);

    for(i = 1; i <= iValue2; i++)
    {
        printf("Enter the position of %dth bit : ",i);
        scanf("%d",&No);

        iRet = Check(iValue1,No);

        if (iRet == TRUE)
        {
            printf("The bit is ON\n");
        }
        else
        {
            printf("The bit is OFF\n");
        }
    }

    return 0;
} 