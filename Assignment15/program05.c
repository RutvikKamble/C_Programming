// WAP which accept number from user and return difference between summation of 
// even digits and summation of odd digits.

#include<stdio.h>

int CountDiff(int iNo)
{
    int iDigit = 0, iCount1 = 0, iCount2 = 0, iDiff = 0;

    while (iNo > 0)
    {
        iDigit = iNo % 10;

        if(iDigit % 2 == 0)
        {
            iCount1 = iCount1 + iDigit;
        }
        else
        {
            iCount2 = iCount2 + iDigit;
        }

        iNo = iNo / 10;
    }
    iDiff = iCount1 - iCount2;

    return iDiff;
}

int main()
{
    int iValue = 0, iRet = 0;
    
    printf("Enter the number : ");
    scanf("%d",&iValue);

    iRet = CountDiff(iValue);    

    printf("The Difference between odd and even digit : %d",iRet);

    return 0;
}

// Time Complexity = N

