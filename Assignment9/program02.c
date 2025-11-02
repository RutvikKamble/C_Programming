// Accept amount in US dollar and return its corresponding value in Indian
// Currency Consider 1$ as 70 rupees

#include<stdio.h>

int DollerToINR(int iNo)
{
    int Doller = 70;

    Doller = Doller * iNo;

    return Doller;
}

int main()
{
    int iValue = 0, iRet = 0;

    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = DollerToINR(iValue);

    printf("Value in INR is : %d",iRet);

    return 0;
}