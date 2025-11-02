// WAP which accept number from user and return multiplicatio of all digits.

#include<stdio.h>

int MultDigit(int iNo)
{
    int iDigit = 0, iCount = 0;

    iCount = 1;
    
    while (iNo > 0)
    {
        iDigit = iNo % 10;

        iCount = iCount * iDigit;

        iNo = iNo / 10;
    }
    return iCount;
}

int main()
{
    int iValue = 0, iRet = 0;
    
    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = MultDigit(iValue);    

    printf("The total Odd Digit are : %d",iRet);

    return 0;
}

// Time Complexity = N