// WAP which accept number from user and return the count of even digit

#include<stdio.h>

int CountEvenDigit(int iNo)
{
    int iDigit = 0, iCount = 0;

    while (iNo > 0)
    {
        iDigit = iNo % 10;

        if(iDigit % 2 == 0)
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

    iRet = CountEvenDigit(iValue);    

    printf("The total Even Digit are : %d",iRet);

    return 0;
}

// Time Complexity = N