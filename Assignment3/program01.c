// Write the program which accept one number from user and print that nmber of even number on screen

#include<stdio.h>

void DisplayEvenNo(int iNo)
{
    int iCnt = 0;
    int iNo1 = 0;
    for(iCnt = 1; iCnt <= iNo; iCnt++)
    {
        iNo1 += 2;
        printf("%d\t",iNo1);
    }
}

int main()
{
    int iValue = 0;

    printf("Enter the Number :");
    scanf("%d",&iValue);

    DisplayEvenNo(iValue);

    return 0;
}
