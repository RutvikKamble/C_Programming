/*
    Step 1 : Understand thr problem Statement
    Step 2 : Writeh the algorithm
    Step 3 : Decide the programming language
    Step 4 : Write THe program
    Step 5 : Test the program
*/

/*

START
	-> Accept first input as No1
	-> Accept first input as No2
	-> Perform Addition of No1 & No2
	-> Display the Addition on Screen
STOP
*/

#include<stdio.h>

float AdditionTwoNumbers(float fNo1,float fNo2)
{
    float fSum = 0;
    fSum = fNo1 + fNo2;    // Business Logic
    return fSum;
}   

int main()
{
    float fValue1 = 0.0f, fValue2 = 0.0f, fRet = 0.0f;

    printf("Enter first number : ");
    scanf("%f",&fValue1);

    printf("Enter second number : ");
    scanf("%f",&fValue2);

    fRet = AdditionTwoNumbers(fValue1,fValue2);

    printf("Addition is : %f\n",fRet);

    return 0;
}