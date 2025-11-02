// Accept one number and check whether is it divisible by 5 or not.

//////////////////////////////////////////////////////////////////////
//
//  Required Header files
//
//////////////////////////////////////////////////////////////////////

#include<stdio.h>
#include<stdbool.h>

//////////////////////////////////////////////////////////////////////
//
//  Function Name : Check
//  Description :   It check the number is divisible by 5 or not
//  Input   :       Bool
//  Output  :       Bool
//  Author  :       Rutvik Bibhishan Kamble
//  Date    :       17/10/2025
//
//////////////////////////////////////////////////////////////////////

bool Check(iNo)
{
    if((iNo % 5) == 0)
    {
        return true;
    }
    else
    {
       return false;
    }
}   // End of Check

//////////////////////////////////////////////////////////////////////
//
//  Entry point function for the program
//
//////////////////////////////////////////////////////////////////////

int main()
{
    int iValue = 0;                             //  Accept the input
    bool bRet = false;                          //  To Store the result

    printf("Enter number : ");
    scanf("%d",&iValue);

    bRet = Check(iValue);                       //  Funciton call

    if(bRet == true)
    {
        printf("Divisible by 5");
    }
    else
    {
        printf("Not Divisible by 5");
    }

    return 0;
}   // End of main