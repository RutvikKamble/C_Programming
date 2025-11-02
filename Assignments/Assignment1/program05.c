// Accept one number from user and print that number of *on screen.

//////////////////////////////////////////////////////////////////////
//
//  REquired Header File
//
//////////////////////////////////////////////////////////////////////

#include<stdio.h>

//////////////////////////////////////////////////////////////////////
//
//  Function Name : Accept
//  Description :   It is print * Accepted number
//  Input   :       int
//  Output  :       void
//  Author  :       Rutvik Bibhishan Kamble
//  Date    :       17/10/2025
//
//////////////////////////////////////////////////////////////////////

void Accept(int iNo)
{
    int iCnt = 0;                                   // Accept user input

    for(iCnt = 1; iCnt <= iNo; iCnt++)              // Iteraton
    {
        printf("*\t");                              
    }
}   //  End of Accept

//////////////////////////////////////////////////////////////////////
//
//  Entry point funciton for this program
//
//////////////////////////////////////////////////////////////////////

int main()
{
    int iVlaue = 0;                                 // To Accept the number
    iVlaue = 5;

    Accept(iVlaue);                                 // Function call

    return 0;
}   //  End of main

