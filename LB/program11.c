/*
START
	-> Accept first input as No1
	-> Accept first input as No2
    -> If the input is negative then convert it into positive
	-> Perform Addition of No1 & No2
	-> Display the Addition on Screen
STOP
*/

//////////////////////////////////////////////////////////////////////
//
//  Required Header files
//
//////////////////////////////////////////////////////////////////////

#include<stdio.h>

//////////////////////////////////////////////////////////////////////
//
//  Function Name : AdditionTwoNumbers
//  Description :   It is used to perform Addition
//  Input   :       Float, Float
//  Output  :       Float
//  Author  :       Rutvik Bibhishan Kamble
//  Date    :       09/10/2025
//
//////////////////////////////////////////////////////////////////////

float AdditionTwoNumbers(
                            float fNo1,                     // First Input
                            float fNo2                      // Second Input
                        )
{
    float fSum = 0;                                         // To Store the result
    
    if(fNo1 < 0.0f)                                         // Updater
    {
        fNo1 = -fNo1;
    }
    
    if(fNo2 < 0.0f)                                         // Updater
    {
        fNo2 = -fNo2;
    }
    fSum = fNo1 + fNo2;                                     // Business Logic

    return fSum;
}   // End of AdditonTwoNumbers   

//////////////////////////////////////////////////////////////////////
//
//  Entry point function for the application
//
//////////////////////////////////////////////////////////////////////

int main()
{
    float fValue1 = 0.0f, fValue2 = 0.0f;                   // To Accept user Input
    float fRet = 0.0f;                                      // To Store the result

    printf("Enter first number : ");
    scanf("%f",&fValue1);

    printf("Enter second number : ");
    scanf("%f",&fValue2);

    fRet = AdditionTwoNumbers(fValue1,fValue2);             // Method Call

    printf("Addition is : %f\n",fRet);  

    return 0;
}   // End of main

//////////////////////////////////////////////////////////////////////
//
//  Testcases successfully handaled bye the application
//
//  Input1 : 10.5       Input : 4.5         Output  :   15
//  Input1 : -10.5      Input : 4.5         Output  :   15
//  Input1 : 10.5       Input : -4.5        Output  :   15
//  Input1 : -10.5      Input : -4.5        Output  :   15
//  Input1 : 10.5       Input : 05          Output  :   10.5
//
//////////////////////////////////////////////////////////////////////