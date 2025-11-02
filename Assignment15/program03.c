// WAP which accept number from user and return the count of dights in between 3 & 7.

#include<stdio.h>

int CountRange(int iNo)
{
    int iDigit = 0, iCount = 0;

    while (iNo > 0)
    {
        iDigit = iNo % 10;

        if(iDigit > 3 && iDigit < 7)
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

    iRet = CountRange(iValue);    

    printf("The Digit between 3 to 7 : %d",iRet);

    return 0;
}

// Time Complexity = N