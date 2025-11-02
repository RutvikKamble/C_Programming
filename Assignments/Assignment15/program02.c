// WAP whic haccept number rom user and return th count of odd Digits

#include<stdio.h>

int CountOddDigit(int iNo)
{
    int iDigit = 0, iCount = 0;

    while (iNo > 0)
    {
        iDigit = iNo % 10;

        if(iDigit % 2 != 0)
        {
            iCount = ++iCount;
        }

        iNo = iNo / 10;
    }

    return iCount;
}

int main()
{
    int iValue = 0, iRet = 0;
    
    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = CountOddDigit(iValue);    

    printf("The total Odd Digit are : %d",iRet);

    return 0;
}

// Time Complexity = N