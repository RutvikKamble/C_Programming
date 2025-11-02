// Program to divide tow numbers

//////////////////////////////////////////////////////////////////////
//
//  Required Header file
//
//////////////////////////////////////////////////////////////////////

# include<stdio.h>

//////////////////////////////////////////////////////////////////////
//
//  Function Name : Divide
//  Description :   Division of two number
//  Input   :       int
//  Output  :       int
//  Author  :       Rutvik Bibhishan Kamble
//  Date    :       17/10/2025
//
//////////////////////////////////////////////////////////////////////

int Divide(int iNo1, int iNo2)
{
    
    int iAns = 0;                                       //  Take user input

    if(iNo1<0)              // Updater
    {
        iNo1 = -iNo1;
    }
    if(iNo2 < 0)            // Updater
    {
        iNo1 = -iNo1;
    }

    iAns = iNo1 / iNo2;                                 // Business Logic
    return iAns;                                        

}   // End of Divide

//////////////////////////////////////////////////////////////////////
//
//  Entry point functon of the program
//
//////////////////////////////////////////////////////////////////////

int main()
{
    int iValue1 = 15, iValue2 = 5;                      //  To Accept the input
    int iRet = 0;                                       //  To Store the result

    iRet = Divide(iValue1,iValue2);                     // Funciton call

    printf("Divisiton is : %d",&iRet);
    
    return 0;

}   // End of main